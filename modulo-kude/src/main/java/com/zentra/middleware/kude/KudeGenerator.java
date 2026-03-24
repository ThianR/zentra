package com.zentra.middleware.kude;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zentra.middleware.core.model.DocumentoElectronico;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.InputStream;
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
            parameters.put("P_TIMBRADO", dte.getTimbrado() != null ? dte.getTimbrado() : "12345678");
            
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
            parameters.put("P_TOTAL_OPERACION", dte.getTotalOperacion());
            parameters.put("P_TOTAL_GRAVADA_10", dte.getTotalGravada10());
            parameters.put("P_TOTAL_GRAVADA_5", dte.getTotalGravada5());
            parameters.put("P_TOTAL_EXENTA", dte.getTotalExenta());
            parameters.put("P_TOTAL_IVA_10", dte.getTotalIva10());
            parameters.put("P_TOTAL_IVA_5", dte.getTotalIva5());
            parameters.put("P_TOTAL_IVA", dte.getTotalIva());
            
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
        if ("4".equals(tipo)) return "NOTA DE CRÉDITO ELECTRÓNICA";
        if ("5".equals(tipo)) return "NOTA DE DÉBITO ELECTRÓNICA";
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

    private BufferedImage generateQrImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
