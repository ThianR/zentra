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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen el transporte de mercaderias
 * 			
 * 
 * <p>Clase Java para tgTransp complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgTransp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTipTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTTrans" minOccurs="0"/&gt;
 *         &lt;element name="dDesTipTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTTrans" minOccurs="0"/&gt;
 *         &lt;element name="iModTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tiModTrans"/&gt;
 *         &lt;element name="dDesModTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesModTrans"/&gt;
 *         &lt;element name="iRespFlete" type="{http://ekuatia.set.gov.py/sifen/xsd}tiRespFlete"/&gt;
 *         &lt;element name="cCondNeg" type="{http://ekuatia.set.gov.py/sifen/xsd}tcCondNeg" minOccurs="0"/&gt;
 *         &lt;element name="dNuManif" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNuDespImp" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;length value="16"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dIniTras" type="{http://ekuatia.set.gov.py/sifen/xsd}tdFeIniS" minOccurs="0"/&gt;
 *         &lt;element name="dFinTras" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion" minOccurs="0"/&gt;
 *         &lt;element name="cPaisDest" type="{http://ekuatia.set.gov.py/sifen/xsd}paisType" minOccurs="0"/&gt;
 *         &lt;element name="dDesPaisDest" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesPais" minOccurs="0"/&gt;
 *         &lt;element name="gCamSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamSal" minOccurs="0"/&gt;
 *         &lt;element name="gCamEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamEnt" maxOccurs="99" minOccurs="0"/&gt;
 *         &lt;element name="gVehTras" type="{http://ekuatia.set.gov.py/sifen/xsd}tgVehTras" maxOccurs="4" minOccurs="0"/&gt;
 *         &lt;element name="gCamTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamTrans" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgTransp", propOrder = {
    "iTipTrans",
    "dDesTipTrans",
    "iModTrans",
    "dDesModTrans",
    "iRespFlete",
    "cCondNeg",
    "dNuManif",
    "dNuDespImp",
    "dIniTras",
    "dFinTras",
    "cPaisDest",
    "dDesPaisDest",
    "gCamSal",
    "gCamEnt",
    "gVehTras",
    "gCamTrans"
})
public class TgTransp {

    protected BigInteger iTipTrans;
    @XmlSchemaType(name = "string")
    protected TdDesTTrans dDesTipTrans;
    @XmlElement(required = true)
    protected BigInteger iModTrans;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesModTrans dDesModTrans;
    @XmlElement(required = true)
    protected BigInteger iRespFlete;
    @XmlSchemaType(name = "string")
    protected TcCondNeg cCondNeg;
    protected String dNuManif;
    protected String dNuDespImp;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dIniTras;
    protected String dFinTras;
    @XmlSchemaType(name = "string")
    protected PaisType cPaisDest;
    protected String dDesPaisDest;
    protected TgCamSal gCamSal;
    protected List<TgCamEnt> gCamEnt;
    protected List<TgVehTras> gVehTras;
    protected TgCamTrans gCamTrans;

    /**
     * Obtiene el valor de la propiedad iTipTrans.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipTrans() {
        return iTipTrans;
    }

    /**
     * Define el valor de la propiedad iTipTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipTrans(BigInteger value) {
        this.iTipTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTipTrans.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTTrans }
     *     
     */
    public TdDesTTrans getDDesTipTrans() {
        return dDesTipTrans;
    }

