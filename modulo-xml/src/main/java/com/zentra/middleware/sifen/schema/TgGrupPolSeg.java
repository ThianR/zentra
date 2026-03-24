//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Póliza de seguros
 * 			
 * 
 * <p>Clase Java para tgGrupPolSeg complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgGrupPolSeg"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dPoliza" type="{http://ekuatia.set.gov.py/sifen/xsd}tdPoliza"/&gt;
 *         &lt;element name="dUnidVig"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="3"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dVigencia" type="{http://ekuatia.set.gov.py/sifen/xsd}tdVigPol"/&gt;
 *         &lt;element name="dNumPoliza"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="25"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dFecIniVig" type="{http://ekuatia.set.gov.py/sifen/xsd}fecHhmmss" minOccurs="0"/&gt;
 *         &lt;element name="dFecFinVig" type="{http://ekuatia.set.gov.py/sifen/xsd}fecHhmmss" minOccurs="0"/&gt;
 *         &lt;element name="dCodInt" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="50"/&gt;
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
@XmlType(name = "tgGrupPolSeg", propOrder = {
    "dPoliza",
    "dUnidVig",
    "dVigencia",
    "dNumPoliza",
    "dFecIniVig",
    "dFecFinVig",
    "dCodInt"
})
public class TgGrupPolSeg {

    @XmlElement(required = true)
    protected String dPoliza;
    @XmlElement(required = true)
    protected String dUnidVig;
    @XmlElement(required = true)
    protected BigDecimal dVigencia;
    @XmlElement(required = true)
    protected String dNumPoliza;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dFecIniVig;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dFecFinVig;
    protected String dCodInt;

    /**
     * Obtiene el valor de la propiedad dPoliza.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDPoliza() {
        return dPoliza;
    }

    /**
     * Define el valor de la propiedad dPoliza.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDPoliza(String value) {
        this.dPoliza = value;
    }

    /**
     * Obtiene el valor de la propiedad dUnidVig.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDUnidVig() {
        return dUnidVig;
    }

    /**
     * Define el valor de la propiedad dUnidVig.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDUnidVig(String value) {
        this.dUnidVig = value;
    }

    /**
     * Obtiene el valor de la propiedad dVigencia.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDVigencia() {
        return dVigencia;
    }

    /**
     * Define el valor de la propiedad dVigencia.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDVigencia(BigDecimal value) {
        this.dVigencia = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumPoliza.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumPoliza() {
        return dNumPoliza;
    }

    /**
     * Define el valor de la propiedad dNumPoliza.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumPoliza(String value) {
        this.dNumPoliza = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecIniVig.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDFecIniVig() {
        return dFecIniVig;
    }

    /**
     * Define el valor de la propiedad dFecIniVig.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDFecIniVig(XMLGregorianCalendar value) {
        this.dFecIniVig = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecFinVig.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDFecFinVig() {
        return dFecFinVig;
    }

    /**
     * Define el valor de la propiedad dFecFinVig.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDFecFinVig(XMLGregorianCalendar value) {
        this.dFecFinVig = value;
    }

    /**
     * Obtiene el valor de la propiedad dCodInt.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCodInt() {
        return dCodInt;
    }

    /**
     * Define el valor de la propiedad dCodInt.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCodInt(String value) {
        this.dCodInt = value;
    }

}
