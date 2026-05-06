package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Campos de Conformidad de un DTE — Evento Receptor tipo 2 (SIFEN v150).
 *
 * <p>La Conformidad es el acuse de recibo y aceptación comercial del documento por parte del comprador.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgEvConf", propOrder = {
    "mOtEve"
})
public class TgEvConf {

    /**
     * Motivo del evento (Opcional para conformidad).
     */
    protected String mOtEve;

    public String getMOtEve() { return mOtEve; }
    public void setMOtEve(String value) { this.mOtEve = value; }
}
