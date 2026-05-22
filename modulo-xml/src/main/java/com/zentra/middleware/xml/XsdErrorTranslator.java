package com.zentra.middleware.xml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Traduce los mensajes técnicos del validador XSD a mensajes comprensibles
 * para el usuario final.
 *
 * <p>El parser XSD genera mensajes como:
 * {@code cvc-datatype-valid.1.2.3: 'Emisor' is not a valid value of union type 'tdDesRespEmiNR'}
 * Este traductor los convierte en mensajes claros en español.</p>
 */
public class XsdErrorTranslator {

    private static final Logger logger = Logger.getLogger(XsdErrorTranslator.class.getName());

    /**
     * Mapa de tipo XSD → mensaje amigable para el usuario.
     * Se usa LinkedHashMap para iterar en orden de inserción (más específico primero).
     */
    private static final Map<String, String> MENSAJES_POR_TIPO = new LinkedHashMap<>();

    /** Prefijo TEST que SIFEN agrega en ambiente de pruebas */
    private static final String PREFIJO_TEST = "TEST - ";

    static {
        // --- Nota de Remisión ---
        MENSAJES_POR_TIPO.put("tdDesRespEmiNR",
                "El campo 'Responsable de Emisión' de la Nota de Remisión contiene un valor inválido. " +
                "Valores permitidos: (1) Emisor del Comprobante, (2) Receptor del Comprobante, " +
                "(3) Empresa Transportista, (4) Despachante de Aduanas, (5) Agente de Transporte.");

        MENSAJES_POR_TIPO.put("tdDMotivTras",
                "El campo 'Motivo de Traslado' de la Nota de Remisión contiene un valor inválido. " +
                "Verifique que el motivo seleccionado corresponda a uno de los permitidos por SIFEN.");

        MENSAJES_POR_TIPO.put("tiMotivTras",
                "El código de motivo de traslado no es válido. Valores permitidos: 1 al 14, o 99.");

        MENSAJES_POR_TIPO.put("tiRespEmiNR",
                "El código de responsable de emisión no es válido. Valores permitidos: 1 al 5.");

        // --- Condición de operación ---
        MENSAJES_POR_TIPO.put("TdDCondOpe",
                "La condición de operación no es válida para este tipo de documento. " +
                "Nota: Notas de Crédito, Débito y Remisión no requieren informar condición de pago.");

        // --- Nota de Crédito / Débito ---
        MENSAJES_POR_TIPO.put("tdDesMotEmi",
                "El campo 'Motivo de Emisión' de la Nota de Crédito/Débito contiene un valor inválido. " +
                "Verifique el motivo seleccionado.");

        MENSAJES_POR_TIPO.put("tiMotEmi",
                "El código de motivo de emisión no es válido. Valores permitidos: 1 al 8.");

        // --- Receptor ---
        MENSAJES_POR_TIPO.put("tdNomRec",
                "El nombre del receptor no es válido. Debe tener al menos 4 caracteres.");

        MENSAJES_POR_TIPO.put("tdNombre",
                "Un nombre o razón social del documento no cumple el mínimo requerido (4 caracteres) " +
                "o supera el máximo (255 caracteres).");

        // --- Emisor ---
        MENSAJES_POR_TIPO.put("tdNomEmi",
                "La razón social del emisor no cumple los requisitos. Verifique la configuración de la empresa.");

        // --- Fechas ---
        MENSAJES_POR_TIPO.put("dFeEmiDE",
                "La fecha de emisión del documento no es válida o está fuera del rango permitido por SIFEN.");

        MENSAJES_POR_TIPO.put("dFeIniT",
                "La fecha de inicio de timbrado no es válida. Verifique la configuración del timbrado en la empresa.");

        // --- Montos ---
        MENSAJES_POR_TIPO.put("dTotGralOpe",
                "El total general de la operación contiene un valor inválido. Verifique los montos de los ítems.");

        MENSAJES_POR_TIPO.put("dTotIVA",
                "El total de IVA calculado contiene un valor inválido. Verifique las tasas de IVA de los ítems.");

        MENSAJES_POR_TIPO.put("dPUniProSer",
                "El precio unitario de uno o más productos contiene un formato inválido. " +
                "Los montos en guaraníes no pueden tener decimales.");

        // --- Items ---
        MENSAJES_POR_TIPO.put("tdDMotivTras",
                "El motivo de traslado seleccionado no corresponde a los valores SIFEN permitidos.");

        // --- Transporte ---
        MENSAJES_POR_TIPO.put("dNomChof",
                "El nombre del chofer es obligatorio y debe tener al menos 4 caracteres.");

        MENSAJES_POR_TIPO.put("dNumIDChof",
                "El número de documento del chofer es obligatorio para la Nota de Remisión.");

        MENSAJES_POR_TIPO.put("dNroIDVeh",
                "La matrícula del vehículo es obligatoria para la Nota de Remisión.");

        // --- Genérico por código de error XSD ---
        MENSAJES_POR_TIPO.put("cvc-datatype-valid",
                "El documento contiene un campo con un valor no permitido según las reglas de SIFEN. " +
                "Verifique los datos ingresados.");

        MENSAJES_POR_TIPO.put("cvc-minLength-valid",
                "Un campo del documento no alcanza el mínimo de caracteres requerido.");

        MENSAJES_POR_TIPO.put("cvc-maxLength-valid",
                "Un campo del documento supera el máximo de caracteres permitido.");

        MENSAJES_POR_TIPO.put("cvc-enumeration-valid",
                "Un campo del documento contiene un valor que no está en la lista de opciones válidas de SIFEN.");

        MENSAJES_POR_TIPO.put("cvc-complex-type",
                "La estructura del documento no cumple con el esquema requerido. " +
                "Es posible que falte un campo obligatorio o haya uno que no corresponde a este tipo de documento.");
    }

