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
 * 				Campos que identifican el local de entrega de las mercaderías
 * 			
 * 
 * <p>Clase Java para tgCamEnt complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamEnt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dDirLocEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec"/&gt;
 *         &lt;element name="dNumCasEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumCas"/&gt;
 *         &lt;element name="dComp1Ent" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *         &lt;element name="dComp2Ent" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *         &lt;element name="cDepEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tDepartamentos"/&gt;
 *         &lt;element name="dDesDepEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDepartamento"/&gt;
 *         &lt;element name="cDisEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tDistrito" minOccurs="0"/&gt;
 *         &lt;element name="dDesDisEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDistrito" minOccurs="0"/&gt;
 *         &lt;element name="cCiuEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tCiudad"/&gt;
 *         &lt;element name="dDesCiuEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesCiudad"/&gt;
 *         &lt;element name="dTelEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tdTel" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamEnt", propOrder = {
    "dDirLocEnt",
    "dNumCasEnt",
    "dComp1Ent",
    "dComp2Ent",
    "cDepEnt",
    "dDesDepEnt",
    "cDisEnt",
    "dDesDisEnt",
    "cCiuEnt",
    "dDesCiuEnt",
    "dTelEnt"
})
public class TgCamEnt {

    @XmlElement(required = true)
    protected String dDirLocEnt;
    @XmlElement(required = true)
    protected BigInteger dNumCasEnt;
    protected String dComp1Ent;
    protected String dComp2Ent;
    @XmlElement(required = true)
    protected BigInteger cDepEnt;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TDesDepartamento dDesDepEnt;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cDisEnt;
    protected String dDesDisEnt;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cCiuEnt;
    @XmlElement(required = true)
    protected String dDesCiuEnt;
    protected String dTelEnt;

    /**
     * Obtiene el valor de la propiedad dDirLocEnt.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirLocEnt() {
        return dDirLocEnt;
    }

    /**
     * Define el valor de la propiedad dDirLocEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirLocEnt(String value) {
        this.dDirLocEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumCasEnt.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNumCasEnt() {
        return dNumCasEnt;
    }

    /**
     * Define el valor de la propiedad dNumCasEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNumCasEnt(BigInteger value) {
        this.dNumCasEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dComp1Ent.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDComp1Ent() {
        return dComp1Ent;
    }

    /**
     * Define el valor de la propiedad dComp1Ent.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDComp1Ent(String value) {
        this.dComp1Ent = value;
    }

    /**
     * Obtiene el valor de la propiedad dComp2Ent.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDComp2Ent() {
        return dComp2Ent;
    }

    /**
     * Define el valor de la propiedad dComp2Ent.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDComp2Ent(String value) {
        this.dComp2Ent = value;
    }

    /**
     * Obtiene el valor de la propiedad cDepEnt.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDepEnt() {
        return cDepEnt;
    }

    /**
     * Define el valor de la propiedad cDepEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDepEnt(BigInteger value) {
        this.cDepEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDepEnt.
     * 
     * @return
     *     possible object is
     *     {@link TDesDepartamento }
     *     
     */
    public TDesDepartamento getDDesDepEnt() {
        return dDesDepEnt;
    }

    /**
     * Define el valor de la propiedad dDesDepEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link TDesDepartamento }
     *     
     */
    public void setDDesDepEnt(TDesDepartamento value) {
        this.dDesDepEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad cDisEnt.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDisEnt() {
        return cDisEnt;
    }

    /**
     * Define el valor de la propiedad cDisEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDisEnt(BigInteger value) {
        this.cDisEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDisEnt.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDisEnt() {
        return dDesDisEnt;
    }

    /**
     * Define el valor de la propiedad dDesDisEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDisEnt(String value) {
        this.dDesDisEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad cCiuEnt.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCCiuEnt() {
        return cCiuEnt;
    }

    /**
     * Define el valor de la propiedad cCiuEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCCiuEnt(BigInteger value) {
        this.cCiuEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCiuEnt.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesCiuEnt() {
        return dDesCiuEnt;
    }

    /**
     * Define el valor de la propiedad dDesCiuEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesCiuEnt(String value) {
        this.dDesCiuEnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dTelEnt.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDTelEnt() {
        return dTelEnt;
    }

    /**
     * Define el valor de la propiedad dTelEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDTelEnt(String value) {
        this.dTelEnt = value;
    }

}
