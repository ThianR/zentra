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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos complementarios comerciales de uso especifico
 * 			
 * 
 * <p>Clase Java para tgCamEsp complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamEsp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gGrupEner" type="{http://ekuatia.set.gov.py/sifen/xsd}tgGrupEner" maxOccurs="9" minOccurs="0"/&gt;
 *         &lt;element name="gGrupSeg" type="{http://ekuatia.set.gov.py/sifen/xsd}tgGrupSeg" minOccurs="0"/&gt;
 *         &lt;element name="gGrupSup" type="{http://ekuatia.set.gov.py/sifen/xsd}tgGrupSup" minOccurs="0"/&gt;
 *         &lt;element name="gGrupAdi" type="{http://ekuatia.set.gov.py/sifen/xsd}tgGrupAdi" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamEsp", propOrder = {
    "gGrupEner",
    "gGrupSeg",
    "gGrupSup",
    "gGrupAdi"
})
public class TgCamEsp {

    protected List<TgGrupEner> gGrupEner;
    protected TgGrupSeg gGrupSeg;
    protected TgGrupSup gGrupSup;
    protected TgGrupAdi gGrupAdi;

    /**
     * Gets the value of the gGrupEner property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gGrupEner property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGGrupEner().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgGrupEner }
     * 
     * 
     */
    public List<TgGrupEner> getGGrupEner() {
        if (gGrupEner == null) {
            gGrupEner = new ArrayList<TgGrupEner>();
        }
        return this.gGrupEner;
    }

    /**
     * Obtiene el valor de la propiedad gGrupSeg.
     * 
     * @return
     *     possible object is
     *     {@link TgGrupSeg }
     *     
     */
    public TgGrupSeg getGGrupSeg() {
        return gGrupSeg;
    }

    /**
     * Define el valor de la propiedad gGrupSeg.
     * 
     * @param value
     *     allowed object is
     *     {@link TgGrupSeg }
     *     
     */
    public void setGGrupSeg(TgGrupSeg value) {
        this.gGrupSeg = value;
    }

    /**
     * Obtiene el valor de la propiedad gGrupSup.
     * 
     * @return
     *     possible object is
     *     {@link TgGrupSup }
     *     
     */
    public TgGrupSup getGGrupSup() {
        return gGrupSup;
    }

    /**
     * Define el valor de la propiedad gGrupSup.
     * 
     * @param value
     *     allowed object is
     *     {@link TgGrupSup }
     *     
     */
    public void setGGrupSup(TgGrupSup value) {
        this.gGrupSup = value;
    }

    /**
     * Obtiene el valor de la propiedad gGrupAdi.
     * 
     * @return
     *     possible object is
     *     {@link TgGrupAdi }
     *     
     */
    public TgGrupAdi getGGrupAdi() {
        return gGrupAdi;
    }

    /**
     * Define el valor de la propiedad gGrupAdi.
     * 
     * @param value
     *     allowed object is
     *     {@link TgGrupAdi }
     *     
     */
    public void setGGrupAdi(TgGrupAdi value) {
        this.gGrupAdi = value;
    }

}
