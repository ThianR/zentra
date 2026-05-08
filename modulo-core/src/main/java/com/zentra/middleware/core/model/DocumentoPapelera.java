package com.zentra.middleware.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad para respaldar documentos electrónicos que han sido eliminados del flujo principal
 * (por ejemplo, documentos rechazados que son re-generados).
 */
@Entity
@Table(name = "documentos_papelera")
public class DocumentoPapelera {

    @Id
    private String id = UUID.randomUUID().toString();

    private String documentoIdOriginal;
    private String cdc;
    private String numeroComprobante;
    private String tipoDocumento;
    private String rucReceptor;
    private String razonSocialReceptor;
    private Double montoTotal;
    private String estadoOriginal;

    @Column(columnDefinition = "TEXT")
    private String xmlGenerado;

    @Column(columnDefinition = "TEXT")
    private String xmlFirmado;

    @Column(columnDefinition = "TEXT")
    private String xmlRespuestaSifen;

    private LocalDateTime fechaEliminacion = LocalDateTime.now();
    private String motivoEliminacion;

    public DocumentoPapelera() {}

    public DocumentoPapelera(DocumentoElectronico dte, String motivo) {
        this.documentoIdOriginal = dte.getId();
        this.cdc = dte.getCdc();
        this.numeroComprobante = dte.getNumeroComprobante();
        this.tipoDocumento = dte.getTipoDocumento();
        this.rucReceptor = dte.getRucReceptor();
        this.razonSocialReceptor = dte.getReceptorRazonSocial();
        this.montoTotal = dte.getTotalOperacion();
        this.estadoOriginal = dte.getEstado() != null ? dte.getEstado().name() : null;
        this.xmlGenerado = dte.getXmlGenerado();
        this.xmlFirmado = dte.getXmlFirmado();
        this.xmlRespuestaSifen = dte.getXmlRespuestaSifen();
        this.motivoEliminacion = motivo;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDocumentoIdOriginal() { return documentoIdOriginal; }
    public void setDocumentoIdOriginal(String documentoIdOriginal) { this.documentoIdOriginal = documentoIdOriginal; }
    public String getCdc() { return cdc; }
    public void setCdc(String cdc) { this.cdc = cdc; }
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getRucReceptor() { return rucReceptor; }
    public void setRucReceptor(String rucReceptor) { this.rucReceptor = rucReceptor; }
    public String getRazonSocialReceptor() { return razonSocialReceptor; }
    public void setRazonSocialReceptor(String razonSocialReceptor) { this.razonSocialReceptor = razonSocialReceptor; }
    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }
    public String getEstadoOriginal() { return estadoOriginal; }
    public void setEstadoOriginal(String estadoOriginal) { this.estadoOriginal = estadoOriginal; }
    public String getXmlGenerado() { return xmlGenerado; }
    public void setXmlGenerado(String xmlGenerado) { this.xmlGenerado = xmlGenerado; }
    public String getXmlFirmado() { return xmlFirmado; }
    public void setXmlFirmado(String xmlFirmado) { this.xmlFirmado = xmlFirmado; }
    public String getXmlRespuestaSifen() { return xmlRespuestaSifen; }
    public void setXmlRespuestaSifen(String xmlRespuestaSifen) { this.xmlRespuestaSifen = xmlRespuestaSifen; }
    public LocalDateTime getFechaEliminacion() { return fechaEliminacion; }
    public void setFechaEliminacion(LocalDateTime fechaEliminacion) { this.fechaEliminacion = fechaEliminacion; }
    public String getMotivoEliminacion() { return motivoEliminacion; }
    public void setMotivoEliminacion(String motivoEliminacion) { this.motivoEliminacion = motivoEliminacion; }
}
