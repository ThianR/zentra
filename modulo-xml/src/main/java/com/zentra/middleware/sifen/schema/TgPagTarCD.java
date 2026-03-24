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
 * 				Campos que describen el pago o entrega inicial de la operación con tarjeta
 * 				de crédito/débito
 * 			
 * 
 * <p>Clase Java para tgPagTarCD complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgPagTarCD"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iDenTarj" type="{http://ekuatia.set.gov.py/sifen/xsd}tiDenTarj"/&gt;
 *         &lt;element name="dDesDenTarj" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesDenTarj"/&gt;
 *         &lt;element name="dRSProTar" type="{http://ekuatia.set.gov.py/sifen/xsd}dNomRazSocial" minOccurs="0"/&gt;
 *         &lt;element name="dRUCProTar" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc" minOccurs="0"/&gt;
 *         &lt;element name="dDVProTar" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer" minOccurs="0"/&gt;
 *         &lt;element name="iForProPa" type="{http://ekuatia.set.gov.py/sifen/xsd}tiForProPa"/&gt;
 *         &lt;element name="dCodAuOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNunApOpe" minOccurs="0"/&gt;
 *         &lt;element name="dNomTit" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="4"/&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNumTarj" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="4"/&gt;
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
@XmlType(name = "tgPagTarCD", propOrder = {
    "iDenTarj",
    "dDesDenTarj",
    "drsProTar",
    "drucProTar",
    "ddvProTar",
    "iForProPa",
    "dCodAuOpe",
    "dNomTit",
    "dNumTarj"
})
public class TgPagTarCD {

    @XmlElement(required = true)
    protected BigInteger iDenTarj;
    @XmlElement(required = true)
    protected String dDesDenTarj;
    @XmlElement(name = "dRSProTar")
    protected String drsProTar;
    @XmlElement(name = "dRUCProTar")
    protected String drucProTar;
    @XmlElement(name = "dDVProTar")
    protected BigInteger ddvProTar;
    protected short iForProPa;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dCodAuOpe;
    protected String dNomTit;
    protected BigInteger dNumTarj;

    /**
     * Obtiene el valor de la propiedad iDenTarj.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIDenTarj() {
        return iDenTarj;
    }

    /**
     * Define el valor de la propiedad iDenTarj.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIDenTarj(BigInteger value) {
        this.iDenTarj = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDenTarj.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDenTarj() {
        return dDesDenTarj;
    }

    /**
     * Define el valor de la propiedad dDesDenTarj.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDenTarj(String value) {
        this.dDesDenTarj = value;
    }

    /**
     * Obtiene el valor de la propiedad drsProTar.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRSProTar() {
        return drsProTar;
    }

    /**
     * Define el valor de la propiedad drsProTar.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRSProTar(String value) {
        this.drsProTar = value;
    }

    /**
     * Obtiene el valor de la propiedad drucProTar.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRUCProTar() {
        return drucProTar;
    }

    /**
     * Define el valor de la propiedad drucProTar.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRUCProTar(String value) {
        this.drucProTar = value;
    }

    /**
     * Obtiene el valor de la propiedad ddvProTar.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDVProTar() {
        return ddvProTar;
    }

    /**
     * Define el valor de la propiedad ddvProTar.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDVProTar(BigInteger value) {
        this.ddvProTar = value;
    }

    /**
     * Obtiene el valor de la propiedad iForProPa.
     * 
     */
    public short getIForProPa() {
        return iForProPa;
    }

    /**
     * Define el valor de la propiedad iForProPa.
     * 
     */
    public void setIForProPa(short value) {
        this.iForProPa = value;
    }

    /**
     * Obtiene el valor de la propiedad dCodAuOpe.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDCodAuOpe() {
        return dCodAuOpe;
    }

    /**
     * Define el valor de la propiedad dCodAuOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDCodAuOpe(BigInteger value) {
        this.dCodAuOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomTit.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomTit() {
        return dNomTit;
    }

    /**
     * Define el valor de la propiedad dNomTit.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomTit(String value) {
        this.dNomTit = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumTarj.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNumTarj() {
        return dNumTarj;
    }

    /**
     * Define el valor de la propiedad dNumTarj.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNumTarj(BigInteger value) {
        this.dNumTarj = value;
    }

}
