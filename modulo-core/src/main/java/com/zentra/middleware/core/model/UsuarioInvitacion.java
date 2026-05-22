package com.zentra.middleware.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa una invitación segura (token temporal) enviada a un correo electrónico 
 * para registrar un nuevo usuario en la plataforma Zentra.
 */
@Entity
@Table(name = "usuario_invitaciones")
public class UsuarioInvitacion {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String email;

    /** Rol del futuro usuario: ADMIN o OPERADOR */
    @Column(nullable = false)
    private String rol;

    /** Indica si el usuario (rol OPERADOR) solo podrá ver los DTEs emitidos por él mismo */
    @Column(nullable = false)
    private Boolean verSoloSusDtes = false;

    /** Cliente al que pertenecerá el usuario. Si es nulo, podría ser un error o caso borde. */
    @ManyToOne(optional = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private Boolean usado = false;

    public UsuarioInvitacion() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getVerSoloSusDtes() { return verSoloSusDtes; }
    public void setVerSoloSusDtes(Boolean verSoloSusDtes) { this.verSoloSusDtes = verSoloSusDtes; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public Boolean getUsado() { return usado; }
    public void setUsado(Boolean usado) { this.usado = usado; }

    /** Retorna true si la invitación ya superó su fecha límite */
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }
}
