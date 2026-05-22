package com.zentra.middleware.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa al tenant (organización o persona) que contrata Zentra.
 * Un Cliente puede gestionar múltiples Empresas emisoras.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    private String id = UUID.randomUUID().toString();

    /** Nombre descriptivo del cliente/organización */
    private String nombre;

    /** Identificador único tipo slug (ej: "estudio-xyz") */
    private String identificador;

    private Boolean activo = true;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Estado de la suscripción: AL_DIA, MES_1_AVISO, MES_2_LIMITADO, MES_3_BLOQUEADO */
    private String estadoSuscripcion = "AL_DIA";

    private LocalDate fechaVencimientoPago;

    /** Límite diario de emisiones (-1 = sin límite) */
    private Integer limiteDiarioEmisiones = -1;

    @OneToMany(mappedBy = "cliente")
    private List<Empresa> empresas = new ArrayList<>();

    public Cliente() {}

    // --- Getters y Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<Empresa> getEmpresas() { return empresas; }
    public void setEmpresas(List<Empresa> empresas) { this.empresas = empresas; }

    public String getEstadoSuscripcion() { return estadoSuscripcion; }
    public void setEstadoSuscripcion(String estadoSuscripcion) { this.estadoSuscripcion = estadoSuscripcion; }

    public LocalDate getFechaVencimientoPago() { return fechaVencimientoPago; }
    public void setFechaVencimientoPago(LocalDate fechaVencimientoPago) { this.fechaVencimientoPago = fechaVencimientoPago; }

    public Integer getLimiteDiarioEmisiones() { return limiteDiarioEmisiones; }
    public void setLimiteDiarioEmisiones(Integer limiteDiarioEmisiones) { this.limiteDiarioEmisiones = limiteDiarioEmisiones; }
}
