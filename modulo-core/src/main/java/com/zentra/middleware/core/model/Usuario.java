package com.zentra.middleware.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Usuario operador del sistema.
 * Pertenece a un Cliente (tenant) y puede tener una empresa por defecto.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    private String passwordHash;

    private String nombreCompleto;
    private String email;

    /** Rol del usuario: ADMIN o OPERADOR */
    private String rol = "OPERADOR";

    private Boolean activo = true;

    /** Indica si el usuario debe cambiar su contraseña en el próximo login */
    private Boolean debeCambiarPassword = false;

    /** Indica si el usuario (rol OPERADOR) solo puede ver los DTEs emitidos por él mismo */
    private Boolean verSoloSusDtes = false;

    @ManyToOne
    private Cliente cliente;

    /** Empresa seleccionada por defecto al iniciar sesión */
    @ManyToOne
    private Empresa empresaDefault;

    private LocalDateTime ultimoAcceso;
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Usuario() {}

    // --- Getters y Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Boolean getDebeCambiarPassword() { return debeCambiarPassword; }
    public void setDebeCambiarPassword(Boolean debeCambiarPassword) { this.debeCambiarPassword = debeCambiarPassword; }

    public Boolean getVerSoloSusDtes() { return verSoloSusDtes; }
    public void setVerSoloSusDtes(Boolean verSoloSusDtes) { this.verSoloSusDtes = verSoloSusDtes; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Empresa getEmpresaDefault() { return empresaDefault; }
    public void setEmpresaDefault(Empresa empresaDefault) { this.empresaDefault = empresaDefault; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
