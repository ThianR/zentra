package com.zentra.middleware.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sifen_diccionario_errores")
public class SifenDiccionarioError {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "codigo_sifen", length = 100, unique = true, nullable = false)
    private String codigoSifen;

    @Column(name = "etiqueta_humana", length = 255, nullable = false)
    private String etiquetaHumana;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoSifen() {
        return codigoSifen;
    }

    public void setCodigoSifen(String codigoSifen) {
        this.codigoSifen = codigoSifen;
    }

    public String getEtiquetaHumana() {
        return etiquetaHumana;
    }

    public void setEtiquetaHumana(String etiquetaHumana) {
        this.etiquetaHumana = etiquetaHumana;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