    public static String traducir(String mensajeXsdCrudo, String tipoDocumento) {
        return traducir(mensajeXsdCrudo, tipoDocumento, null);
    }

    /**
     * Traduce un mensaje de error XSD crudo a un mensaje comprensible para el usuario,
     * utilizando opcionalmente un diccionario dinámico externo.
     *
     * @param mensajeXsdCrudo El mensaje original del parser XSD.
     * @param tipoDocumento   Código del tipo de documento.
     * @param diccionarioExterno Mapa de código a etiqueta humana.
     * @return Mensaje en español claro para mostrar al usuario.
     */
    public static String traducir(String mensajeXsdCrudo, String tipoDocumento, Map<String, String> diccionarioExterno) {
        if (mensajeXsdCrudo == null || mensajeXsdCrudo.isBlank()) {
            return "El documento contiene un error de formato no identificado.";
        }

        // Buscar coincidencia de tipo XSD específico en el mensaje crudo
        for (Map.Entry<String, String> entrada : MENSAJES_POR_TIPO.entrySet()) {
            if (mensajeXsdCrudo.contains(entrada.getKey())) {
                String mensaje = entrada.getValue();
                // Agregar contexto del tipo de documento si está disponible
                String contexto = resolverContextoTipoDoc(tipoDocumento);
                logger.fine("Error XSD traducido: [" + entrada.getKey() + "] → " + mensaje);
                return contexto + mensaje;
            }
        }

        // Fallback: extraer la parte más legible del mensaje XSD y mostrarlo con contexto
        logger.warning("Mensaje XSD no mapeado, mostrando simplificado: " + mensajeXsdCrudo);
        return generarMensajeFallback(mensajeXsdCrudo, tipoDocumento, diccionarioExterno);
    }

    /**
     * Limpia el mensaje de respuesta de SIFEN para presentarlo al usuario.
     * Elimina prefijos técnicos como "TEST - " y entidades HTML como &#243;.
     *
     * @param mensajeSifen Mensaje crudo de SIFEN.
     * @return Mensaje limpio y legible.
     */
    public static String limpiarMensajeSifen(String mensajeSifen) {
        if (mensajeSifen == null || mensajeSifen.isBlank()) {
            return "Sin descripción.";
        }
        return mensajeSifen
                .replace(PREFIJO_TEST, "")
                .replace("&#243;", "ó")
                .replace("&#233;", "é")
                .replace("&#225;", "á")
                .replace("&#237;", "í")
                .replace("&#250;", "ú")
                .replace("&#241;", "ñ")
                .replace("&#161;", "¡")
                .replace("&#191;", "¿")
                .trim();
    }

    /**
     * Resuelve el prefijo de contexto según el tipo de documento.
     */
    private static String resolverContextoTipoDoc(String tipoDocumento) {
        if (tipoDocumento == null) return "";
        return switch (tipoDocumento) {
            case "1"  -> "⚠ Error en Factura Electrónica: ";
            case "4"  -> "⚠ Error en Autofactura Electrónica: ";
            case "5"  -> "⚠ Error en Nota de Crédito: ";
            case "6"  -> "⚠ Error en Nota de Débito: ";
            case "7"  -> "⚠ Error en Nota de Remisión: ";
            default   -> "⚠ Error en Documento Electrónico: ";
        };
    }

    /**
     * Genera un mensaje de fallback cuando no se encuentra una traducción específica.
     * Intenta extraer la parte más informativa del mensaje técnico y usar el diccionario.
     */
    private static String generarMensajeFallback(String mensajeXsd, String tipoDocumento, Map<String, String> diccionarioExterno) {
        String contexto = resolverContextoTipoDoc(tipoDocumento);

        // Intentar extraer el nombre del campo del mensaje XSD (formato: 'valor' ... 'campo')
        String campoPosible = extraerCampo(mensajeXsd);
        if (campoPosible != null) {
            String etiqueta = (diccionarioExterno != null && diccionarioExterno.containsKey(campoPosible)) 
                ? diccionarioExterno.get(campoPosible) 
                : campoPosible;
            return contexto + "El campo '" + etiqueta + "' contiene un valor inválido según las reglas de SIFEN. " +
                   "Por favor verifique el dato ingresado o contacte al soporte técnico.";
        }

        return contexto + "El documento contiene un error de validación. " +
               "Por favor revise todos los campos del formulario. " +
               "Detalle para soporte: " + mensajeXsd;
    }

    /**
     * Intenta extraer el nombre del tipo o campo del mensaje XSD.
     * Busca el último valor entre comillas simples que empiece con 'td', 'ti' o 'd'.
     */
    private static String extraerCampo(String mensaje) {
        // Patrones comunes: 'tdNombreTipo' o 'dNombreCampo'
        java.util.regex.Pattern pat = java.util.regex.Pattern.compile("'([td][A-Za-z]+)'");
        java.util.regex.Matcher m = pat.matcher(mensaje);
        String ultimo = null;
        while (m.find()) {
            ultimo = m.group(1);
        }
        return ultimo;
    }
}
