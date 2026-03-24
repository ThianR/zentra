//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que componen la factura electronica de exportacion
 * 			
 * 
 * <p>Clase Java para tgCamFEE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamFEE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cFleExp" type="{http://ekuatia.set.gov.py/sifen/xsd}paisType"/&gt;
 *         &lt;element name="dDesFleExp" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesPais"/&gt;
 *         &lt;element name="dPuEmb"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="5"/&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamFEE", propOrder = {
    "cFleExp",
    "dDesFleExp",
    "dPuEmb"
})
public class TgCamFEE {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected PaisType cFleExp;
    @XmlElement(required = true)
    protected String dDesFleExp;
    @XmlElement(required = true)
    protected String dPuEmb;

    /**
     * Obtiene el valor de la propiedad cFleExp.
     * 
     * @return
     *     possible object is
     *     {@link PaisType }
     *     
     */
    public PaisType getCFleExp() {
        return cFleExp;
    }

    /**
     * Define el valor de la propiedad cFleExp.
     * 
     * @param value
     *     allowed object is
     *     {@link PaisType }
     *     
     */
    public void setCFleExp(PaisType value) {
        this.cFleExp = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesFleExp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesFleExp() {
        return dDesFleExp;
    }

    /**
     * Define el valor de la propiedad dDesFleExp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesFleExp(String value) {
        this.dDesFleExp = value;
    }

    /**
     * Obtiene el valor de la propiedad dPuEmb.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDPuEmb() {
        return dPuEmb;
    }

    /**
     * Define el valor de la propiedad dPuEmb.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDPuEmb(String value) {
        this.dPuEmb = value;
    }

}
