package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Campos de Cancelación de un DTE — Evento Emisor tipo 1 (SIFEN v150).
 * Corresponde a tgEvCan definido en Evento_v150.xsd.
 * Manual Técnico v150, Sección 11.1.1.
 *
 * <p>La Cancelación se aplica a un DTE previamente aprobado por SIFEN.
 * El motivo debe tener entre 5 y 500 caracteres.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgEvCan", propOrder = {
    "mOtEve"
})
public class TgEvCan {

    /**
     * Motivo del evento de cancelación.
     * Longitud: mínimo 5, máximo 500 caracteres.
     */
    @XmlElement(required = true)
    protected String mOtEve;

    public String getMOtEve() { return mOtEve; }
    public void setMOtEve(String value) { this.mOtEve = value; }
}
