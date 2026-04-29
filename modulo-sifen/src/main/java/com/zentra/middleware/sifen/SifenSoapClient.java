package com.zentra.middleware.sifen;

import com.zentra.middleware.core.enums.Ambiente;
import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.EstadoDte;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zentra.middleware.sifen.schema.REnviDeRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Cliente para comunicación SOAP con los servidores de SIFEN (SET Paraguay).
 * Implementa mTLS y la estructura de sobres requerida para DTE v150.
 */
@Service
public class SifenSoapClient {

    // Directorio centralizado para archivos de diagnóstico en tiempo de ejecución
    private static final String DIRECTORIO_TEMP = "temp";

    private static final Logger logger = Logger.getLogger(SifenSoapClient.class.getName());

    // Servicio: siRecepDE (Recepción de Documento Electrónico síncrono)
    private static final String ENDPOINT_TEST = "https://sifen-test.set.gov.py/de/ws/sync/recibe.wsdl";
    private static final String ENDPOINT_PROD = "https://sifen.set.gov.py/de/ws/sync/recibe.wsdl";
    
    // Servicio: siRecepLoteDE (Recepción de Lotes asíncrono)
    private static final String ENDPOINT_LOTE_TEST = "https://sifen-test.set.gov.py/de/ws/async/recibe-lote.wsdl";
    private static final String ENDPOINT_LOTE_PROD = "https://sifen.set.gov.py/de/ws/async/recibe-lote.wsdl";
    
    // Servicio: siConsLoteDE (Consulta de Lotes)
    private static final String ENDPOINT_CONSULTA_TEST = "https://sifen-test.set.gov.py/de/ws/consultas/consulta-lote.wsdl";
    private static final String ENDPOINT_CONSULTA_PROD = "https://sifen.set.gov.py/de/ws/consultas/consulta-lote.wsdl";

    private static final int TIMEOUT_CONEXION = 30000;
    private static final int TIMEOUT_LECTURA = 60000;

    /**
     * Envía un DTE firmado a SIFEN y procesa el acuse de recibo.
     * 
     * @param dte El documento con el XML firmado ya generado.
     * @return true si fue aprobado, false si fue rechazado o hubo error técnico.
     */
    public boolean enviarDteSincrono(DocumentoElectronico dte) {
        if (dte.getXmlFirmado() == null || dte.getXmlFirmado().isBlank()) {
            throw new IllegalStateException("El documento no está firmado. No se puede enviar a SIFEN.");
        }

        String endpoint = resolverEndpoint(dte.getAmbiente());
        logger.info("Transmitiendo a SIFEN [ambiente=" + dte.getAmbiente() + "] -> " + endpoint);
        logger.info("CDC: " + dte.getCdc());

        try {
            // Construir el contexto SSL con el P12 del emisor
            SSLContext sslContext = construirSslContext(dte);
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext, "http://ekuatia.set.gov.py/sifen/xsd/siRecepDE");

            // Armar el SOAP Envelope requerido por la SET
            String soapBody = construirSoapEnvelope(dte);

            try {
                java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
                java.nio.file.Files.createDirectories(dirTemp);
                java.nio.file.Files.writeString(dirTemp.resolve("DEBUG_soap_request.xml"), soapBody, java.nio.charset.StandardCharsets.UTF_8);
            } catch(Exception ignored) {}

            logger.info("============== PETICIÓN SOAP ENVIADA (UTF-8) ==============\n" + soapBody
                    + "\n===================================================");

            // Transmitir en UTF-8 (estándar SOAP 1.2)
            int httpStatus;
            try {
                byte[] soapBytes = soapBody.getBytes(StandardCharsets.UTF_8);
                logger.info("Enviando SOAP Request (" + soapBytes.length + " bytes): " + soapBody);
                
                // TAREA 3 — VERIFICAR CON LOG DE BYTES
                String marker = "electr";
                int idx = soapBody.indexOf(marker);
                if (idx >= 0 && soapBytes.length > idx + marker.length() + 3) {
                    byte[] sample = java.util.Arrays.copyOfRange(soapBytes, 
                        idx + marker.length(), idx + marker.length() + 3);
                    logger.info("Bytes reales del stream (electr+3): " + java.util.Arrays.toString(sample));
                }

                // Eliminado System.out.println del SOAP crudo que causa corrupción visual (CP437)
                // y confusión sobre las "dos rutas". La única ruta real es: soapBytes
                
                // TAREA 2 — VERIFICAR dDMoneTiPag ESPECÍFICAMENTE:
                String markerMone = "<dDMoneTiPag>";
                int idxMone = soapBody.indexOf(markerMone);
                if (idxMone >= 0) {
                    int endMone = soapBody.indexOf("</dDMoneTiPag>", idxMone);
                    String val = soapBody.substring(idxMone + markerMone.length(), endMone);
                    logger.info("dDMoneTiPag valor extraído del SOAP: '" + val + "'");
                    logger.info("dDMoneTiPag bytes reales antes de HTTP (UTF-16BE): " + 
                        java.util.Arrays.toString(val.getBytes(java.nio.charset.Charset.forName("UTF-16BE"))));
                }

                // Desactivar FixedLengthStreamingMode para evitar problemas de fragmentación con proxies
                // conn.setFixedLengthStreamingMode(soapBytes.length);
                
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(soapBytes);
                    os.flush();
                }
                httpStatus = conn.getResponseCode();
            } catch (IOException e) {
                // SIFEN pudo haber cortado temprano con un HTTP 400, 422 o 500
                httpStatus = conn.getResponseCode();
                logger.warning("Error de red detectado, recuperando status SIFEN: " + httpStatus);
            }
            logger.info("HTTP Status SIFEN: " + httpStatus);
            logger.info("Headers respuesta SIFEN: " + conn.getHeaderFields());

