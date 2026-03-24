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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Grupo del sector de energia electrica
 * 			
 * 
 * <p>Clase Java para tgGrupEner complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgGrupEner"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dNroMed" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dActiv" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;totalDigits value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dCateg" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dLecAnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tLectura" minOccurs="0"/&gt;
 *         &lt;element name="dLecAct" type="{http://ekuatia.set.gov.py/sifen/xsd}tLectura" minOccurs="0"/&gt;
 *         &lt;element name="dConKwh" type="{http://ekuatia.set.gov.py/sifen/xsd}tLectura" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgGrupEner", propOrder = {
    "dNroMed",
    "dActiv",
    "dCateg",
    "dLecAnt",
    "dLecAct",
    "dConKwh"
})
public class TgGrupEner {

    protected String dNroMed;
    protected BigInteger dActiv;
    protected String dCateg;
    protected BigDecimal dLecAnt;
    protected BigDecimal dLecAct;
    protected BigDecimal dConKwh;

    /**
     * Obtiene el valor de la propiedad dNroMed.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNroMed() {
        return dNroMed;
    }

    /**
     * Define el valor de la propiedad dNroMed.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNroMed(String value) {
        this.dNroMed = value;
    }

    /**
     * Obtiene el valor de la propiedad dActiv.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDActiv() {
        return dActiv;
    }

    /**
     * Define el valor de la propiedad dActiv.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDActiv(BigInteger value) {
        this.dActiv = value;
    }

    /**
     * Obtiene el valor de la propiedad dCateg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCateg() {
        return dCateg;
    }

    /**
     * Define el valor de la propiedad dCateg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCateg(String value) {
        this.dCateg = value;
    }

    /**
     * Obtiene el valor de la propiedad dLecAnt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDLecAnt() {
        return dLecAnt;
    }

    /**
     * Define el valor de la propiedad dLecAnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDLecAnt(BigDecimal value) {
        this.dLecAnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dLecAct.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDLecAct() {
        return dLecAct;
    }

    /**
     * Define el valor de la propiedad dLecAct.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDLecAct(BigDecimal value) {
        this.dLecAct = value;
    }

    /**
     * Obtiene el valor de la propiedad dConKwh.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDConKwh() {
        return dConKwh;
    }

    /**
     * Define el valor de la propiedad dConKwh.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDConKwh(BigDecimal value) {
        this.dConKwh = value;
    }

}
