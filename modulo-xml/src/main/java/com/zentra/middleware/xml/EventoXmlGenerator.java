package com.zentra.middleware.xml;

import com.zentra.middleware.sifen.schema.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Generador de XML para Eventos SIFEN v150 (Emisor).
 *
 * <p>Construye el árbol de objetos JAXB correspondiente a los eventos
 * de cancelación e inutilización según el Manual Técnico v150 y los
 * serializa a una cadena XML lista para ser firmada y enviada a SIFEN.</p>
 *
 * <p>Tipos de eventos soportados:
 * <ul>
 *   <li>Tipo 1 — Cancelación de un DTE aprobado.</li>
 *   <li>Tipo 3 — Inutilización de rango de numeración.</li>
 * </ul>
 * </p>
 */
@Service
public class EventoXmlGenerator {

    private static final Logger logger = Logger.getLogger(EventoXmlGenerator.class.getName());

    /** Namespace SIFEN v150 */
    private static final String NAMESPACE_SIFEN = "http://ekuatia.set.gov.py/sifen/xsd";

    /** Identificador de tipo de evento: Cancelación */
    public static final int TIPO_EVENTO_CANCELACION = 1;

    /** Identificador de tipo de evento: Conformidad */
    public static final int TIPO_EVENTO_CONFORMIDAD = 2;

    /** Identificador de tipo de evento: Inutilización */
    public static final int TIPO_EVENTO_INUTILIZACION = 3;

    /** Identificador de tipo de evento: Disconformidad */
    public static final int TIPO_EVENTO_DISCONFORMIDAD = 4;

    /** Identificador de tipo de evento: Desconocimiento */
    public static final int TIPO_EVENTO_DESCONOCIMIENTO = 5;

    /** Identificador de tipo de evento: Notificación de Recepción */
    public static final int TIPO_EVENTO_NOTIFICACION_RECEPCION = 6;

    private static JAXBContext jaxbContext;

    static {
        try {
            // Se reutiliza el mismo JAXBContext del paquete de esquemas,
            // ya que las nuevas clases de evento se ubican en el mismo paquete.
            jaxbContext = JAXBContext.newInstance("com.zentra.middleware.sifen.schema");
            logger.info("[EventoXmlGenerator] JAXBContext inicializado correctamente.");
        } catch (Exception e) {
            logger.severe("[EventoXmlGenerator] Error al inicializar JAXBContext: " + e.getMessage());
        }
    }

    // =========================================================================
    // API Pública
    // =========================================================================

    /**
     * Genera el XML de Cancelación de un DTE.
     *
     * <p>La cancelación se aplica sobre el CDC de 44 dígitos del DTE que se desea
     * cancelar. El {@code idEvento} se usa como atributo {@code Id} del nodo
     * {@code gGroupGestE} para que la firma XMLDSig lo referencie.</p>
     *
     * @param cdcDteAfectado CDC del DTE a cancelar (44 dígitos).
     * @param motivo         Motivo de cancelación (5-500 caracteres).
     * @param rucEmisor      RUC del emisor (sin DV).
     * @param dvEmisor       Dígito verificador del RUC.
     * @param idEvento       Identificador único del evento (se usará como atributo Id para firma).
     * @return XML serializado UTF-8 del mensaje de cancelación, listo para firmar.
     */
    public String generarXmlCancelacion(String cdcDteAfectado,
                                         String motivo,
                                         String rucEmisor,
                                         String dvEmisor,
                                         String idEvento) {
        logger.info("[EventoXmlGenerator] Generando XML de Cancelación para CDC: " + cdcDteAfectado);

        try {
            TgEvCan evCan = new TgEvCan();
            evCan.setMOtEve(motivo);

            TgGestEv gestEv = construirGestEv(TIPO_EVENTO_CANCELACION, "Cancelación", evCan);
            TgGroupGestE groupGestE = construirGroupGestE(cdcDteAfectado, idEvento, gestEv);

            REnviEvt rEnviEvt = construirREnviEvt(rucEmisor, dvEmisor, groupGestE);

            return serializar(rEnviEvt, cdcDteAfectado);

        } catch (Exception e) {
            throw new RuntimeException("Error generando XML de Cancelación: " + e.getMessage(), e);
        }
    }

