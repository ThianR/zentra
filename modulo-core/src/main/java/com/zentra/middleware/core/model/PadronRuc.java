package com.zentra.middleware.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad altamente optimizada para el Padrón de RUC de la DNIT.
 * No contiene relaciones pesadas y la clave primaria es directamente el RUC sin DV.
 */
@Entity
@Table(name = "padron_ruc")
public class PadronRuc {

    @Id
    @Column(name = "ruc", length = 20, nullable = false)
    private String ruc;

    @Column(name = "dv", length = 2, nullable = false)
    private String dv;

    @Column(name = "razon_social", length = 255, nullable = false)
    private String razonSocial;

    @Column(name = "estado", length = 50)
    private String estado;

    public PadronRuc() {
    }

    public PadronRuc(String ruc, String dv, String razonSocial, String estado) {
        this.ruc = ruc;
        this.dv = dv;
        this.razonSocial = razonSocial;
        this.estado = estado;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
