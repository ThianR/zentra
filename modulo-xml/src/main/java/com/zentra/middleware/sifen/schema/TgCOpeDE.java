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
 * 				Campos de la operacion del Documento Electronico
 * 			
 * 
 * <p>Clase Java para tgCOpeDE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCOpeDE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTipEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipEmi"/&gt;
 *         &lt;element name="dDesTipEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTipEmi"/&gt;
 *         &lt;element name="dCodSeg" type="{http://ekuatia.set.gov.py/sifen/xsd}tiCodSe"/&gt;
 *         &lt;element name="dInfoEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdInfoEmi" minOccurs="0"/&gt;
 *         &lt;element name="dInfoFisc" type="{http://ekuatia.set.gov.py/sifen/xsd}tdInfoFisc" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCOpeDE", propOrder = {
    "iAmb",
    "dDesAmb",
    "iTipEmi",
    "dDesTipEmi",
    "dCodSeg",
    "dInfoEmi",
    "dInfoFisc"
})
public class TgCOpeDE {

    @XmlElement(required = true)
    protected BigInteger iAmb;
    @XmlElement(required = true)
    protected String dDesAmb;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger iTipEmi;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesTipEmi dDesTipEmi;
    @XmlElement(required = true)
    protected BigInteger dCodSeg;
    protected String dInfoEmi;
    protected String dInfoFisc;

    /**
     * Obtiene el valor de la propiedad iTipEmi.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipEmi() {
        return iTipEmi;
    }

    /**
     * Define el valor de la propiedad iTipEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipEmi(BigInteger value) {
        this.iTipEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTipEmi.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTipEmi }
     *     
     */
    public TdDesTipEmi getDDesTipEmi() {
        return dDesTipEmi;
    }

    /**
     * Define el valor de la propiedad dDesTipEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTipEmi }
     *     
     */
    public void setDDesTipEmi(TdDesTipEmi value) {
        this.dDesTipEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dCodSeg.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDCodSeg() {
        return dCodSeg;
    }

    /**
     * Define el valor de la propiedad dCodSeg.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDCodSeg(BigInteger value) {
        this.dCodSeg = value;
    }

    /**
     * Obtiene el valor de la propiedad dInfoEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDInfoEmi() {
        return dInfoEmi;
    }

    /**
     * Define el valor de la propiedad dInfoEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDInfoEmi(String value) {
        this.dInfoEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dInfoFisc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDInfoFisc() {
        return dInfoFisc;
    }

    public void setDInfoFisc(String value) {
        this.dInfoFisc = value;
    }

    public BigInteger getIAmb() { return iAmb; }
    public void setIAmb(BigInteger value) { this.iAmb = value; }
    public String getDDesAmb() { return dDesAmb; }
    public void setDDesAmb(String value) { this.dDesAmb = value; }
}
