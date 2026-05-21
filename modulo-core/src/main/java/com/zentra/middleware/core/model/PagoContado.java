package com.zentra.middleware.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "documento_pagos")
public class PagoContado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "documento_id")
    private DocumentoElectronico documento;

    /**
     * Identificador del tipo de pago según SIFEN (ej. 1=Efectivo, 2=Cheque, 3=Tarjeta Crédito).
     */
    private Integer tipoPago;

    /**
     * Monto abonado con este medio de pago.
     */
    private Double monto;

    /**
     * Indica si se deben usar valores genéricos seguros cuando faltan datos
     * de tarjeta o cheque. Si es true (o null), el generador XML aplica
     * valores por defecto aceptados por SIFEN. Si es false, los datos reales
     * son obligatorios y son validados antes de generar el XML.
     */
    @Column(name = "safe_secure")
    private Boolean safeSecure = true;

    // --- Campos para pago con Tarjeta (tipos 3, 4, 8) ---

    /**
     * Denominación de la tarjeta según catálogo SIFEN:
     * 1=Visa, 2=Mastercard, 3=American Express, 4=Maestro, 5=Panal, 6=Caball, 99=Otro.
     */
    @Column(name = "tarjeta_denominacion")
    private Integer tarjetaDenominacion;

    /**
     * Descripción libre de la denominación. Requerida cuando tarjetaDenominacion=99.
     * Mínimo 4 caracteres.
     */
    @Column(name = "tarjeta_descripcion", length = 100)
    private String tarjetaDescripcion;

    /**
     * Forma de procesamiento del pago con tarjeta según SIFEN:
     * 1=POS, 2=Pago Electrónico.
     */
    @Column(name = "tarjeta_forma_procesamiento")
    private Integer tarjetaFormaProcesamiento;

    // --- Campos para pago con Cheque (tipo 2) ---

    /**
     * Número del cheque. Mínimo 8 caracteres numéricos requeridos por SIFEN.
     */
    @Column(name = "cheque_numero", length = 30)
    private String chequeNumero;

    /**
     * Nombre del banco emisor del cheque. Mínimo 4 caracteres.
     */
    @Column(name = "cheque_banco", length = 50)
    private String chequeBanco;

    public PagoContado() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DocumentoElectronico getDocumento() { return documento; }
    public void setDocumento(DocumentoElectronico documento) { this.documento = documento; }

    public Integer getTipoPago() { return tipoPago; }
    public void setTipoPago(Integer tipoPago) { this.tipoPago = tipoPago; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public Boolean getSafeSecure() { return safeSecure; }
    public void setSafeSecure(Boolean safeSecure) { this.safeSecure = safeSecure; }

    public Integer getTarjetaDenominacion() { return tarjetaDenominacion; }
    public void setTarjetaDenominacion(Integer tarjetaDenominacion) { this.tarjetaDenominacion = tarjetaDenominacion; }

    public String getTarjetaDescripcion() { return tarjetaDescripcion; }
    public void setTarjetaDescripcion(String tarjetaDescripcion) { this.tarjetaDescripcion = tarjetaDescripcion; }

    public Integer getTarjetaFormaProcesamiento() { return tarjetaFormaProcesamiento; }
    public void setTarjetaFormaProcesamiento(Integer tarjetaFormaProcesamiento) { this.tarjetaFormaProcesamiento = tarjetaFormaProcesamiento; }

    public String getChequeNumero() { return chequeNumero; }
    public void setChequeNumero(String chequeNumero) { this.chequeNumero = chequeNumero; }

    public String getChequeBanco() { return chequeBanco; }
    public void setChequeBanco(String chequeBanco) { this.chequeBanco = chequeBanco; }
}

