//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Grupo de datos adicionales de uso comercial
 * 			
 * 
 * <p>Clase Java para tgGrupAdi complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgGrupAdi"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dCiclo" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dFecIniC" type="{http://ekuatia.set.gov.py/sifen/xsd}tdFeIniS" minOccurs="0"/&gt;
 *         &lt;element name="dFecFinC" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion" minOccurs="0"/&gt;
 *         &lt;element name="dVencPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion" maxOccurs="3" minOccurs="0"/&gt;
 *         &lt;element name="dContrato" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dSalAnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase4" minOccurs="0"/&gt;
 *         &lt;element name="dCodConDncp" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="30"/&gt;
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
@XmlType(name = "tgGrupAdi", propOrder = {
    "dCiclo",
    "dFecIniC",
    "dFecFinC",
    "dVencPag",
    "dContrato",
    "dSalAnt",
    "dCodConDncp"
})
public class TgGrupAdi {

    protected String dCiclo;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dFecIniC;
    protected String dFecFinC;
    protected List<String> dVencPag;
    protected String dContrato;
    protected BigDecimal dSalAnt;
    protected String dCodConDncp;

    /**
     * Obtiene el valor de la propiedad dCiclo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCiclo() {
        return dCiclo;
    }

    /**
     * Define el valor de la propiedad dCiclo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCiclo(String value) {
        this.dCiclo = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecIniC.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDFecIniC() {
        return dFecIniC;
    }

    /**
     * Define el valor de la propiedad dFecIniC.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDFecIniC(XMLGregorianCalendar value) {
        this.dFecIniC = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecFinC.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFecFinC() {
        return dFecFinC;
    }

    /**
     * Define el valor de la propiedad dFecFinC.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFecFinC(String value) {
        this.dFecFinC = value;
    }

    /**
     * Gets the value of the dVencPag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dVencPag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDVencPag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDVencPag() {
        if (dVencPag == null) {
            dVencPag = new ArrayList<String>();
        }
        return this.dVencPag;
    }

    /**
     * Obtiene el valor de la propiedad dContrato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDContrato() {
        return dContrato;
    }

    /**
     * Define el valor de la propiedad dContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDContrato(String value) {
        this.dContrato = value;
    }

    /**
     * Obtiene el valor de la propiedad dSalAnt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDSalAnt() {
        return dSalAnt;
    }

    /**
     * Define el valor de la propiedad dSalAnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDSalAnt(BigDecimal value) {
        this.dSalAnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dCodConDncp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCodConDncp() {
        return dCodConDncp;
    }

    /**
     * Define el valor de la propiedad dCodConDncp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCodConDncp(String value) {
        this.dCodConDncp = value;
    }

}
