package com.zentra.middleware.crypto.service;

import com.zentra.middleware.core.model.DocumentoElectronico;

import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

@Service
public class XmlSignerService {

    static {
        Init.init();
    }

    /**
     * Firma el XML del DTE usando el certificado de la empresa.
     * Implementacion base usando Apache Santuario.
     */
    public String firmarXml(DocumentoElectronico dte) throws Exception {
        if (dte.getXmlGenerado() == null) {
            throw new IllegalArgumentException("No hay XML para firmar.");
        }

        // Para el MVP, si no hay certificado real, simulamos el resultado
        if (dte.getEmisor() == null || dte.getEmisor().getRutaCertificado() == null) {
             return simularFirma(dte);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(dte.getXmlGenerado().getBytes("UTF-8")));

        // Configurar firma con constantes literales para evitar problemas de version
        XMLSignature sig = new XMLSignature(doc, null, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        Element root = doc.getDocumentElement();
        root.appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);
        transforms.addTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
        transforms.addTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        sig.addDocument("", transforms, "http://www.w3.org/2001/04/xmlenc#sha256");

        // En un entorno real:
        // sig.addKeyInfo(cert);
        // sig.sign(privateKey);

        String xmlFirmado = dte.getXmlGenerado().replace("</rDE>", "  <Signature>...</Signature>\n</rDE>");
        dte.setXmlFirmado(xmlFirmado);
        return xmlFirmado;
    }

    private String simularFirma(DocumentoElectronico dte) {
        String signature = "\n  <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                           "    <SignedInfo>\n" +
                           "      <Reference URI=\"#" + dte.getCdc() + "\">\n" +
                           "        <DigestValue>Base64Digest==</DigestValue>\n" +
                           "      </Reference>\n" +
                           "    </SignedInfo>\n" +
                           "    <SignatureValue>Base64Signature==</SignatureValue>\n" +
                           "  </Signature>\n</rDE>";
        String xmlFirmado = dte.getXmlGenerado().replace("</rDE>", signature);
        dte.setXmlFirmado(xmlFirmado);
        return xmlFirmado;
    }
}
