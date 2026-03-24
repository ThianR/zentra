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
 * 				Campos que componen la Autofatura electrónica
 * 			
 * 
 * <p>Clase Java para tgCamAE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamAE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iNatVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tiNatVen"/&gt;
 *         &lt;element name="dDesNatVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesNatVen"/&gt;
 *         &lt;element name="iTipIDVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipDoc"/&gt;
 *         &lt;element name="dDTipIDVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDtipDoc"/&gt;
 *         &lt;element name="dNumIDVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumDocId"/&gt;
 *         &lt;element name="dNomVen" type="{http://ekuatia.set.gov.py/sifen/xsd}dNomRazSocial"/&gt;
 *         &lt;element name="dDirVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec"/&gt;
 *         &lt;element name="dNumCasVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumCas"/&gt;
 *         &lt;element name="cDepVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tDepartamentos"/&gt;
 *         &lt;element name="dDesDepVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDepartamento"/&gt;
 *         &lt;element name="cDisVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tDistrito" minOccurs="0"/&gt;
 *         &lt;element name="dDesDisVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDistrito" minOccurs="0"/&gt;
 *         &lt;element name="cCiuVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tCiudad"/&gt;
 *         &lt;element name="dDesCiuVen" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesCiudad"/&gt;
 *         &lt;element name="dDirProv" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec"/&gt;
 *         &lt;element name="cDepProv" type="{http://ekuatia.set.gov.py/sifen/xsd}tDepartamentos"/&gt;
 *         &lt;element name="dDesDepProv" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDepartamento"/&gt;
 *         &lt;element name="cDisProv" type="{http://ekuatia.set.gov.py/sifen/xsd}tDistrito" minOccurs="0"/&gt;
 *         &lt;element name="dDesDisProv" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDistrito" minOccurs="0"/&gt;
 *         &lt;element name="cCiuProv" type="{http://ekuatia.set.gov.py/sifen/xsd}tCiudad"/&gt;
 *         &lt;element name="dDesCiuProv" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesCiudad"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamAE", propOrder = {
    "iNatVen",
    "dDesNatVen",
    "iTipIDVen",
    "ddTipIDVen",
    "dNumIDVen",
    "dNomVen",
    "dDirVen",
    "dNumCasVen",
    "cDepVen",
    "dDesDepVen",
    "cDisVen",
    "dDesDisVen",
    "cCiuVen",
    "dDesCiuVen",
    "dDirProv",
    "cDepProv",
    "dDesDepProv",
    "cDisProv",
    "dDesDisProv",
    "cCiuProv",
    "dDesCiuProv"
})
public class TgCamAE {

    @XmlElement(required = true)
    protected BigInteger iNatVen;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesNatVen dDesNatVen;
    @XmlElement(required = true)
    protected BigInteger iTipIDVen;
    @XmlElement(name = "dDTipIDVen", required = true)
    @XmlSchemaType(name = "string")
    protected TdDtipDoc ddTipIDVen;
    @XmlElement(required = true)
    protected String dNumIDVen;
    @XmlElement(required = true)
    protected String dNomVen;
    @XmlElement(required = true)
    protected String dDirVen;
    @XmlElement(required = true)
    protected BigInteger dNumCasVen;
    @XmlElement(required = true)
    protected BigInteger cDepVen;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TDesDepartamento dDesDepVen;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cDisVen;
    protected String dDesDisVen;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cCiuVen;
    @XmlElement(required = true)
    protected String dDesCiuVen;
    @XmlElement(required = true)
    protected String dDirProv;
    @XmlElement(required = true)
    protected BigInteger cDepProv;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TDesDepartamento dDesDepProv;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cDisProv;
    protected String dDesDisProv;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cCiuProv;
    @XmlElement(required = true)
    protected String dDesCiuProv;

    /**
     * Obtiene el valor de la propiedad iNatVen.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getINatVen() {
        return iNatVen;
    }

    /**
     * Define el valor de la propiedad iNatVen.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setINatVen(BigInteger value) {
        this.iNatVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesNatVen.
     * 
     * @return
     *     possible object is
     *     {@link TdDesNatVen }
     *     
     */
    public TdDesNatVen getDDesNatVen() {
        return dDesNatVen;
    }

    /**
     * Define el valor de la propiedad dDesNatVen.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesNatVen }
     *     
     */
    public void setDDesNatVen(TdDesNatVen value) {
        this.dDesNatVen = value;
    }

