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
 * 				Campos que componen la Nota de credito/Debito Electronica
 * 			
 * 
 * <p>Clase Java para tgCamNCDE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamNCDE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iMotEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tiMotEmi"/&gt;
 *         &lt;element name="dDesMotEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesMotEmi"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamNCDE", propOrder = {
    "iMotEmi",
    "dDesMotEmi"
})
public class TgCamNCDE {

    @XmlElement(required = true)
    protected String iMotEmi;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesMotEmi dDesMotEmi;

    /**
     * Obtiene el valor de la propiedad iMotEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMotEmi() {
        return iMotEmi;
    }

    /**
     * Define el valor de la propiedad iMotEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMotEmi(String value) {
        this.iMotEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesMotEmi.
     * 
     * @return
     *     possible object is
     *     {@link TdDesMotEmi }
     *     
     */
    public TdDesMotEmi getDDesMotEmi() {
        return dDesMotEmi;
    }

    /**
     * Define el valor de la propiedad dDesMotEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesMotEmi }
     *     
     */
    public void setDDesMotEmi(TdDesMotEmi value) {
        this.dDesMotEmi = value;
    }

}
