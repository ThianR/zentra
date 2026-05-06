package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Campos de Desconocimiento de un DTE — Evento Receptor tipo 5 (SIFEN v150).
 *
 * <p>El Desconocimiento indica que el comprador afirma no haber participado en la operación declarada por el emisor.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgEvDescon", propOrder = {
    "mOtEve"
})
public class TgEvDescon {

    /**
     * Motivo del evento. Obligatorio (5 a 500 caracteres).
     */
    @XmlElement(required = true)
    protected String mOtEve;

    public String getMOtEve() { return mOtEve; }
    public void setMOtEve(String value) { this.mOtEve = value; }
}