            // Leer la respuesta (éxito o error)
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();

            // Almacenar el acuse crudo para auditoría
            dte.setXmlRespuestaSifen(xmlRespuesta);
            logger.info("============== RESPUESTA CRUDA SIFEN ==============\n" + xmlRespuesta
                    + "\n===================================================");

            // Parsear el código de respuesta SIFEN
            String codRes = extraerCodigo(xmlRespuesta);
            dte.setCodigoEstadoSifen(codRes);
            logger.info("Código de respuesta SIFEN: " + codRes);

            // SIFEN usa '0300' para aprobación exitosa
            boolean aprobado = "0300".equals(codRes);
            dte.setEstado(aprobado ? EstadoDte.APROBADO : EstadoDte.RECHAZADO);

            if (aprobado) {
                logger.info("[SIFEN] Documento aprobado satisfactoriamente. CDC=" + dte.getCdc());
                dte.setMensajeUsuario("Documento aprobado exitosamente por SIFEN.");
            } else {
                String msgRes = extraerMensaje(xmlRespuesta);
                dte.setMensajeSifen(msgRes);
                dte.setMensajeUsuario(mapearMensajeAmigable(codRes, msgRes));
                logger.warning("[SIFEN] Documento NO aprobado. Código=" + codRes + " | Mensaje=" + msgRes);
            }

