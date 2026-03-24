//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen la operación a crédito
 * 			
 * 
 * <p>Clase Java para tgPagCred complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgPagCred"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iCondCred"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="1"/&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dDCondCred" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDCondCred"/&gt;
 *         &lt;element name="dPlazoCre" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="2"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dCuotas" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dMonEnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase4" minOccurs="0"/&gt;
 *         &lt;element name="gCuotas" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCuotas" maxOccurs="999" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgPagCred", propOrder = {
    "iCondCred",
    "ddCondCred",
    "dPlazoCre",
    "dCuotas",
    "dMonEnt",
    "gCuotas"
})
public class TgPagCred {

    @XmlElement(required = true)
    protected BigInteger iCondCred;
    @XmlElement(name = "dDCondCred", required = true)
    @XmlSchemaType(name = "string")
    protected TdDCondCred ddCondCred;
    protected String dPlazoCre;
    protected BigInteger dCuotas;
    protected BigDecimal dMonEnt;
    protected List<TgCuotas> gCuotas;

    /**
     * Obtiene el valor de la propiedad iCondCred.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getICondCred() {
        return iCondCred;
    }

    /**
     * Define el valor de la propiedad iCondCred.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setICondCred(BigInteger value) {
        this.iCondCred = value;
    }

    /**
     * Obtiene el valor de la propiedad ddCondCred.
     * 
     * @return
     *     possible object is
     *     {@link TdDCondCred }
     *     
     */
    public TdDCondCred getDDCondCred() {
        return ddCondCred;
    }

    /**
     * Define el valor de la propiedad ddCondCred.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDCondCred }
     *     
     */
    public void setDDCondCred(TdDCondCred value) {
        this.ddCondCred = value;
    }

    /**
     * Obtiene el valor de la propiedad dPlazoCre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDPlazoCre() {
        return dPlazoCre;
    }

    /**
     * Define el valor de la propiedad dPlazoCre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDPlazoCre(String value) {
        this.dPlazoCre = value;
    }

    /**
     * Obtiene el valor de la propiedad dCuotas.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDCuotas() {
        return dCuotas;
    }

    /**
     * Define el valor de la propiedad dCuotas.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDCuotas(BigInteger value) {
        this.dCuotas = value;
    }

    /**
     * Obtiene el valor de la propiedad dMonEnt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDMonEnt() {
        return dMonEnt;
    }

    /**
     * Define el valor de la propiedad dMonEnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDMonEnt(BigDecimal value) {
        this.dMonEnt = value;
    }

    /**
     * Gets the value of the gCuotas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gCuotas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGCuotas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgCuotas }
     * 
     * 
     */
    public List<TgCuotas> getGCuotas() {
        if (gCuotas == null) {
            gCuotas = new ArrayList<TgCuotas>();
        }
        return this.gCuotas;
    }

}
