//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

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
 * 				Campos que describen la condición de la operación
 * 			
 * 
 * <p>Clase Java para tgCamCond complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamCond"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iCondOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tiCondOpe"/&gt;
 *         &lt;element name="dDCondOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDCondOpe"/&gt;
 *         &lt;element name="gPaConEIni" type="{http://ekuatia.set.gov.py/sifen/xsd}tgPagCont" maxOccurs="999" minOccurs="0"/&gt;
 *         &lt;element name="gPagCred" type="{http://ekuatia.set.gov.py/sifen/xsd}tgPagCred" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamCond", propOrder = {
    "iCondOpe",
    "ddCondOpe",
    "gPaConEIni",
    "gPagCred"
})
public class TgCamCond {

    @XmlElement(required = true)
    protected BigInteger iCondOpe;
    @XmlElement(name = "dDCondOpe", required = true)
    @XmlSchemaType(name = "string")
    protected TdDCondOpe ddCondOpe;
    protected List<TgPagCont> gPaConEIni;
    protected TgPagCred gPagCred;

    /**
     * Obtiene el valor de la propiedad iCondOpe.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getICondOpe() {
        return iCondOpe;
    }

    /**
     * Define el valor de la propiedad iCondOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setICondOpe(BigInteger value) {
        this.iCondOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad ddCondOpe.
     * 
     * @return
     *     possible object is
     *     {@link TdDCondOpe }
     *     
     */
    public TdDCondOpe getDDCondOpe() {
        return ddCondOpe;
    }

    /**
     * Define el valor de la propiedad ddCondOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDCondOpe }
     *     
     */
    public void setDDCondOpe(TdDCondOpe value) {
        this.ddCondOpe = value;
    }

    /**
     * Gets the value of the gPaConEIni property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gPaConEIni property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGPaConEIni().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgPagCont }
     * 
     * 
     */
    public List<TgPagCont> getGPaConEIni() {
        if (gPaConEIni == null) {
            gPaConEIni = new ArrayList<TgPagCont>();
        }
        return this.gPaConEIni;
    }

    /**
     * Obtiene el valor de la propiedad gPagCred.
     * 
     * @return
     *     possible object is
     *     {@link TgPagCred }
     *     
     */
    public TgPagCred getGPagCred() {
        return gPagCred;
    }

    /**
     * Define el valor de la propiedad gPagCred.
     * 
     * @param value
     *     allowed object is
     *     {@link TgPagCred }
     *     
     */
    public void setGPagCred(TgPagCred value) {
        this.gPagCred = value;
    }

}
