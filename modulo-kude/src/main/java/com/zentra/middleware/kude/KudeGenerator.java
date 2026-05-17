package com.zentra.middleware.kude;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Transporte;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class KudeGenerator {
    private static final Logger logger = Logger.getLogger(KudeGenerator.class.getName());
    private final Map<String, JasperReport> reportCache = new ConcurrentHashMap<>();

    public byte[] generarKudePdf(DocumentoElectronico dte, String formato) {
        try {
            String templateName = "TICKET".equalsIgnoreCase(formato) ? "kude_ticket" : "kude_a4";
            JasperReport jasperReport = getOrCompileReport(templateName);

            // 2. Parámetros extendidos para KuDE oficial
            Map<String, Object> parameters = new HashMap<>();
            
            // Datos del Emisor
            parameters.put("P_EMISOR_NOMBRE", dte.getRazonSocialEmisor() != null ? dte.getRazonSocialEmisor() : "EMISOR TEST");
            parameters.put("P_EMISOR_RUC", dte.getRucEmisor() != null ? dte.getRucEmisor() : "80000001-0");
            parameters.put("P_EMISOR_DIRECCION", dte.getDireccionEmisor() != null ? dte.getDireccionEmisor() : "DIRECCION DEL EMISOR");
            parameters.put("P_EMISOR_TELEFONO", dte.getTelefonoEmisor() != null ? dte.getTelefonoEmisor() : "021-000-000");
            parameters.put("P_EMISOR_ACTIVIDAD", dte.getActividadEconomicaEmisor() != null ? dte.getActividadEconomicaEmisor() : "VENTA DE BIENES Y SERVICIOS");
            parameters.put("P_TIMBRADO", dte.getTimbrado() != null ? dte.getTimbrado() : "16770994");
            
            // Datos del Documento
            parameters.put("P_NUMERO_DOCUMENTO", dte.getNumeroComprobante());
            parameters.put("P_TIPO_DOCUMENTO_TEXTO", getTipoDocTexto(dte.getTipoDocumento()));
            parameters.put("P_FECHA_EMISION", formatDateTime(dte.getFechaCreacion()));
            parameters.put("P_CONDICION_VENTA", dte.getCondicionOperacion() == 2 ? "CRÉDITO" : "CONTADO");
            parameters.put("P_CDC", formatCdc(dte.getCdc()));
            
            // Datos del Receptor
            parameters.put("P_RECEPTOR_NOMBRE", dte.getReceptorRazonSocial());
            parameters.put("P_RECEPTOR_RUC", dte.getRucReceptor());
            parameters.put("P_RECEPTOR_DIRECCION", dte.getReceptorDireccion() != null ? dte.getReceptorDireccion() : "-");
            
            // Totales
            parameters.put("P_TOTAL_OPERACION", dte.getTotalOperacion() != null ? dte.getTotalOperacion() : 0.0);
            parameters.put("P_TOTAL_GRAVADA_10", dte.getTotalGravada10() != null ? dte.getTotalGravada10() : 0.0);
            parameters.put("P_TOTAL_GRAVADA_5", dte.getTotalGravada5() != null ? dte.getTotalGravada5() : 0.0);
            parameters.put("P_TOTAL_EXENTA", dte.getTotalExenta() != null ? dte.getTotalExenta() : 0.0);
            parameters.put("P_TOTAL_IVA_10", dte.getTotalIva10() != null ? dte.getTotalIva10() : 0.0);
            parameters.put("P_TOTAL_IVA_5", dte.getTotalIva5() != null ? dte.getTotalIva5() : 0.0);
            parameters.put("P_TOTAL_IVA", dte.getTotalIva() != null ? dte.getTotalIva() : 0.0);
            
            // Logo Dinámico
            parameters.put("P_LOGO", loadLogo(dte));

            // Control de Estado (Marca de Agua)
            boolean estaCancelado = dte.getEstado() != null && dte.getEstado().name().equals("ANULADO");
            parameters.put("P_CANCELADO", estaCancelado);

            // Parámetros de Transporte y Notas de Remisión
            if (dte.getTransporte() != null) {
                parameters.put("P_ES_REMISION", "7".equals(dte.getTipoDocumento()));
                
                Transporte t = dte.getTransporte();
                parameters.put("P_TRANSP_RUC", t.getRucTransportista() != null ? t.getRucTransportista() + (t.getDvTransportista() != null ? "-" + t.getDvTransportista() : "") : "-");
                parameters.put("P_TRANSP_NOMBRE", t.getNombreTransportista() != null ? t.getNombreTransportista() : "-");
                
                parameters.put("P_CHOFER_DOC", t.getNumeroDocumentoChofer() != null ? t.getNumeroDocumentoChofer() : "-");
                parameters.put("P_CHOFER_NOMBRE", t.getNombreChofer() != null ? t.getNombreChofer() : "-");
                
                parameters.put("P_VEHICULO_MARCA", t.getMarcaVehiculo() != null ? t.getMarcaVehiculo() : "-");
                parameters.put("P_VEHICULO_MATRICULA", t.getMatriculaVehiculo() != null ? t.getMatriculaVehiculo() : "-");
                parameters.put("P_VEHICULO_TIPO", t.getTipoVehiculo() != null ? t.getTipoVehiculo() : "-");
                parameters.put("P_VEHICULO_CHASIS", t.getChasisVehiculo() != null ? t.getChasisVehiculo() : "-");
                
                String motivo = t.getDescripcionMotivoTraslado();
                if (motivo == null || motivo.trim().isEmpty()) {
                    motivo = getMotivoTrasladoTexto(t.getMotivoTraslado());
                }
                parameters.put("P_MOTIVO_TRASLADO", motivo);
                parameters.put("P_FECHA_INICIO_TRASLADO", formatDate(t.getFechaInicioTraslado()));
                parameters.put("P_FECHA_FIN_TRASLADO", formatDate(t.getFechaFinTraslado()));
                parameters.put("P_KMS_RECORRIDO", t.getKmsRecorrido() != null ? String.valueOf(t.getKmsRecorrido()) : "-");
                
                parameters.put("P_SALIDA_DIR", formatDireccion(t.getLocalSalidaDireccion(), t.getLocalSalidaNumeroCasa(), t.getLocalSalidaDescripcionCiudad(), t.getLocalSalidaDescripcionDepartamento()));
                parameters.put("P_ENTREGA_DIR", formatDireccion(t.getLocalEntregaDireccion(), t.getLocalEntregaNumeroCasa(), t.getLocalEntregaDescripcionCiudad(), t.getLocalEntregaDescripcionDepartamento()));
            } else {
                parameters.put("P_ES_REMISION", "7".equals(dte.getTipoDocumento()));
                parameters.put("P_TRANSP_RUC", "-");
                parameters.put("P_TRANSP_NOMBRE", "-");
                parameters.put("P_CHOFER_DOC", "-");
                parameters.put("P_CHOFER_NOMBRE", "-");
                parameters.put("P_VEHICULO_MARCA", "-");
                parameters.put("P_VEHICULO_MATRICULA", "-");
                parameters.put("P_VEHICULO_TIPO", "-");
                parameters.put("P_VEHICULO_CHASIS", "-");
                parameters.put("P_MOTIVO_TRASLADO", "-");
                parameters.put("P_FECHA_INICIO_TRASLADO", "-");
                parameters.put("P_FECHA_FIN_TRASLADO", "-");
                parameters.put("P_KMS_RECORRIDO", "-");
                parameters.put("P_SALIDA_DIR", "-");
                parameters.put("P_ENTREGA_DIR", "-");
            }

            // Generar QR
            String qrUrl = "https://kuatia.set.gov.py/consultas/qr?nDe=" + dte.getCdc();
            parameters.put("P_QR", generateQrImage(qrUrl));

            // 3. DataSource (Items)
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dte.getItems());

            // 4. Fill & Export
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fallo crítico en generación de PDF: " + e.getMessage(), e);
            throw new RuntimeException("Error generando KuDE: " + e.getMessage());
        }
    }

    private String getTipoDocTexto(String tipo) {
        if ("1".equals(tipo)) return "FACTURA ELECTRÓNICA";
        if ("4".equals(tipo)) return "AUTOFACTURA ELECTRÓNICA";
        if ("5".equals(tipo)) return "NOTA DE CRÉDITO ELECTRÓNICA";
        if ("6".equals(tipo)) return "NOTA DE DÉBITO ELECTRÓNICA";
        if ("7".equals(tipo)) return "NOTA DE REMISIÓN ELECTRÓNICA";
        return "DOCUMENTO ELECTRÓNICO";
    }

    private String formatDateTime(java.time.LocalDateTime dt) {
        if (dt == null) return "-";
        return dt.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    private JasperReport getOrCompileReport(String name) throws JRException {
        if (reportCache.containsKey(name)) return reportCache.get(name);
        
        String path = "/kude/" + name + ".jrxml";
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("Plantilla no encontrada: " + path);
        
        JasperReport compiled = JasperCompileManager.compileReport(is);
        reportCache.put(name, compiled);
        return compiled;
    }

    private String formatCdc(String cdc) {
        if (cdc == null || cdc.length() != 44) return cdc;
        return String.join(" ", cdc.split("(?<=\\G.{4})"));
    }

    private BufferedImage loadLogo(DocumentoElectronico dte) {
        try {
            // 1. Intentar cargar logo de la empresa (Base64)
            if (dte.getEmisor() != null && dte.getEmisor().getLogoBase64() != null) {
                String base64 = dte.getEmisor().getLogoBase64();
                if (base64.contains(",")) base64 = base64.split(",")[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64);
                return ImageIO.read(new ByteArrayInputStream(imageBytes));
            }
            
            // 2. Si no tiene, cargar logo por defecto (Zentra)
            InputStream is = getClass().getResourceAsStream("/kude/zentra_logo.png");
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (Exception e) {
            logger.warning("No se pudo cargar el logo: " + e.getMessage());
        }
        return null;
    }

    private BufferedImage generateQrImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private String getMotivoTrasladoTexto(Integer motivo) {
        if (motivo == null) return "-";
        switch (motivo) {
            case 1: return "Venta";
            case 2: return "Traslado por emisor";
            case 3: return "Traslado por tercero";
            case 4: return "Devolución";
            case 5: return "Compra";
            case 6: return "Importación";
            case 7: return "Exportación";
            case 99: return "Otro motivo";
            default: return "Motivo " + motivo;
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "-";
        try {
            if (dateStr.contains("-")) {
                String[] parts = dateStr.split("-");
                if (parts.length == 3) {
                    if (parts[0].length() == 4) {
                        return parts[2] + "-" + parts[1] + "-" + parts[0];
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar y devolver original
        }
        return dateStr;
    }

    private String formatDireccion(String direccion, Integer nroCasa, String ciudad, String depto) {
        StringBuilder sb = new StringBuilder();
        if (direccion != null && !direccion.trim().isEmpty()) {
            sb.append(direccion);
        } else {
            sb.append("-");
        }
        if (nroCasa != null && nroCasa > 0) {
            sb.append(" Nro. ").append(nroCasa);
        }
        if (ciudad != null && !ciudad.trim().isEmpty()) {
            sb.append(", ").append(ciudad);
        }
        if (depto != null && !depto.trim().isEmpty()) {
            sb.append(", ").append(depto);
        }
        return sb.toString();
    }
}
