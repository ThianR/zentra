package com.zentra.middleware.sifen;

import com.zentra.middleware.core.enums.Ambiente;
import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.model.EventoDocumento;
import com.zentra.middleware.core.enums.EstadoEvento;
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

    // Servicio: siRecepEvento (Recepción de Eventos: Cancelación, Inutilización)
    private static final String ENDPOINT_EVENTO_TEST = "https://sifen-test.set.gov.py/de/ws/eventos/evento.wsdl";
    private static final String ENDPOINT_EVENTO_PROD = "https://sifen.set.gov.py/de/ws/eventos/evento.wsdl";

    // SOAPAction requerida por el WS de eventos SIFEN
    private static final String SOAP_ACTION_EVENTO = "http://ekuatia.set.gov.py/sifen/xsd/siRecepEvento";

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

            // SIFEN usa '0300' para lotes y '0260' para DE individual síncrono.
            boolean aprobado = "0300".equals(codRes) || "0260".equals(codRes);
            dte.setEstado(aprobado ? EstadoDte.APROBADO : EstadoDte.RECHAZADO);

            String msgRes = extraerMensaje(xmlRespuesta);
            dte.setMensajeSifen(msgRes);

            if (aprobado) {
                logger.info("[SIFEN] Documento aprobado satisfactoriamente. CDC=" + dte.getCdc());
                dte.setMensajeUsuario("Respuesta SIFEN (" + codRes + "): " + msgRes);
            } else {
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

            try {
                conn.getResponseCode();
            } catch (IOException e) {
                conn.getResponseCode();
            }
            
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();
            
            dte.setXmlRespuestaSifen(xmlRespuesta);
            String codRes = extraerEtiqueta(xmlRespuesta, "dCodRes", null);
            if (codRes == null) {
                codRes = extraerEtiqueta(xmlRespuesta, "dCodResLot", "SIN_COD");
            }
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

    public boolean enviarLoteAsincrono(com.zentra.middleware.core.model.LoteTransmision lote, String xmlLote) {
        if (lote.getEmpresa() == null) {
            throw new IllegalStateException("El lote no tiene empresa emisora asociada.");
        }

        Ambiente ambiente = lote.getEmpresa().getAmbiente() != null ? lote.getEmpresa().getAmbiente() : Ambiente.TEST;
        String endpoint = (ambiente == Ambiente.PRODUCCION) ? ENDPOINT_LOTE_PROD : ENDPOINT_LOTE_TEST;
        
        logger.info("Transmitiendo LOTE ASÍNCRONO MULTIPLE a SIFEN [ambiente=" + ambiente + "] -> " + endpoint);

        try {
            SSLContext sslContext = construirSslContextDesdeEmpresa(lote.getEmpresa());
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext, "http://ekuatia.set.gov.py/sifen/xsd/siRecepLoteDE");

            try {
                java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
                java.nio.file.Files.createDirectories(dirTemp);
                java.nio.file.Files.writeString(dirTemp.resolve("DEBUG_xml_lote_multiple_" + lote.getId() + ".xml"), xmlLote, StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
                java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry("lote_" + lote.getId() + ".xml");
                zos.putNextEntry(entry);
                zos.write(xmlLote.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            String base64Zip = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());

            String dId = "1";
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
                java.nio.file.Files.writeString(dirTemp.resolve("DEBUG_soap_request_lote_" + lote.getId() + ".xml"), soapBody, StandardCharsets.UTF_8);
            } catch(Exception ignored) {}

            byte[] soapBytes = soapBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapBytes);
                os.flush();
            }

            try {
                conn.getResponseCode();
            } catch (IOException e) {
                conn.getResponseCode();
            }
            
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();
            
            String codRes = extraerEtiqueta(xmlRespuesta, "dCodRes", null);
            if (codRes == null) {
                codRes = extraerEtiqueta(xmlRespuesta, "dCodResLot", "SIN_COD");
            }
            
            if ("0300".equals(codRes)) {
                String dProtConsLote = extraerEtiqueta(xmlRespuesta, "dProtConsLote", null);
                if (dProtConsLote != null) {
                    lote.setNumeroTicket(dProtConsLote);
                    lote.setEstado(com.zentra.middleware.core.model.EstadoLote.ENVIADO);
                    lote.setFechaEnvio(java.time.LocalDateTime.now());
                    logger.info("Lote aceptado. Ticket: " + dProtConsLote);
                    return true;
                } else {
                    lote.setEstado(com.zentra.middleware.core.model.EstadoLote.ERROR);
                    logger.severe("SIFEN aceptó el lote pero no devolvió Ticket.");
                    return false;
                }
            } else {
                String msgRes = extraerMensaje(xmlRespuesta);
                lote.setEstado(com.zentra.middleware.core.model.EstadoLote.ERROR);
                logger.severe("Rechazo de lote SIFEN (" + codRes + "): " + msgRes);
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en transmisión asíncrona múltiple: " + e.getMessage(), e);
            lote.setEstado(com.zentra.middleware.core.model.EstadoLote.ERROR);
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

            try {
                conn.getResponseCode();
            } catch (IOException e) {
                conn.getResponseCode();
            }
            
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();
            
            dte.setXmlRespuestaSifen(xmlRespuesta);
            
            // Parsear resultado del lote (SIFEN usa dCodResLot/dMsgResLot para el Lote)
            String dCodResLote = extraerEtiqueta(xmlRespuesta, "dCodResLot", "SIN_COD");
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
            
            if ("0300".equals(dCodResDoc) || "0260".equals(dCodResDoc)) {
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

    public String consultarTicketLoteMultiple(com.zentra.middleware.core.model.LoteTransmision lote) {
        if (lote.getNumeroTicket() == null || lote.getNumeroTicket().isBlank()) {
            throw new IllegalStateException("El lote no tiene un número de Ticket asociado para consultar.");
        }

        Ambiente ambiente = lote.getEmpresa().getAmbiente() != null ? lote.getEmpresa().getAmbiente() : Ambiente.TEST;
        String endpoint = (ambiente == Ambiente.PRODUCCION) ? ENDPOINT_CONSULTA_PROD : ENDPOINT_CONSULTA_TEST;
        logger.info("Consultando LOTE MULTIPLE Ticket " + lote.getNumeroTicket() + " -> " + endpoint);

        try {
            SSLContext sslContext = construirSslContextDesdeEmpresa(lote.getEmpresa());
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext, "http://ekuatia.set.gov.py/sifen/xsd/siConsLoteDE");

            String dId = "1";
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                              "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
                              "<env:Header/>" +
                              "<env:Body>" +
                              "<rEnviConsLoteDe xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">" +
                              "<dId>" + dId + "</dId>" +
                              "<dProtConsLote>" + lote.getNumeroTicket() + "</dProtConsLote>" +
                              "</rEnviConsLoteDe>" +
                              "</env:Body>" +
                              "</env:Envelope>";

            try {
                java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
                java.nio.file.Files.writeString(dirTemp.resolve("DEBUG_soap_request_consulta_lote_mult.xml"), soapBody, StandardCharsets.UTF_8);
            } catch(Exception ignored) {}

            byte[] soapBytes = soapBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapBytes);
                os.flush();
            }

            try {
                conn.getResponseCode();
            } catch (IOException e) {
                conn.getResponseCode();
            }
            
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();
            
            return xmlRespuesta;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en consulta de lote múltiple asíncrona: " + e.getMessage(), e);
            return null;
        }
    }

    private String resolverEndpoint(Ambiente ambiente) {
        return (ambiente == Ambiente.PRODUCCION) ? ENDPOINT_PROD : ENDPOINT_TEST;
    }

    // =========================================================================
    // Envío de Eventos SIFEN (Cancelación / Inutilización) — Fase E4
    // =========================================================================

    /**
     * Envía un Evento SIFEN firmado al Web Service {@code siRecepEvento}.
     *
     * <p>Construye el SOAP Envelope con el XML del evento firmado y lo transmite
     * mediante mTLS usando el certificado P12 de la empresa emisora.</p>
     *
     * <p>Actualiza el {@link EventoDocumento} con el resultado de SIFEN:
     * código de respuesta, mensaje y estado final (APROBADO/RECHAZADO/ERROR_ENVIO).</p>
     *
     * @param evento   El evento con el XML firmado ya generado.
     * @param ambiente Ambiente destino (PRODUCCION o TEST).
     * @return {@code true} si SIFEN respondió con código 0300 (Aprobado).
     */
    public boolean enviarEvento(EventoDocumento evento, Ambiente ambiente) {
        if (evento.getXmlFirmado() == null || evento.getXmlFirmado().isBlank()) {
            throw new IllegalStateException("El evento no tiene XML firmado. No se puede enviar a SIFEN.");
        }
        if (evento.getEmpresa() == null) {
            throw new IllegalStateException("El evento no tiene empresa emisora asociada.");
        }

        String endpoint = (ambiente == Ambiente.PRODUCCION) ? ENDPOINT_EVENTO_PROD : ENDPOINT_EVENTO_TEST;
        logger.info("[SifenSoapClient.enviarEvento] Transmitiendo evento [" + evento.getTipoEvento()
            + "] a SIFEN [" + ambiente + "] -> " + endpoint);

        try {
            // Construir el contexto SSL con el certificado P12 de la empresa
            SSLContext sslContext = construirSslContextDesdeEmpresa(evento.getEmpresa());
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext, SOAP_ACTION_EVENTO);

            // Armar el SOAP Envelope para el evento
            String soapBody = construirSoapEnvelopeEvento(evento);

            // Diagnóstico en temp/
            guardarDiagnosticoEvento(soapBody, "DEBUG_soap_evento_request.xml");
            logger.info("[SifenSoapClient.enviarEvento] SOAP Evento:\n" + soapBody);

            // Transmitir
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
                logger.warning("[SifenSoapClient.enviarEvento] IOException al obtener status: " + e.getMessage());
            }
            logger.info("[SifenSoapClient.enviarEvento] HTTP Status: " + httpStatus);

            // Leer la respuesta
            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();

            evento.setXmlRespuestaSifen(xmlRespuesta);
            guardarDiagnosticoEvento(xmlRespuesta, "DEBUG_soap_evento_response.xml");
            logger.info("[SifenSoapClient.enviarEvento] Respuesta SIFEN:\n" + xmlRespuesta);

            // Parsear código y mensaje de resultado
            String codRes = extraerEtiqueta(xmlRespuesta, "dCodRes", "SIN_COD");
            String msgRes = extraerEtiqueta(xmlRespuesta, "dMsgRes", "Sin descripcion");

            evento.setCodigoSifen(codRes);
            evento.setMensajeSifen(msgRes);
            evento.setFechaRespuesta(java.time.LocalDateTime.now());

            boolean aprobado = "0300".equals(codRes);
            evento.setEstado(aprobado ? EstadoEvento.APROBADO : EstadoEvento.RECHAZADO);

            if (aprobado) {
                evento.setMensajeUsuario("Evento aprobado exitosamente por SIFEN.");
                logger.info("[SifenSoapClient.enviarEvento] Evento APROBADO. Tipo=" + evento.getTipoEvento());
            } else {
                evento.setMensajeUsuario(mapearMensajeAmigable(codRes, msgRes));
                logger.warning("[SifenSoapClient.enviarEvento] Evento RECHAZADO. Codigo=" + codRes + " | Msg=" + msgRes);
            }

            return aprobado;

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE,
                "[SifenSoapClient.enviarEvento] Error de transmision: " + e.getMessage(), e);
            evento.setEstado(EstadoEvento.ERROR_ENVIO);
            evento.setMensajeUsuario("Error de transmision al enviar evento: " + e.getMessage());
            evento.setFechaRespuesta(java.time.LocalDateTime.now());
            return false;
        }
    }

    /**
     * Construye el SOAP Envelope 1.2 para el WS {@code siRecepEvento}.
     *
     * <p>Estructura requerida por SIFEN v150, Sección 9.5:</p>
     * <pre>
     *   &lt;env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope"&gt;
     *     &lt;env:Header/&gt;
     *     &lt;env:Body&gt;
     *       &lt;rEnviEvt xmlns="http://ekuatia.set.gov.py/sifen/xsd"&gt;
     *         [XML del evento firmado — sin declaración &lt;?xml?&gt;]
     *       &lt;/rEnviEvt&gt;
     *     &lt;/env:Body&gt;
     *   &lt;/env:Envelope&gt;
     * </pre>
     *
     * <p>El contenido del evento ya viene serializado dentro del elemento raíz
     * {@code &lt;rEnviEvt&gt;}, por lo que se inserta directamente sin envoltura
     * adicional (el XML firmado ya contiene ese nodo raíz).</p>
     *
     * @param evento El evento con su XML firmado listo para enviar.
     * @return SOAP Envelope completo como cadena UTF-8.
     */
    private String construirSoapEnvelopeEvento(EventoDocumento evento) {
        // Eliminar la declaración <?xml?> para embeber dentro del SOAP Body
        String xmlFirmado = evento.getXmlFirmado()
            .replaceFirst("<\\?xml.*?\\?>", "").trim();

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
               "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
               "<env:Header/>" +
               "<env:Body>" +
               xmlFirmado +
               "</env:Body>" +
               "</env:Envelope>";
    }

    /**
     * Construye un {@link SSLContext} para mTLS usando el certificado P12 de
     * la empresa emisora, sin necesitar un {@link DocumentoElectronico} como
     * intermediario.
     *
     * <p>Reutiliza la misma lógica que {@link #construirSslContext(DocumentoElectronico)}
     * pero acepta directamente la entidad {@link Empresa}.</p>
     *
     * @param empresa Empresa con el certificado P12 configurado.
     * @return SSLContext configurado con la clave del cliente para mTLS.
     * @throws Exception si el certificado no está configurado o no puede cargarse.
     */
    private SSLContext construirSslContextDesdeEmpresa(Empresa empresa) throws Exception {
        byte[] p12Bytes = empresa.getCertificadoFisico();
        String ruta     = empresa.getRutaCertificado();
        String rawPass  = empresa.getPasswordCertificado();

        if (p12Bytes == null && (ruta == null || ruta.isBlank())) {
            throw new Exception("La empresa no tiene configurado el certificado P12 (ni bytes ni ruta).");
        }

        String p12Pass;
        try {
            p12Pass = com.zentra.middleware.crypto.util.AesEncryptionUtil.decrypt(rawPass);
        } catch (Exception e) {
            p12Pass = rawPass;
        }
        char[] passwordChars = p12Pass != null ? p12Pass.toCharArray() : new char[0];

        KeyStore p12Store = KeyStore.getInstance("PKCS12");
        if (p12Bytes != null) {
            try (java.io.InputStream is = new java.io.ByteArrayInputStream(p12Bytes)) {
                p12Store.load(is, passwordChars);
            }
        } else {
            try (java.io.FileInputStream fis = new java.io.FileInputStream(ruta)) {
                p12Store.load(fis, passwordChars);
            }
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(p12Store, passwordChars);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        TrustManager[] trustAll = new TrustManager[] {
            new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {}
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }
        };
        sslContext.init(kmf.getKeyManagers(), trustAll, new java.security.SecureRandom());
        return sslContext;
    }

    /**
     * Guarda un XML de diagnóstico en el directorio {@code temp/}.
     * No interrumpe el flujo si falla (best-effort).
     *
     * @param contenido  Contenido XML a guardar.
     * @param nombreArchivo Nombre del archivo dentro de {@code temp/}.
     */
    private void guardarDiagnosticoEvento(String contenido, String nombreArchivo) {
        try {
            java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
            java.nio.file.Files.createDirectories(dirTemp);
            java.nio.file.Files.writeString(
                dirTemp.resolve(nombreArchivo),
                contenido != null ? contenido : "",
                StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
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

        if (dte.getEmisor() != null) {
            byte[] p12Bytes = dte.getEmisor().getCertificadoFisico();
            String ruta = dte.getEmisor().getRutaCertificado();
            String rawPass = dte.getEmisor().getPasswordCertificado();

            if (p12Bytes != null || (ruta != null && !ruta.isBlank())) {
                String p12Pass;
                try {
                    // Try to decrypt the password
                    p12Pass = com.zentra.middleware.crypto.util.AesEncryptionUtil.decrypt(rawPass);
                } catch (Exception e) {
                    p12Pass = rawPass;
                }
                char[] passwordChars = p12Pass != null ? p12Pass.toCharArray() : new char[0];

                KeyStore p12Store = KeyStore.getInstance("PKCS12");
                
                if (p12Bytes != null) {
                    try (java.io.InputStream is = new java.io.ByteArrayInputStream(p12Bytes)) {
                        p12Store.load(is, passwordChars);
                    }
                } else {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(ruta)) {
                        p12Store.load(fis, passwordChars);
                    }
                }

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(p12Store, passwordChars);

                sslContext = SSLContext.getInstance("TLSv1.2");
                TrustManager[] trustAll = new TrustManager[] {
                        new X509TrustManager() {
                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                        }
                };
                sslContext.init(kmf.getKeyManagers(), trustAll, new java.security.SecureRandom());
            } else {
                throw new Exception("El emisor no tiene configurada la ruta física ni el binario del certificado.");
            }
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
        return extraerEtiqueta(xml, "dCodRes", "SIN_COD");
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

    // --- Consulta individual de DTE por CDC (rEnviConsDe / siConsDe) ---
    // Endpoint separado del de lotes; permite consultar cualquier DTE por su CDC directamente.
    private static final String ENDPOINT_CONS_DTE_TEST = "https://sifen-test.set.gov.py/de/ws/consultas/consulta.wsdl";
    private static final String ENDPOINT_CONS_DTE_PROD = "https://sifen.set.gov.py/de/ws/consultas/consulta.wsdl";

    /**
     * Consulta el estado oficial de un DTE en SIFEN usando su CDC (Código de Control).
     * Este método se utiliza como medida preventiva para DTEs "huérfanos": documentos cuyo
     * Ticket de Lote se perdió o cuyo Lote lleva más de 24 horas sin respuesta definitiva.
     *
     * @param dte El documento a consultar. Debe tener CDC y el certificado del emisor cargado.
     * @return true si SIFEN confirma el estado APROBADO (0300), false en cualquier otro caso.
     */
    public boolean consultarDtePorCdc(DocumentoElectronico dte) {
        if (dte.getCdc() == null || dte.getCdc().isBlank()) {
            throw new IllegalStateException("El documento no tiene CDC para consultar en SIFEN.");
        }

        Ambiente ambiente = dte.getAmbiente() != null ? dte.getAmbiente() : Ambiente.TEST;
        String endpoint = (ambiente == Ambiente.PRODUCCION) ? ENDPOINT_CONS_DTE_PROD : ENDPOINT_CONS_DTE_TEST;
        logger.info("Consulta individual por CDC [" + dte.getCdc() + "] -> " + endpoint);

        try {
            SSLContext sslContext = construirSslContext(dte);
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext,
                    "http://ekuatia.set.gov.py/sifen/xsd/siConsDe");

            // Estructura SOAP según especificación SIFEN v150 para consulta individual por CDC
            String soapBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
                    "<env:Header/>" +
                    "<env:Body>" +
                    "<rEnviConsDe xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">" +
                    "<dId>1</dId>" +
                    "<dCDC>" + dte.getCdc() + "</dCDC>" +
                    "</rEnviConsDe>" +
                    "</env:Body>" +
                    "</env:Envelope>";

            // Guardar SOAP de diagnóstico en temp/
            try {
                java.nio.file.Path dirTemp = java.nio.file.Paths.get(DIRECTORIO_TEMP);
                java.nio.file.Files.createDirectories(dirTemp);
                java.nio.file.Files.writeString(
                        dirTemp.resolve("DEBUG_consulta_cdc_" + dte.getCdc() + ".xml"),
                        soapBody, StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            byte[] soapBytes = soapBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapBytes);
                os.flush();
            }

            // Ignoramos explícitamente el HTTP status ya que SIFEN puede devolver 500 con cuerpo válido
            try { conn.getResponseCode(); } catch (IOException ignored) {}

            String xmlRespuesta = leerRespuesta(conn);
            conn.disconnect();

            dte.setXmlRespuestaSifen(xmlRespuesta);

            // Según especificación SIFEN v150: dCodRes = 0300 -> aprobado, 0400 -> rechazado
            String dCodRes = extraerEtiqueta(xmlRespuesta, "dCodRes", "SIN_COD");
            String dMsgRes = extraerEtiqueta(xmlRespuesta, "dMsgRes", "Sin descripcion");

            dte.setCodigoEstadoSifen(dCodRes);
            logger.info("Resultado consulta CDC [" + dte.getCdc() + "]: " + dCodRes + " - " + dMsgRes);

            if ("0300".equals(dCodRes) || "0260".equals(dCodRes)) {
                dte.setEstado(EstadoDte.APROBADO);
                dte.setMensajeUsuario("DTE aprobado (consulta directa por CDC): " + dMsgRes);
                return true;
            } else if ("0304".equals(dCodRes)) {
                // SIFEN aún no procesó el documento
                dte.setMensajeUsuario("SIFEN indica que el DTE aún está en proceso de verificación.");
                return false;
            } else {
                dte.setEstado(EstadoDte.RECHAZADO);
                dte.setMensajeSifen(dMsgRes);
                dte.setMensajeUsuario("DTE rechazado por SIFEN (" + dCodRes + "): " + dMsgRes);
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en consulta individual por CDC: " + e.getMessage(), e);
            dte.setMensajeUsuario("Error técnico al consultar CDC en SIFEN: " + e.getMessage());
            return false;
        }
    }
}

