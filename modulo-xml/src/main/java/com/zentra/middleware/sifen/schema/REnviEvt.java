package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor del mensaje de envío de eventos al WS siRecepEvento de SIFEN v150.
 * Corresponde a rEnviEvt definido en siRecepEvento_v150.xsd.
 * Manual Técnico v150, Sección 9.5.
 *
 * <p>Este es el elemento raíz que se embebe dentro del SOAP Body al llamar
 * al servicio {@code siRecepEvento}. Puede transportar hasta 250 eventos.</p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rEnviEvt", propOrder = {
    "dId",
    "dFecFirma",
    "dRucEm",
    "dDVEmi",
    "gGroupGestE"
})
@XmlRootElement(name = "rEnviEvt")
public class REnviEvt {

    /** Identificador correlativo de la transmisión (1..999) */
    @XmlElement(required = true)
    protected BigInteger dId;

    /** Fecha y hora de firma del envío */
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dFecFirma;

    /** RUC del emisor que envía el evento (sin DV) */
    @XmlElement(required = true)
    protected String dRucEm;

    /** Dígito verificador del RUC del emisor */
    @XmlElement(required = true)
    protected BigInteger dDVEmi;

    /** Lista de grupos de eventos (máximo 250 por envío) */
    @XmlElement(required = true, name = "gGroupGestE")
    protected List<TgGroupGestE> gGroupGestE;

    public BigInteger getDId() { return dId; }
    public void setDId(BigInteger value) { this.dId = value; }

    public XMLGregorianCalendar getDFecFirma() { return dFecFirma; }
    public void setDFecFirma(XMLGregorianCalendar value) { this.dFecFirma = value; }

    public String getDRucEm() { return dRucEm; }
    public void setDRucEm(String value) { this.dRucEm = value; }

    public BigInteger getDDVEmi() { return dDVEmi; }
    public void setDDVEmi(BigInteger value) { this.dDVEmi = value; }

    public List<TgGroupGestE> getGGroupGestE() {
        if (gGroupGestE == null) {
            gGroupGestE = new ArrayList<>();
        }
        return gGroupGestE;
    }
}
