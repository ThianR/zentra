package com.zentra.middleware.xml;

import org.springframework.stereotype.Service;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.zentra.middleware.core.service.SifenDictionaryProvider;

@Service
public class XsdValidatorService {

    private static final Logger logger = Logger.getLogger(XsdValidatorService.class.getName());

    @Autowired(required = false)
    private SifenDictionaryProvider dictionaryProvider;

    /**
     * Valida un XML contra los esquemas oficiales de SIFEN.
     * Detecta automáticamente si es un DTE o un Evento.
     *
     * @param xml El contenido XML a validar.
     * @throws RuntimeException con mensaje amigable para el usuario si la validación falla.
     */
    public void validarXml(String xml) {
        validarXml(xml, null);
    }

    /**
     * Valida un XML contra los esquemas oficiales de SIFEN, incluyendo contexto del tipo de documento
     * para generar mensajes de error más precisos al usuario.
     *
     * @param xml             El contenido XML a validar.
     * @param tipoDocumento   Código del tipo de documento (1=FE, 5=NC, 6=ND, 7=NR, etc.) — puede ser null.
     * @throws RuntimeException con mensaje amigable para el usuario si la validación falla.
     */
    public void validarXml(String xml, String tipoDocumento) {
        if (xml == null || xml.isEmpty()) {
            throw new IllegalArgumentException("El XML a validar no puede estar vacío");
        }

        if (xml.contains("<rDE")) {
            ejecutarValidacion(xml, "/xsd/siRecepDE_v150.xsd", "rDE", "siRecepDE_v150.xsd", tipoDocumento);
        } else if (xml.contains("<rEnviEvt")) {
            ejecutarValidacion(xml, "/xsd/siRecepEvento_v150.xsd", "rEnviEvt", "siRecepEvento_v150.xsd", tipoDocumento);
        } else {
            throw new IllegalArgumentException("Raíz XML no reconocida para validación SIFEN (se esperaba rDE o rEnviEvt).");
        }
    }

    private void ejecutarValidacion(String xml, String xsdPath, String rootElement, String xsdFileName,
                                    String tipoDocumento) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            java.net.URL schemaUrl = getClass().getResource(xsdPath);
            if (schemaUrl == null) {
                logger.warning("No se encontró el archivo XSD " + xsdPath + " en el classpath. Saltando validación.");
                return;
            }

            Schema schema = factory.newSchema(schemaUrl);
            Validator validator = schema.newValidator();

            // Inyectar schemaLocation temporalmente para forzar validación estricta
            String regexRaiz = "<" + rootElement + "[^>]*>";
            String raizInyectada = "<" + rootElement + " xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\" " +
                                   "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                                   "xsi:schemaLocation=\"http://ekuatia.set.gov.py/sifen/xsd " + xsdFileName + "\">";

            String xmlParaValidar = xml.replaceAll(regexRaiz, raizInyectada);

            // Inyectar xmlns en Signature para validación
            xmlParaValidar = xmlParaValidar.replace("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");

            // Capturar todos los errores (para que no se pierda el nombre del elemento)
            StringBuilder errorMessages = new StringBuilder();
            validator.setErrorHandler(new org.xml.sax.ErrorHandler() {
                @Override
                public void warning(org.xml.sax.SAXParseException exception) { }

                @Override
                public void error(org.xml.sax.SAXParseException exception) {
                    errorMessages.append(exception.getMessage()).append(" | ");
                }

                @Override
                public void fatalError(org.xml.sax.SAXParseException exception) {
                    errorMessages.append(exception.getMessage()).append(" | ");
                }
            });

            validator.validate(new StreamSource(new StringReader(xmlParaValidar)));

            if (errorMessages.length() > 0) {
                // Eliminar el último " | "
                String errorCompleto = errorMessages.substring(0, errorMessages.length() - 3);
                throw new RuntimeException(errorCompleto);
            }

            logger.info("VALIDACIÓN XSD EXITOSA contra " + xsdFileName);

        } catch (Exception e) {
            // Registrar el error técnico completo en el log (para el soporte)
            String errorTecnico = e.getMessage();
            logger.severe("RECHAZO XSD INTERNO (" + xsdFileName + "): " + errorTecnico);

            // Traducir a mensaje amigable para el usuario
            String mensajeUsuario = XsdErrorTranslator.traducir(
                errorTecnico, 
                tipoDocumento, 
                dictionaryProvider != null ? dictionaryProvider.getDictionary() : null
            );
            throw new XsdValidationException(mensajeUsuario, errorTecnico, e);
        }
    }
}
