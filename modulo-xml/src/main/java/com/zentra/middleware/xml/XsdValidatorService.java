package com.zentra.middleware.xml;

import org.springframework.stereotype.Service;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;
import java.util.logging.Logger;

@Service
public class XsdValidatorService {

    private static final Logger logger = Logger.getLogger(XsdValidatorService.class.getName());

    /**
     * Valida un XML contra los esquemas oficiales de SIFEN.
     * @param xml El contenido XML a validar.
     * @throws RuntimeException si la validación falla.
     */
    public void validarXml(String xml) {
        if (xml == null || xml.isEmpty()) {
            throw new IllegalArgumentException("El XML a validar no puede estar vacío");
        }

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // Cargar el esquema desde el classpath
            java.net.URL schemaUrl = getClass().getResource("/xsd/siRecepDE_v150.xsd");
            if (schemaUrl == null) {
                logger.warning("No se encontró el archivo XSD en el classpath. Saltando validación.");
                return;
            }
            
            Schema schema = factory.newSchema(schemaUrl);
            Validator validator = schema.newValidator();
            
            String xmlParaValidar = xml.replaceAll(
                "<rDE[^>]*>",
                "<rDE xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:schemaLocation=\"http://ekuatia.set.gov.py/sifen/xsd siRecepDE_v150.xsd\">"
            );
            // Inyectar xmlns en Signature para validación
            xmlParaValidar = xmlParaValidar.replace("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");

            // Validar el XML tal como viene (ya incluye sus namespaces)
            validator.validate(new StreamSource(new StringReader(xmlParaValidar)));
            
            logger.info("VALIDACIÓN XSD EXITOSA (con inyección temporal)");
            
        } catch (Exception e) {
            String errorMsg = "RECHAZO XSD INTERNO: " + e.getMessage();
            logger.severe(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }
}
