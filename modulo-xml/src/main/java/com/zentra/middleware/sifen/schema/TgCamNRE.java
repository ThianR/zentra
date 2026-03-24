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
 * 				Campos que componen la nora de remision electronica
 * 			
 * 
 * <p>Clase Java para tgCamNRE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamNRE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iMotEmiNR" type="{http://ekuatia.set.gov.py/sifen/xsd}tiMotivTras"/&gt;
 *         &lt;element name="dDesMotEmiNR" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDMotivTras"/&gt;
 *         &lt;element name="iRespEmiNR" type="{http://ekuatia.set.gov.py/sifen/xsd}tiRespEmiNR"/&gt;
 *         &lt;element name="dDesRespEmiNR" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesRespEmiNR"/&gt;
 *         &lt;element name="dKmR"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *               &lt;maxInclusive value="99999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dFecEm" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion" minOccurs="0"/&gt;
 *         &lt;element name="cPreFle" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamNRE", propOrder = {
    "iMotEmiNR",
    "dDesMotEmiNR",
    "iRespEmiNR",
    "dDesRespEmiNR",
    "dKmR",
    "dFecEm",
    "cPreFle"
})
public class TgCamNRE {

    @XmlElement(required = true)
    protected BigInteger iMotEmiNR;
    @XmlElement(required = true)
    protected String dDesMotEmiNR;
    @XmlElement(required = true)
    protected BigInteger iRespEmiNR;
    @XmlElement(required = true)
    protected String dDesRespEmiNR;
    protected int dKmR;
    protected String dFecEm;
    protected BigDecimal cPreFle;

    /**
     * Obtiene el valor de la propiedad iMotEmiNR.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIMotEmiNR() {
        return iMotEmiNR;
    }

    /**
     * Define el valor de la propiedad iMotEmiNR.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIMotEmiNR(BigInteger value) {
        this.iMotEmiNR = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesMotEmiNR.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesMotEmiNR() {
        return dDesMotEmiNR;
    }

    /**
     * Define el valor de la propiedad dDesMotEmiNR.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesMotEmiNR(String value) {
        this.dDesMotEmiNR = value;
    }

    /**
     * Obtiene el valor de la propiedad iRespEmiNR.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIRespEmiNR() {
        return iRespEmiNR;
    }

    /**
     * Define el valor de la propiedad iRespEmiNR.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIRespEmiNR(BigInteger value) {
        this.iRespEmiNR = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesRespEmiNR.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesRespEmiNR() {
        return dDesRespEmiNR;
    }

    /**
     * Define el valor de la propiedad dDesRespEmiNR.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesRespEmiNR(String value) {
        this.dDesRespEmiNR = value;
    }

    /**
     * Obtiene el valor de la propiedad dKmR.
     * 
     */
    public int getDKmR() {
        return dKmR;
    }

    /**
     * Define el valor de la propiedad dKmR.
     * 
     */
    public void setDKmR(int value) {
        this.dKmR = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecEm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFecEm() {
        return dFecEm;
    }

    /**
     * Define el valor de la propiedad dFecEm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFecEm(String value) {
        this.dFecEm = value;
    }

    /**
     * Obtiene el valor de la propiedad cPreFle.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCPreFle() {
        return cPreFle;
    }

    /**
     * Define el valor de la propiedad cPreFle.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCPreFle(BigDecimal value) {
        this.cPreFle = value;
    }

}
