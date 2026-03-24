//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * ISO 4217 Alpha
 * 
 * <p>Clase Java para CurrencyCodeType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CurrencyCodeType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://ekuatia.set.gov.py/sifen/xsd&gt;cMondT"&gt;
 *       &lt;attribute name="codeListID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="ISO 4217 Alpha" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrencyCodeType", propOrder = {
    "value"
})
public class CurrencyCodeType {

    @XmlValue
    protected CMondT value;
    @XmlAttribute(name = "codeListID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String codeListID;

    /**
     * Obtiene el valor de la propiedad value.
     * 
     * @return
     *     possible object is
     *     {@link CMondT }
     *     
     */
    public CMondT getValue() {
        return value;
    }

    /**
     * Define el valor de la propiedad value.
     * 
     * @param value
     *     allowed object is
     *     {@link CMondT }
     *     
     */
    public void setValue(CMondT value) {
        this.value = value;
    }

    /**
     * Obtiene el valor de la propiedad codeListID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeListID() {
        if (codeListID == null) {
            return "ISO 4217 Alpha";
        } else {
            return codeListID;
        }
    }

    /**
     * Define el valor de la propiedad codeListID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeListID(String value) {
        this.codeListID = value;
    }

}
