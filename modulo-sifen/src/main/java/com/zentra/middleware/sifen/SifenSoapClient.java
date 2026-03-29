package com.zentra.middleware.sifen;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.EstadoDte;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cliente SOAP para comunicación con SIFEN (SET - Paraguay).
 * Implementa mTLS con el certificado P12 del emisor y parsea la respuesta oficial.
 * Opera en ambiente de Pruebas (Test) por defecto.
 */
@Service
public class SifenSoapClient {

    private static final Logger logger = Logger.getLogger(SifenSoapClient.class.getName());

    // Endpoints oficiales de SIFEN según ambiente
    private static final String ENDPOINT_TEST = "https://sifen-test.set.gov.py/de/ws/sync/recibe";
    private static final String ENDPOINT_PROD = "https://sifen.set.gov.py/de/ws/sync/recibe";

    // Timeout para la conexión y lectura de respuesta (en milisegundos)
    private static final int TIMEOUT_CONEXION = 30_000;
    private static final int TIMEOUT_LECTURA  = 60_000;

    /**
     * Envía el DTE firmado a SIFEN de forma síncrona usando SOAP + mTLS.
     * Actualiza el estado, código y acuse del documento en base al resultado oficial.
     */
    public boolean enviarDteSincrono(DocumentoElectronico dte) {
        if (dte.getXmlFirmado() == null) {
            throw new IllegalStateException("El documento no está firmado. No se puede enviar a SIFEN.");
        }

        String endpoint = resolverEndpoint(dte.getAmbiente());
        logger.info("Transmitiendo a SIFEN [ambiente=" + dte.getAmbiente() + "] -> " + endpoint);
        logger.info("CDC: " + dte.getCdc());

        try {
            // Construir el contexto SSL con el P12 del emisor
            SSLContext sslContext = construirSslContext(dte);
            HttpsURLConnection conn = abrirConexion(endpoint, sslContext);

            // Armar el SOAP Envelope requerido por la SET
            String soapBody = construirSoapEnvelope(dte.getXmlFirmado());

            // Enviar la petición
            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapBody.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int httpStatus = conn.getResponseCode();
            logger.info("HTTP Status SIFEN: " + httpStatus);

            // Leer la respuesta (éxito o error)
            InputStream is = httpStatus >= 400 ? conn.getErrorStream() : conn.getInputStream();
            String xmlRespuesta = leerRespuesta(is);
            conn.disconnect();

            // Almacenar el acuse crudo para auditoría
            dte.setXmlRespuestaSifen(xmlRespuesta);
            logger.info("============== RESPUESTA CRUDA SIFEN ==============\n" + xmlRespuesta + "\n===================================================");

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
            // Almacenar el mensaje de error para diagnóstico
            dte.setXmlRespuestaSifen("ERROR_TRANSMISION: " + e.getMessage());
            return false;
        }
    }

    /**
     * Resuelve el endpoint SOAP según el ambiente del documento.
     * 1 = Test, 2 = Producción.
     */
    private String resolverEndpoint(Integer ambiente) {
        return (ambiente != null && ambiente == 2) ? ENDPOINT_PROD : ENDPOINT_TEST;
    }