    /**
     * Genera el XML de Inutilización de rango de numeración.
     *
     * <p>La inutilización informa a SIFEN que un rango de números de comprobante
     * no será utilizado. El sistema genera un {@code idEvento} único basado
     * en un UUID para el atributo {@code Id} del nodo raíz del evento.</p>
     *
     * @param timbrado       Número de timbrado de 8 dígitos.
     * @param tipoDoc        Código del tipo de documento (ej: 1 = Factura).
     * @param establecimiento Código de establecimiento (3 dígitos).
     * @param puntoExpedicion Código de punto de expedición (3 dígitos).
     * @param numDesde       Número inicial del rango a inutilizar.
     * @param numHasta       Número final del rango a inutilizar.
     * @param motivo         Motivo de inutilización (5-500 caracteres).
     * @param rucEmisor      RUC del emisor (sin DV).
     * @param dvEmisor       Dígito verificador del RUC.
     * @return XML serializado UTF-8 del mensaje de inutilización, listo para firmar.
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
        // Genera un idEvento interno para retrocompatibilidad.
        // Usar la sobrecarga con idEvento explícito cuando la firma debe ser coherente.
        String idEvento = "INU-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        return generarXmlInutilizacion(timbrado, tipoDoc, establecimiento, puntoExpedicion,
                numDesde, numHasta, motivo, rucEmisor, dvEmisor, idEvento);
    }

    /**
     * Genera el XML de Inutilización de rango de numeración con {@code idEvento} controlado externamente.
     *
     * <p>Esta sobrecarga permite al orquestador (ej: {@code EventoController}) establecer
     * el mismo {@code idEvento} que se usará en la firma XMLDSig, garantizando que la
     * {@code Reference URI="#<idEvento>"} del firmador coincida con el atributo {@code Id}
     * del nodo {@code gGroupGestE} en el XML generado.</p>
     *
     * @param timbrado        Número de timbrado de 8 dígitos.
     * @param tipoDoc         Código del tipo de documento.
     * @param establecimiento Código de establecimiento (3 dígitos).
     * @param puntoExpedicion Código de punto de expedición (3 dígitos).
     * @param numDesde        Número inicial del rango.
     * @param numHasta        Número final del rango.
     * @param motivo          Motivo de inutilización.
     * @param rucEmisor       RUC del emisor (sin DV).
     * @param dvEmisor        Dígito verificador del RUC.
     * @param idEvento        Identificador único del evento (usado como atributo Id para firma).
     * @return XML serializado UTF-8 del mensaje de inutilización, listo para firmar.
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
            TgEvInut evInut = new TgEvInut();
            evInut.setDNumTim(timbrado);
            evInut.setITiDE(BigInteger.valueOf(tipoDoc));
            evInut.setDDesTiDE(mapearDescripcionTipoDoc(tipoDoc));
            evInut.setDEst(String.format("%03d", Integer.parseInt(establecimiento.replaceAll("[^0-9]", "0"))));
            evInut.setDPunExp(String.format("%03d", Integer.parseInt(puntoExpedicion.replaceAll("[^0-9]", "0"))));
            evInut.setDNumIniFolio(BigInteger.valueOf(numDesde));
            evInut.setDNumFinFolio(BigInteger.valueOf(numHasta));
            evInut.setMOtEve(motivo);

            String dIdContenido = String.format("%s-%d-%s-%s-%07d-%07d",
                timbrado, tipoDoc, establecimiento, puntoExpedicion, numDesde, numHasta);

            TgGestEv gestEv = construirGestEv(TIPO_EVENTO_INUTILIZACION, "Inutilización", evInut);
            TgGroupGestE groupGestE = construirGroupGestE(dIdContenido, idEvento, gestEv);

            REnviEvt rEnviEvt = construirREnviEvt(rucEmisor, dvEmisor, groupGestE);

            return serializar(rEnviEvt, dIdContenido);

        } catch (Exception e) {
            throw new RuntimeException("Error generando XML de Inutilización: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // Métodos de construcción internos
    // =========================================================================

    /**
     * Construye el objeto {@code TgGestEv} con el tipo de evento, descripción y
     * la fecha/hora actual en zona horaria de Asunción, asignando dinámicamente el payload.
     */
    private TgGestEv construirGestEv(int tipoEvt, String desEvt, Object eventoEspecifico) throws Exception {
        TgGestEv gestEv = new TgGestEv();
        gestEv.setITiEvt(BigInteger.valueOf(tipoEvt));
        gestEv.setDDesTiEvt(desEvt);
        gestEv.setDFecHoraEvt(obtenerFechaHoraActual());
        
        if (eventoEspecifico instanceof TgEvCan) gestEv.setGEvCan((TgEvCan) eventoEspecifico);
        else if (eventoEspecifico instanceof TgEvInut) gestEv.setGEvInut((TgEvInut) eventoEspecifico);
        else if (eventoEspecifico instanceof TgEvConf) gestEv.setGEvConf((TgEvConf) eventoEspecifico);
        else if (eventoEspecifico instanceof TgEvDisconf) gestEv.setGEvDisconf((TgEvDisconf) eventoEspecifico);
        else if (eventoEspecifico instanceof TgEvDescon) gestEv.setGEvDescon((TgEvDescon) eventoEspecifico);
        else if (eventoEspecifico instanceof TgEvNotRec) gestEv.setGEvNotRec((TgEvNotRec) eventoEspecifico);
        
        return gestEv;
    }

