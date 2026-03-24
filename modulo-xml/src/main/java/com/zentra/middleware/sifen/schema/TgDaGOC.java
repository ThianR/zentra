//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos Generales del Documento Electrónico DE
 * 			
 * 
 * <p>Clase Java para tgDaGOC complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgDaGOC"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dFeEmiDE" type="{http://ekuatia.set.gov.py/sifen/xsd}fecHhmmss"/&gt;
 *         &lt;element name="gOpeCom" type="{http://ekuatia.set.gov.py/sifen/xsd}tgOpeCom" minOccurs="0"/&gt;
 *         &lt;element name="gEmis" type="{http://ekuatia.set.gov.py/sifen/xsd}tgEmis"/&gt;
 *         &lt;element name="gDatRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tgDatRec"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgDaGOC", propOrder = {
    "dFeEmiDE",
    "gOpeCom",
    "gEmis",
    "gDatRec"
})
public class TgDaGOC {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dFeEmiDE;
    protected TgOpeCom gOpeCom;
    @XmlElement(required = true)
    protected TgEmis gEmis;
    @XmlElement(required = true)
    protected TgDatRec gDatRec;

    /**
     * Obtiene el valor de la propiedad dFeEmiDE.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDFeEmiDE() {
        return dFeEmiDE;
    }

    /**
     * Define el valor de la propiedad dFeEmiDE.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDFeEmiDE(XMLGregorianCalendar value) {
        this.dFeEmiDE = value;
    }

    /**
     * Obtiene el valor de la propiedad gOpeCom.
     * 
     * @return
     *     possible object is
     *     {@link TgOpeCom }
     *     
     */
    public TgOpeCom getGOpeCom() {
        return gOpeCom;
    }

    /**
     * Define el valor de la propiedad gOpeCom.
     * 
     * @param value
     *     allowed object is
     *     {@link TgOpeCom }
     *     
     */
    public void setGOpeCom(TgOpeCom value) {
        this.gOpeCom = value;
    }

    /**
     * Obtiene el valor de la propiedad gEmis.
     * 
     * @return
     *     possible object is
     *     {@link TgEmis }
     *     
     */
    public TgEmis getGEmis() {
        return gEmis;
    }

    /**
     * Define el valor de la propiedad gEmis.
     * 
     * @param value
     *     allowed object is
     *     {@link TgEmis }
     *     
     */
    public void setGEmis(TgEmis value) {
        this.gEmis = value;
    }

    /**
     * Obtiene el valor de la propiedad gDatRec.
     * 
     * @return
     *     possible object is
     *     {@link TgDatRec }
     *     
     */
    public TgDatRec getGDatRec() {
        return gDatRec;
    }

    /**
     * Define el valor de la propiedad gDatRec.
     * 
     * @param value
     *     allowed object is
     *     {@link TgDatRec }
     *     
     */
    public void setGDatRec(TgDatRec value) {
        this.gDatRec = value;
    }

}
