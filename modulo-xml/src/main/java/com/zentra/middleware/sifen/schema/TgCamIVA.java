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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen el IVA de la operación por ítem
 * 			
 * 
 * <p>Clase Java para tgCamIVA complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamIVA"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iAfecIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tiAfecIVA"/&gt;
 *         &lt;element name="dDesAfecIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesAfecIVA"/&gt;
 *         &lt;element name="dPropIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tPorcDesc8"/&gt;
 *         &lt;element name="dTasaIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tdTasaIVA"/&gt;
 *         &lt;element name="dBasGravIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dLiqIVAItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dBasExe" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamIVA", propOrder = {
    "iAfecIVA",
    "dDesAfecIVA",
    "dPropIVA",
    "dTasaIVA",
    "dBasGravIVA",
    "dLiqIVAItem",
    "dBasExe"
})
public class TgCamIVA {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger iAfecIVA;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesAfecIVA dDesAfecIVA;
    @XmlElement(required = true)
    protected BigDecimal dPropIVA;
    @XmlElement(required = true)
    protected BigInteger dTasaIVA;
    @XmlElement(required = true)
    protected BigDecimal dBasGravIVA;
    @XmlElement(required = true)
    protected BigDecimal dLiqIVAItem;
    @XmlElement(required = true)
    protected BigDecimal dBasExe;

    /**
     * Obtiene el valor de la propiedad iAfecIVA.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIAfecIVA() {
        return iAfecIVA;
    }

    /**
     * Define el valor de la propiedad iAfecIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIAfecIVA(BigInteger value) {
        this.iAfecIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesAfecIVA.
     * 
     * @return
     *     possible object is
     *     {@link TdDesAfecIVA }
     *     
     */
    public TdDesAfecIVA getDDesAfecIVA() {
        return dDesAfecIVA;
    }

    /**
     * Define el valor de la propiedad dDesAfecIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesAfecIVA }
     *     
     */
    public void setDDesAfecIVA(TdDesAfecIVA value) {
        this.dDesAfecIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad dPropIVA.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDPropIVA() {
        return dPropIVA;
    }

    /**
     * Define el valor de la propiedad dPropIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDPropIVA(BigDecimal value) {
        this.dPropIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad dTasaIVA.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDTasaIVA() {
        return dTasaIVA;
    }

    /**
     * Define el valor de la propiedad dTasaIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDTasaIVA(BigInteger value) {
        this.dTasaIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad dBasGravIVA.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDBasGravIVA() {
        return dBasGravIVA;
    }

    /**
     * Define el valor de la propiedad dBasGravIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDBasGravIVA(BigDecimal value) {
        this.dBasGravIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad dLiqIVAItem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDLiqIVAItem() {
        return dLiqIVAItem;
    }

    /**
     * Define el valor de la propiedad dLiqIVAItem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDLiqIVAItem(BigDecimal value) {
        this.dLiqIVAItem = value;
    }

    /**
     * Obtiene el valor de la propiedad dBasExe.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDBasExe() {
        return dBasExe;
    }

    /**
     * Define el valor de la propiedad dBasExe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDBasExe(BigDecimal value) {
        this.dBasExe = value;
    }

}
