package com.zentra.middleware.core.model;

import jakarta.persistence.*;

/**
 * Entidad que centraliza todas las tablas de referencia proveídas por SIFEN.
 * Permite evitar el hardcoding de códigos de departamentos, ciudades, monedas, etc.
 *
 * @author Antigravity
 */
@Entity
@Table(name = "sifen_referencia",
    uniqueConstraints = {@UniqueConstraint(name = "uk_sifen_ref_tipo_cod", columnNames = {"tipo", "codigo", "padre_codigo"})})
public class SifenReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "padre_codigo", length = 20)
    private String padreCodigo;

    @Column(name = "valor_aux", length = 100)
    private String valorAux;

    @Column(name = "descripcion_aux", length = 255)
    private String descripcionAux;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer orden = 0;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;

    // --- Constructores ---
    
    public SifenReferencia() {}

    public SifenReferencia(String tipo, String codigo, String descripcion) {
        this.tipo = tipo;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPadreCodigo() { return padreCodigo; }
    public void setPadreCodigo(String padreCodigo) { this.padreCodigo = padreCodigo; }

    public String getValorAux() { return valorAux; }
    public void setValorAux(String valorAux) { this.valorAux = valorAux; }

    public String getDescripcionAux() { return descripcionAux; }
    public void setDescripcionAux(String descripcionAux) { this.descripcionAux = descripcionAux; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
