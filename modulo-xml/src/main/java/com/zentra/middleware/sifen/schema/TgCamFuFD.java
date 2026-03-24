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
 * 				Campos fuera de la firma digital
 * 			
 * 
 * <p>Clase Java para tgCamFuFD complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamFuFD"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dCarQR"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="100"/&gt;
 *               &lt;maxLength value="600"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dInfAdic" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="5000"/&gt;
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
@XmlType(name = "tgCamFuFD", propOrder = {
    "dCarQR",
    "dInfAdic"
})
public class TgCamFuFD {

    @XmlElement(required = true)
    protected String dCarQR;
    protected String dInfAdic;

    /**
     * Obtiene el valor de la propiedad dCarQR.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCarQR() {
        return dCarQR;
    }

    /**
     * Define el valor de la propiedad dCarQR.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCarQR(String value) {
        this.dCarQR = value;
    }

    /**
     * Obtiene el valor de la propiedad dInfAdic.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDInfAdic() {
        return dInfAdic;
    }

    /**
     * Define el valor de la propiedad dInfAdic.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDInfAdic(String value) {
        this.dInfAdic = value;
    }

}
