//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que identifican al transportista (persona física o jurídica)
 * 			
 * 
 * <p>Clase Java para tgCamTrans complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamTrans"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iNatTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tiNatRec"/&gt;
 *         &lt;element name="dNomTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}dNomRazSocial"/&gt;
 *         &lt;element name="dRucTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc" minOccurs="0"/&gt;
 *         &lt;element name="dDVTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer" minOccurs="0"/&gt;
 *         &lt;element name="iTipIDTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipDoc" minOccurs="0"/&gt;
 *         &lt;element name="dDTipIDTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDtipDoc" minOccurs="0"/&gt;
 *         &lt;element name="dNumIDTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumDocId" minOccurs="0"/&gt;
 *         &lt;element name="cNacTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}paisType" minOccurs="0"/&gt;
 *         &lt;element name="dDesNacTrans" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesPais" minOccurs="0"/&gt;
 *         &lt;element name="dNumIDChof" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumDocId"/&gt;
 *         &lt;element name="dNomChof" type="{http://ekuatia.set.gov.py/sifen/xsd}dNomRazSocial"/&gt;
 *         &lt;element name="dDomFisc"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="150"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dDirChof" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec"/&gt;
 *         &lt;element name="dNombAg" type="{http://ekuatia.set.gov.py/sifen/xsd}dNomRazSocial" minOccurs="0"/&gt;
 *         &lt;element name="dRucAg" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc" minOccurs="0"/&gt;
 *         &lt;element name="dDVAg" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer" minOccurs="0"/&gt;
 *         &lt;element name="dDirAge" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamTrans", propOrder = {
    "iNatTrans",
    "dNomTrans",
    "dRucTrans",
    "ddvTrans",
    "iTipIDTrans",
    "ddTipIDTrans",
    "dNumIDTrans",
    "cNacTrans",
    "dDesNacTrans",
    "dNumIDChof",
    "dNomChof",
    "dDomFisc",
    "dDirChof",
    "dNombAg",
    "dRucAg",
    "ddvAg",
    "dDirAge"
})
public class TgCamTrans {

    @XmlElement(required = true)
    protected BigInteger iNatTrans;
    @XmlElement(required = true)
    protected String dNomTrans;
    protected String dRucTrans;
    @XmlElement(name = "dDVTrans")
    protected BigInteger ddvTrans;
    protected BigInteger iTipIDTrans;
    @XmlElement(name = "dDTipIDTrans")
    @XmlSchemaType(name = "string")
    protected TdDtipDoc ddTipIDTrans;
    protected String dNumIDTrans;
    @XmlSchemaType(name = "string")
    protected PaisType cNacTrans;
    protected String dDesNacTrans;
    @XmlElement(required = true)
    protected String dNumIDChof;
    @XmlElement(required = true)
    protected String dNomChof;
    @XmlElement(required = true)
    protected String dDomFisc;
    @XmlElement(required = true)
    protected String dDirChof;
    protected String dNombAg;
    protected String dRucAg;
    @XmlElement(name = "dDVAg")
    protected BigInteger ddvAg;
    protected String dDirAge;

    /**
     * Obtiene el valor de la propiedad iNatTrans.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getINatTrans() {
        return iNatTrans;
    }

    /**
     * Define el valor de la propiedad iNatTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setINatTrans(BigInteger value) {
        this.iNatTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomTrans.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomTrans() {
        return dNomTrans;
    }

    /**
     * Define el valor de la propiedad dNomTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomTrans(String value) {
        this.dNomTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dRucTrans.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRucTrans() {
        return dRucTrans;
    }

    /**
     * Define el valor de la propiedad dRucTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRucTrans(String value) {
        this.dRucTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad ddvTrans.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDVTrans() {
        return ddvTrans;
    }

    /**
     * Define el valor de la propiedad ddvTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDVTrans(BigInteger value) {
        this.ddvTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad iTipIDTrans.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipIDTrans() {
        return iTipIDTrans;
    }

    /**
     * Define el valor de la propiedad iTipIDTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipIDTrans(BigInteger value) {
        this.iTipIDTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad ddTipIDTrans.
     * 
     * @return
     *     possible object is
     *     {@link TdDtipDoc }
     *     
     */
    public TdDtipDoc getDDTipIDTrans() {
        return ddTipIDTrans;
    }

    /**
     * Define el valor de la propiedad ddTipIDTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDtipDoc }
     *     
     */
    public void setDDTipIDTrans(TdDtipDoc value) {
        this.ddTipIDTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumIDTrans.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumIDTrans() {
        return dNumIDTrans;
    }

    /**
     * Define el valor de la propiedad dNumIDTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumIDTrans(String value) {
        this.dNumIDTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad cNacTrans.
     * 
     * @return
     *     possible object is
     *     {@link PaisType }
     *     
     */
    public PaisType getCNacTrans() {
        return cNacTrans;
    }

    /**
     * Define el valor de la propiedad cNacTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link PaisType }
     *     
     */
    public void setCNacTrans(PaisType value) {
        this.cNacTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesNacTrans.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesNacTrans() {
        return dDesNacTrans;
    }

    /**
     * Define el valor de la propiedad dDesNacTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesNacTrans(String value) {
        this.dDesNacTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumIDChof.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumIDChof() {
        return dNumIDChof;
    }

    /**
     * Define el valor de la propiedad dNumIDChof.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumIDChof(String value) {
        this.dNumIDChof = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomChof.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomChof() {
        return dNomChof;
    }

    /**
     * Define el valor de la propiedad dNomChof.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomChof(String value) {
        this.dNomChof = value;
    }

    /**
     * Obtiene el valor de la propiedad dDomFisc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDomFisc() {
        return dDomFisc;
    }

    /**
     * Define el valor de la propiedad dDomFisc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDomFisc(String value) {
        this.dDomFisc = value;
    }

    /**
     * Obtiene el valor de la propiedad dDirChof.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirChof() {
        return dDirChof;
    }

    /**
     * Define el valor de la propiedad dDirChof.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirChof(String value) {
        this.dDirChof = value;
    }

    /**
     * Obtiene el valor de la propiedad dNombAg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNombAg() {
        return dNombAg;
    }

    /**
     * Define el valor de la propiedad dNombAg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNombAg(String value) {
        this.dNombAg = value;
    }

    /**
     * Obtiene el valor de la propiedad dRucAg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRucAg() {
        return dRucAg;
    }

    /**
     * Define el valor de la propiedad dRucAg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRucAg(String value) {
        this.dRucAg = value;
    }

    /**
     * Obtiene el valor de la propiedad ddvAg.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDVAg() {
        return ddvAg;
    }

    /**
     * Define el valor de la propiedad ddvAg.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDVAg(BigInteger value) {
        this.ddvAg = value;
    }

    /**
     * Obtiene el valor de la propiedad dDirAge.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirAge() {
        return dDirAge;
    }

    /**
     * Define el valor de la propiedad dDirAge.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirAge(String value) {
        this.dDirAge = value;
    }

}
