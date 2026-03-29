package com.zentra.middleware.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documentos_electronicos")
public class DocumentoElectronico {

    @Id
    private String id = UUID.randomUUID().toString();

    private String cdc;
    
    private String tipoDocumento; // 1=FE, 2=FEx, etc.
    private Integer tipoOperacion = 1; // 1=B2B, 2=B2C, 3=B2G, 4=B2F
    private Integer condicionOperacion = 1; // 1=Contado, 2=Crédito

    @Enumerated(EnumType.STRING)
    private EstadoDte estado = EstadoDte.CREADO;

    @ManyToOne
    private Empresa emisor;

    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, fetch = jakarta.persistence.FetchType.EAGER)
    private List<ItemDocumento> items = new ArrayList<>();


    @Lob
    private String xmlGenerado;
    
    @Lob
    private String xmlFirmado;

    /** Acuse de recibo SOAP devuelto por SIFEN (XML crudo). Para auditoría. */
    @Lob
    private String xmlRespuestaSifen;

    /** Código de estado oficial de SIFEN (ej: '0300'=Aprobado, '0400'=Error). */
    private String codigoEstadoSifen;

    private String numeroComprobante;
    private String timbrado;
    private String rucEmisor;
    private String razonSocialEmisor;
    private String actividadEconomicaEmisor;
    private String direccionEmisor;
    private String telefonoEmisor;
    
    private String rucReceptor;
    private String receptorRazonSocial;
    private String receptorDireccion;
    private String receptorTelefono;
    private String receptorEmail;
    private Integer tipoReceptor = 1; // 1=Contribuyente, 2=No Contribuyente
    private String cPaisReceptor = "PRY"; 
    private Integer naturalezaVendedor; // Para Autofactura: 1=No Contribuyente, 2=Extranjero
    
    private Double totalOperacion;
    private Double descuentoGlobal = 0.0;
    private Double porcentajeDescuentoGlobal = 0.0;
    
    // Totales resumidos para KuDE
    private Double totalGravada10 = 0.0;
    private Double totalGravada5 = 0.0;
    private Double totalExenta = 0.0;
    private Double totalIva10 = 0.0;
    private Double totalIva5 = 0.0;
    private Double totalIva = 0.0;
    
    // Documentos Asociados (Notas de Crédito/Débito)
    private String cdcDocumentoAsociado;
    private Integer tipoDocumentoAsociado; // 1=Electrónico, 2=Impreso
    private String motivoEmision; // Para NC/ND
    
    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, fetch = jakarta.persistence.FetchType.EAGER)
    private List<Cuota> cuotas = new ArrayList<>();

    private Integer indicadorPresencia = 1; // 1=Operación presencial, etc.

    @jakarta.persistence.Embedded
    private ComprasPublicas comprasPublicas;

    @jakarta.persistence.Embedded
    private Transporte transporte;

    private Integer ambiente = 1; // 1=Test, 2=Prod
    private String formatoKuDE = "A4"; // A4, TICKET

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public DocumentoElectronico() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCdc() { return cdc; }
    public void setCdc(String cdc) { this.cdc = cdc; }
    public EstadoDte getEstado() { return estado; }
    public void setEstado(EstadoDte estado) { this.estado = estado; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public Integer getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(Integer tipoOperacion) { this.tipoOperacion = tipoOperacion; }
    public Integer getCondicionOperacion() { return condicionOperacion; }
    public void setCondicionOperacion(Integer condicionOperacion) { this.condicionOperacion = condicionOperacion; }
    public Empresa getEmisor() { return emisor; }
    public void setEmisor(Empresa emisor) { this.emisor = emisor; }
    public String getXmlGenerado() { return xmlGenerado; }
    public void setXmlGenerado(String xmlGenerado) { this.xmlGenerado = xmlGenerado; }
    public String getXmlFirmado() { return xmlFirmado; }
    public void setXmlFirmado(String xmlFirmado) { this.xmlFirmado = xmlFirmado; }
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    public String getTimbrado() { return timbrado; }
    public void setTimbrado(String timbrado) { this.timbrado = timbrado; }
    public String getRucEmisor() { return rucEmisor; }
    public void setRucEmisor(String rucEmisor) { this.rucEmisor = rucEmisor; }
    public String getRazonSocialEmisor() { return razonSocialEmisor; }
    public void setRazonSocialEmisor(String razonSocialEmisor) { this.razonSocialEmisor = razonSocialEmisor; }
    public String getActividadEconomicaEmisor() { return actividadEconomicaEmisor; }
    public void setActividadEconomicaEmisor(String actividadEconomicaEmisor) { this.actividadEconomicaEmisor = actividadEconomicaEmisor; }
    public String getDireccionEmisor() { return direccionEmisor; }
    public void setDireccionEmisor(String direccionEmisor) { this.direccionEmisor = direccionEmisor; }
    public String getTelefonoEmisor() { return telefonoEmisor; }
    public void setTelefonoEmisor(String telefonoEmisor) { this.telefonoEmisor = telefonoEmisor; }

    public String getRucReceptor() { return rucReceptor; }
    public void setRucReceptor(String rucReceptor) { this.rucReceptor = rucReceptor; }
    public String getReceptorRazonSocial() { return receptorRazonSocial; }
    public void setReceptorRazonSocial(String receptorRazonSocial) { this.receptorRazonSocial = receptorRazonSocial; }
    public String getReceptorDireccion() { return receptorDireccion; }
    public void setReceptorDireccion(String receptorDireccion) { this.receptorDireccion = receptorDireccion; }
    public String getReceptorTelefono() { return receptorTelefono; }
    public void setReceptorTelefono(String receptorTelefono) { this.receptorTelefono = receptorTelefono; }
    public String getReceptorEmail() { return receptorEmail; }
    public void setReceptorEmail(String receptorEmail) { this.receptorEmail = receptorEmail; }
    public String getCPaisReceptor() { return cPaisReceptor; }
    public void setCPaisReceptor(String cPaisReceptor) { this.cPaisReceptor = cPaisReceptor; }
    public Integer getTipoReceptor() { return tipoReceptor; }
    public void setTipoReceptor(Integer tipoReceptor) { this.tipoReceptor = tipoReceptor; }
    public Integer getNaturalezaVendedor() { return naturalezaVendedor; }
    public void setNaturalezaVendedor(Integer naturalezaVendedor) { this.naturalezaVendedor = naturalezaVendedor; }

    public Double getTotalOperacion() { return totalOperacion; }
    public void setTotalOperacion(Double totalOperacion) { this.totalOperacion = totalOperacion; }
    public Double getDescuentoGlobal() { return descuentoGlobal; }
    public void setDescuentoGlobal(Double descuentoGlobal) { this.descuentoGlobal = descuentoGlobal; }
    public Double getPorcentajeDescuentoGlobal() { return porcentajeDescuentoGlobal; }
    public void setPorcentajeDescuentoGlobal(Double porcentajeDescuentoGlobal) { this.porcentajeDescuentoGlobal = porcentajeDescuentoGlobal; }
    
    public Double getTotalGravada10() { return totalGravada10; }
    public void setTotalGravada10(Double totalGravada10) { this.totalGravada10 = totalGravada10; }
    public Double getTotalGravada5() { return totalGravada5; }
    public void setTotalGravada5(Double totalGravada5) { this.totalGravada5 = totalGravada5; }
    public Double getTotalExenta() { return totalExenta; }
    public void setTotalExenta(Double totalExenta) { this.totalExenta = totalExenta; }
    public Double getTotalIva10() { return totalIva10; }
    public void setTotalIva10(Double totalIva10) { this.totalIva10 = totalIva10; }
    public Double getTotalIva5() { return totalIva5; }
    public void setTotalIva5(Double totalIva5) { this.totalIva5 = totalIva5; }
    public Double getTotalIva() { return totalIva; }
    public void setTotalIva(Double totalIva) { this.totalIva = totalIva; }

    public String getCdcDocumentoAsociado() { return cdcDocumentoAsociado; }
    public void setCdcDocumentoAsociado(String cdcDocumentoAsociado) { this.cdcDocumentoAsociado = cdcDocumentoAsociado; }
    public Integer getTipoDocumentoAsociado() { return tipoDocumentoAsociado; }
    public void setTipoDocumentoAsociado(Integer tipoDocumentoAsociado) { this.tipoDocumentoAsociado = tipoDocumentoAsociado; }
    public String getMotivoEmision() { return motivoEmision; }
    public void setMotivoEmision(String motivoEmision) { this.motivoEmision = motivoEmision; }

    public java.util.List<ItemDocumento> getItems() { return items; }
    public void setItems(java.util.List<ItemDocumento> items) { this.items = items; }
    public java.util.List<Cuota> getCuotas() { return cuotas; }
    public void setCuotas(java.util.List<Cuota> cuotas) { this.cuotas = cuotas; }
    public Integer getIndicadorPresencia() { return indicadorPresencia; }
    public void setIndicadorPresencia(Integer indicadorPresencia) { this.indicadorPresencia = indicadorPresencia; }
    public ComprasPublicas getComprasPublicas() { return comprasPublicas; }
    public void setComprasPublicas(ComprasPublicas comprasPublicas) { this.comprasPublicas = comprasPublicas; }
    public Transporte getTransporte() { return transporte; }
    public void setTransporte(Transporte transporte) { this.transporte = transporte; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Integer getAmbiente() { return ambiente; }
    public void setAmbiente(Integer ambiente) { this.ambiente = ambiente; }
    public String getFormatoKuDE() { return formatoKuDE; }
    public void setFormatoKuDE(String formatoKuDE) { this.formatoKuDE = formatoKuDE; }
    public String getXmlRespuestaSifen() { return xmlRespuestaSifen; }
    public void setXmlRespuestaSifen(String xmlRespuestaSifen) { this.xmlRespuestaSifen = xmlRespuestaSifen; }
    public String getCodigoEstadoSifen() { return codigoEstadoSifen; }
    public void setCodigoEstadoSifen(String codigoEstadoSifen) { this.codigoEstadoSifen = codigoEstadoSifen; }

    /** Mensaje de descripción oficial de SIFEN (dMsgRes). */
    private String mensajeSifen;
    /** Mensaje amigable mapeado para el usuario final. */
    private String mensajeUsuario;

    public String getMensajeSifen() { return mensajeSifen; }
    public void setMensajeSifen(String mensajeSifen) { this.mensajeSifen = mensajeSifen; }
    public String getMensajeUsuario() { return mensajeUsuario; }
    public void setMensajeUsuario(String mensajeUsuario) { this.mensajeUsuario = mensajeUsuario; }
}
