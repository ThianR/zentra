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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen el pago o entrega inicial de la operación con cheque
 * 			
 * 
 * <p>Clase Java para tgPagCheq complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgPagCheq"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dNumCheq" type="{http://ekuatia.set.gov.py/sifen/xsd}tNumCheq"/&gt;
 *         &lt;element name="dBcoEmi"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="4"/&gt;
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
@XmlType(name = "tgPagCheq", propOrder = {
    "dNumCheq",
    "dBcoEmi"
})
public class TgPagCheq {

    @XmlElement(required = true)
    protected String dNumCheq;
    @XmlElement(required = true)
    protected String dBcoEmi;

    /**
     * Obtiene el valor de la propiedad dNumCheq.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumCheq() {
        return dNumCheq;
    }

    /**
     * Define el valor de la propiedad dNumCheq.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumCheq(String value) {
        this.dNumCheq = value;
    }

    /**
     * Obtiene el valor de la propiedad dBcoEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDBcoEmi() {
        return dBcoEmi;
    }

    /**
     * Define el valor de la propiedad dBcoEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDBcoEmi(String value) {
        this.dBcoEmi = value;
    }

}
