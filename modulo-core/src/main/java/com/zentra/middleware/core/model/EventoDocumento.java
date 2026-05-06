package com.zentra.middleware.core.model;

import com.zentra.middleware.core.enums.EstadoEvento;
import com.zentra.middleware.core.enums.TipoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que registra un Evento SIFEN v150.
 *
 * <p>Persiste el ciclo de vida completo de un evento post-emisión:
 * Cancelación de un DTE o Inutilización de numeración. Cada registro
 * almacena los XMLs generados, firmados y la respuesta de SIFEN,
 * garantizando trazabilidad y auditoría.</p>
 *
 * <p>La relación {@code documentoAsociado} es nullable para soportar
 * eventos de Inutilización, que no corresponden a un DTE específico.</p>
 */
@Entity
@Table(name = "eventos_sifen")
public class EventoDocumento {

    @Id
    private String id = UUID.randomUUID().toString();

    // =========================================================================
    // Tipo y estado
    // =========================================================================

    /** Tipo de evento SIFEN: CANCELACION o INUTILIZACION. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento;

    /** Estado actual del ciclo de vida del evento. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado = EstadoEvento.PENDIENTE;

    // =========================================================================
    // Relaciones
    // =========================================================================

    /**
     * Empresa emisora que envía el evento a SIFEN.
     * Requerido; provee el certificado P12 para la firma.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    /**
     * Documento Electrónico afectado por el evento.
     * Nulo para eventos de Inutilización (no aplican a un DTE específico).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id")
    private DocumentoElectronico documentoAsociado;

    // =========================================================================
    // Datos del evento
    // =========================================================================

    /**
     * CDC del DTE afectado (44 dígitos).
     * Obligatorio para Cancelación. Nulo para Inutilización.
     */
    @Column(length = 44)
    private String cdcRelacionado;

    /**
     * Motivo del evento (texto libre).
     * Requerido por SIFEN: mínimo 5, máximo 500 caracteres.
     */
    @Column(length = 500, nullable = false)
    private String motivo;

    // =========================================================================
    // Campos específicos de Inutilización (nulos en Cancelación)
    // =========================================================================

    /** Número de timbrado para inutilización (8 dígitos). */
    @Column(length = 8)
    private String timbrado;

    /** Código de establecimiento para inutilización (3 dígitos). */
    @Column(length = 3)
    private String establecimiento;

    /** Código de punto de expedición para inutilización (3 dígitos). */
    @Column(length = 3)
    private String puntoExpedicion;

    /** Tipo de documento a inutilizar (ej: 1 = Factura Electrónica). */
    private Integer tipoDocumentoInutilizar;

    /** Número inicial del rango inutilizado (7 dígitos). */
    private Long rangoDesde;

    /** Número final del rango inutilizado (7 dígitos). */
    private Long rangoHasta;

    // =========================================================================
    // Trazabilidad XML
    // =========================================================================

    /** XML del evento generado por EventoXmlGenerator antes de la firma. */
    @Column(columnDefinition = "TEXT")
    private String xmlGenerado;

    /**
     * XML del evento después de la firma XMLDSig.
     * Este es el contenido enviado al WS siRecepEvento de SIFEN.
     */
    @Column(columnDefinition = "TEXT")
    private String xmlFirmado;

    /** Respuesta XML cruda devuelta por el WS siRecepEvento. Para auditoría. */
    @Column(columnDefinition = "TEXT")
    private String xmlRespuestaSifen;

    // =========================================================================
    // Respuesta SIFEN
    // =========================================================================

    /**
     * Código de resultado devuelto por SIFEN (dCodRes).
     * Ej: "0300" = Aprobado, "0400" = Rechazado.
     */
    @Column(length = 4)
    private String codigoSifen;

    /** Mensaje de resultado de SIFEN (dMsgRes). */
    @Column(length = 500)
    private String mensajeSifen;

    /** Mensaje amigable mapeado para el usuario final. */
    @Column(length = 500)
    private String mensajeUsuario;

    // =========================================================================
    // Auditoría de fechas
    // =========================================================================

    /** Fecha y hora en que se creó el registro del evento en el sistema. */
    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Fecha y hora en que se envió el evento a SIFEN. */
    private LocalDateTime fechaEnvio;

    /** Fecha y hora en que SIFEN respondió (aprobó o rechazó). */
    private LocalDateTime fechaRespuesta;

    // =========================================================================
    // Identificador del evento para la firma XMLDSig
    // =========================================================================

    /**
     * Identificador usado como atributo Id del nodo gGroupGestE.
     * Referenciado por la firma XMLDSig mediante Reference URI="#<idFirma>".
     */
    @Column(length = 64)
    private String idFirma;

    // =========================================================================
    // Constructor
    // =========================================================================

    public EventoDocumento() {}

    // =========================================================================
    // Getters y Setters
    // =========================================================================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }

    public EstadoEvento getEstado() { return estado; }
    public void setEstado(EstadoEvento estado) { this.estado = estado; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public DocumentoElectronico getDocumentoAsociado() { return documentoAsociado; }
    public void setDocumentoAsociado(DocumentoElectronico documentoAsociado) { this.documentoAsociado = documentoAsociado; }

    public String getCdcRelacionado() { return cdcRelacionado; }
    public void setCdcRelacionado(String cdcRelacionado) { this.cdcRelacionado = cdcRelacionado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getTimbrado() { return timbrado; }
    public void setTimbrado(String timbrado) { this.timbrado = timbrado; }

    public String getEstablecimiento() { return establecimiento; }
    public void setEstablecimiento(String establecimiento) { this.establecimiento = establecimiento; }

    public String getPuntoExpedicion() { return puntoExpedicion; }
    public void setPuntoExpedicion(String puntoExpedicion) { this.puntoExpedicion = puntoExpedicion; }

    public Integer getTipoDocumentoInutilizar() { return tipoDocumentoInutilizar; }
    public void setTipoDocumentoInutilizar(Integer tipoDocumentoInutilizar) { this.tipoDocumentoInutilizar = tipoDocumentoInutilizar; }

    public Long getRangoDesde() { return rangoDesde; }
    public void setRangoDesde(Long rangoDesde) { this.rangoDesde = rangoDesde; }

    public Long getRangoHasta() { return rangoHasta; }
    public void setRangoHasta(Long rangoHasta) { this.rangoHasta = rangoHasta; }

    public String getXmlGenerado() { return xmlGenerado; }
    public void setXmlGenerado(String xmlGenerado) { this.xmlGenerado = xmlGenerado; }

    public String getXmlFirmado() { return xmlFirmado; }
    public void setXmlFirmado(String xmlFirmado) { this.xmlFirmado = xmlFirmado; }

    public String getXmlRespuestaSifen() { return xmlRespuestaSifen; }
    public void setXmlRespuestaSifen(String xmlRespuestaSifen) { this.xmlRespuestaSifen = xmlRespuestaSifen; }

    public String getCodigoSifen() { return codigoSifen; }
    public void setCodigoSifen(String codigoSifen) { this.codigoSifen = codigoSifen; }

    public String getMensajeSifen() { return mensajeSifen; }
    public void setMensajeSifen(String mensajeSifen) { this.mensajeSifen = mensajeSifen; }

    public String getMensajeUsuario() { return mensajeUsuario; }
    public void setMensajeUsuario(String mensajeUsuario) { this.mensajeUsuario = mensajeUsuario; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    public String getIdFirma() { return idFirma; }
    public void setIdFirma(String idFirma) { this.idFirma = idFirma; }
}
