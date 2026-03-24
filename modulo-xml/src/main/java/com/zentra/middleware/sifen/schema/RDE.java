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
 * <p>Clase Java para rDE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="rDE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dVerFor"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;pattern value="[1][5][0]"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DE" type="{http://ekuatia.set.gov.py/sifen/xsd}tDE"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature"/&gt;
 *         &lt;element name="gCamFuFD" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamFuFD"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rDE", propOrder = {
    "dVerFor",
    "de",
    "signature",
    "gCamFuFD"
})
public class RDE {

    @XmlElement(required = true)
    protected BigInteger dVerFor;
    @XmlElement(name = "DE", required = true)
    protected TDE de;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignatureType signature;
    @XmlElement(required = true)
    protected TgCamFuFD gCamFuFD;

    /**
     * Obtiene el valor de la propiedad dVerFor.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDVerFor() {
        return dVerFor;
    }

    /**
     * Define el valor de la propiedad dVerFor.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDVerFor(BigInteger value) {
        this.dVerFor = value;
    }

    /**
     * Obtiene el valor de la propiedad de.
     * 
     * @return
     *     possible object is
     *     {@link TDE }
     *     
     */
    public TDE getDE() {
        return de;
    }

    /**
     * Define el valor de la propiedad de.
     * 
     * @param value
     *     allowed object is
     *     {@link TDE }
     *     
     */
    public void setDE(TDE value) {
        this.de = value;
    }

    /**
     * 
     * 						Firma Digital del DE
     * 					
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Define el valor de la propiedad signature.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamFuFD.
     * 
     * @return
     *     possible object is
     *     {@link TgCamFuFD }
     *     
     */
    public TgCamFuFD getGCamFuFD() {
        return gCamFuFD;
    }

    /**
     * Define el valor de la propiedad gCamFuFD.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamFuFD }
     *     
     */
    public void setGCamFuFD(TgCamFuFD value) {
        this.gCamFuFD = value;
    }

}
