//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen los descuentos, anticipos y valor total por item
 * 			
 * 
 * <p>Clase Java para tgValorRestaItem complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgValorRestaItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dDescItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dPorcDesIt" type="{http://ekuatia.set.gov.py/sifen/xsd}tPorcDesc8" minOccurs="0"/&gt;
 *         &lt;element name="dDescGloItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dAntPreUniIt" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dAntGloPreUniIt" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dTotOpeItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dTotOpeGs" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgValorRestaItem", propOrder = {
    "dDescItem",
    "dPorcDesIt",
    "dDescGloItem",
    "dAntPreUniIt",
    "dAntGloPreUniIt",
    "dTotOpeItem",
    "dTotOpeGs"
})
public class TgValorRestaItem {

    protected BigDecimal dDescItem;
    protected BigDecimal dPorcDesIt;
    protected BigDecimal dDescGloItem;
    protected BigDecimal dAntPreUniIt;
    protected BigDecimal dAntGloPreUniIt;
    @XmlElement(required = true)
    protected BigDecimal dTotOpeItem;
    protected BigDecimal dTotOpeGs;

    /**
     * Obtiene el valor de la propiedad dDescItem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDDescItem() {
        return dDescItem;
    }

    /**
     * Define el valor de la propiedad dDescItem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDDescItem(BigDecimal value) {
        this.dDescItem = value;
    }

    /**
     * Obtiene el valor de la propiedad dPorcDesIt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDPorcDesIt() {
        return dPorcDesIt;
    }

    /**
     * Define el valor de la propiedad dPorcDesIt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDPorcDesIt(BigDecimal value) {
        this.dPorcDesIt = value;
    }

    /**
     * Obtiene el valor de la propiedad dDescGloItem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDDescGloItem() {
        return dDescGloItem;
    }

    /**
     * Define el valor de la propiedad dDescGloItem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDDescGloItem(BigDecimal value) {
        this.dDescGloItem = value;
    }

    /**
     * Obtiene el valor de la propiedad dAntPreUniIt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDAntPreUniIt() {
        return dAntPreUniIt;
    }

    /**
     * Define el valor de la propiedad dAntPreUniIt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDAntPreUniIt(BigDecimal value) {
        this.dAntPreUniIt = value;
    }

    /**
     * Obtiene el valor de la propiedad dAntGloPreUniIt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDAntGloPreUniIt() {
        return dAntGloPreUniIt;
    }

    /**
     * Define el valor de la propiedad dAntGloPreUniIt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDAntGloPreUniIt(BigDecimal value) {
        this.dAntGloPreUniIt = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotOpeItem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotOpeItem() {
        return dTotOpeItem;
    }

    /**
     * Define el valor de la propiedad dTotOpeItem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotOpeItem(BigDecimal value) {
        this.dTotOpeItem = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotOpeGs.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotOpeGs() {
        return dTotOpeGs;
    }

    /**
     * Define el valor de la propiedad dTotOpeGs.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotOpeGs(BigDecimal value) {
        this.dTotOpeGs = value;
    }

}
