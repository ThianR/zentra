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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen informaciones de compras publicas
 * 			
 * 
 * <p>Clase Java para tgCompPub complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCompPub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dModCont" type="{http://ekuatia.set.gov.py/sifen/xsd}tdModCont"/&gt;
 *         &lt;element name="dEntCont" type="{http://ekuatia.set.gov.py/sifen/xsd}tdEntCont"/&gt;
 *         &lt;element name="dAnoCont" type="{http://ekuatia.set.gov.py/sifen/xsd}tdAnoCont"/&gt;
 *         &lt;element name="dSecCont" type="{http://ekuatia.set.gov.py/sifen/xsd}tdSecCont"/&gt;
 *         &lt;element name="dFeCodCont" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCompPub", propOrder = {
    "dModCont",
    "dEntCont",
    "dAnoCont",
    "dSecCont",
    "dFeCodCont"
})
public class TgCompPub {

    @XmlElement(required = true)
    protected String dModCont;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dEntCont;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dAnoCont;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dSecCont;
    @XmlElement(required = true)
    protected String dFeCodCont;

    /**
     * Obtiene el valor de la propiedad dModCont.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDModCont() {
        return dModCont;
    }

    /**
     * Define el valor de la propiedad dModCont.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDModCont(String value) {
        this.dModCont = value;
    }

    /**
     * Obtiene el valor de la propiedad dEntCont.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDEntCont() {
        return dEntCont;
    }

    /**
     * Define el valor de la propiedad dEntCont.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDEntCont(BigInteger value) {
        this.dEntCont = value;
    }

    /**
     * Obtiene el valor de la propiedad dAnoCont.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDAnoCont() {
        return dAnoCont;
    }

    /**
     * Define el valor de la propiedad dAnoCont.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDAnoCont(BigInteger value) {
        this.dAnoCont = value;
    }

    /**
     * Obtiene el valor de la propiedad dSecCont.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDSecCont() {
        return dSecCont;
    }

    /**
     * Define el valor de la propiedad dSecCont.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDSecCont(BigInteger value) {
        this.dSecCont = value;
    }

    /**
     * Obtiene el valor de la propiedad dFeCodCont.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFeCodCont() {
        return dFeCodCont;
    }

    /**
     * Define el valor de la propiedad dFeCodCont.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFeCodCont(String value) {
        this.dFeCodCont = value;
    }

}
