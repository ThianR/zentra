//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos de datos del timbrado
 * 			
 * 
 * <p>Clase Java para tgDTim complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgDTim"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTiDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTiDE"/&gt;
 *         &lt;element name="dDesTiDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTiDE"/&gt;
 *         &lt;element name="dNumTim" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumTim"/&gt;
 *         &lt;element name="dEst" type="{http://ekuatia.set.gov.py/sifen/xsd}tdEst"/&gt;
 *         &lt;element name="dPunExp" type="{http://ekuatia.set.gov.py/sifen/xsd}tdPunExp"/&gt;
 *         &lt;element name="dNumDoc" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumDoc"/&gt;
 *         &lt;element name="dSerieNum" type="{http://ekuatia.set.gov.py/sifen/xsd}tdSerieNum" minOccurs="0"/&gt;
 *         &lt;element name="dFeIniT" type="{http://ekuatia.set.gov.py/sifen/xsd}tdFeIniT"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgDTim", propOrder = {
    "iTiDE",
    "dDesTiDE",
    "dNumTim",
    "dEst",
    "dPunExp",
    "dNumDoc",
    "dSerieNum",
    "dFeIniT"
})
public class TgDTim {

    @XmlElement(required = true)
    protected BigInteger iTiDE;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesTiDE dDesTiDE;
    @XmlElement(required = true)
    protected String dNumTim;
    @XmlElement(required = true)
    protected String dEst;
    @XmlElement(required = true)
    protected String dPunExp;
    @XmlElement(required = true)
    protected String dNumDoc;
    protected String dSerieNum;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dFeIniT;

    /**
     * Obtiene el valor de la propiedad iTiDE.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITiDE() {
        return iTiDE;
    }

    /**
     * Define el valor de la propiedad iTiDE.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITiDE(BigInteger value) {
        this.iTiDE = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTiDE.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTiDE }
     *     
     */
    public TdDesTiDE getDDesTiDE() {
        return dDesTiDE;
    }

    /**
     * Define el valor de la propiedad dDesTiDE.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTiDE }
     *     
     */
    public void setDDesTiDE(TdDesTiDE value) {
        this.dDesTiDE = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumTim.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumTim() {
        return dNumTim;
    }

    /**
     * Define el valor de la propiedad dNumTim.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumTim(String value) {
        this.dNumTim = value;
    }

    /**
     * Obtiene el valor de la propiedad dEst.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEst() {
        return dEst;
    }

    /**
     * Define el valor de la propiedad dEst.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEst(String value) {
        this.dEst = value;
    }

    /**
     * Obtiene el valor de la propiedad dPunExp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDPunExp() {
        return dPunExp;
    }

    /**
     * Define el valor de la propiedad dPunExp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDPunExp(String value) {
        this.dPunExp = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumDoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumDoc() {
        return dNumDoc;
    }

    /**
     * Define el valor de la propiedad dNumDoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumDoc(String value) {
        this.dNumDoc = value;
    }

    /**
     * Obtiene el valor de la propiedad dSerieNum.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDSerieNum() {
        return dSerieNum;
    }

    /**
     * Define el valor de la propiedad dSerieNum.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDSerieNum(String value) {
        this.dSerieNum = value;
    }

    /**
     * Obtiene el valor de la propiedad dFeIniT.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDFeIniT() {
        return dFeIniT;
    }

    /**
     * Define el valor de la propiedad dFeIniT.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDFeIniT(XMLGregorianCalendar value) {
        this.dFeIniT = value;
    }

}
