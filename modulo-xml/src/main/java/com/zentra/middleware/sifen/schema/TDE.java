//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos firmados del DE
 * 			
 * 
 * <p>Clase Java para tDE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tDE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dDVId" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer"/&gt;
 *         &lt;element name="dFecFirma" type="{http://ekuatia.set.gov.py/sifen/xsd}fecHhmmss"/&gt;
 *         &lt;element name="dSisFact"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;maxInclusive value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="gOpeDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCOpeDE"/&gt;
 *         &lt;element name="gTimb" type="{http://ekuatia.set.gov.py/sifen/xsd}tgDTim"/&gt;
 *         &lt;element name="gDatGralOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tgDaGOC"/&gt;
 *         &lt;element name="gDtipDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgDtipDE"/&gt;
 *         &lt;element name="gTotSub" type="{http://ekuatia.set.gov.py/sifen/xsd}tgTotSub" minOccurs="0"/&gt;
 *         &lt;element name="gCamGen" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamGen" minOccurs="0"/&gt;
 *         &lt;element name="gCamDEAsoc" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamDEAsoc" maxOccurs="99" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Id" use="required" type="{http://ekuatia.set.gov.py/sifen/xsd}tCDC" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDE", propOrder = {
    "ddvId",
    "dFecFirma",
    "dSisFact",
    "gOpeDE",
    "gTimb",
    "gDatGralOpe",
    "gDtipDE",
    "gTotSub",
    "gCamGen",
    "gCamDEAsoc"
})
public class TDE {

    @XmlElement(name = "dDVId", required = true)
    protected BigInteger ddvId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dFecFirma;
    protected int dSisFact;
    @XmlElement(required = true)
    protected TgCOpeDE gOpeDE;
    @XmlElement(required = true)
    protected TgDTim gTimb;
    @XmlElement(required = true)
    protected TgDaGOC gDatGralOpe;
    @XmlElement(required = true)
    protected TgDtipDE gDtipDE;
    protected TgTotSub gTotSub;
    protected TgCamGen gCamGen;
    protected List<TgCamDEAsoc> gCamDEAsoc;
    @XmlAttribute(name = "Id", required = true)
    protected String id;

    /**
     * Obtiene el valor de la propiedad ddvId.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDVId() {
        return ddvId;
    }

    /**
     * Define el valor de la propiedad ddvId.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDVId(BigInteger value) {
        this.ddvId = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecFirma.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDFecFirma() {
        return dFecFirma;
    }

    /**
     * Define el valor de la propiedad dFecFirma.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDFecFirma(XMLGregorianCalendar value) {
        this.dFecFirma = value;
    }

    /**
     * Obtiene el valor de la propiedad dSisFact.
     * 
     */
    public int getDSisFact() {
        return dSisFact;
    }

    /**
     * Define el valor de la propiedad dSisFact.
     * 
     */
    public void setDSisFact(int value) {
        this.dSisFact = value;
    }

    /**
     * Obtiene el valor de la propiedad gOpeDE.
     * 
     * @return
     *     possible object is
     *     {@link TgCOpeDE }
     *     
     */
    public TgCOpeDE getGOpeDE() {
        return gOpeDE;
    }

    /**
     * Define el valor de la propiedad gOpeDE.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCOpeDE }
     *     
     */
    public void setGOpeDE(TgCOpeDE value) {
        this.gOpeDE = value;
    }

    /**
     * Obtiene el valor de la propiedad gTimb.
     * 
     * @return
     *     possible object is
     *     {@link TgDTim }
     *     
     */
    public TgDTim getGTimb() {
        return gTimb;
    }

    /**
     * Define el valor de la propiedad gTimb.
     * 
     * @param value
     *     allowed object is
     *     {@link TgDTim }
     *     
     */
    public void setGTimb(TgDTim value) {
        this.gTimb = value;
    }

    /**
     * Obtiene el valor de la propiedad gDatGralOpe.
     * 
     * @return
     *     possible object is
     *     {@link TgDaGOC }
     *     
     */
    public TgDaGOC getGDatGralOpe() {
        return gDatGralOpe;
    }

    /**
     * Define el valor de la propiedad gDatGralOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link TgDaGOC }
     *     
     */
    public void setGDatGralOpe(TgDaGOC value) {
        this.gDatGralOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad gDtipDE.
     * 
     * @return
     *     possible object is
     *     {@link TgDtipDE }
     *     
     */
    public TgDtipDE getGDtipDE() {
        return gDtipDE;
    }

    /**
     * Define el valor de la propiedad gDtipDE.
     * 
     * @param value
     *     allowed object is
     *     {@link TgDtipDE }
     *     
     */
    public void setGDtipDE(TgDtipDE value) {
        this.gDtipDE = value;
    }

    /**
     * Obtiene el valor de la propiedad gTotSub.
     * 
     * @return
     *     possible object is
     *     {@link TgTotSub }
     *     
     */
    public TgTotSub getGTotSub() {
        return gTotSub;
    }

    /**
     * Define el valor de la propiedad gTotSub.
     * 
     * @param value
     *     allowed object is
     *     {@link TgTotSub }
     *     
     */
    public void setGTotSub(TgTotSub value) {
        this.gTotSub = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamGen.
     * 
     * @return
     *     possible object is
     *     {@link TgCamGen }
     *     
     */
    public TgCamGen getGCamGen() {
        return gCamGen;
    }

    /**
     * Define el valor de la propiedad gCamGen.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamGen }
     *     
     */
    public void setGCamGen(TgCamGen value) {
        this.gCamGen = value;
    }

    /**
     * Gets the value of the gCamDEAsoc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gCamDEAsoc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGCamDEAsoc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgCamDEAsoc }
     * 
     * 
     */
    public List<TgCamDEAsoc> getGCamDEAsoc() {
        if (gCamDEAsoc == null) {
            gCamDEAsoc = new ArrayList<TgCamDEAsoc>();
        }
        return this.gCamDEAsoc;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
