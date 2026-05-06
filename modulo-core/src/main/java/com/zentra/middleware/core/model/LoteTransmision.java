package com.zentra.middleware.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lotes_transmision")
public class LoteTransmision {

    @Id
    private String id = UUID.randomUUID().toString();

    private String numeroTicket;

    @Enumerated(EnumType.STRING)
    private EstadoLote estado = EstadoLote.PENDIENTE;

    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaUltimaConsulta;
    
    private Integer intentosConsulta = 0;

    @ManyToOne
    private Empresa empresa;

    @OneToMany(mappedBy = "loteTransmision")
    private List<DocumentoElectronico> documentos = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }
    public EstadoLote getEstado() { return estado; }
    public void setEstado(EstadoLote estado) { this.estado = estado; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public LocalDateTime getFechaUltimaConsulta() { return fechaUltimaConsulta; }
    public void setFechaUltimaConsulta(LocalDateTime fechaUltimaConsulta) { this.fechaUltimaConsulta = fechaUltimaConsulta; }
    public Integer getIntentosConsulta() { return intentosConsulta; }
    public void setIntentosConsulta(Integer intentosConsulta) { this.intentosConsulta = intentosConsulta; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public List<DocumentoElectronico> getDocumentos() { return documentos; }
    public void setDocumentos(List<DocumentoElectronico> documentos) { this.documentos = documentos; }
}
