//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Grupo de rastreo de la mercaderia
 * 			
 * 
 * <p>Clase Java para tgRasMerc complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgRasMerc"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dNumLote" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;pattern value=".+"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dVencMerc" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion" minOccurs="0"/&gt;
 *         &lt;element name="dNSerie" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNumPedi" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNumSegui" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNumReg" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCadenaBase20" minOccurs="0"/&gt;
 *         &lt;element name="dNumRegEntCom" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCadenaBase20" minOccurs="0"/&gt;
 *         &lt;element name="dNomPro" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNomPro" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgRasMerc", propOrder = {
    "dNumLote",
    "dVencMerc",
    "dnSerie",
    "dNumPedi",
    "dNumSegui",
    "dNumReg",
    "dNumRegEntCom",
    "dNomPro"
})
public class TgRasMerc {

    protected String dNumLote;
    protected String dVencMerc;
    @XmlElement(name = "dNSerie")
    protected String dnSerie;
    protected String dNumPedi;
    protected String dNumSegui;
    protected String dNumReg;
    protected String dNumRegEntCom;
    protected String dNomPro;

    /**
     * Obtiene el valor de la propiedad dNumLote.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumLote() {
        return dNumLote;
    }

    /**
     * Define el valor de la propiedad dNumLote.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumLote(String value) {
        this.dNumLote = value;
    }

    /**
     * Obtiene el valor de la propiedad dVencMerc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDVencMerc() {
        return dVencMerc;
    }

    /**
     * Define el valor de la propiedad dVencMerc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDVencMerc(String value) {
        this.dVencMerc = value;
    }

    /**
     * Obtiene el valor de la propiedad dnSerie.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNSerie() {
        return dnSerie;
    }

    /**
     * Define el valor de la propiedad dnSerie.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNSerie(String value) {
        this.dnSerie = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumPedi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumPedi() {
        return dNumPedi;
    }

    /**
     * Define el valor de la propiedad dNumPedi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumPedi(String value) {
        this.dNumPedi = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumSegui.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumSegui() {
        return dNumSegui;
    }

    /**
     * Define el valor de la propiedad dNumSegui.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumSegui(String value) {
        this.dNumSegui = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumReg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumReg() {
        return dNumReg;
    }

    /**
     * Define el valor de la propiedad dNumReg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumReg(String value) {
        this.dNumReg = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumRegEntCom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumRegEntCom() {
        return dNumRegEntCom;
    }

    /**
     * Define el valor de la propiedad dNumRegEntCom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumRegEntCom(String value) {
        this.dNumRegEntCom = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomPro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomPro() {
        return dNomPro;
    }

    /**
     * Define el valor de la propiedad dNomPro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomPro(String value) {
        this.dNomPro = value;
    }

}
