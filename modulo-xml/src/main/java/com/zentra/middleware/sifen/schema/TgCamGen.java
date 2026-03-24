//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos complementarios comerciales de uso general
 * 			
 * 
 * <p>Clase Java para tgCamGen complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamGen"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dOrdCompra" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dOrdVta" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dAsiento" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="gCamCarg" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamCarg" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamGen", propOrder = {
    "dOrdCompra",
    "dOrdVta",
    "dAsiento",
    "gCamCarg"
})
public class TgCamGen {

    protected String dOrdCompra;
    protected String dOrdVta;
    protected String dAsiento;
    protected TgCamCarg gCamCarg;

    /**
     * Obtiene el valor de la propiedad dOrdCompra.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOrdCompra() {
        return dOrdCompra;
    }

    /**
     * Define el valor de la propiedad dOrdCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOrdCompra(String value) {
        this.dOrdCompra = value;
    }

    /**
     * Obtiene el valor de la propiedad dOrdVta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOrdVta() {
        return dOrdVta;
    }

    /**
     * Define el valor de la propiedad dOrdVta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOrdVta(String value) {
        this.dOrdVta = value;
    }

    /**
     * Obtiene el valor de la propiedad dAsiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDAsiento() {
        return dAsiento;
    }

    /**
     * Define el valor de la propiedad dAsiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDAsiento(String value) {
        this.dAsiento = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamCarg.
     * 
     * @return
     *     possible object is
     *     {@link TgCamCarg }
     *     
     */
    public TgCamCarg getGCamCarg() {
        return gCamCarg;
    }

    /**
     * Define el valor de la propiedad gCamCarg.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamCarg }
     *     
     */
    public void setGCamCarg(TgCamCarg value) {
        this.gCamCarg = value;
    }

}
