package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Grupo principal del Evento Emisor SIFEN v150.
 * Corresponde a tgGroupGestE definido en Evento_v150.xsd.
 *
 * El atributo {@code Id} es el identificador que usa la firma XMLDSig
 * para referenciar el nodo a firmar mediante {@code Reference URI="#<Id>"}.
 * Se construye con el CDC del DTE afectado (para Cancelación) o con
 * un identificador único para Inutilización.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgGroupGestE", propOrder = {
    "dId",
    "gGestEv"
})
public class TgGroupGestE {

    /** CDC del DTE afectado (44 dígitos), o ID único para inutilización */
    @XmlElement(required = true)
    protected String dId;

    /** Datos del evento */
    @XmlElement(required = true)
    protected TgGestEv gGestEv;

    /**
     * Atributo Id — requerido por XMLDSig para referenciar el nodo a firmar.
     * SIFEN firma sobre este nodo con Reference URI="#<Id>".
     */
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    public String getDId() { return dId; }
    public void setDId(String value) { this.dId = value; }

    public TgGestEv getGGestEv() { return gGestEv; }
    public void setGGestEv(TgGestEv value) { this.gGestEv = value; }

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
}
