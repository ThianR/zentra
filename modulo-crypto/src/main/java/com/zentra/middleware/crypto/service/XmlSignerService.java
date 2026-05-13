package com.zentra.middleware.crypto.service;

import com.zentra.middleware.core.model.DocumentoElectronico;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servicio de firma XMLDSig para DTEs y Eventos SIFEN v150.
 *
 * <p>Gestiona dos variantes de firma:</p>
 * <ul>
 *   <li>{@link #firmarXml(DocumentoElectronico)} — firma el nodo {@code <DE>}
 *       de un DTE e incluye el bloque QR (gCamFuFD).</li>
 *   <li>{@link #firmarEventoXml(String, String, byte[], String, String)} — firma
 *       el nodo {@code <gGroupGestE>} de un evento (Cancelación/Inutilización),
 *       sin bloque QR.</li>
 * </ul>
 *
 * <p>Ambos métodos usan los mismos algoritmos criptográficos mandatados por SIFEN:</p>
 * <ul>
 *   <li>Firma: RSA-SHA256</li>
 *   <li>Digest: SHA-256</li>
 *   <li>Canonicalización: Exclusive Canonicalization (C14N exc)</li>
 * </ul>
 */
@Service
public class XmlSignerService {

    private static final Logger logger = Logger.getLogger(XmlSignerService.class.getName());

    // Algoritmos XMLDSig mandatados por SIFEN v150
    private static final String ALGO_FIRMA  = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String ALGO_DIGEST = DigestMethod.SHA256;
    private static final String ALGO_C14N   = CanonicalizationMethod.EXCLUSIVE;

    private final boolean usarFirmaSimulada = false;

    public XmlSignerService() {}

    // =========================================================================
    // Firma de DTEs (Facturas, NC, ND, Remisiones)
    // =========================================================================

    /**
     * Firma el XML de un DTE (Documento Tributario Electrónico) con XMLDSig
     * y genera el bloque QR (gCamFuFD) según SIFEN v150.
     *
     * @param dte Documento con XML generado, CDC y datos del emisor/certificado.
     * @return XML firmado con Signature embebida y bloque QR construido.
     * @throws Exception si la firma o la carga del certificado falla.
     */
    public String firmarXml(DocumentoElectronico dte) throws Exception {
        if (usarFirmaSimulada) {
            return simularFirma(dte);
        }

        String xmlGenerado = dte.getXmlGenerado();
        String cdc         = dte.getCdc();

        // Cargar certificado P12 del emisor
        KeyStore ks = cargarKeyStore(dte.getEmisor().getCertificadoFisico(),
                                     dte.getEmisor().getRutaCertificado(),
                                     dte.getEmisor().getPasswordCertificado());

        String alias = ks.aliases().nextElement();
        String p12Pass = resolverPassword(dte.getEmisor().getPasswordCertificado());

        PrivateKey      privateKey = (PrivateKey) ks.getKey(alias, p12Pass.toCharArray());
        X509Certificate cert       = (X509Certificate) ks.getCertificate(alias);

        if (privateKey == null || cert == null) {
            throw new Exception("No se pudo cargar la clave privada o el certificado del archivo P12.");
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xmlGenerado)));

        Element root            = doc.getDocumentElement();
        Element deElement       = (Element) doc.getElementsByTagName("DE").item(0);
        Element gCamFuFDElement = (Element) doc.getElementsByTagName("gCamFuFD").item(0);

        if (deElement != null) {
            deElement.setIdAttribute("Id", true);
        } else {
            throw new Exception("No se encontro el elemento <DE> en el XML generado.");
        }

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        List<Transform> transformList = new ArrayList<>();
        transformList.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        transformList.add(fac.newTransform(ALGO_C14N, (C14NMethodParameterSpec) null));

        Reference ref = fac.newReference(
            "#" + cdc,
            fac.newDigestMethod(ALGO_DIGEST, null),
            transformList, null, null
        );

        SignedInfo si = fac.newSignedInfo(
            fac.newCanonicalizationMethod(ALGO_C14N, (C14NMethodParameterSpec) null),
            fac.newSignatureMethod(ALGO_FIRMA, null),
            Collections.singletonList(ref)
        );

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        X509Data xd = kif.newX509Data(Collections.singletonList(cert));
        KeyInfo ki  = kif.newKeyInfo(Collections.singletonList(xd));

        DOMSignContext dsc = (gCamFuFDElement != null)
            ? new DOMSignContext(privateKey, root, gCamFuFDElement)
            : new DOMSignContext(privateKey, root);

        fac.newXMLSignature(si, ki).sign(dsc);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(baos));
        String xmlFirmado = limpiarSaltosBase64(baos.toString(StandardCharsets.UTF_8.name())).trim();

        dte.setXmlFirmado(xmlFirmado);

        // --- Bloque QR (exclusivo de DTEs, no aplica en eventos) ---
        String pVersion  = "150";
        String dFeEmiDE = "";
        java.util.regex.Matcher mFecha = java.util.regex.Pattern.compile("<dFeEmiDE>([^<]+)</dFeEmiDE>").matcher(xmlFirmado);
        if (mFecha.find()) {
            dFeEmiDE = mFecha.group(1).trim();
            // SIFEN requiere AAAA-MM-DDThh:mm:ss, sin el offset de zona horaria (-04:00)
            if (dFeEmiDE.length() > 19) {
                dFeEmiDE = dFeEmiDE.substring(0, 19);
            }
        } else {
            java.time.LocalDateTime fecha = dte.getFechaCreacion() != null
                ? dte.getFechaCreacion() : java.time.LocalDateTime.now();
            dFeEmiDE = fecha.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        }
        String paramRucRecName = "dRucRec";
        String dRucRecValue = "";
        java.util.regex.Matcher mRuc = java.util.regex.Pattern.compile("<dRucRec>([^<]+)</dRucRec>").matcher(xmlFirmado);
        if (mRuc.find()) {
            dRucRecValue = mRuc.group(1).trim();
            paramRucRecName = "dRucRec";
        } else {
            java.util.regex.Matcher mIden = java.util.regex.Pattern.compile("<dNumIDRec>([^<]+)</dNumIDRec>").matcher(xmlFirmado);
            if (mIden.find()) {
                dRucRecValue = mIden.group(1).trim();
                paramRucRecName = "dNumIDRec";
            } else {
                dRucRecValue = "0";
                paramRucRecName = "dRucRec";
            }
        }
        
        String dTotGralOpe = "";
        java.util.regex.Matcher mOpe = java.util.regex.Pattern.compile("<dTotGralOpe>([^<]+)</dTotGralOpe>").matcher(xmlFirmado);
        if (mOpe.find()) dTotGralOpe = mOpe.group(1).trim();
        else dTotGralOpe = dte.getTotalOperacion() != null ? String.valueOf(Math.round(dte.getTotalOperacion())) : "0";

        String dTotIVA = "";
        java.util.regex.Matcher mIva = java.util.regex.Pattern.compile("<dTotIVA>([^<]+)</dTotIVA>").matcher(xmlFirmado);
        if (mIva.find()) dTotIVA = mIva.group(1).trim();
        else dTotIVA = dte.getTotalIva() != null ? String.valueOf(Math.round(dte.getTotalIva())) : "0";

        int cItems       = dte.getItems()           != null ? dte.getItems().size()               : 0;

        String digestHex = extraerDigestValueHex(xmlFirmado);
        String IdCSC     = dte.getEmisor().getIdCsc() != null ? dte.getEmisor().getIdCsc() : "0001";

        StringBuilder qrParams = new StringBuilder();
        qrParams.append("nVersion=").append(pVersion)
                .append("&Id=").append(cdc)
                .append("&dFeEmiDE=").append(toHex(dFeEmiDE))
                .append("&").append(paramRucRecName).append("=").append(dRucRecValue)
                .append("&dTotGralOpe=").append(dTotGralOpe)
                .append("&dTotIVA=").append(dTotIVA)
                .append("&cItems=").append(cItems)
                .append("&DigestValue=").append(digestHex)
                .append("&IdCSC=").append(IdCSC);

        // Manual SIFEN v150, sección 13.8: "compuesto de 32 dígitos alfanuméricos"
        // Marangatu envía CSC genéricos de 64 chars; SIFEN usa solo los primeros 32 para el hash.
        String secretCSC     = dte.getEmisor().getValorCsc() != null
            ? dte.getEmisor().getValorCsc() : "ABCD0000000000000000000000000000";
        if (secretCSC.length() > 32) {
            secretCSC = secretCSC.substring(0, 32);
        }
        logger.info("DEBUG_QR_STRING: " + qrParams.toString() + secretCSC);
        String realHashQr    = sha256Hex(qrParams.toString() + secretCSC);

        StringBuilder dCarQR = new StringBuilder(
            dte.getAmbiente() == com.zentra.middleware.core.enums.Ambiente.PRODUCCION
                ? "https://ekuatia.set.gov.py/consultas/qr"
                : "https://ekuatia.set.gov.py/consultas-test/qr");
        dCarQR.append("?")
              .append(qrParams.toString().replace("&", "&amp;"))
              .append("&amp;cHashQR=").append(realHashQr);

        String qrBlock = "<gCamFuFD><dCarQR>" + dCarQR + "</dCarQR></gCamFuFD>";

        if (xmlFirmado.contains("<gCamFuFD")) {
            xmlFirmado = xmlFirmado.replaceFirst("<gCamFuFD>.*?</gCamFuFD>", qrBlock);
        } else {
            xmlFirmado = xmlFirmado.replace("</rDE>", qrBlock + "</rDE>");
        }

        xmlFirmado = xmlFirmado.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").trim();

        dte.setXmlFirmado(xmlFirmado);
        return xmlFirmado;
    }

    // =========================================================================
    // Firma de Eventos SIFEN (Cancelación / Inutilización) — Fase E3
    // =========================================================================

    /**
     * Firma el XML de un evento SIFEN (Cancelación o Inutilización) con XMLDSig.
     *
     * <p><b>Diferencias respecto a {@link #firmarXml(DocumentoElectronico)}:</b></p>
     * <ul>
     *   <li>El nodo a firmar es {@code <gGroupGestE>} (identificado por su atributo {@code Id}).</li>
     *   <li>La firma se inserta como último hijo de {@code <rEnviEvt>}.</li>
     *   <li>No se genera bloque QR (exclusivo de DTEs).</li>
     * </ul>
     *
     * <p><b>Sobre el atributo Id:</b> SIFEN requiere que el nodo referenciado en la firma
     * tenga el atributo {@code Id} marcado como {@code xsd:ID} en el DOM para que
     * {@code XMLSignatureFactory} resuelva la URI correctamente. Este método lo hace
     * mediante {@code element.setIdAttribute("Id", true)}.</p>
     *
     * <p>Según el Manual Técnico SIFEN v150, capítulo 11.5:</p>
     * <ul>
     *   <li>GDE002 = {@code rEve} — nodo firmable (contiene atributo {@code Id})</li>
     *   <li>GDE008 = {@code Signature} — hijo de {@code rGesEve} (GDE001), hermano de {@code rEve}</li>
     * </ul>
     *
     * @param xmlEventoGenerado   XML del evento producido por {@code EventoXmlGenerator}.
     * @param idFirma             Valor del atributo {@code Id} del nodo {@code rEve}
     *                            (se usará como {@code Reference URI="#<idFirma>"}).
     * @param p12Bytes            Bytes del certificado P12 (null si se usa rutaP12).
     * @param rutaP12             Ruta al archivo .p12 (null si se usan p12Bytes).
     * @param passwordCertificado Contraseña del certificado (puede estar cifrada con AES-256).
     * @return XML del evento con la firma XMLDSig embebida, listo para envolver en SOAP.
     * @throws Exception si la estructura del XML es inválida, el certificado no carga
     *                   o la firma falla.
     */
    public String firmarEventoXml(String xmlEventoGenerado,
                                   String idFirma,
                                   byte[] p12Bytes,
                                   String rutaP12,
                                   String passwordCertificado) throws Exception {

        logger.info("[XmlSignerService.firmarEventoXml] Iniciando firma de evento. Id=" + idFirma);

        // 1. Cargar certificado P12
        KeyStore ks = cargarKeyStore(p12Bytes, rutaP12, passwordCertificado);

        String alias  = ks.aliases().nextElement();
        String p12Pass = resolverPassword(passwordCertificado);

        PrivateKey      privateKey = (PrivateKey) ks.getKey(alias, p12Pass.toCharArray());
        X509Certificate cert       = (X509Certificate) ks.getCertificate(alias);

        if (privateKey == null || cert == null) {
            throw new Exception("No se pudo cargar la clave privada o el certificado para firmar el evento.");
        }

        // 2. Parsear el XML del evento a DOM
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xmlEventoGenerado)));

        // 3. Localizar el nodo <rEve Id="..."> que se va a firmar (GDE002)
        Element nodoAFirmar = buscarNodoPorAtributoId(doc, "rEve", idFirma);
        if (nodoAFirmar == null) {
            throw new Exception(
                "No se encontró el nodo <rEve> con Id=\"" + idFirma + "\" en el XML del evento.");
        }

        // 4. Marcar atributo Id como xsd:ID para que XMLSignatureFactory
        //    resuelva la referencia URI="#<idFirma>" correctamente.
        nodoAFirmar.setIdAttribute("Id", true);
        logger.info("[XmlSignerService.firmarEventoXml] Nodo <rEve Id=\"" + idFirma + "\"> marcado para firma.");

        // 5. Construir la firma XMLDSig (mismos algoritmos que para DTEs)
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        List<Transform> transformList = new ArrayList<>();
        transformList.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        transformList.add(fac.newTransform(ALGO_C14N, (C14NMethodParameterSpec) null));

        Reference ref = fac.newReference(
            "#" + idFirma,
            fac.newDigestMethod(ALGO_DIGEST, null),
            transformList, null, null
        );

        SignedInfo si = fac.newSignedInfo(
            fac.newCanonicalizationMethod(ALGO_C14N, (C14NMethodParameterSpec) null),
            fac.newSignatureMethod(ALGO_FIRMA, null),
            Collections.singletonList(ref)
        );

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        X509Data xd = kif.newX509Data(Collections.singletonList(cert));
        KeyInfo ki  = kif.newKeyInfo(Collections.singletonList(xd));

        // 6. La firma se inserta como hijo de <rGesEve> (GDE001), hermano de <rEve>.
        //    Según Manual Técnico v150, GDE008 (Signature) es hijo de GDE001 (rGesEve).
        Element rGesEve = (Element) nodoAFirmar.getParentNode();
        DOMSignContext dsc = new DOMSignContext(privateKey, rGesEve);
        fac.newXMLSignature(si, ki).sign(dsc);

        // 7. Serializar el DOM firmado a String UTF-8
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(baos));
        String xmlFirmado = baos.toString(StandardCharsets.UTF_8.name());

        // 8. Limpiar saltos de línea inyectados en valores Base64
        xmlFirmado = limpiarSaltosBase64(xmlFirmado);

        logger.info("[XmlSignerService.firmarEventoXml] Evento firmado exitosamente. Longitud=" + xmlFirmado.length());
        return xmlFirmado;
    }

    // =========================================================================
    // Métodos privados compartidos
    // =========================================================================

    /**
     * Carga un KeyStore PKCS12 desde bytes en memoria (prioridad) o desde ruta en disco.
     * Centraliza la lógica para evitar duplicación entre los dos firmadores.
     */
    private KeyStore cargarKeyStore(byte[] p12Bytes, String rutaP12,
                                     String passwordCertificado) throws Exception {
        if (passwordCertificado == null) {
            throw new Exception("El emisor no tiene configurada la contraseña del certificado.");
        }

        String p12Pass = resolverPassword(passwordCertificado);
        KeyStore ks = KeyStore.getInstance("PKCS12");

        if (p12Bytes != null) {
            try (java.io.InputStream is = new java.io.ByteArrayInputStream(p12Bytes)) {
                ks.load(is, p12Pass.toCharArray());
            }
        } else if (rutaP12 != null) {
            try (java.io.InputStream is = new java.io.FileInputStream(rutaP12)) {
                ks.load(is, p12Pass.toCharArray());
            }
        } else {
            throw new Exception("El emisor no tiene configurada la ruta física ni el binario del certificado.");
        }

        return ks;
    }

    /**
     * Resuelve la contraseña del certificado intentando descifrarla con AES-256.
     * Si el descifrado falla (legacy / texto plano), se usa la contraseña tal cual.
     */
    private String resolverPassword(String rawPass) {
        try {
            return com.zentra.middleware.crypto.util.AesEncryptionUtil.decrypt(rawPass);
        } catch (Exception e) {
            return rawPass;
        }
    }

    /**
     * Busca un elemento DOM por nombre de tag y valor del atributo {@code Id}.
     * Intenta primero sin namespace y luego con namespace wildcard.
     *
     * @param doc       Documento DOM donde buscar.
     * @param tagName   Nombre del elemento (ej: {@code "gGroupGestE"}).
     * @param idBuscado Valor del atributo Id a localizar.
     * @return El elemento encontrado o {@code null} si no existe.
     */
    private Element buscarNodoPorAtributoId(Document doc, String tagName, String idBuscado) {
        NodeList nodos = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            if (idBuscado.equals(el.getAttribute("Id"))) {
                return el;
            }
        }
        // Intento con wildcard de namespace
        nodos = doc.getElementsByTagNameNS("*", tagName);
        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            if (idBuscado.equals(el.getAttribute("Id"))) {
                return el;
            }
        }
        return null;
    }

    private String simularFirma(DocumentoElectronico dte) {
        return dte.getXmlGenerado();
    }

    private String limpiarSaltosBase64(String xml) {
        String[] etiquetas = {"SignatureValue", "DigestValue", "X509Certificate"};
        for (String etq : etiquetas) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "(<" + etq + ">)([\\s\\S]*?)(</" + etq + ">)");
            java.util.regex.Matcher m = p.matcher(xml);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String contenidoLimpio = m.group(2)
                    .replace("&#13;", "")
                    .replace("\r\n", "\n")
                    .replace("\r", "\n")
                    .trim();
                m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(
                    m.group(1) + contenidoLimpio + m.group(3)));
            }
            m.appendTail(sb);
            xml = sb.toString();
        }
        return xml;
    }

    private String toHex(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            sb.append(String.format("%02x", (int) c));
        }
        return sb.toString();
    }

    private String sha256Hex(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error calculando SHA-256", ex);
        }
    }

    private String extraerDigestValueHex(String xml) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "<DigestValue>([^<]+)</DigestValue>");
        java.util.regex.Matcher m = p.matcher(xml);
        if (m.find()) {
            try {
                String b64 = m.group(1).trim();
                // SIFEN v150 requiere el HEX de los caracteres ASCII del string Base64, NO de los bytes decodificados.
                return toHex(b64);
            } catch (Exception e) {
                logger.warning("Error decodificando DigestValue: " + e.getMessage());
            }
        }
        return "";
    }
}
