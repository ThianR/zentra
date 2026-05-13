package com.zentra.middleware.xml;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generador de XML para Eventos SIFEN v150 (Emisor y Receptor).
 *
 * <p>Construye el árbol DOM correspondiente a los eventos de cancelación,
 * inutilización y receptor según el Manual Técnico SIFEN v150, capítulos
 * 9.5 y 11.5.</p>
 *
 * <p>Estructura del WS siRecepEvento (Schema XML 13):</p>
 * <pre>
 *   rEnviEventoDe             (raíz del WS)
 *     dId                     (secuencial del emisor)
 *     dEvReg                  (contenedor del evento registrado)
 *       gGroupGesEve          (grupo de eventos)
 *         rGesEve             (raíz de gestión de evento)
 *           rEve Id="..."     (grupo firmable, contiene la firma)
 *             dFecFirma       (fecha y hora de la firma)
 *             dVerFor         (versión del formato = 150)
 *             dTiGDE          (tipo de evento: 1=Cancelación, 2=Inutilización)
 *             gGroupTiEvt     (datos específicos del tipo de evento)
 *               rGeVeCan / rGeVeInu / etc.
 *           Signature         (firma XMLDSig de rEve)
 * </pre>
 */
@Service
public class EventoXmlGenerator {

    private static final Logger logger = Logger.getLogger(EventoXmlGenerator.class.getName());

    /** Namespace SIFEN v150 */
    private static final String NS_SIFEN = "http://ekuatia.set.gov.py/sifen/xsd";

    /** Versión del formato de evento */
    private static final String VERSION_FORMATO = "150";

    /** Formato de fecha/hora SIFEN sin fracciones de segundo */
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /** Identificador de tipo de evento: Cancelación */
    public static final int TIPO_EVENTO_CANCELACION = 1;

    /** Identificador de tipo de evento: Inutilización */
    public static final int TIPO_EVENTO_INUTILIZACION = 2;

    /** Identificador de tipo de evento: Conformidad */
    public static final int TIPO_EVENTO_CONFORMIDAD = 11;

    /** Identificador de tipo de evento: Disconformidad */
    public static final int TIPO_EVENTO_DISCONFORMIDAD = 12;

    /** Identificador de tipo de evento: Desconocimiento */
    public static final int TIPO_EVENTO_DESCONOCIMIENTO = 13;

    /** Identificador de tipo de evento: Notificación de Recepción */
    public static final int TIPO_EVENTO_NOTIFICACION_RECEPCION = 10;

    // =========================================================================
    // API Pública
    // =========================================================================

