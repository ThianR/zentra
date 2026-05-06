package com.zentra.middleware.xml;

import com.zentra.middleware.core.model.DocumentoElectronico;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de generar el XML contenedor de lote (<rLoteDE>)
 * que agrupa hasta 50 DTEs firmados para su envío asíncrono.
 */
@Service
public class LoteXmlGenerator {

    /**
     * Envuelve una lista de DTEs firmados en la etiqueta <rLoteDE>.
     *
     * @param dtes Lista de documentos electrónicos (máximo 50).
     * @return El XML del lote como String.
     */
    public String generarXmlLote(List<DocumentoElectronico> dtes) {
        if (dtes == null || dtes.isEmpty()) {
            throw new IllegalArgumentException("La lista de DTEs para el lote no puede estar vacía.");
        }
        if (dtes.size() > 50) {
            throw new IllegalArgumentException("Un lote no puede contener más de 50 DTEs según SIFEN v150.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<rLoteDE xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">\n");

        for (DocumentoElectronico dte : dtes) {
            String xmlFirmado = dte.getXmlFirmado();
            if (xmlFirmado == null || xmlFirmado.isBlank()) {
                throw new IllegalStateException("El DTE " + dte.getNumeroComprobante() + " no está firmado.");
            }
            // Eliminar el prólogo XML individual
            String xmlLimpio = xmlFirmado.replaceFirst("<\\?xml.*?\\?>", "").trim();
            sb.append(xmlLimpio).append("\n");
        }

        sb.append("</rLoteDE>");

        return sb.toString();
    }
}
