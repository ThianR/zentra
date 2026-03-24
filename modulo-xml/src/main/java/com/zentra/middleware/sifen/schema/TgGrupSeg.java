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
 * 				Datos del sector de seguros
 * 			
 * 
 * <p>Clase Java para tgGrupSeg complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgGrupSeg"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dCodEmpSeg" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="gGrupPolSeg" type="{http://ekuatia.set.gov.py/sifen/xsd}tgGrupPolSeg" maxOccurs="999"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgGrupSeg", propOrder = {
    "dCodEmpSeg",
    "gGrupPolSeg"
})
public class TgGrupSeg {

    protected String dCodEmpSeg;
    @XmlElement(required = true)
    protected List<TgGrupPolSeg> gGrupPolSeg;

    /**
     * Obtiene el valor de la propiedad dCodEmpSeg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCodEmpSeg() {
        return dCodEmpSeg;
    }

    /**
     * Define el valor de la propiedad dCodEmpSeg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCodEmpSeg(String value) {
        this.dCodEmpSeg = value;
    }

    /**
     * Gets the value of the gGrupPolSeg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gGrupPolSeg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGGrupPolSeg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgGrupPolSeg }
     * 
     * 
     */
    public List<TgGrupPolSeg> getGGrupPolSeg() {
        if (gGrupPolSeg == null) {
            gGrupPolSeg = new ArrayList<TgGrupPolSeg>();
        }
        return this.gGrupPolSeg;
    }

}
