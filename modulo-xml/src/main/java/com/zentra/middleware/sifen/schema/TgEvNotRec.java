package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Campos de Notificación de Recepción de un DTE — Evento Receptor tipo 6 (SIFEN v150).
 *
 * <p>Acuse de recibo técnico del archivo XML por parte del comprador, sin que implique aceptación comercial de la transacción.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgEvNotRec", propOrder = {
    "mOtEve"
})
public class TgEvNotRec {

    /**
     * Motivo del evento. Opcional.
     */
    protected String mOtEve;

    public String getMOtEve() { return mOtEve; }
    public void setMOtEve(String value) { this.mOtEve = value; }
}
