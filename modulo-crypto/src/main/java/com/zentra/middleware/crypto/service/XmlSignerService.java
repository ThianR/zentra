package com.zentra.middleware.crypto.service;

import com.zentra.middleware.core.model.DocumentoElectronico;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Service
public class XmlSignerService {

    private static final Logger logger = Logger.getLogger(XmlSignerService.class.getName());

    private final boolean usarFirmaSimulada = false;

    public XmlSignerService() {
    }

    public String firmarXml(DocumentoElectronico dte) throws Exception {
        if (usarFirmaSimulada) {
            return simularFirma(dte);
        }

        String xmlGenerado = dte.getXmlGenerado();
        String cdc = dte.getCdc();
        
        // Carga de certificado (Memoria BD vs Archivo Físico)
        byte[] p12Bytes = dte.getEmisor().getCertificadoFisico();
        String p12Path = dte.getEmisor().getRutaCertificado();
        String rawPass = dte.getEmisor().getPasswordCertificado();
        
        if (rawPass == null) {
            throw new Exception("El emisor no tiene configurada la contraseña del certificado.");
        }

        String p12Pass;
        try {
            // Intentamos desencriptar (Si falla, asumimos que es texto plano legacy de entorno local)
            p12Pass = com.zentra.middleware.crypto.util.AesEncryptionUtil.decrypt(rawPass);
        } catch (Exception e) {
            p12Pass = rawPass;
        }

        java.security.KeyStore ks = java.security.KeyStore.getInstance("PKCS12");
        
        if (p12Bytes != null) {
            try (java.io.InputStream is = new java.io.ByteArrayInputStream(p12Bytes)) {
                ks.load(is, p12Pass.toCharArray());
            }
        } else if (p12Path != null) {
            try (java.io.InputStream is = new java.io.FileInputStream(p12Path)) {
                ks.load(is, p12Pass.toCharArray());
            }
        } else {
            throw new Exception("El emisor no tiene configurada la ruta física ni el binario del certificado.");
        }

        String alias = ks.aliases().nextElement();
        java.security.PrivateKey privateKey = (java.security.PrivateKey) ks.getKey(alias, p12Pass.toCharArray());
        java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) ks.getCertificate(alias);

        if (privateKey == null || cert == null) {
            throw new Exception("No se pudo cargar la clave privada o el certificado del archivo P12.");
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xmlGenerado)));

        Element root = doc.getDocumentElement();
        Element deElement = (Element) doc.getElementsByTagName("DE").item(0);
        Element gCamFuFDElement = (Element) doc.getElementsByTagName("gCamFuFD").item(0);

        if (deElement != null) {
            deElement.setIdAttribute("Id", true);
        } else {
            throw new Exception("No se encontro el elemento <DE> en el XML generado.");
        }

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        List<Transform> transformList = new ArrayList<>();
        transformList.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        transformList.add(fac.newTransform(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null));

        Reference ref = fac.newReference(
            "#" + cdc,
            fac.newDigestMethod(DigestMethod.SHA256, null),
            transformList,
            null,
            null
        );

        SignedInfo si = fac.newSignedInfo(
            fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
            fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
            Collections.singletonList(ref)
        );

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        X509Data xd = kif.newX509Data(Collections.singletonList(cert));
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        DOMSignContext dsc;
        if (gCamFuFDElement != null) {
            dsc = new DOMSignContext(privateKey, root, gCamFuFDElement);
        } else {
            dsc = new DOMSignContext(privateKey, root);
        }
        
        XMLSignature signature = fac.newXMLSignature(si, ki);
        signature.sign(dsc);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(baos));
        String xmlFirmado = baos.toString(StandardCharsets.UTF_8.name());
        
        xmlFirmado = limpiarSaltosBase64(xmlFirmado);
        
        // Se mantiene el xmlns="http://www.w3.org/2000/09/xmldsig#" en <Signature> para cumplimiento del estándar SIFEN.
        xmlFirmado = xmlFirmado.trim();

        dte.setXmlFirmado(xmlFirmado);
        
        // --- QR ---
        String pVersion = "150";
        String pId = dte.getCdc();
        java.time.LocalDateTime fecha = dte.getFechaCreacion() != null ? dte.getFechaCreacion() : java.time.LocalDateTime.now();
        String dFeEmiDE = fecha.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String hexFecha = toHex(dFeEmiDE);
        String dRucRec = dte.getRucReceptor() != null ? dte.getRucReceptor() : "0";
        long dTotGralOpe = dte.getTotalOperacion() != null ? Math.round(dte.getTotalOperacion()) : 0L;
        long dTotIVA = dte.getTotalIva() != null ? Math.round(dte.getTotalIva()) : 0L;
        int cItems = dte.getItems() != null ? dte.getItems().size() : 0;

        String digestHex = extraerDigestValueHex(xmlFirmado);
        String IdCSC = dte.getEmisor().getIdCsc() != null ? dte.getEmisor().getIdCsc() : "0001";
        
        StringBuilder urlParaHash = new StringBuilder("https://ekuatia.set.gov.py/consultas/qr");
        urlParaHash.append("?nVersion=").append(pVersion);
        urlParaHash.append("&Id=").append(pId);
        urlParaHash.append("&dFeEmiDE=").append(hexFecha);
        urlParaHash.append("&dRucRec=").append(dRucRec);
        urlParaHash.append("&dTotGralOpe=").append(dTotGralOpe);
        urlParaHash.append("&dTotIVA=").append(dTotIVA);
        urlParaHash.append("&cItems=").append(cItems);
        urlParaHash.append("&DigestValue=").append(digestHex);
        urlParaHash.append("&IdCSC=").append(IdCSC);

        String secretCSC = dte.getEmisor().getValorCsc() != null ? dte.getEmisor().getValorCsc() : "ABCD0000000000000000000000000000";
        String cadenaACifrar = urlParaHash.toString() + secretCSC;
        String realHashQr = sha256Hex(cadenaACifrar);

        StringBuilder dCarQR = new StringBuilder(dte.getAmbiente() == com.zentra.middleware.core.enums.Ambiente.PRODUCCION 
            ? "https://ekuatia.set.gov.py/consultas/qr" 
            : "https://sifen-test.set.gov.py/consultas/qr");
            
        dCarQR.append("?").append(urlParaHash.toString().replace("https://ekuatia.set.gov.py/consultas/qr?", "").replace("&", "&amp;"));
        dCarQR.append("&amp;cHashQR=").append(realHashQr);

        String qrBlock = "<gCamFuFD><dCarQR>" + dCarQR.toString() + "</dCarQR></gCamFuFD>";

        if (xmlFirmado.contains("<gCamFuFD")) {
            xmlFirmado = xmlFirmado.replaceFirst("<gCamFuFD>.*?</gCamFuFD>", qrBlock);
        } else {
            xmlFirmado = xmlFirmado.replace("</rDE>", qrBlock + "</rDE>");
        }

        xmlFirmado = xmlFirmado.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").trim();

        dte.setXmlFirmado(xmlFirmado);
        return xmlFirmado;
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
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("<DigestValue>([^<]+)</DigestValue>");
        java.util.regex.Matcher m = p.matcher(xml);
        if (m.find()) {
            String base64 = m.group(1).trim();
            return toHex(base64);
        }
        return "";
    }
}