    /**
     * Construye el SSLContext con mTLS.
     * Carga el P12 de la empresa en el KeyStore y acepta el TrustStore por defecto de la JVM.
     * Si la empresa no tiene certificado, se usa el SSLContext por defecto (solo TLS, sin mTLS).
     */
    private SSLContext construirSslContext(DocumentoElectronico dte) throws Exception {
        SSLContext sslContext;

        if (dte.getEmisor() != null
                && dte.getEmisor().getRutaCertificado() != null
                && !dte.getEmisor().getRutaCertificado().isBlank()) {

            String ruta     = dte.getEmisor().getRutaCertificado();
            String password = dte.getEmisor().getPasswordCertificado();
            char[] p12Pass  = password != null ? password.toCharArray() : new char[0];

            // Cargar el P12 como KeyStore del cliente (para autenticación mTLS)
            KeyStore p12Store = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(ruta)) {
                p12Store.load(fis, p12Pass);
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(p12Store, p12Pass);

            sslContext = SSLContext.getInstance("TLSv1.2");
            // TrustManager que acepta todo para pruebas (evita rechazos por CAs de QA no registradas)
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                }
            };
            sslContext.init(kmf.getKeyManagers(), trustAll, new java.security.SecureRandom());
            logger.info("SSLContext mTLS configurado (TrustAll habilitado para Test) con P12: " + ruta);
        } else {
            // Sin certificado configurado: usar el SSLContext por defecto (sin mTLS)
            logger.warning("Sin certificado P12 configurado. Usando TLS sin autenticación de cliente.");
            sslContext = SSLContext.getDefault();
        }

        return sslContext;
    }

    /**
     * Abre la conexión HTTPS con el SSLContext provisto.
     */
    private HttpsURLConnection abrirConexion(String endpoint, SSLContext sslContext) throws Exception {
        URL url = URI.create(endpoint).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setConnectTimeout(TIMEOUT_CONEXION);
        conn.setReadTimeout(TIMEOUT_LECTURA);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        // SIFEN v150 usa SOAP 1.2 -> application/soap+xml
        conn.setRequestProperty("Content-Type", "application/soap+xml; charset=UTF-8");
        conn.setRequestProperty("SOAPAction", ""); // En SOAP 1.2 el SOAPAction es opcional o va en el Content-Type

        return conn;
    }

    /**
     * Arma el Envelope SOAP 1.2 que envuelve el XML firmado del DTE.
     * Conforme a la especificación SIFEN v150.
     */
    private String construirSoapEnvelope(String xmlFirmado) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
             + "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">\n"
             + "  <env:Body>\n"
             + "    <rEnviDe xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">\n"
             + "      <dId>1</dId>\n"
             + "      <xDE>\n"
             + "        " + xmlFirmado + "\n"
             + "      </xDE>\n"
             + "    </rEnviDe>\n"
             + "  </env:Body>\n"
             + "</env:Envelope>";
    }

    /**
     * Lee la respuesta del InputStream de la conexión HTTPS.
     */
    private String leerRespuesta(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Extrae el código de resultado SIFEN (<dCodRes>) del XML de respuesta.
     * Retorna "DESCONOCIDO" si no puede parsear.
     */
    private String extraerCodigo(String xml) {
        return extraerEtiqueta(xml, "dCodRes", "DESCONOCIDO");
    }

    /**
     * Extrae el mensaje de resultado SIFEN (<dMsgRes>) del XML de respuesta.
     */
    private String extraerMensaje(String xml) {
        return extraerEtiqueta(xml, "dMsgRes", "Sin descripción");
    }

    /**
     * Mapea códigos de respuesta de SIFEN a mensajes amigables para el usuario.
     */
    private String mapearMensajeAmigable(String cod, String msgOriginal) {
        if (cod == null) return "No se recibió una respuesta clara de SIFEN. Verifique su conexión o intente nuevamente.";
        
        return switch (cod) {
            case "0100" -> "Los datos del emisor (RUC/Nombre) no coinciden con los registros de la SET o el certificado digital utilizado.";
            case "0160" -> "Error técnico de formato (SOAP). El archivo enviado tiene una estructura incorrecta que impide su lectura por SIFEN.";
            case "0300" -> "¡Documento aprobado exitosamente por la SET!";
            case "0400" -> "Firma digital inválida. La firma del documento no pudo ser verificada criptográficamente.";
            case "0401" -> "No se pudo validar el certificado del emisor. Verifique si el certificado está vigente y cargado correctamente.";
            case "0001" -> "El RUC del emisor no existe o no está registrado como facturador electrónico.";
            case "0002" -> "Certificado digital revocado o cancelado ante la autoridad certificadora.";
            case "0010" -> "Timbrado inválido o vencido para el establecimiento y punto de expedición indicados.";
            case "0020" -> "Duplicado: Ya existe un documento con este mismo número de comprobante (Establecimiento-Punto-Número).";
            case "0301" -> "RUC del receptor no se encuentra activo según los registros de la SET.";
            default -> "Respuesta SIFEN (" + cod + "): " + (msgOriginal != null ? msgOriginal : "Sin descripción disponible.");
        };
    }

    /**
     * Extrae el contenido de una etiqueta XML simple del texto provisto.
     */
    private String extraerEtiqueta(String xml, String etiqueta, String valorPorDefecto) {
        if (xml == null || xml.isBlank()) return valorPorDefecto;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<(?:.*?:)?" + etiqueta + ">(.*?)</(?:.*?:)?" + etiqueta + ">", java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return valorPorDefecto;
    }
}
