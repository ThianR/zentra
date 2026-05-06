package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Campos de Disconformidad de un DTE — Evento Receptor tipo 4 (SIFEN v150).
 *
 * <p>La Disconformidad indica que el comprador rechaza comercialmente la transacción (ej: montos incorrectos).</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgEvDisconf", propOrder = {
    "mOtEve"
})
public class TgEvDisconf {

    /**
     * Motivo del evento. Obligatorio (5 a 500 caracteres).
     */
    @XmlElement(required = true)
    protected String mOtEve;

    public String getMOtEve() { return mOtEve; }
    public void setMOtEve(String value) { this.mOtEve = value; }
}
