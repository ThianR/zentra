package com.zentra.middleware.core.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Transporte {

    // Identificación del Transportista (gCamTrans)
    private Integer naturalezaTransportista; // 1=Contribuyente, 2=No
    private Integer tipoTransporte; // 1=Propio, 2=Tercero
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
    private String fechaInicioTraslado; // formato AAAA-MM-DD
    private String fechaFinTraslado;    // formato AAAA-MM-DD
    // Datos del local de salida (gCamSal)
    private String localSalidaDireccion;
    private Integer localSalidaNumeroCasa;
    private Integer localSalidaCodigoDepartamento;
    private String localSalidaDescripcionDepartamento;
    private Integer localSalidaCodigoCiudad;
    private String localSalidaDescripcionCiudad;
    // Datos del local de entrega (gCamEnt)
    private String localEntregaDireccion;
    private Integer localEntregaNumeroCasa;
    private Integer localEntregaCodigoDepartamento;
    private String localEntregaDescripcionDepartamento;
    private Integer localEntregaCodigoCiudad;
    private String localEntregaDescripcionCiudad;
    // Datos del vehículo de traslado (gVehTras)
    private String marcaVehiculo;
    private String tipoVehiculo;
    private String chasisVehiculo;

    public Transporte() {}

    public Integer getNaturalezaTransportista() { return naturalezaTransportista; }
    public void setNaturalezaTransportista(Integer naturalezaTransportista) { this.naturalezaTransportista = naturalezaTransportista; }
    public Integer getTipoTransporte() { return tipoTransporte; }
    public void setTipoTransporte(Integer tipoTransporte) { this.tipoTransporte = tipoTransporte; }
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
    public String getFechaInicioTraslado() { return fechaInicioTraslado; }
    public void setFechaInicioTraslado(String fechaInicioTraslado) { this.fechaInicioTraslado = fechaInicioTraslado; }
    public String getFechaFinTraslado() { return fechaFinTraslado; }
    public void setFechaFinTraslado(String fechaFinTraslado) { this.fechaFinTraslado = fechaFinTraslado; }
    public String getLocalSalidaDireccion() { return localSalidaDireccion; }
    public void setLocalSalidaDireccion(String v) { this.localSalidaDireccion = v; }
    public Integer getLocalSalidaNumeroCasa() { return localSalidaNumeroCasa; }
    public void setLocalSalidaNumeroCasa(Integer v) { this.localSalidaNumeroCasa = v; }
    public Integer getLocalSalidaCodigoDepartamento() { return localSalidaCodigoDepartamento; }
    public void setLocalSalidaCodigoDepartamento(Integer v) { this.localSalidaCodigoDepartamento = v; }
    public String getLocalSalidaDescripcionDepartamento() { return localSalidaDescripcionDepartamento; }
    public void setLocalSalidaDescripcionDepartamento(String v) { this.localSalidaDescripcionDepartamento = v; }
    public Integer getLocalSalidaCodigoCiudad() { return localSalidaCodigoCiudad; }
    public void setLocalSalidaCodigoCiudad(Integer v) { this.localSalidaCodigoCiudad = v; }
    public String getLocalSalidaDescripcionCiudad() { return localSalidaDescripcionCiudad; }
    public void setLocalSalidaDescripcionCiudad(String v) { this.localSalidaDescripcionCiudad = v; }
    public String getLocalEntregaDireccion() { return localEntregaDireccion; }
    public void setLocalEntregaDireccion(String v) { this.localEntregaDireccion = v; }
    public Integer getLocalEntregaNumeroCasa() { return localEntregaNumeroCasa; }
    public void setLocalEntregaNumeroCasa(Integer v) { this.localEntregaNumeroCasa = v; }
    public Integer getLocalEntregaCodigoDepartamento() { return localEntregaCodigoDepartamento; }
    public void setLocalEntregaCodigoDepartamento(Integer v) { this.localEntregaCodigoDepartamento = v; }
    public String getLocalEntregaDescripcionDepartamento() { return localEntregaDescripcionDepartamento; }
    public void setLocalEntregaDescripcionDepartamento(String v) { this.localEntregaDescripcionDepartamento = v; }
    public Integer getLocalEntregaCodigoCiudad() { return localEntregaCodigoCiudad; }
    public void setLocalEntregaCodigoCiudad(Integer v) { this.localEntregaCodigoCiudad = v; }
    public String getLocalEntregaDescripcionCiudad() { return localEntregaDescripcionCiudad; }
    public void setLocalEntregaDescripcionCiudad(String v) { this.localEntregaDescripcionCiudad = v; }
    public String getMarcaVehiculo() { return marcaVehiculo; }
    public void setMarcaVehiculo(String v) { this.marcaVehiculo = v; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String v) { this.tipoVehiculo = v; }
    public String getChasisVehiculo() { return chasisVehiculo; }
    public void setChasisVehiculo(String v) { this.chasisVehiculo = v; }
}
