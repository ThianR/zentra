//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que componen el Recibo de Dinero Electrónico
 * 			
 * 
 * <p>Clase Java para tgCamRDE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamRDE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iForPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tiForPag"/&gt;
 *         &lt;element name="dDesForPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesForPag"/&gt;
 *         &lt;element name="dNumTrans" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="3"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dConc"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dRucEntFin" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc" minOccurs="0"/&gt;
 *         &lt;element name="dDvEntFin" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer" minOccurs="0"/&gt;
 *         &lt;element name="dNomEntFin" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="4"/&gt;
 *               &lt;maxLength value="255"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dImpPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase4"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamRDE", propOrder = {
    "iForPag",
    "dDesForPag",
    "dNumTrans",
    "dConc",
    "dRucEntFin",
    "dDvEntFin",
    "dNomEntFin",
    "dImpPag"
})
public class TgCamRDE {

    @XmlElement(required = true)
    protected BigInteger iForPag;
    @XmlElement(required = true)
    protected String dDesForPag;
    protected String dNumTrans;
    @XmlElement(required = true)
    protected String dConc;
    protected String dRucEntFin;
    protected BigInteger dDvEntFin;
    protected String dNomEntFin;
    @XmlElement(required = true)
    protected BigDecimal dImpPag;

    /**
     * Obtiene el valor de la propiedad iForPag.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIForPag() {
        return iForPag;
    }

    /**
     * Define el valor de la propiedad iForPag.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIForPag(BigInteger value) {
        this.iForPag = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesForPag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesForPag() {
        return dDesForPag;
    }

    /**
     * Define el valor de la propiedad dDesForPag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesForPag(String value) {
        this.dDesForPag = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumTrans.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumTrans() {
        return dNumTrans;
    }

    /**
     * Define el valor de la propiedad dNumTrans.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumTrans(String value) {
        this.dNumTrans = value;
    }

    /**
     * Obtiene el valor de la propiedad dConc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDConc() {
        return dConc;
    }

    /**
     * Define el valor de la propiedad dConc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDConc(String value) {
        this.dConc = value;
    }

    /**
     * Obtiene el valor de la propiedad dRucEntFin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRucEntFin() {
        return dRucEntFin;
    }

    /**
     * Define el valor de la propiedad dRucEntFin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRucEntFin(String value) {
        this.dRucEntFin = value;
    }

    /**
     * Obtiene el valor de la propiedad dDvEntFin.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDvEntFin() {
        return dDvEntFin;
    }

    /**
     * Define el valor de la propiedad dDvEntFin.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDvEntFin(BigInteger value) {
        this.dDvEntFin = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomEntFin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomEntFin() {
        return dNomEntFin;
    }

    /**
     * Define el valor de la propiedad dNomEntFin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomEntFin(String value) {
        this.dNomEntFin = value;
    }

    /**
     * Obtiene el valor de la propiedad dImpPag.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDImpPag() {
        return dImpPag;
    }

    /**
     * Define el valor de la propiedad dImpPag.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDImpPag(BigDecimal value) {
        this.dImpPag = value;
    }

}
