package com.zentra.middleware.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    
    // Datos de Localización SIFEN
    private String direccion;
    private String numeroCasa = "0";
    private Integer codDepartamento = 1; // Distrito Capital por defecto
    private String departamendo = "CAPITAL";
    private Integer codDistrito = 1;
    private String distrito = "ASUNCION";
    private Integer codCiudad = 1;
    private String ciudad = "ASUNCION";
    
    // Contacto
    private String telefono = "021000000";
    private String email = "emisor@example.com";
    
    // Fiscal
    private String actividadEconomica = "Venta de Mercaderías y Servicios";
    private Integer tipoContribuyente = 2; // Jurídica por defecto
    
    // Ruta al certificado P12 (simplificado para el MVP)
    private String rutaCertificado;
    private String passwordCertificado;

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

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(String numeroCasa) { this.numeroCasa = numeroCasa; }
    public Integer getCodDepartamento() { return codDepartamento; }
    public void setCodDepartamento(Integer codDepartamento) { this.codDepartamento = codDepartamento; }
    public String getDepartamendo() { return departamendo; }
    public void setDepartamendo(String departamendo) { this.departamendo = departamendo; }
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
    public String getActividadEconomica() { return actividadEconomica; }
    public void setActividadEconomica(String actividadEconomica) { this.actividadEconomica = actividadEconomica; }
    public Integer getTipoContribuyente() { return tipoContribuyente; }
    public void setTipoContribuyente(Integer tipoContribuyente) { this.tipoContribuyente = tipoContribuyente; }

    public String getRutaCertificado() { return rutaCertificado; }
    public void setRutaCertificado(String rutaCertificado) { this.rutaCertificado = rutaCertificado; }
    public String getPasswordCertificado() { return passwordCertificado; }
    public void setPasswordCertificado(String passwordCertificado) { this.passwordCertificado = passwordCertificado; }
}
