package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Campos de Inutilización de numeración — Evento Emisor tipo 3 (SIFEN v150).
 * Corresponde a tgEvInut definido en Evento_v150.xsd.
 * Manual Técnico v150, Sección 11.1.3.
 *
 * <p>La Inutilización informa a SIFEN que ciertos números de comprobante
 * no serán utilizados, evitando brechas en la numeración correlativa.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgEvInut", propOrder = {
    "dNumTim",
    "iTiDE",
    "dDesTiDE",
    "dEst",
    "dPunExp",
    "dNumIniFolio",
    "dNumFinFolio",
    "mOtEve"
})
public class TgEvInut {

    /** Número de timbrado (8 dígitos) */
    @XmlElement(required = true)
    protected String dNumTim;

    /** Tipo de documento a inutilizar (ej: 1 = Factura Electrónica) */
    @XmlElement(required = true)
    protected BigInteger iTiDE;

    /** Descripción del tipo de documento */
    @XmlElement(required = true)
    protected String dDesTiDE;

    /** Código de establecimiento (3 dígitos, ej: "001") */
    @XmlElement(required = true)
    protected String dEst;

    /** Código de punto de expedición (3 dígitos, ej: "001") */
    @XmlElement(required = true)
    protected String dPunExp;

    /** Número inicial del rango a inutilizar (7 dígitos) */
    @XmlElement(required = true)
    protected BigInteger dNumIniFolio;

    /** Número final del rango a inutilizar (7 dígitos) */
    @XmlElement(required = true)
    protected BigInteger dNumFinFolio;

    /**
     * Motivo de la inutilización.
     * Longitud: mínimo 5, máximo 500 caracteres.
     */
    @XmlElement(required = true)
    protected String mOtEve;

    public String getDNumTim() { return dNumTim; }
    public void setDNumTim(String value) { this.dNumTim = value; }

    public BigInteger getITiDE() { return iTiDE; }
    public void setITiDE(BigInteger value) { this.iTiDE = value; }

    public String getDDesTiDE() { return dDesTiDE; }
    public void setDDesTiDE(String value) { this.dDesTiDE = value; }

    public String getDEst() { return dEst; }
    public void setDEst(String value) { this.dEst = value; }

    public String getDPunExp() { return dPunExp; }
    public void setDPunExp(String value) { this.dPunExp = value; }

    public BigInteger getDNumIniFolio() { return dNumIniFolio; }
    public void setDNumIniFolio(BigInteger value) { this.dNumIniFolio = value; }

    public BigInteger getDNumFinFolio() { return dNumFinFolio; }
    public void setDNumFinFolio(BigInteger value) { this.dNumFinFolio = value; }

    public String getMOtEve() { return mOtEve; }
    public void setMOtEve(String value) { this.mOtEve = value; }
}