    /**
     * Obtiene el valor de la propiedad iTipIDVen.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipIDVen() {
        return iTipIDVen;
    }

    /**
     * Define el valor de la propiedad iTipIDVen.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipIDVen(BigInteger value) {
        this.iTipIDVen = value;
    }

    /**
     * Obtiene el valor de la propiedad ddTipIDVen.
     * 
     * @return
     *     possible object is
     *     {@link TdDtipDoc }
     *     
     */
    public TdDtipDoc getDDTipIDVen() {
        return ddTipIDVen;
    }

    /**
     * Define el valor de la propiedad ddTipIDVen.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDtipDoc }
     *     
     */
    public void setDDTipIDVen(TdDtipDoc value) {
        this.ddTipIDVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumIDVen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumIDVen() {
        return dNumIDVen;
    }

    /**
     * Define el valor de la propiedad dNumIDVen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumIDVen(String value) {
        this.dNumIDVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomVen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomVen() {
        return dNomVen;
    }

    /**
     * Define el valor de la propiedad dNomVen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomVen(String value) {
        this.dNomVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dDirVen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirVen() {
        return dDirVen;
    }

    /**
     * Define el valor de la propiedad dDirVen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirVen(String value) {
        this.dDirVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumCasVen.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNumCasVen() {
        return dNumCasVen;
    }

    /**
     * Define el valor de la propiedad dNumCasVen.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNumCasVen(BigInteger value) {
        this.dNumCasVen = value;
    }

    /**
     * Obtiene el valor de la propiedad cDepVen.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDepVen() {
        return cDepVen;
    }

    /**
     * Define el valor de la propiedad cDepVen.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDepVen(BigInteger value) {
        this.cDepVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDepVen.
     * 
     * @return
     *     possible object is
     *     {@link TDesDepartamento }
     *     
     */
    public TDesDepartamento getDDesDepVen() {
        return dDesDepVen;
    }

    /**
     * Define el valor de la propiedad dDesDepVen.
     * 
     * @param value
     *     allowed object is
     *     {@link TDesDepartamento }
     *     
     */
    public void setDDesDepVen(TDesDepartamento value) {
        this.dDesDepVen = value;
    }

    /**
     * Obtiene el valor de la propiedad cDisVen.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDisVen() {
        return cDisVen;
    }

    /**
     * Define el valor de la propiedad cDisVen.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDisVen(BigInteger value) {
        this.cDisVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDisVen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDisVen() {
        return dDesDisVen;
    }

    /**
     * Define el valor de la propiedad dDesDisVen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDisVen(String value) {
        this.dDesDisVen = value;
    }

    /**
     * Obtiene el valor de la propiedad cCiuVen.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCCiuVen() {
        return cCiuVen;
    }

    /**
     * Define el valor de la propiedad cCiuVen.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCCiuVen(BigInteger value) {
        this.cCiuVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCiuVen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesCiuVen() {
        return dDesCiuVen;
    }

    /**
     * Define el valor de la propiedad dDesCiuVen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesCiuVen(String value) {
        this.dDesCiuVen = value;
    }

    /**
     * Obtiene el valor de la propiedad dDirProv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirProv() {
        return dDirProv;
    }

    /**
     * Define el valor de la propiedad dDirProv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirProv(String value) {
        this.dDirProv = value;
    }

    /**
     * Obtiene el valor de la propiedad cDepProv.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDepProv() {
        return cDepProv;
    }

    /**
     * Define el valor de la propiedad cDepProv.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDepProv(BigInteger value) {
        this.cDepProv = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDepProv.
     * 
     * @return
     *     possible object is
     *     {@link TDesDepartamento }
     *     
     */
    public TDesDepartamento getDDesDepProv() {
        return dDesDepProv;
    }

    /**
     * Define el valor de la propiedad dDesDepProv.
     * 
     * @param value
     *     allowed object is
     *     {@link TDesDepartamento }
     *     
     */
    public void setDDesDepProv(TDesDepartamento value) {
        this.dDesDepProv = value;
    }

    /**
     * Obtiene el valor de la propiedad cDisProv.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDisProv() {
        return cDisProv;
    }

    /**
     * Define el valor de la propiedad cDisProv.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDisProv(BigInteger value) {
        this.cDisProv = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDisProv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDisProv() {
        return dDesDisProv;
    }

    /**
     * Define el valor de la propiedad dDesDisProv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDisProv(String value) {
        this.dDesDisProv = value;
    }

    /**
     * Obtiene el valor de la propiedad cCiuProv.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCCiuProv() {
        return cCiuProv;
    }

    /**
     * Define el valor de la propiedad cCiuProv.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCCiuProv(BigInteger value) {
        this.cCiuProv = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCiuProv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesCiuProv() {
        return dDesCiuProv;
    }

    /**
     * Define el valor de la propiedad dDesCiuProv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesCiuProv(String value) {
        this.dDesCiuProv = value;
    }

}
