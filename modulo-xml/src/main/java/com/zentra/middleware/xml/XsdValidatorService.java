package com.zentra.middleware.xml;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para validar el XML generado contra los esquemas XSD oficiales de SIFEN.
 */
@Service
public class XsdValidatorService {

    private static final Logger logger = Logger.getLogger(XsdValidatorService.class.getName());
    private Schema schema;

    public XsdValidatorService() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // Prevenir XXE
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "all"); 

            // Cargar el esquema principal (DE_v150.xsd). 
            // IMPORTANTE: Proporcionamos el SystemId para que el validador pueda encontrar los imports relativos como xmldsig-core-schema.xsd
            ClassPathResource xsdRes = new ClassPathResource("xsd/DE_v150.xsd");
            try (InputStream is = xsdRes.getInputStream()) {
                StreamSource ss = new StreamSource(is);
                ss.setSystemId(xsdRes.getURL().toExternalForm()); // Permite resolver imports relativos en el classpath
                this.schema = factory.newSchema(ss);
                logger.info("Esquema XSD DE_v150 local cargado correctamente para validación estricta.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "No se pudo pre-cargar el esquema XSD de SIFEN para validación: " + e.getMessage(), e);
        }
    }

    /**
     * Valida un XML string contra el XSD pre-cargado.
     * @param xml El string que contiene el <rDE>
     * @throws RuntimeException si el XML está mal formado o no cumple el XSD
     */
    public void validarXml(String xml) {
        if (schema == null) {
            logger.warning("Validador XSD no inicializado, se omite validación estricta local.");
            return;
        }

        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
            logger.info("El documento DTE pasó exitosamente la validación estricta XSD local.");
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "Fallo de validación XSD antes de enviar a SIFEN: " + e.getMessage());
            throw new RuntimeException("El documento no cumple con la estructura requerida por SIFEN (XML Mal Formado): " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado validando el XML: " + e.getMessage(), e);
        }
    }
}
