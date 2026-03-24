package com.zentra.middleware.core.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "documento_cuotas")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "documento_id")
    private DocumentoElectronico documento;

    private Integer numeroCuota;
    private Double monto;
    private LocalDate fechaVencimiento;

    public Cuota() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DocumentoElectronico getDocumento() { return documento; }
    public void setDocumento(DocumentoElectronico documento) { this.documento = documento; }
    public Integer getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(Integer numeroCuota) { this.numeroCuota = numeroCuota; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
