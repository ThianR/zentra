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
 * 				Campos que describen el precio, tipo de cambio y valor total de la
 * 				operación por ítem
 * 			
 * 
 * <p>Clase Java para tgValorItem complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgValorItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dPUniProSer" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dTiCamIt" type="{http://ekuatia.set.gov.py/sifen/xsd}tTipoCambioBase" minOccurs="0"/&gt;
 *         &lt;element name="dTotBruOpeItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="gValorRestaItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tgValorRestaItem"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgValorItem", propOrder = {
    "dpUniProSer",
    "dTiCamIt",
    "dTotBruOpeItem",
    "gValorRestaItem"
})
public class TgValorItem {

    @XmlElement(name = "dPUniProSer", required = true)
    protected BigDecimal dpUniProSer;
    protected BigDecimal dTiCamIt;
    @XmlElement(required = true)
    protected BigDecimal dTotBruOpeItem;
    @XmlElement(required = true)
    protected TgValorRestaItem gValorRestaItem;

    /**
     * Obtiene el valor de la propiedad dpUniProSer.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDPUniProSer() {
        return dpUniProSer;
    }

    /**
     * Define el valor de la propiedad dpUniProSer.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDPUniProSer(BigDecimal value) {
        this.dpUniProSer = value;
    }

    /**
     * Obtiene el valor de la propiedad dTiCamIt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTiCamIt() {
        return dTiCamIt;
    }

    /**
     * Define el valor de la propiedad dTiCamIt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTiCamIt(BigDecimal value) {
        this.dTiCamIt = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotBruOpeItem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotBruOpeItem() {
        return dTotBruOpeItem;
    }

    /**
     * Define el valor de la propiedad dTotBruOpeItem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotBruOpeItem(BigDecimal value) {
        this.dTotBruOpeItem = value;
    }

    /**
     * Obtiene el valor de la propiedad gValorRestaItem.
     * 
     * @return
     *     possible object is
     *     {@link TgValorRestaItem }
     *     
     */
    public TgValorRestaItem getGValorRestaItem() {
        return gValorRestaItem;
    }

    /**
     * Define el valor de la propiedad gValorRestaItem.
     * 
     * @param value
     *     allowed object is
     *     {@link TgValorRestaItem }
     *     
     */
    public void setGValorRestaItem(TgValorRestaItem value) {
        this.gValorRestaItem = value;
    }

}
