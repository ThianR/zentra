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
 * 				Campos que componen la factura electronica
 * 			
 * 
 * <p>Clase Java para tgCamFE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamFE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iIndPres" type="{http://ekuatia.set.gov.py/sifen/xsd}tiIndPres"/&gt;
 *         &lt;element name="dDesIndPres" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesIndPres"/&gt;
 *         &lt;element name="dFecEmNR" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion" minOccurs="0"/&gt;
 *         &lt;element name="gCompPub" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCompPub" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamFE", propOrder = {
    "iIndPres",
    "dDesIndPres",
    "dFecEmNR",
    "gCompPub"
})
public class TgCamFE {

    @XmlElement(required = true)
    protected BigInteger iIndPres;
    @XmlElement(required = true)
    protected String dDesIndPres;
    protected String dFecEmNR;
    protected TgCompPub gCompPub;

    /**
     * Obtiene el valor de la propiedad iIndPres.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIIndPres() {
        return iIndPres;
    }

    /**
     * Define el valor de la propiedad iIndPres.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIIndPres(BigInteger value) {
        this.iIndPres = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesIndPres.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesIndPres() {
        return dDesIndPres;
    }

    /**
     * Define el valor de la propiedad dDesIndPres.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesIndPres(String value) {
        this.dDesIndPres = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecEmNR.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFecEmNR() {
        return dFecEmNR;
    }

    /**
     * Define el valor de la propiedad dFecEmNR.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFecEmNR(String value) {
        this.dFecEmNR = value;
    }

    /**
     * Obtiene el valor de la propiedad gCompPub.
     * 
     * @return
     *     possible object is
     *     {@link TgCompPub }
     *     
     */
    public TgCompPub getGCompPub() {
        return gCompPub;
    }

    /**
     * Define el valor de la propiedad gCompPub.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCompPub }
     *     
     */
    public void setGCompPub(TgCompPub value) {
        this.gCompPub = value;
    }

}
