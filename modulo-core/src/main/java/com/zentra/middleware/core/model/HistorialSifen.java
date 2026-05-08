package com.zentra.middleware.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documento_historial_sifen")
public class HistorialSifen {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id", nullable = false)
    @JsonIgnore
    private DocumentoElectronico documento;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(length = 50, nullable = false)
    private String operacion;

    @Column(name = "codigo_estado", length = 10)
    private String codigoEstado;

    @Column(name = "mensaje_respuesta", length = 255)
    private String mensajeRespuesta;

    @Column(name = "xml_respuesta", columnDefinition = "TEXT")
    private String xmlRespuesta;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public DocumentoElectronico getDocumento() { return documento; }
    public void setDocumento(DocumentoElectronico documento) { this.documento = documento; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }

    public String getCodigoEstado() { return codigoEstado; }
    public void setCodigoEstado(String codigoEstado) { this.codigoEstado = codigoEstado; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }
    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public String getXmlRespuesta() { return xmlRespuesta; }
    public void setXmlRespuesta(String xmlRespuesta) { this.xmlRespuesta = xmlRespuesta; }
}
