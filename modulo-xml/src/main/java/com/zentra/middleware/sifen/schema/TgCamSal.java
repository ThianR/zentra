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
 * 				Campos que identifican el local de salida de las mercaderías
 * 			
 * 
 * <p>Clase Java para tgCamSal complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamSal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dDirLocSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec"/&gt;
 *         &lt;element name="dNumCasSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumCas"/&gt;
 *         &lt;element name="dComp1Sal" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *         &lt;element name="dComp2Sal" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *         &lt;element name="cDepSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tDepartamentos"/&gt;
 *         &lt;element name="dDesDepSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDepartamento"/&gt;
 *         &lt;element name="cDisSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tDistrito" minOccurs="0"/&gt;
 *         &lt;element name="dDesDisSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDistrito" minOccurs="0"/&gt;
 *         &lt;element name="cCiuSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tCiudad"/&gt;
 *         &lt;element name="dDesCiuSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesCiudad"/&gt;
 *         &lt;element name="dTelSal" type="{http://ekuatia.set.gov.py/sifen/xsd}tdTel" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamSal", propOrder = {
    "dDirLocSal",
    "dNumCasSal",
    "dComp1Sal",
    "dComp2Sal",
    "cDepSal",
    "dDesDepSal",
    "cDisSal",
    "dDesDisSal",
    "cCiuSal",
    "dDesCiuSal",
    "dTelSal"
})
public class TgCamSal {

    @XmlElement(required = true)
    protected String dDirLocSal;
    @XmlElement(required = true)
    protected BigInteger dNumCasSal;
    protected String dComp1Sal;
    protected String dComp2Sal;
    @XmlElement(required = true)
    protected BigInteger cDepSal;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TDesDepartamento dDesDepSal;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cDisSal;
    protected String dDesDisSal;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cCiuSal;
    @XmlElement(required = true)
    protected String dDesCiuSal;
    protected String dTelSal;

    /**
     * Obtiene el valor de la propiedad dDirLocSal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirLocSal() {
        return dDirLocSal;
    }

    /**
     * Define el valor de la propiedad dDirLocSal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirLocSal(String value) {
        this.dDirLocSal = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumCasSal.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNumCasSal() {
        return dNumCasSal;
    }

    /**
     * Define el valor de la propiedad dNumCasSal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNumCasSal(BigInteger value) {
        this.dNumCasSal = value;
    }

    /**
     * Obtiene el valor de la propiedad dComp1Sal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDComp1Sal() {
        return dComp1Sal;
    }

    /**
     * Define el valor de la propiedad dComp1Sal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDComp1Sal(String value) {
        this.dComp1Sal = value;
    }

    /**
     * Obtiene el valor de la propiedad dComp2Sal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDComp2Sal() {
        return dComp2Sal;
    }

    /**
     * Define el valor de la propiedad dComp2Sal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDComp2Sal(String value) {
        this.dComp2Sal = value;
    }

    /**
     * Obtiene el valor de la propiedad cDepSal.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDepSal() {
        return cDepSal;
    }

    /**
     * Define el valor de la propiedad cDepSal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDepSal(BigInteger value) {
        this.cDepSal = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDepSal.
     * 
     * @return
     *     possible object is
     *     {@link TDesDepartamento }
     *     
     */
    public TDesDepartamento getDDesDepSal() {
        return dDesDepSal;
    }

    /**
     * Define el valor de la propiedad dDesDepSal.
     * 
     * @param value
     *     allowed object is
     *     {@link TDesDepartamento }
     *     
     */
    public void setDDesDepSal(TDesDepartamento value) {
        this.dDesDepSal = value;
    }

    /**
     * Obtiene el valor de la propiedad cDisSal.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDisSal() {
        return cDisSal;
    }

    /**
     * Define el valor de la propiedad cDisSal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDisSal(BigInteger value) {
        this.cDisSal = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDisSal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDisSal() {
        return dDesDisSal;
    }

    /**
     * Define el valor de la propiedad dDesDisSal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDisSal(String value) {
        this.dDesDisSal = value;
    }

    /**
     * Obtiene el valor de la propiedad cCiuSal.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCCiuSal() {
        return cCiuSal;
    }

    /**
     * Define el valor de la propiedad cCiuSal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCCiuSal(BigInteger value) {
        this.cCiuSal = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCiuSal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesCiuSal() {
        return dDesCiuSal;
    }

    /**
     * Define el valor de la propiedad dDesCiuSal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesCiuSal(String value) {
        this.dDesCiuSal = value;
    }

    /**
     * Obtiene el valor de la propiedad dTelSal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDTelSal() {
        return dTelSal;
    }

    /**
     * Define el valor de la propiedad dTelSal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDTelSal(String value) {
        this.dTelSal = value;
    }

}
