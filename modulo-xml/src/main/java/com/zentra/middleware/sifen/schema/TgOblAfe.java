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
 * 				Grupo de campos que identifican las obligaciones afectadas
 * 			
 * 
 * <p>Clase Java para tgOblAfe complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgOblAfe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cOblAfe" type="{http://ekuatia.set.gov.py/sifen/xsd}tcOblAfe"/&gt;
 *         &lt;element name="dDesOblAfe" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesOblAfe"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgOblAfe", propOrder = {
    "cOblAfe",
    "dDesOblAfe"
})
public class TgOblAfe {

    @XmlElement(required = true)
    protected BigInteger cOblAfe;
    @XmlElement(required = true)
    protected String dDesOblAfe;

    /**
     * Obtiene el valor de la propiedad cOblAfe.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCOblAfe() {
        return cOblAfe;
    }

    /**
     * Define el valor de la propiedad cOblAfe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCOblAfe(BigInteger value) {
        this.cOblAfe = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesOblAfe.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesOblAfe() {
        return dDesOblAfe;
    }

    /**
     * Define el valor de la propiedad dDesOblAfe.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesOblAfe(String value) {
        this.dDesOblAfe = value;
    }

}
