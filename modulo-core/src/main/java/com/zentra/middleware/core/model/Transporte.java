package com.zentra.middleware.core.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Transporte {

    // Identificación del Transportista (gCamTrans)
    private Integer naturalezaTransportista; // 1=Contribuyente, 2=No
    private String nombreTransportista;
    private String rucTransportista;
    private String dvTransportista;
    private String numeroDocumentoChofer;
    private String nombreChofer;
    private String direccionChofer;

    // Nota de Remisión (gCamNRE)
    private Integer motivoTraslado; // iMotEmiNR
    private String descripcionMotivoTraslado;
    private Integer responsableEmision; // iRespEmiNR
    private Integer kmsRecorrido;
    private Double precioFlete;
    private String matriculaVehiculo;

    public Transporte() {}

    public Integer getNaturalezaTransportista() { return naturalezaTransportista; }
    public void setNaturalezaTransportista(Integer naturalezaTransportista) { this.naturalezaTransportista = naturalezaTransportista; }
    public String getNombreTransportista() { return nombreTransportista; }
    public void setNombreTransportista(String nombreTransportista) { this.nombreTransportista = nombreTransportista; }
    public String getRucTransportista() { return rucTransportista; }
    public void setRucTransportista(String rucTransportista) { this.rucTransportista = rucTransportista; }
    public String getDvTransportista() { return dvTransportista; }
    public void setDvTransportista(String dvTransportista) { this.dvTransportista = dvTransportista; }
    public String getNumeroDocumentoChofer() { return numeroDocumentoChofer; }
    public void setNumeroDocumentoChofer(String numeroDocumentoChofer) { this.numeroDocumentoChofer = numeroDocumentoChofer; }
    public String getNombreChofer() { return nombreChofer; }
    public void setNombreChofer(String nombreChofer) { this.nombreChofer = nombreChofer; }
    public String getDireccionChofer() { return direccionChofer; }
    public void setDireccionChofer(String direccionChofer) { this.direccionChofer = direccionChofer; }
    public Integer getMotivoTraslado() { return motivoTraslado; }
    public void setMotivoTraslado(Integer motivoTraslado) { this.motivoTraslado = motivoTraslado; }
    public String getDescripcionMotivoTraslado() { return descripcionMotivoTraslado; }
    public void setDescripcionMotivoTraslado(String descripcionMotivoTraslado) { this.descripcionMotivoTraslado = descripcionMotivoTraslado; }
    public Integer getResponsableEmision() { return responsableEmision; }
    public void setResponsableEmision(Integer responsableEmision) { this.responsableEmision = responsableEmision; }
    public Integer getKmsRecorrido() { return kmsRecorrido; }
    public void setKmsRecorrido(Integer kmsRecorrido) { this.kmsRecorrido = kmsRecorrido; }
    public Double getPrecioFlete() { return precioFlete; }
    public void setPrecioFlete(Double precioFlete) { this.precioFlete = precioFlete; }
    public String getMatriculaVehiculo() { return matriculaVehiculo; }
    public void setMatriculaVehiculo(String matriculaVehiculo) { this.matriculaVehiculo = matriculaVehiculo; }
}