    /**
     * Define el valor de la propiedad dDesTipTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTTrans }
     *     
     */
    public void setDDesTipTrans(TdDesTTrans value) {
        this.dDesTipTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad iModTrans.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIModTrans() {
        return iModTrans;
    }

    /**
     * Define el valor de la propiedad iModTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIModTrans(BigInteger value) {
        this.iModTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesModTrans.
     * 
     * @return
     *     possible object is
     *     {@link TdDesModTrans }
     *     
     */
    public TdDesModTrans getDDesModTrans() {
        return dDesModTrans;
    }

    /**
     * Define el valor de la propiedad dDesModTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesModTrans }
     *     
     */
    public void setDDesModTrans(TdDesModTrans value) {
        this.dDesModTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad iRespFlete.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIRespFlete() {
        return iRespFlete;
    }

    /**
     * Define el valor de la propiedad iRespFlete.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIRespFlete(BigInteger value) {
        this.iRespFlete = value;
    }

    /**
     * Obtiene el valor de la propiedad cCondNeg.
     * 
     * @return
     *     possible object is
     *     {@link TcCondNeg }
     *     
     */
    public TcCondNeg getCCondNeg() {
        return cCondNeg;
    }

    /**
     * Define el valor de la propiedad cCondNeg.
     * 
     * @param value
     *     allowed object is
     *     {@link TcCondNeg }
     *     
     */
    public void setCCondNeg(TcCondNeg value) {
        this.cCondNeg = value;
    }

    /**
     * Obtiene el valor de la propiedad dNuManif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNuManif() {
        return dNuManif;
    }

    /**
     * Define el valor de la propiedad dNuManif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNuManif(String value) {
        this.dNuManif = value;
    }

    /**
     * Obtiene el valor de la propiedad dNuDespImp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNuDespImp() {
        return dNuDespImp;
    }

    /**
     * Define el valor de la propiedad dNuDespImp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNuDespImp(String value) {
        this.dNuDespImp = value;
    }

    /**
     * Obtiene el valor de la propiedad dIniTras.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDIniTras() {
        return dIniTras;
    }

    /**
     * Define el valor de la propiedad dIniTras.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDIniTras(XMLGregorianCalendar value) {
        this.dIniTras = value;
    }

    /**
     * Obtiene el valor de la propiedad dFinTras.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFinTras() {
        return dFinTras;
    }

    /**
     * Define el valor de la propiedad dFinTras.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFinTras(String value) {
        this.dFinTras = value;
    }

    /**
     * Obtiene el valor de la propiedad cPaisDest.
     * 
     * @return
     *     possible object is
     *     {@link PaisType }
     *     
     */
    public PaisType getCPaisDest() {
        return cPaisDest;
    }

    /**
     * Define el valor de la propiedad cPaisDest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaisType }
     *     
     */
    public void setCPaisDest(PaisType value) {
        this.cPaisDest = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesPaisDest.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesPaisDest() {
        return dDesPaisDest;
    }

    /**
     * Define el valor de la propiedad dDesPaisDest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesPaisDest(String value) {
        this.dDesPaisDest = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamSal.
     * 
     * @return
     *     possible object is
     *     {@link TgCamSal }
     *     
     */
    public TgCamSal getGCamSal() {
        return gCamSal;
    }

    /**
     * Define el valor de la propiedad gCamSal.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamSal }
     *     
     */
    public void setGCamSal(TgCamSal value) {
        this.gCamSal = value;
    }

    /**
     * Gets the value of the gCamEnt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gCamEnt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGCamEnt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgCamEnt }
     * 
     * 
     */
    public List<TgCamEnt> getGCamEnt() {
        if (gCamEnt == null) {
            gCamEnt = new ArrayList<TgCamEnt>();
        }
        return this.gCamEnt;
    }

    /**
     * Gets the value of the gVehTras property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gVehTras property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGVehTras().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgVehTras }
     * 
     * 
     */
    public List<TgVehTras> getGVehTras() {
        if (gVehTras == null) {
            gVehTras = new ArrayList<TgVehTras>();
        }
        return this.gVehTras;
    }

    /**
     * Obtiene el valor de la propiedad gCamTrans.
     * 
     * @return
     *     possible object is
     *     {@link TgCamTrans }
     *     
     */
    public TgCamTrans getGCamTrans() {
        return gCamTrans;
    }

    /**
     * Define el valor de la propiedad gCamTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamTrans }
     *     
     */
    public void setGCamTrans(TgCamTrans value) {
        this.gCamTrans = value;
    }

}
