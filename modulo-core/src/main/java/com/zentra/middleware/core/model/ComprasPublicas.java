package com.zentra.middleware.core.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ComprasPublicas {

    private String modalidadContratacion; // dModCont
    private Integer entidadContratante;     // dEntCont
    private Integer anioContratacion;        // dAnoCont
    private Integer secuencialContrato;     // dSecCont
    private String fechaCodigoContrato;    // dFeCodCont

    public ComprasPublicas() {}

    public String getModalidadContratacion() { return modalidadContratacion; }
    public void setModalidadContratacion(String modalidadContratacion) { this.modalidadContratacion = modalidadContratacion; }
    public Integer getEntidadContratante() { return entidadContratante; }
    public void setEntidadContratante(Integer entidadContratante) { this.entidadContratante = entidadContratante; }
    public Integer getAnioContratacion() { return anioContratacion; }
    public void setAnioContratacion(Integer anioContratacion) { this.anioContratacion = anioContratacion; }
    public Integer getSecuencialContrato() { return secuencialContrato; }
    public void setSecuencialContrato(Integer secuencialContrato) { this.secuencialContrato = secuencialContrato; }
    public String getFechaCodigoContrato() { return fechaCodigoContrato; }
    public void setFechaCodigoContrato(String fechaCodigoContrato) { this.fechaCodigoContrato = fechaCodigoContrato; }
}
