package com.zentra.middleware.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Historial de pagos y suscripciones de un cliente.
 */
@Entity
@Table(name = "suscripciones_historial")
public class SuscripcionHistorial {

    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDateTime fechaPago = LocalDateTime.now();

    private BigDecimal montoPagado;

    private LocalDate periodoDesde;

    private LocalDate periodoHasta;

    private String metodoPago;

    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "usuario_registro_id")
    private Usuario usuarioRegistro;

    public SuscripcionHistorial() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }

    public LocalDate getPeriodoDesde() { return periodoDesde; }
    public void setPeriodoDesde(LocalDate periodoDesde) { this.periodoDesde = periodoDesde; }

    public LocalDate getPeriodoHasta() { return periodoHasta; }
    public void setPeriodoHasta(LocalDate periodoHasta) { this.periodoHasta = periodoHasta; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Usuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(Usuario usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }
}
