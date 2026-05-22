package com.zentra.middleware.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "centro_eventos_logs")
public class CentroEventosLog {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "empresa_id", length = 36)
    private String empresaId;

    @Column(name = "usuario_id", length = 36)
    private String usuarioId;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "mensaje_amigable", columnDefinition = "TEXT")
    private String mensajeAmigable;

    @Column(name = "detalle_tecnico", columnDefinition = "TEXT")
    private String detalleTecnico;

    @Column(name = "datos_contexto", columnDefinition = "TEXT")
    private String datosContexto;

    @Column(length = 50)
    private String estado = "PENDIENTE";

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMensajeAmigable() {
        return mensajeAmigable;
    }

    public void setMensajeAmigable(String mensajeAmigable) {
        this.mensajeAmigable = mensajeAmigable;
    }

    public String getDetalleTecnico() {
        return detalleTecnico;
    }

    public void setDetalleTecnico(String detalleTecnico) {
        this.detalleTecnico = detalleTecnico;
    }

    public String getDatosContexto() {
        return datosContexto;
    }

    public void setDatosContexto(String datosContexto) {
        this.datosContexto = datosContexto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