    /**
     * Genera el XML genérico para un evento de receptor (Tipos 2, 4, 5, 6).
     *
     * @param cdcDteAfectado CDC del documento recibido.
     * @param tipoEvento     Código del evento de receptor.
     * @param motivo         Motivo del evento (puede ser nulo para conformidad/notificación).
     * @param rucReceptor    RUC de la empresa que envía el evento.
     * @param dvReceptor     DV del RUC de la empresa.
     * @param idEvento       Identificador único para la firma.
     * @return XML serializado.
     */
    public String generarXmlEventoReceptor(String cdcDteAfectado, int tipoEvento, String motivo,
                                           String rucReceptor, String dvReceptor, String idEvento) {
        logger.info("[EventoXmlGenerator] Generando XML Evento Receptor (Tipo " + tipoEvento + ") para CDC: " + cdcDteAfectado);

        try {
            Object evData = null;
            String descripcion = "";

            switch (tipoEvento) {
                case TIPO_EVENTO_CONFORMIDAD -> {
                    descripcion = "Conformidad";
                    TgEvConf conf = new TgEvConf();
                    if (motivo != null && !motivo.isBlank()) conf.setMOtEve(motivo.trim());
                    evData = conf;
                }
                case TIPO_EVENTO_DISCONFORMIDAD -> {
                    descripcion = "Disconformidad";
                    TgEvDisconf disconf = new TgEvDisconf();
                    disconf.setMOtEve(motivo.trim());
                    evData = disconf;
                }
                case TIPO_EVENTO_DESCONOCIMIENTO -> {
                    descripcion = "Desconocimiento";
                    TgEvDescon descon = new TgEvDescon();
                    descon.setMOtEve(motivo.trim());
                    evData = descon;
                }
                case TIPO_EVENTO_NOTIFICACION_RECEPCION -> {
                    descripcion = "Notificación de Recepción";
                    TgEvNotRec notRec = new TgEvNotRec();
                    if (motivo != null && !motivo.isBlank()) notRec.setMOtEve(motivo.trim());
                    evData = notRec;
                }
                default -> throw new IllegalArgumentException("Tipo de evento no soportado para receptor: " + tipoEvento);
            }

            TgGestEv gestEv = construirGestEv(tipoEvento, descripcion, evData);
            TgGroupGestE groupGestE = construirGroupGestE(cdcDteAfectado, idEvento, gestEv);
            REnviEvt rEnviEvt = construirREnviEvt(rucReceptor, dvReceptor, groupGestE);

            return serializar(rEnviEvt, cdcDteAfectado);

        } catch (Exception e) {
            throw new RuntimeException("Error generando XML de Evento Receptor: " + e.getMessage(), e);
        }
    }

