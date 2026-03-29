package com.zentra.middleware.crypto.service;

import com.zentra.middleware.core.model.DocumentoElectronico;

import org.apache.xml.security.Init;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

/**
 * Servicio de firma XMLDSig usando Apache Santuario.
 * Cuando no hay certificado configurado, cae en modo simulación para pruebas locales.
 */
@Service
public class XmlSignerService {

    private static final Logger logger = Logger.getLogger(XmlSignerService.class.getName());

    // Algoritmos requeridos por SIFEN v150
    // IMPORTANTE: Se usa C14N Exclusive (exc-c14n#) según el XML de referencia aprobado por SIFEN
    private static final String ALGO_FIRMA    = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String ALGO_DIGEST   = "http://www.w3.org/2001/04/xmlenc#sha256";
    private static final String ALGO_C14N     = "http://www.w3.org/2001/10/xml-exc-c14n#";
    private static final String ALGO_ENVUELTO = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

    static {
        Init.init();
    }

    /**
     * Firma el XML del DTE usando el certificado P12 de la empresa emisora.
     * Si la empresa no tiene certificado configurado, ejecuta una firma simulada para pruebas locales.
     */
    public String firmarXml(DocumentoElectronico dte) throws Exception {
        if (dte.getXmlGenerado() == null) {
            throw new IllegalArgumentException("No hay XML generado para firmar.");
        }

        // Verificar si hay certificado real configurado para la empresa emisora
        if (dte.getEmisor() == null
                || dte.getEmisor().getRutaCertificado() == null
                || dte.getEmisor().getRutaCertificado().isBlank()) {
            logger.warning("Empresa sin certificado configurado. Usando firma simulada para ambiente local.");
            return simularFirma(dte);
        }

        try {
            return firmarConCertificado(dte);
        } catch (Exception e) {
            logger.severe("Error durante la firma real: " + e.getMessage());
            throw new RuntimeException("Fallo en la firma digital: " + e.getMessage(), e);
        }
    }

    /**
     * Realiza la firma XMLDSig real usando Apache Santuario y el P12 de la empresa.
     * La estructura generada es idéntica a la requerida por SIFEN v150.
     */
    private String firmarConCertificado(DocumentoElectronico dte) throws Exception {
        String rutaP12 = dte.getEmisor().getRutaCertificado();
        String password = dte.getEmisor().getPasswordCertificado();

        // Cargar el almacén de claves P12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(rutaP12)) {
            keyStore.load(fis, password.toCharArray());
        }

        // Obtener el alias del primer certificado disponible
        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

        logger.info("Firmando con certificado: " + cert.getSubjectX500Principal().getName()
                + " | Vence: " + cert.getNotAfter());

        // Forzar el prefijo 'ds' para el bloque de firma. Esto evita que Santuario inyecte
        // xmlns="" en el elemento raíz <rDE>, lo cual SIFEN rechaza con error de prefijo nulo.
        org.apache.xml.security.utils.ElementProxy.setDefaultPrefix(
            org.apache.xml.security.utils.Constants.SignatureSpecNS, "ds");

        // Parsear el XML generado como Document DOM con soporte de namespaces
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
                .parse(new ByteArrayInputStream(dte.getXmlGenerado().getBytes("UTF-8")));

        // Marcar el atributo "Id" del elemento <DE> como identificador criptográfico.
        // Esto es indispensable para que Santuario resuelva la referencia URI="#CDC"
        Element root = doc.getDocumentElement();
        NodeList deList = doc.getElementsByTagNameNS("*", "DE");
        if (deList.getLength() == 0) {
            throw new RuntimeException("No se encontró el elemento <DE> en el XML generado.");
        }
        Element deElement = (Element) deList.item(0);
        deElement.setIdAttribute("Id", true);

        // Crear el objeto de firma y añadirlo al final del elemento raíz <rDE>
        XMLSignature sig = new XMLSignature(doc, null, ALGO_FIRMA);
        root.appendChild(sig.getElement());

        // Transformaciones requeridas por SIFEN v150:
        // 1. enveloped-signature: excluye el propio nodo <Signature> del hash
        // 2. Exclusive C14N: normalización canónica excluyente
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(ALGO_ENVUELTO);
        transforms.addTransform(ALGO_C14N);

        // La referencia URI apunta al elemento <DE> mediante su atributo Id (el CDC de 44 dígitos)
        sig.addDocument("#" + dte.getCdc(), transforms, ALGO_DIGEST);

        // Incluir el certificado X509 completo en el KeyInfo de la firma
        KeyInfo keyInfo = sig.getKeyInfo();
        keyInfo.addKeyName(alias);
        X509Data x509Data = new X509Data(doc);
        x509Data.addCertificate(cert);
        keyInfo.add(x509Data);

        // Ejecutar la firma criptográfica con la clave privada del P12
        sig.sign(privateKey);

        // Serializar el documento firmado sin declaración XML para evitar conflicto al embeber en SOAP
        StringWriter sw = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.transform(new DOMSource(doc), new StreamResult(sw));

        String xmlFirmado = sw.toString();
        dte.setXmlFirmado(xmlFirmado);
        logger.info("Firma XMLDSig completada exitosamente. CDC: " + dte.getCdc());
        return xmlFirmado;
    }

    /**
     * Genera una firma simulada para pruebas locales sin certificado real.
     * El XML resultante NO es válido criptográficamente pero mantiene la estructura
     * esperada por SIFEN v150 para no bloquear el flujo en desarrollo.
     */
    private String simularFirma(DocumentoElectronico dte) {
        String cdc = dte.getCdc() != null ? dte.getCdc() : "SIN_CDC";
        String bloqueSignature =
                "\n  <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n"
                + "    <SignedInfo>\n"
                + "      <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n"
                + "      <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\n"
                + "      <Reference URI=\"#" + cdc + "\">\n"
                + "        <Transforms>\n"
                + "          <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n"
                + "          <Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n"
                + "        </Transforms>\n"
                + "        <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n"
                + "        <DigestValue>SIMULADO_BASE64==</DigestValue>\n"
                + "      </Reference>\n"
                + "    </SignedInfo>\n"
                + "    <SignatureValue>SIMULADO_BASE64==</SignatureValue>\n"
                + "    <KeyInfo><X509Data><X509Certificate>SIMULADO</X509Certificate></X509Data></KeyInfo>\n"
                + "  </Signature>\n";

        String xmlGenerado = dte.getXmlGenerado();
        String xmlFirmado;

        // JAXB puede generar el cierre con prefijo ns2 dependiendo de la configuración
        if (xmlGenerado.contains("</ns2:rDE>")) {
            xmlFirmado = xmlGenerado.replace("</ns2:rDE>", bloqueSignature + "</ns2:rDE>");
        } else {
            xmlFirmado = xmlGenerado.replace("</rDE>", bloqueSignature + "</rDE>");
        }

        dte.setXmlFirmado(xmlFirmado);
        return xmlFirmado;
    }
}
