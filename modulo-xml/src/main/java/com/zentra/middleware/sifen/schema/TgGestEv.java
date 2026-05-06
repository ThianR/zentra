package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Campos del Evento de Gestión SIFEN v150.
 * Corresponde al tipo tgGestEv definido en Evento_v150.xsd.
 * Contiene los datos comunes de todos los tipos de eventos.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgGestEv", propOrder = {
    "iTiEvt",
    "dDesTiEvt",
    "dFecHoraEvt",
    "gEvCan",
    "gEvInut",
    "gEvConf",
    "gEvDisconf",
    "gEvDescon",
    "gEvNotRec"
})
public class TgGestEv {

    /** Tipo de evento: 1=Cancelación, 3=Inutilización */
    @XmlElement(required = true)
    protected BigInteger iTiEvt;

    /** Descripción del tipo de evento */
    @XmlElement(required = true)
    protected String dDesTiEvt;

    /** Fecha y hora del evento en formato ISO 8601 */
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dFecHoraEvt;

    /** Datos de cancelación (solo para iTiEvt = 1) */
    protected TgEvCan gEvCan;

    /** Datos de inutilización (solo para iTiEvt = 3) */
    protected TgEvInut gEvInut;

    /** Datos de conformidad (solo para iTiEvt = 2) */
    protected TgEvConf gEvConf;

    /** Datos de disconformidad (solo para iTiEvt = 4) */
    protected TgEvDisconf gEvDisconf;

    /** Datos de desconocimiento (solo para iTiEvt = 5) */
    protected TgEvDescon gEvDescon;

    /** Datos de notificación de recepción (solo para iTiEvt = 6) */
    protected TgEvNotRec gEvNotRec;

    public BigInteger getITiEvt() { return iTiEvt; }
    public void setITiEvt(BigInteger value) { this.iTiEvt = value; }

    public String getDDesTiEvt() { return dDesTiEvt; }
    public void setDDesTiEvt(String value) { this.dDesTiEvt = value; }

    public XMLGregorianCalendar getDFecHoraEvt() { return dFecHoraEvt; }
    public void setDFecHoraEvt(XMLGregorianCalendar value) { this.dFecHoraEvt = value; }

    public TgEvCan getGEvCan() { return gEvCan; }
    public void setGEvCan(TgEvCan value) { this.gEvCan = value; }

    public TgEvInut getGEvInut() { return gEvInut; }
    public void setGEvInut(TgEvInut value) { this.gEvInut = value; }

    public TgEvConf getGEvConf() { return gEvConf; }
    public void setGEvConf(TgEvConf value) { this.gEvConf = value; }

    public TgEvDisconf getGEvDisconf() { return gEvDisconf; }
    public void setGEvDisconf(TgEvDisconf value) { this.gEvDisconf = value; }

    public TgEvDescon getGEvDescon() { return gEvDescon; }
    public void setGEvDescon(TgEvDescon value) { this.gEvDescon = value; }

    public TgEvNotRec getGEvNotRec() { return gEvNotRec; }
    public void setGEvNotRec(TgEvNotRec value) { this.gEvNotRec = value; }
}