    /**
     * Construye el nodo raíz del evento {@code TgGroupGestE}.
     *
     * @param dId      Contenido del elemento dId (CDC afectado u otro identificador).
     * @param idAtrib  Valor del atributo Id (usado por XMLDSig para la firma).
     * @param gestEv   Datos del evento.
     */
    private TgGroupGestE construirGroupGestE(String dId, String idAtrib, TgGestEv gestEv) {
        TgGroupGestE group = new TgGroupGestE();
        group.setDId(dId);
        group.setId(idAtrib);
        group.setGGestEv(gestEv);
        return group;
    }

    /**
     * Construye el contenedor {@code REnviEvt} con los datos del emisor
     * y el grupo de eventos.
     */
    private REnviEvt construirREnviEvt(String rucEmisor, String dvEmisor, TgGroupGestE groupGestE) throws Exception {
        REnviEvt rEnviEvt = new REnviEvt();
        rEnviEvt.setDId(BigInteger.ONE);
        rEnviEvt.setDFecFirma(obtenerFechaHoraActual());
        rEnviEvt.setDRucEm(rucEmisor.replaceAll("[^0-9]", ""));
        rEnviEvt.setDDVEmi(new BigInteger(dvEmisor.replaceAll("[^0-9]", "0")));
        rEnviEvt.getGGroupGestE().add(groupGestE);
        return rEnviEvt;
    }

    /**
     * Serializa el objeto {@code REnviEvt} a una cadena XML UTF-8.
     *
     * <p>Se aplica el mismo patrón que {@code DteXmlGenerator}: marshalling
     * a {@code ByteArrayOutputStream} para asegurar la declaración
     * {@code <?xml ... encoding="UTF-8"?>} correcta.</p>
     *
     * @param rEnviEvt    Objeto raíz a serializar.
     * @param idReferencia Identificador para logging.
     * @return XML generado como String.
     */
    private String serializar(REnviEvt rEnviEvt, String idReferencia) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
            NAMESPACE_SIFEN + " siRecepEvento_v150.xsd");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(rEnviEvt, baos);
        String xml = baos.toString(StandardCharsets.UTF_8);

        // Inyectar namespace explícito en la raíz (mismo patrón que DteXmlGenerator)
        xml = xml.replaceFirst(
            "<rEnviEvt\\s+xmlns=\"" + NAMESPACE_SIFEN.replace(".", "\\.") + "\">",
            "<rEnviEvt xmlns=\"" + NAMESPACE_SIFEN + "\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"" + NAMESPACE_SIFEN + " siRecepEvento_v150.xsd\">");

        logger.info("[EventoXmlGenerator] XML generado para evento con id=" + idReferencia
            + " (" + xml.length() + " chars)");
        return xml;
    }

    /**
     * Retorna la fecha y hora actual en la zona horaria de Asunción, Paraguay
     * sin fracciones de segundo ni zona horaria explícita (formato SIFEN).
     */
    private XMLGregorianCalendar obtenerFechaHoraActual() throws Exception {
        ZonedDateTime zdtAsuncion = LocalDateTime.now()
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("America/Asuncion"));
        GregorianCalendar gcal = GregorianCalendar.from(zdtAsuncion);
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        xmlCal.setFractionalSecond(null);
        xmlCal.setTimezone(javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
        return xmlCal;
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
