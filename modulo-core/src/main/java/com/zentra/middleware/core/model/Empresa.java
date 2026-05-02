package com.zentra.middleware.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zentra.middleware.core.enums.Ambiente;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;
import java.util.UUID;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    private String id = UUID.randomUUID().toString();

    private String ruc;
    private String razonSocial;
    private String dv; // Digito verificador del RUC
    
    private String codEstablecimiento; // Ej: 001
    private String puntoExpedicion;      // Ej: 001
    private String timbrado; // Timbrado asignado por la SET
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate fechaInicioTimbrado; // Fecha de inicio de vigencia del timbrado
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate fechaVencimientoTimbrado; // Fecha de fin de vigencia del timbrado
    
    // Datos de Localización SIFEN
    private String direccion;
    private String numeroCasa = "0";
    private Integer codDepartamento = 1; // Distrito Capital por defecto
    private String departamento = "CAPITAL";
    private Integer codDistrito = 1;
    private String distrito = "ASUNCION";
    private Integer codCiudad = 1;
    private String ciudad = "ASUNCION";
    
    // Contacto
    private String telefono = "021000000";
    private String email = "emisor@example.com";
    
    // Fiscal
    private String codActividadEconomica; // Código de actividad según SET (ej: 45301)
    private String actividadEconomica = "Venta de Mercaderías y Servicios";
    private Integer tipoContribuyente = 2; // Jurídica por defecto
    
    // Ruta al certificado P12 (simplificado para el MVP - Deprecated en Plan B)
    private String rutaCertificado;
    private String passwordCertificado;

    // SIFEN: Seguridad, KuDE y Autenticación (Plan B Multi-Tenant)
    private String idCsc = "0001";
    private String valorCsc;
    @Convert(converter = com.zentra.middleware.core.converter.AmbienteConverter.class)
    private Ambiente ambiente = Ambiente.TEST;
    
    @jakarta.persistence.Lob
    private byte[] certificadoFisico; // Archivo P12/PFX almacenado internamente
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate fechaVencimientoCertificado;
    private String aliasCertificado;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String logoBase64;

    public Empresa() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getDv() { return dv; }
    public void setDv(String dv) { this.dv = dv; }
    public String getCodEstablecimiento() { return codEstablecimiento; }
    public void setCodEstablecimiento(String codEstablecimiento) { this.codEstablecimiento = codEstablecimiento; }
    public String getPuntoExpedicion() { return puntoExpedicion; }
    public void setPuntoExpedicion(String puntoExpedicion) { this.puntoExpedicion = puntoExpedicion; }
    public String getTimbrado() { return timbrado; }
    public void setTimbrado(String timbrado) { this.timbrado = timbrado; }

    public java.time.LocalDate getFechaInicioTimbrado() { return fechaInicioTimbrado; }
    public void setFechaInicioTimbrado(java.time.LocalDate fechaInicioTimbrado) { this.fechaInicioTimbrado = fechaInicioTimbrado; }

    public java.time.LocalDate getFechaVencimientoTimbrado() { return fechaVencimientoTimbrado; }
    public void setFechaVencimientoTimbrado(java.time.LocalDate fechaVencimientoTimbrado) { this.fechaVencimientoTimbrado = fechaVencimientoTimbrado; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(String numeroCasa) { this.numeroCasa = numeroCasa; }
    public Integer getCodDepartamento() { return codDepartamento; }
    public void setCodDepartamento(Integer codDepartamento) { this.codDepartamento = codDepartamento; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public Integer getCodDistrito() { return codDistrito; }
    public void setCodDistrito(Integer codDistrito) { this.codDistrito = codDistrito; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public Integer getCodCiudad() { return codCiudad; }
    public void setCodCiudad(Integer codCiudad) { this.codCiudad = codCiudad; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCodActividadEconomica() { return codActividadEconomica; }
    public void setCodActividadEconomica(String codActividadEconomica) { this.codActividadEconomica = codActividadEconomica; }

    public String getActividadEconomica() { return actividadEconomica; }
    public void setActividadEconomica(String actividadEconomica) { this.actividadEconomica = actividadEconomica; }
    public Integer getTipoContribuyente() { return tipoContribuyente; }
    public void setTipoContribuyente(Integer tipoContribuyente) { this.tipoContribuyente = tipoContribuyente; }

    public String getRutaCertificado() { return rutaCertificado; }
    public void setRutaCertificado(String rutaCertificado) { this.rutaCertificado = rutaCertificado; }
    public String getPasswordCertificado() { return passwordCertificado; }
    public void setPasswordCertificado(String passwordCertificado) { this.passwordCertificado = passwordCertificado; }

    public String getIdCsc() { return idCsc; }
    public void setIdCsc(String idCsc) { this.idCsc = idCsc; }
    public String getValorCsc() { return valorCsc; }
    public void setValorCsc(String valorCsc) { this.valorCsc = valorCsc; }
    public Ambiente getAmbiente() { return ambiente; }
    public void setAmbiente(Ambiente ambiente) { this.ambiente = ambiente; }
    public byte[] getCertificadoFisico() { return certificadoFisico; }
    public void setCertificadoFisico(byte[] certificadoFisico) { this.certificadoFisico = certificadoFisico; }
    public java.time.LocalDate getFechaVencimientoCertificado() { return fechaVencimientoCertificado; }
    public void setFechaVencimientoCertificado(java.time.LocalDate fechaVencimientoCertificado) { this.fechaVencimientoCertificado = fechaVencimientoCertificado; }
    public String getAliasCertificado() { return aliasCertificado; }
    public void setAliasCertificado(String aliasCertificado) { this.aliasCertificado = aliasCertificado; }

    public String getLogoBase64() { return logoBase64; }
    public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }
}
