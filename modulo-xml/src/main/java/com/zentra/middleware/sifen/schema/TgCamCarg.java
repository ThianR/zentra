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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos generales de la carga
 * 			
 * 
 * <p>Clase Java para tgCamCarg complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamCarg"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cUniMedTotVol" type="{http://ekuatia.set.gov.py/sifen/xsd}tcUniMed" minOccurs="0"/&gt;
 *         &lt;element name="dDesUniMedTotVol" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesUniMed" minOccurs="0"/&gt;
 *         &lt;element name="dTotVolMerc" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cUniMedTotPes" type="{http://ekuatia.set.gov.py/sifen/xsd}tcUniMed" minOccurs="0"/&gt;
 *         &lt;element name="dDesUniMedTotPes" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesUniMed" minOccurs="0"/&gt;
 *         &lt;element name="dTotPesMerc" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="iCarCarga" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *               &lt;enumeration value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dDesCarCarga" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;union&gt;
 *               &lt;simpleType&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                   &lt;enumeration value="Mercaderías con cadena de frío"/&gt;
 *                   &lt;enumeration value="Carga peligrosa"/&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/simpleType&gt;
 *               &lt;simpleType&gt;
 *                 &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *                   &lt;minLength value="1"/&gt;
 *                   &lt;maxLength value="50"/&gt;
 *                   &lt;pattern value=".+"/&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/simpleType&gt;
 *             &lt;/union&gt;
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
@XmlType(name = "tgCamCarg", propOrder = {
    "cUniMedTotVol",
    "dDesUniMedTotVol",
    "dTotVolMerc",
    "cUniMedTotPes",
    "dDesUniMedTotPes",
    "dTotPesMerc",
    "iCarCarga",
    "dDesCarCarga"
})
public class TgCamCarg {

    protected BigInteger cUniMedTotVol;
    protected String dDesUniMedTotVol;
    protected BigInteger dTotVolMerc;
    protected BigInteger cUniMedTotPes;
    protected String dDesUniMedTotPes;
    protected BigInteger dTotPesMerc;
    protected BigInteger iCarCarga;
    protected String dDesCarCarga;

    /**
     * Obtiene el valor de la propiedad cUniMedTotVol.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCUniMedTotVol() {
        return cUniMedTotVol;
    }

    /**
     * Define el valor de la propiedad cUniMedTotVol.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCUniMedTotVol(BigInteger value) {
        this.cUniMedTotVol = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesUniMedTotVol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesUniMedTotVol() {
        return dDesUniMedTotVol;
    }

    /**
     * Define el valor de la propiedad dDesUniMedTotVol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesUniMedTotVol(String value) {
        this.dDesUniMedTotVol = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotVolMerc.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDTotVolMerc() {
        return dTotVolMerc;
    }

    /**
     * Define el valor de la propiedad dTotVolMerc.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDTotVolMerc(BigInteger value) {
        this.dTotVolMerc = value;
    }

    /**
     * Obtiene el valor de la propiedad cUniMedTotPes.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCUniMedTotPes() {
        return cUniMedTotPes;
    }

    /**
     * Define el valor de la propiedad cUniMedTotPes.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCUniMedTotPes(BigInteger value) {
        this.cUniMedTotPes = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesUniMedTotPes.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesUniMedTotPes() {
        return dDesUniMedTotPes;
    }

    /**
     * Define el valor de la propiedad dDesUniMedTotPes.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesUniMedTotPes(String value) {
        this.dDesUniMedTotPes = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotPesMerc.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDTotPesMerc() {
        return dTotPesMerc;
    }

    /**
     * Define el valor de la propiedad dTotPesMerc.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDTotPesMerc(BigInteger value) {
        this.dTotPesMerc = value;
    }

    /**
     * Obtiene el valor de la propiedad iCarCarga.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getICarCarga() {
        return iCarCarga;
    }

    /**
     * Define el valor de la propiedad iCarCarga.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setICarCarga(BigInteger value) {
        this.iCarCarga = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCarCarga.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesCarCarga() {
        return dDesCarCarga;
    }

    /**
     * Define el valor de la propiedad dDesCarCarga.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesCarCarga(String value) {
        this.dDesCarCarga = value;
    }

}