            return aprobado;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error de transmisión SIFEN: " + e.getMessage(), e);
            dte.setEstado(EstadoDte.ERROR_ENVIO);
            dte.setXmlRespuestaSifen("ERROR_TRANSMISION: " + e.getMessage());
            return false;
        }
    }

    public boolean enviarDteAsincrono(DocumentoElectronico dte) {
        if (dte.getXmlFirmado() == null || dte.getXmlFirmado().isBlank()) {
            throw new IllegalStateException("El documento no está firmado. No se puede enviar a SIFEN.");
        }

        String endpoint = (dte.getAmbiente() == Ambiente.PRODUCCION) ? ENDPOINT_LOTE_PROD : ENDPOINT_LOTE_TEST;
        logger.info("Transmitiendo LOTE ASÍNCRONO a SIFEN [ambiente=" + dte.getAmbiente() + "] -> " + endpoint);

        try {
            SSLContext sslContext = construirSslContext(dte);
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext, "http://ekuatia.set.gov.py/sifen/xsd/siRecepLoteDE");

            // Comprimir XML en ZIP y luego a Base64
            // SIFEN v150 requiere que el XML dentro del ZIP tenga como raíz <rLoteDE>
            String xmlLimpio = dte.getXmlFirmado().replaceFirst("<\\?xml.*?\\?>", "").trim();
            String xmlLote = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                             "<rLoteDE xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">\n" +
                             xmlLimpio + "\n" +
                             "</rLoteDE>";

            // Diagnóstico: guardar el XML completo del lote ANTES de comprimir
            // para verificar que los campos del XML coincidan con el CDC generado
            try {
                java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
                java.nio.file.Files.createDirectories(dirTemp);
                java.nio.file.Files.writeString(dirTemp.resolve("DEBUG_xml_lote.xml"), xmlLote, StandardCharsets.UTF_8);
                logger.info("XML del lote guardado en temp/DEBUG_xml_lote.xml para diagnóstico CDC");
            } catch (Exception ignored) {}

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
                java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(dte.getCdc() + ".xml");
                zos.putNextEntry(entry);
                zos.write(xmlLote.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            String base64Zip = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());

            String dId = "1"; // SIFEN requiere ID numérico para el lote, no un UUID
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                              "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
                              "<env:Header/>" +
                              "<env:Body>" +
                              "<rEnvioLote xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">" +
                              "<dId>" + dId + "</dId>" +
                              "<xDE>" + base64Zip + "</xDE>" +
                              "</rEnvioLote>" +
                              "</env:Body>" +
                              "</env:Envelope>";

            try {
                java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
                java.nio.file.Files.createDirectories(dirTemp);
                java.nio.file.Files.writeString(dirTemp.resolve("DEBUG_soap_request_lote.xml"), soapBody, StandardCharsets.UTF_8);
            } catch(Exception ignored) {}

            byte[] soapBytes = soapBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapBytes);
                os.flush();
            }

            int httpStatus;
            try {
                httpStatus = conn.getResponseCode();
            } catch (IOException e) {
                httpStatus = conn.getResponseCode();
            }
            
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();
            
            dte.setXmlRespuestaSifen(xmlRespuesta);
            String codRes = extraerEtiqueta(xmlRespuesta, "dCodRes", "DESCONOCIDO");
            dte.setCodigoEstadoSifen(codRes);
            
            // 0300 significa lote recibido exitosamente
            if ("0300".equals(codRes)) {
                String dProtConsLote = extraerEtiqueta(xmlRespuesta, "dProtConsLote", null);
                if (dProtConsLote != null) {
                    dte.setNumeroTicketLote(dProtConsLote);
                    dte.setEstado(EstadoDte.EN_PROCESO);
                    dte.setMensajeUsuario("Lote recibido por SIFEN. Ticket: " + dProtConsLote);
                    logger.info("Lote aceptado. Ticket: " + dProtConsLote);
                    return true;
                } else {
                    dte.setEstado(EstadoDte.ERROR_ENVIO);
                    dte.setMensajeUsuario("SIFEN aceptó el lote pero no devolvió el número de Ticket (dProtConsLote).");
                    return false;
                }
            } else {
                String msgRes = extraerMensaje(xmlRespuesta);
                dte.setMensajeSifen(msgRes);
                dte.setMensajeUsuario("Rechazo de lote SIFEN (" + codRes + "): " + msgRes);
                dte.setEstado(EstadoDte.RECHAZADO);
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en transmisión asíncrona: " + e.getMessage(), e);
            dte.setEstado(EstadoDte.ERROR_ENVIO);
            dte.setXmlRespuestaSifen("ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean consultarLoteSifen(DocumentoElectronico dte) {
        if (dte.getNumeroTicketLote() == null || dte.getNumeroTicketLote().isBlank()) {
            throw new IllegalStateException("El documento no tiene un número de Ticket asociado para consultar.");
        }

        String endpoint = (dte.getAmbiente() == Ambiente.PRODUCCION) ? ENDPOINT_CONSULTA_PROD : ENDPOINT_CONSULTA_TEST;
        logger.info("Consultando LOTE ASÍNCRONO Ticket " + dte.getNumeroTicketLote() + " -> " + endpoint);

        try {
            SSLContext sslContext = construirSslContext(dte);
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext, "http://ekuatia.set.gov.py/sifen/xsd/siConsLoteDE");

            String dId = "1"; // SIFEN requiere numérico
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                              "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
                              "<env:Header/>" +
                              "<env:Body>" +
                              "<rEnviConsLoteDe xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">" +
                              "<dId>" + dId + "</dId>" +
                              "<dProtConsLote>" + dte.getNumeroTicketLote() + "</dProtConsLote>" +
                              "</rEnviConsLoteDe>" +
                              "</env:Body>" +
                              "</env:Envelope>";

            try {
                java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
                java.nio.file.Files.createDirectories(dirTemp);
                java.nio.file.Files.writeString(dirTemp.resolve("DEBUG_soap_request_consulta_lote.xml"), soapBody, StandardCharsets.UTF_8);
            } catch(Exception ignored) {}

            byte[] soapBytes = soapBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapBytes);
                os.flush();
            }

            int httpStatus;
            try {
                httpStatus = conn.getResponseCode();
            } catch (IOException e) {
                httpStatus = conn.getResponseCode();
            }
            
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();
            
            dte.setXmlRespuestaSifen(xmlRespuesta);
            
            // Parsear resultado del lote (SIFEN usa dCodResLot/dMsgResLot para el Lote)
            String dCodResLote = extraerEtiqueta(xmlRespuesta, "dCodResLot", "DESCONOCIDO");
            String dMsgResLote = extraerEtiqueta(xmlRespuesta, "dMsgResLot", "Sin descripcion");
            
            logger.info("Resultado Consulta Lote SIFEN: " + dCodResLote + " - " + dMsgResLote);
            
            if ("0304".equals(dCodResLote)) {
                // Lote en proceso (Todavía no terminado)
                dte.setCodigoEstadoSifen(dCodResLote);
                dte.setMensajeUsuario("Lote en proceso de verificación por SIFEN. Intente más tarde.");
                return false;
            }

            // Si el lote fue procesado (ej. 0300), se verifica el resultado interno del DTE (dCodRes)
            String dCodResDoc = extraerEtiqueta(xmlRespuesta, "dCodRes", dCodResLote);
            String dMsgResDoc = extraerEtiqueta(xmlRespuesta, "dMsgRes", dMsgResLote);
            
            dte.setCodigoEstadoSifen(dCodResDoc);
            
            if ("0300".equals(dCodResDoc)) {
                dte.setEstado(EstadoDte.APROBADO);
                dte.setMensajeUsuario("DTE aprobado exitosamente: " + dMsgResDoc);
                return true;
            } else {
                dte.setEstado(EstadoDte.RECHAZADO);
                dte.setMensajeSifen(dMsgResDoc);
                dte.setMensajeUsuario("DTE rechazado (" + dCodResDoc + "): " + dMsgResDoc);
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en consulta de lote asíncrona: " + e.getMessage(), e);
            dte.setMensajeUsuario("Error de red al consultar: " + e.getMessage());
            return false;
        }
    }

    private String resolverEndpoint(Ambiente ambiente) {
        return (ambiente == Ambiente.PRODUCCION) ? ENDPOINT_PROD : ENDPOINT_TEST;
    }


    private String construirSoapEnvelope(DocumentoElectronico dte) {
        // Extraer el contenido del rDE sin el prólogo XML, preservando cada byte para la firma.
        String xmlFirmado = dte.getXmlFirmado();
        String xmlLimpio = xmlFirmado.replaceFirst("<\\?xml.*?\\?>", "").trim();

        // jsifenlib envía dId como un número secuencial o "1".
        // SIFEN rechaza si se envía un UUID en este campo.
        String dId = "1";

        // SIFEN v150 REQUIERE que <dId> y <xDE> estén en el namespace vacío (unqualified).
        // Usamos el namespace por defecto para que los elementos hijos (dId y xDE)
        // pertenezcan al namespace de SIFEN sin necesidad de prefijos redundantes.
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
               "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
               "<env:Header/>" +
               "<env:Body>" +
               "<rEnviDe xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">" +
               "<dId>" + dId + "</dId>" +
               "<xDE>" +
               xmlLimpio +
               "</xDE>" +
               "</rEnviDe>" +
               "</env:Body>" +
               "</env:Envelope>";
    }

    private SSLContext construirSslContext(DocumentoElectronico dte) throws Exception {
        SSLContext sslContext;

        if (dte.getEmisor() != null
                && dte.getEmisor().getRutaCertificado() != null
                && !dte.getEmisor().getRutaCertificado().isBlank()) {

            String ruta = dte.getEmisor().getRutaCertificado();
            String password = dte.getEmisor().getPasswordCertificado();
            char[] p12Pass = password != null ? password.toCharArray() : new char[0];

            KeyStore p12Store = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(ruta)) {
                p12Store.load(fis, p12Pass);
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(p12Store, p12Pass);

            sslContext = SSLContext.getInstance("TLSv1.2");
            TrustManager[] trustAll = new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }
                    }
            };
            sslContext.init(kmf.getKeyManagers(), trustAll, new java.security.SecureRandom());
        } else {
            sslContext = SSLContext.getDefault();
        }

        return sslContext;
    }

    private HttpsURLConnection abrirConexion(String endpoint, SSLContext sslContext, String action) throws Exception {
        URL url = URI.create(endpoint).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setConnectTimeout(TIMEOUT_CONEXION);
        conn.setReadTimeout(TIMEOUT_LECTURA);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        // SIFEN v150: Content-Type validado contra InventivaFE (usa jsifenlib).
        // La librería Roshka usa 'application/xml; charset=utf-8' — NO application/soap+xml.
        // DataPower Gateway acepta ambos, pero 'application/xml' es el que produce aprobaciones.
        String contentType = "application/xml; charset=utf-8";
        conn.setRequestProperty("Content-Type", contentType);
        if (action != null && !action.isEmpty()) {
            conn.setRequestProperty("SOAPAction", action);
        }
        logger.info("Headers enviados a SIFEN -> Content-Type: " + contentType + " | SOAPAction: " + action);

        return conn;
    }


    private String leerRespuesta(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        if (is != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
        }

        String xml = sb.toString();
        logger.info("Respuesta cruda de SIFEN [" + responseCode + "]: " + xml);

        if (xml == null || xml.isBlank()) {
            logger.warning("Respuesta vacía de SIFEN (HTTP " + responseCode + "). Se continúa el flujo.");
            return "";
        }
        return xml;
    }

    private String extraerCodigo(String xml) {
        return extraerEtiqueta(xml, "dCodRes", "DESCONOCIDO");
    }

    private String extraerMensaje(String xml) {
        return extraerEtiqueta(xml, "dMsgRes", "Sin descripcion");
    }

    private String mapearMensajeAmigable(String cod, String msgOriginal) {
        if (cod == null)
            return "No se recibio una respuesta clara de SIFEN.";
        return switch (cod) {
            case "0160" ->
                "Error tecnico de formato (SOAP). El archivo enviado tiene una estructura incorrecta que impide su lectura por SIFEN.";
            case "0300" -> "Documento aprobado exitosamente!";
            default -> "Respuesta SIFEN (" + cod + "): " + (msgOriginal != null ? msgOriginal : "Sin descripcion.");
        };
    }

    private String extraerEtiqueta(String xml, String etiqueta, String valorPorDefecto) {
        if (xml == null || xml.isBlank())
            return valorPorDefecto;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "<(?:.*?:)?" + etiqueta + ">(.*?)</(?:.*?:)?" + etiqueta + ">", java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return valorPorDefecto;
    }
}