    /**
     * Genera el XML de Cancelación de un DTE (evento tipo 1).
     *
     * @param cdcDteAfectado CDC del DTE a cancelar (44 dígitos).
     * @param motivo         Motivo de cancelación (5-500 caracteres).
     * @param rucEmisor      RUC del emisor (sin DV) — no se incluye en el evento.
     * @param dvEmisor       DV del RUC — no se incluye en el evento.
     * @param idEvento       Identificador numérico del evento (atributo Id de rEve).
     * @return XML serializado UTF-8 listo para firmar.
     */
    public String generarXmlCancelacion(String cdcDteAfectado,
                                         String motivo,
                                         String rucEmisor,
                                         String dvEmisor,
                                         String idEvento) {
        logger.info("[EventoXmlGenerator] Generando XML de Cancelación para CDC: " + cdcDteAfectado);

        try {
            Document doc = crearDocumento();
            String fechaFirma = obtenerFechaHoraActual();

            // Raíz: rEnviEventoDe (como en jsifenlib — Roshka)
            Element raiz = crearElementoConNs(doc, "rEnviEventoDe");
            doc.appendChild(raiz);

            // dId: identificador de control de envío, secuencial (GSch02)
            agregarElemento(doc, raiz, "dId", "1");

            // dEvReg: evento a ser registrado (GSch03)
            Element dEvReg = agregarElemento(doc, raiz, "dEvReg", null);

            // gGroupGesEve: con xsi:schemaLocation (como en jsifenlib)
            Element gGroupGesEve = agregarElemento(doc, dEvReg, "gGroupGesEve", null);
            gGroupGesEve.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
            gGroupGesEve.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                NS_SIFEN + " siRecepEvento_v150.xsd");

            // rGesEve: raíz de gestión de evento
            Element rGesEve = agregarElemento(doc, gGroupGesEve, "rGesEve", null);

            // rEve: grupo firmable con atributo Id
            Element rEve = agregarElemento(doc, rGesEve, "rEve", null);
            rEve.setAttribute("Id", idEvento);

            agregarElemento(doc, rEve, "dFecFirma", fechaFirma);
            agregarElemento(doc, rEve, "dVerFor", VERSION_FORMATO);
            // NOTA: dTiGDE NO se incluye (jsifenlib no lo genera)

            // gGroupTiEvt: datos específicos del evento
            Element gGroupTiEvt = agregarElemento(doc, rEve, "gGroupTiEvt", null);

            // rGeVeCan: evento de cancelación
            Element rGeVeCan = agregarElemento(doc, gGroupTiEvt, "rGeVeCan", null);
            agregarElemento(doc, rGeVeCan, "Id", cdcDteAfectado);
            agregarElemento(doc, rGeVeCan, "mOtEve", motivo);

            String xml = serializar(doc);
            logger.info("[EventoXmlGenerator] XML de Cancelación generado (" + xml.length() + " chars)");
            return xml;

        } catch (Exception e) {
            throw new RuntimeException("Error generando XML de Cancelación: " + e.getMessage(), e);
        }
    }

    /**
     * Genera el XML de Inutilización de rango de numeración (evento tipo 2).
     */
    public String generarXmlInutilizacion(String timbrado,
                                           int tipoDoc,
                                           String establecimiento,
                                           String puntoExpedicion,
                                           long numDesde,
                                           long numHasta,
                                           String motivo,
                                           String rucEmisor,
                                           String dvEmisor) {
        String idEvento = String.valueOf(System.currentTimeMillis() % 10_000_000);
        return generarXmlInutilizacion(timbrado, tipoDoc, establecimiento, puntoExpedicion,
                numDesde, numHasta, motivo, rucEmisor, dvEmisor, idEvento);
    }

    /**
     * Genera el XML de Inutilización de rango de numeración con idEvento controlado.
     */
    public String generarXmlInutilizacion(String timbrado,
                                           int tipoDoc,
                                           String establecimiento,
                                           String puntoExpedicion,
                                           long numDesde,
                                           long numHasta,
                                           String motivo,
                                           String rucEmisor,
                                           String dvEmisor,
                                           String idEvento) {
        logger.info(String.format(
            "[EventoXmlGenerator] Generando XML de Inutilización: Timb=%s, TiDE=%d, Est=%s, Pun=%s, %d-%d, Id=%s",
            timbrado, tipoDoc, establecimiento, puntoExpedicion, numDesde, numHasta, idEvento));

        try {
            Document doc = crearDocumento();
            String fechaFirma = obtenerFechaHoraActual();

            // Raíz: rEnviEventoDe (como en jsifenlib — Roshka)
            Element raiz = crearElementoConNs(doc, "rEnviEventoDe");
            doc.appendChild(raiz);

            // dId: identificador de control de envío, secuencial (GSch02)
            agregarElemento(doc, raiz, "dId", "1");

            // dEvReg: evento a ser registrado (GSch03)
            Element dEvReg = agregarElemento(doc, raiz, "dEvReg", null);

            // gGroupGesEve: con xsi:schemaLocation (como en jsifenlib)
            Element gGroupGesEve = agregarElemento(doc, dEvReg, "gGroupGesEve", null);
            gGroupGesEve.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
            gGroupGesEve.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                NS_SIFEN + " siRecepEvento_v150.xsd");
            Element rGesEve = agregarElemento(doc, gGroupGesEve, "rGesEve", null);

            // rEve: grupo firmable
            Element rEve = agregarElemento(doc, rGesEve, "rEve", null);
            rEve.setAttribute("Id", idEvento);

            agregarElemento(doc, rEve, "dFecFirma", fechaFirma);
            agregarElemento(doc, rEve, "dVerFor", VERSION_FORMATO);
            // NOTA: dTiGDE NO se incluye (jsifenlib no lo genera)

            Element gGroupTiEvt = agregarElemento(doc, rEve, "gGroupTiEvt", null);

            // rGeVeInu: evento de inutilización
            Element rGeVeInu = agregarElemento(doc, gGroupTiEvt, "rGeVeInu", null);
            agregarElemento(doc, rGeVeInu, "dNumTim", timbrado);
            agregarElemento(doc, rGeVeInu, "dEst",
                String.format("%03d", Integer.parseInt(establecimiento.replaceAll("[^0-9]", "0"))));
            agregarElemento(doc, rGeVeInu, "dPunExp",
                String.format("%03d", Integer.parseInt(puntoExpedicion.replaceAll("[^0-9]", "0"))));
            agregarElemento(doc, rGeVeInu, "dNumIn", String.format("%07d", numDesde));
            agregarElemento(doc, rGeVeInu, "dNumFin", String.format("%07d", numHasta));
            agregarElemento(doc, rGeVeInu, "iTiDE", String.valueOf(tipoDoc));
            agregarElemento(doc, rGeVeInu, "mOtEve", motivo);

            String xml = serializar(doc);
            logger.info("[EventoXmlGenerator] XML de Inutilización generado (" + xml.length() + " chars)");
            return xml;

        } catch (Exception e) {
            throw new RuntimeException("Error generando XML de Inutilización: " + e.getMessage(), e);
        }
    }

    /**
     * Genera el XML genérico para un evento de receptor.
     */
    public String generarXmlEventoReceptor(String cdcDteAfectado, int tipoEvento, String motivo,
                                           String rucReceptor, String dvReceptor, String idEvento) {
        logger.info("[EventoXmlGenerator] Generando XML Evento Receptor (Tipo " + tipoEvento + ") para CDC: " + cdcDteAfectado);

        try {
            Document doc = crearDocumento();
            String fechaFirma = obtenerFechaHoraActual();

            // Raíz: rEnviEventoDe (como en jsifenlib — Roshka)
            Element raiz = crearElementoConNs(doc, "rEnviEventoDe");
            doc.appendChild(raiz);

            // dId: identificador de control de envío, secuencial (GSch02)
            agregarElemento(doc, raiz, "dId", "1");

            // dEvReg: evento a ser registrado (GSch03)
            Element dEvReg = agregarElemento(doc, raiz, "dEvReg", null);

            // gGroupGesEve: con xsi:schemaLocation (como en jsifenlib)
            Element gGroupGesEve = agregarElemento(doc, dEvReg, "gGroupGesEve", null);
            gGroupGesEve.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
            gGroupGesEve.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                NS_SIFEN + " siRecepEvento_v150.xsd");
            Element rGesEve = agregarElemento(doc, gGroupGesEve, "rGesEve", null);

            Element rEve = agregarElemento(doc, rGesEve, "rEve", null);
            rEve.setAttribute("Id", idEvento);

            agregarElemento(doc, rEve, "dFecFirma", fechaFirma);
            agregarElemento(doc, rEve, "dVerFor", VERSION_FORMATO);
            // NOTA: dTiGDE NO se incluye (jsifenlib no lo genera)

            Element gGroupTiEvt = agregarElemento(doc, rEve, "gGroupTiEvt", null);

            // Agregar los datos específicos del evento de receptor según el tipo
            switch (tipoEvento) {
                case TIPO_EVENTO_CONFORMIDAD -> {
                    Element rGeVeConf = agregarElemento(doc, gGroupTiEvt, "rGeVeConf", null);
                    agregarElemento(doc, rGeVeConf, "Id", cdcDteAfectado);
                    if (motivo != null && !motivo.isBlank()) {
                        agregarElemento(doc, rGeVeConf, "mOtEve", motivo.trim());
                    }
                }
                case TIPO_EVENTO_DISCONFORMIDAD -> {
                    Element rGeVeDisconf = agregarElemento(doc, gGroupTiEvt, "rGeVeDisconf", null);
                    agregarElemento(doc, rGeVeDisconf, "Id", cdcDteAfectado);
                    agregarElemento(doc, rGeVeDisconf, "mOtEve", motivo.trim());
                }
                case TIPO_EVENTO_DESCONOCIMIENTO -> {
                    Element rGeVeDescon = agregarElemento(doc, gGroupTiEvt, "rGeVeDescon", null);
                    agregarElemento(doc, rGeVeDescon, "Id", cdcDteAfectado);
                    agregarElemento(doc, rGeVeDescon, "mOtEve", motivo.trim());
                }
                case TIPO_EVENTO_NOTIFICACION_RECEPCION -> {
                    Element rGeVeNotRec = agregarElemento(doc, gGroupTiEvt, "rGeVeNotRec", null);
                    agregarElemento(doc, rGeVeNotRec, "Id", cdcDteAfectado);
                    if (motivo != null && !motivo.isBlank()) {
                        agregarElemento(doc, rGeVeNotRec, "mOtEve", motivo.trim());
                    }
                }
                default -> throw new IllegalArgumentException(
                    "Tipo de evento de receptor no soportado: " + tipoEvento);
            }

            String xml = serializar(doc);
            logger.info("[EventoXmlGenerator] XML de Evento Receptor generado (" + xml.length() + " chars)");
            return xml;

        } catch (Exception e) {
            throw new RuntimeException("Error generando XML de Evento Receptor: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // Métodos auxiliares DOM
    // =========================================================================

    /** Crea un documento DOM vacío. */
    private Document crearDocumento() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.newDocument();
    }

    /** Crea un elemento con el namespace SIFEN como raíz. */
    private Element crearElementoConNs(Document doc, String nombre) {
        return doc.createElementNS(NS_SIFEN, nombre);
    }

    /**
     * Agrega un elemento hijo al padre dado.
     * Si valor es null, crea solo el contenedor (sin texto).
     */
    private Element agregarElemento(Document doc, Element padre, String nombre, String valor) {
        Element elem = doc.createElementNS(NS_SIFEN, nombre);
        if (valor != null) {
            elem.setTextContent(valor);
        }
        padre.appendChild(elem);
        return elem;
    }

    /** Serializa el Document DOM a String UTF-8. */
    private String serializar(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(baos));
        return baos.toString(StandardCharsets.UTF_8);
    }

    /**
     * Retorna la fecha y hora actual en la zona horaria de Asunción, Paraguay,
     * formateada según SIFEN: AAAA-MM-DDThh:mm:ss
     */
    private String obtenerFechaHoraActual() {
        ZonedDateTime zdtAsuncion = LocalDateTime.now()
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("America/Asuncion"));
        return zdtAsuncion.format(FMT_FECHA);
    }

    /**
     * Mapea el código de tipo de documento SIFEN a su descripción oficial.
     */
    private String mapearDescripcionTipoDoc(int tipoDoc) {
        return switch (tipoDoc) {
            case 1 -> "Factura electrónica";
            case 4 -> "Autofactura electrónica";
            case 5 -> "Nota de crédito electrónica";
            case 6 -> "Nota de débito electrónica";
            case 7 -> "Nota de remisión electrónica";
            default -> "Documento electrónico";
        };
    }
}
