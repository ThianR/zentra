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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Grupo del sector de supermercados
 * 			
 * 
 * <p>Clase Java para tgGrupSup complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgGrupSup"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dNomCaj" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dEfectivo" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase4" minOccurs="0"/&gt;
 *         &lt;element name="dVuelto" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase6" minOccurs="0"/&gt;
 *         &lt;element name="dDonac" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase6" minOccurs="0"/&gt;
 *         &lt;element name="dDesDonac" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="20"/&gt;
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
@XmlType(name = "tgGrupSup", propOrder = {
    "dNomCaj",
    "dEfectivo",
    "dVuelto",
    "dDonac",
    "dDesDonac"
})
public class TgGrupSup {

    protected String dNomCaj;
    protected BigDecimal dEfectivo;
    protected BigDecimal dVuelto;
    protected BigDecimal dDonac;
    protected String dDesDonac;

    /**
     * Obtiene el valor de la propiedad dNomCaj.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomCaj() {
        return dNomCaj;
    }

    /**
     * Define el valor de la propiedad dNomCaj.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomCaj(String value) {
        this.dNomCaj = value;
    }

    /**
     * Obtiene el valor de la propiedad dEfectivo.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDEfectivo() {
        return dEfectivo;
    }

    /**
     * Define el valor de la propiedad dEfectivo.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDEfectivo(BigDecimal value) {
        this.dEfectivo = value;
    }

    /**
     * Obtiene el valor de la propiedad dVuelto.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDVuelto() {
        return dVuelto;
    }

    /**
     * Define el valor de la propiedad dVuelto.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDVuelto(BigDecimal value) {
        this.dVuelto = value;
    }

    /**
     * Obtiene el valor de la propiedad dDonac.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDDonac() {
        return dDonac;
    }

    /**
     * Define el valor de la propiedad dDonac.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDDonac(BigDecimal value) {
        this.dDonac = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDonac.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDonac() {
        return dDesDonac;
    }

    /**
     * Define el valor de la propiedad dDesDonac.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDonac(String value) {
        this.dDesDonac = value;
    }

}
