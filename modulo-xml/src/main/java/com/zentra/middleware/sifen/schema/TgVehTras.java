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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que identifican el vehículo de traslado de mercaderías
 * 			
 * 
 * <p>Clase Java para tgVehTras complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgVehTras"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dTiVehTras"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="4"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dMarVeh"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dTipIdenVeh"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;pattern value="[1-2]"/&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNroIDVeh" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dAdicVeh" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNroMatVeh" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="7"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNroVuelo" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;length value="6"/&gt;
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
@XmlType(name = "tgVehTras", propOrder = {
    "dTiVehTras",
    "dMarVeh",
    "dTipIdenVeh",
    "dNroIDVeh",
    "dAdicVeh",
    "dNroMatVeh",
    "dNroVuelo"
})
public class TgVehTras {

    @XmlElement(required = true)
    protected String dTiVehTras;
    @XmlElement(required = true)
    protected String dMarVeh;
    @XmlElement(required = true)
    protected BigInteger dTipIdenVeh;
    protected String dNroIDVeh;
    protected String dAdicVeh;
    protected String dNroMatVeh;
    protected String dNroVuelo;

    /**
     * Obtiene el valor de la propiedad dTiVehTras.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDTiVehTras() {
        return dTiVehTras;
    }

    /**
     * Define el valor de la propiedad dTiVehTras.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDTiVehTras(String value) {
        this.dTiVehTras = value;
    }

    /**
     * Obtiene el valor de la propiedad dMarVeh.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDMarVeh() {
        return dMarVeh;
    }

    /**
     * Define el valor de la propiedad dMarVeh.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDMarVeh(String value) {
        this.dMarVeh = value;
    }

    /**
     * Obtiene el valor de la propiedad dTipIdenVeh.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDTipIdenVeh() {
        return dTipIdenVeh;
    }

    /**
     * Define el valor de la propiedad dTipIdenVeh.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDTipIdenVeh(BigInteger value) {
        this.dTipIdenVeh = value;
    }

    /**
     * Obtiene el valor de la propiedad dNroIDVeh.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNroIDVeh() {
        return dNroIDVeh;
    }

    /**
     * Define el valor de la propiedad dNroIDVeh.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNroIDVeh(String value) {
        this.dNroIDVeh = value;
    }

    /**
     * Obtiene el valor de la propiedad dAdicVeh.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDAdicVeh() {
        return dAdicVeh;
    }

    /**
     * Define el valor de la propiedad dAdicVeh.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDAdicVeh(String value) {
        this.dAdicVeh = value;
    }

    /**
     * Obtiene el valor de la propiedad dNroMatVeh.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNroMatVeh() {
        return dNroMatVeh;
    }

    /**
     * Define el valor de la propiedad dNroMatVeh.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNroMatVeh(String value) {
        this.dNroMatVeh = value;
    }

    /**
     * Obtiene el valor de la propiedad dNroVuelo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNroVuelo() {
        return dNroVuelo;
    }

    /**
     * Define el valor de la propiedad dNroVuelo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNroVuelo(String value) {
        this.dNroVuelo = value;
    }

}
