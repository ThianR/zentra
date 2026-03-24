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
 * 				Campos que describen el ISC de la operacion por ítem
 * 			
 * 
 * <p>Clase Java para tgCamISC complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamISC"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cCatISC" type="{http://ekuatia.set.gov.py/sifen/xsd}tcCatISC"/&gt;
 *         &lt;element name="dDesCatISC" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesCatISC"/&gt;
 *         &lt;element name="cTasaISC" type="{http://ekuatia.set.gov.py/sifen/xsd}tcTasaISC"/&gt;
 *         &lt;element name="dBaseGravISC" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dLiqISCItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamISC", propOrder = {
    "cCatISC",
    "dDesCatISC",
    "cTasaISC",
    "dBaseGravISC",
    "dLiqISCItem"
})
public class TgCamISC {

    @XmlElement(required = true)
    protected BigInteger cCatISC;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesCatISC dDesCatISC;
    @XmlElement(required = true)
    protected BigInteger cTasaISC;
    @XmlElement(required = true)
    protected BigDecimal dBaseGravISC;
    @XmlElement(required = true)
    protected BigDecimal dLiqISCItem;

    /**
     * Obtiene el valor de la propiedad cCatISC.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCCatISC() {
        return cCatISC;
    }

    /**
     * Define el valor de la propiedad cCatISC.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCCatISC(BigInteger value) {
        this.cCatISC = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCatISC.
     * 
     * @return
     *     possible object is
     *     {@link TdDesCatISC }
     *     
     */
    public TdDesCatISC getDDesCatISC() {
        return dDesCatISC;
    }

    /**
     * Define el valor de la propiedad dDesCatISC.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesCatISC }
     *     
     */
    public void setDDesCatISC(TdDesCatISC value) {
        this.dDesCatISC = value;
    }

    /**
     * Obtiene el valor de la propiedad cTasaISC.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCTasaISC() {
        return cTasaISC;
    }

    /**
     * Define el valor de la propiedad cTasaISC.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCTasaISC(BigInteger value) {
        this.cTasaISC = value;
    }

    /**
     * Obtiene el valor de la propiedad dBaseGravISC.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDBaseGravISC() {
        return dBaseGravISC;
    }

    /**
     * Define el valor de la propiedad dBaseGravISC.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDBaseGravISC(BigDecimal value) {
        this.dBaseGravISC = value;
    }

    /**
     * Obtiene el valor de la propiedad dLiqISCItem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDLiqISCItem() {
        return dLiqISCItem;
    }

    /**
     * Define el valor de la propiedad dLiqISCItem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDLiqISCItem(BigDecimal value) {
        this.dLiqISCItem = value;
    }

}
