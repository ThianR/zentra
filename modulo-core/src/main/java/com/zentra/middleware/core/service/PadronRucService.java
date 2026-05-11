package com.zentra.middleware.core.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.logging.Logger;

@Service
public class PadronRucService {

    private static final Logger logger = Logger.getLogger(PadronRucService.class.getName());
    private static final int BATCH_SIZE = 10000;
    
    // URL del portal donde se listan los archivos segmentados
    private static final String URL_PORTAL_DNIT = "https://www.dnit.gov.py/web/portal-institucional/listado-de-ruc-con-sus-equivalencias";

    private final JdbcTemplate jdbcTemplate;

    public PadronRucService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Sincroniza el padrón completo descargando los segmentos ruc0.zip...ruc9.zip
     */
    @Transactional
    public String descargarYProcesarPadron() throws Exception {
        logger.info("Iniciando sincronización automática desde el portal DNIT...");
        
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        // 1. Obtener el HTML del portal para extraer los enlaces actuales (tienen UUIDs dinámicos)
        HttpRequest requestPortal = HttpRequest.newBuilder()
                .uri(URI.create(URL_PORTAL_DNIT))
                .GET()
                .build();

        HttpResponse<String> responsePortal = client.send(requestPortal, HttpResponse.BodyHandlers.ofString());
        if (responsePortal.statusCode() != 200) {
            throw new RuntimeException("No se pudo acceder al portal de la DNIT. HTTP " + responsePortal.statusCode());
        }

        // 2. Extraer enlaces que terminen en .zip
        List<String> zipUrls = extraerEnlacesZip(responsePortal.body());
        if (zipUrls.isEmpty()) {
            throw new RuntimeException("No se encontraron archivos ZIP en el portal de la DNIT.");
        }

        logger.info("Se encontraron " + zipUrls.size() + " segmentos de padrón para descargar.");

        // 3. Limpiar tabla antes de empezar la carga masiva
        logger.info("Vaciando tabla padron_ruc (TRUNCATE)...");
        jdbcTemplate.execute("TRUNCATE TABLE padron_ruc");

        long totalGlobal = 0;

        // 4. Procesar cada segmento
        for (String zipUrl : zipUrls) {
            logger.info("Descargando segmento: " + zipUrl);
            HttpRequest requestZip = HttpRequest.newBuilder()
                    .uri(URI.create(zipUrl))
                    .GET()
                    .build();

            HttpResponse<InputStream> responseZip = client.send(requestZip, HttpResponse.BodyHandlers.ofInputStream());
            if (responseZip.statusCode() == 200) {
                totalGlobal += procesarInputStreamZip(responseZip.body(), false);
            } else {
                logger.warning("Error al descargar segmento " + zipUrl + ". Saltando... (HTTP " + responseZip.statusCode() + ")");
            }
        }

        return "Sincronización completa. Total de registros importados: " + totalGlobal;
    }

    /**
     * Procesa un flujo ZIP (subido manualmente), limpia tabla e inserta.
     */
    @Transactional
    public String procesarZipPadron(InputStream zipInputStream) throws Exception {
        jdbcTemplate.execute("TRUNCATE TABLE padron_ruc");
        long total = procesarInputStreamZip(zipInputStream, true);
        return "Carga manual completada. Registros insertados: " + total;
    }

    /**
     * Lógica interna para procesar el contenido de un ZIP
     */
    private long procesarInputStreamZip(InputStream zipInputStream, boolean closeStream) throws Exception {
        long totalProcesados = 0;
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory() && (entry.getName().toLowerCase().endsWith(".txt") || entry.getName().toLowerCase().endsWith(".csv"))) {
                    logger.info("Procesando archivo interno: " + entry.getName());
                    
                    @SuppressWarnings("resource")
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
                    String line;
                    List<Object[]> batchArgs = new ArrayList<>();
                    
                    while ((line = reader.readLine()) != null) {
                        String delimiter = line.contains("|") ? "\\|" : ";";
                        String[] partes = line.split(delimiter);
                        
                        if (partes.length >= 3) {
                            String ruc = partes[0].trim();
                            String razonSocial = partes[1].trim();
                            String dv = partes[2].trim();
                            
                            if (!dv.matches("\\d+")) {
                                if (partes[1].trim().matches("\\d+")) {
                                    dv = partes[1].trim();
                                    razonSocial = partes[2].trim();
                                } else {
                                    dv = "0";
                                }
                            }

                            if (ruc.equalsIgnoreCase("RUC") || ruc.equalsIgnoreCase("DOCUMENTO") || ruc.isEmpty()) {
                                continue;
                            }
                            
                            if (ruc.length() > 20) ruc = ruc.substring(0, 20);
                            if (dv.length() > 2) dv = dv.substring(0, 2);
                            if (razonSocial.length() > 255) razonSocial = razonSocial.substring(0, 255);
                            
                            batchArgs.add(new Object[]{ruc, dv, razonSocial, "ACTIVO"});
                            totalProcesados++;
                            
                            if (batchArgs.size() == BATCH_SIZE) {
                                ejecutarBatchInsert(batchArgs);
                                batchArgs.clear();
                            }
                        }
                    }
                    if (!batchArgs.isEmpty()) {
                        ejecutarBatchInsert(batchArgs);
                    }
                    break;
                }
                entry = zis.getNextEntry();
            }
        } finally {
            if (closeStream && zipInputStream != null) {
                zipInputStream.close();
            }
        }
        return totalProcesados;
    }

    private List<String> extraerEnlacesZip(String html) {
        List<String> links = new ArrayList<>();
        // Busca href="..." que contengan ".zip"
        // El regex ahora es más flexible para capturar URLs relativas o absolutas
        Pattern pattern = Pattern.compile("href=\"([^\"]+\\.zip[^\"]*)\"");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String url = matcher.group(1);
            
            // Si es relativa, anteponer el host
            if (url.startsWith("/")) {
                url = "https://www.dnit.gov.py" + url;
            } else if (!url.startsWith("http")) {
                // Por si acaso hay rutas relativas sin barra inicial (poco común en este portal)
                url = "https://www.dnit.gov.py/" + url;
            }
            
            // Evitar duplicados
            if (!links.contains(url)) {
                links.add(url);
            }
        }
        return links;
    }

    private void ejecutarBatchInsert(List<Object[]> batchArgs) {
        String sql = "INSERT INTO padron_ruc (ruc, dv, razon_social, estado) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT (ruc) DO UPDATE SET razon_social = EXCLUDED.razon_social, dv = EXCLUDED.dv, estado = EXCLUDED.estado";
        jdbcTemplate.batchUpdate(sql, java.util.Objects.requireNonNull(batchArgs));
    }
}

