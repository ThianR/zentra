//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos específicos por tipo de documento electronico
 * 			
 * 
 * <p>Clase Java para tgDtipDE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgDtipDE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gCamFE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamFE" minOccurs="0"/&gt;
 *         &lt;element name="gCamAE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamAE" minOccurs="0"/&gt;
 *         &lt;element name="gCamNCDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamNCDE" minOccurs="0"/&gt;
 *         &lt;element name="gCamNRE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamNRE" minOccurs="0"/&gt;
 *         &lt;element name="gCamCond" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamCond" minOccurs="0"/&gt;
 *         &lt;element name="gCamItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamItem" maxOccurs="999"/&gt;
 *         &lt;element name="gCamEsp" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamEsp" minOccurs="0"/&gt;
 *         &lt;element name="gTransp" type="{http://ekuatia.set.gov.py/sifen/xsd}tgTransp" minOccurs="0"/&gt;
 *         &lt;element name="gCamRDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamRDE" maxOccurs="999" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgDtipDE", propOrder = {
    "gCamFE",
    "gCamAE",
    "gCamNCDE",
    "gCamNRE",
    "gCamCond",
    "gCamItem",
    "gCamEsp",
    "gTransp",
    "gCamRDE"
})
public class TgDtipDE {

    protected TgCamFE gCamFE;
    protected TgCamAE gCamAE;
    protected TgCamNCDE gCamNCDE;
    protected TgCamNRE gCamNRE;
    protected TgCamCond gCamCond;
    @XmlElement(required = true)
    protected List<TgCamItem> gCamItem;
    protected TgCamEsp gCamEsp;
    protected TgTransp gTransp;
    protected List<TgCamRDE> gCamRDE;

    /**
     * Obtiene el valor de la propiedad gCamFE.
     * 
     * @return
     *     possible object is
     *     {@link TgCamFE }
     *     
     */
    public TgCamFE getGCamFE() {
        return gCamFE;
    }

    /**
     * Define el valor de la propiedad gCamFE.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamFE }
     *     
     */
    public void setGCamFE(TgCamFE value) {
        this.gCamFE = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamAE.
     * 
     * @return
     *     possible object is
     *     {@link TgCamAE }
     *     
     */
    public TgCamAE getGCamAE() {
        return gCamAE;
    }

    /**
     * Define el valor de la propiedad gCamAE.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamAE }
     *     
     */
    public void setGCamAE(TgCamAE value) {
        this.gCamAE = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamNCDE.
     * 
     * @return
     *     possible object is
     *     {@link TgCamNCDE }
     *     
     */
    public TgCamNCDE getGCamNCDE() {
        return gCamNCDE;
    }

    /**
     * Define el valor de la propiedad gCamNCDE.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamNCDE }
     *     
     */
    public void setGCamNCDE(TgCamNCDE value) {
        this.gCamNCDE = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamNRE.
     * 
     * @return
     *     possible object is
     *     {@link TgCamNRE }
     *     
     */
    public TgCamNRE getGCamNRE() {
        return gCamNRE;
    }

    /**
     * Define el valor de la propiedad gCamNRE.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamNRE }
     *     
     */
    public void setGCamNRE(TgCamNRE value) {
        this.gCamNRE = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamCond.
     * 
     * @return
     *     possible object is
     *     {@link TgCamCond }
     *     
     */
    public TgCamCond getGCamCond() {
        return gCamCond;
    }

    /**
     * Define el valor de la propiedad gCamCond.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamCond }
     *     
     */
    public void setGCamCond(TgCamCond value) {
        this.gCamCond = value;
    }

    /**
     * Gets the value of the gCamItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gCamItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGCamItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgCamItem }
     * 
     * 
     */
    public List<TgCamItem> getGCamItem() {
        if (gCamItem == null) {
            gCamItem = new ArrayList<TgCamItem>();
        }
        return this.gCamItem;
    }

    /**
     * Obtiene el valor de la propiedad gCamEsp.
     * 
     * @return
     *     possible object is
     *     {@link TgCamEsp }
     *     
     */
    public TgCamEsp getGCamEsp() {
        return gCamEsp;
    }

    /**
     * Define el valor de la propiedad gCamEsp.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamEsp }
     *     
     */
    public void setGCamEsp(TgCamEsp value) {
        this.gCamEsp = value;
    }

    /**
     * Obtiene el valor de la propiedad gTransp.
     * 
     * @return
     *     possible object is
     *     {@link TgTransp }
     *     
     */
    public TgTransp getGTransp() {
        return gTransp;
    }

    /**
     * Define el valor de la propiedad gTransp.
     * 
     * @param value
     *     allowed object is
     *     {@link TgTransp }
     *     
     */
    public void setGTransp(TgTransp value) {
        this.gTransp = value;
    }

    /**
     * Gets the value of the gCamRDE property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gCamRDE property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGCamRDE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgCamRDE }
     * 
     * 
     */
    public List<TgCamRDE> getGCamRDE() {
        if (gCamRDE == null) {
            gCamRDE = new ArrayList<TgCamRDE>();
        }
        return this.gCamRDE;
    }

}
