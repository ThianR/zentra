//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen las cuotas
 * 			
 * 
 * <p>Clase Java para tgCuotas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCuotas"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cMoneCuo" type="{http://ekuatia.set.gov.py/sifen/xsd}cMondT"/&gt;
 *         &lt;element name="dDMoneCuo" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDMoneTiPag"/&gt;
 *         &lt;element name="dMonCuota" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase4"/&gt;
 *         &lt;element name="dVencCuo" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCuotas", propOrder = {
    "cMoneCuo",
    "ddMoneCuo",
    "dMonCuota",
    "dVencCuo"
})
public class TgCuotas {

    @XmlElement(required = true)
    @XmlSchemaType(name = "normalizedString")
    protected CMondT cMoneCuo;
    @XmlElement(name = "dDMoneCuo", required = true)
    protected String ddMoneCuo;
    @XmlElement(required = true)
    protected BigDecimal dMonCuota;
    protected String dVencCuo;

    /**
     * Obtiene el valor de la propiedad cMoneCuo.
     * 
     * @return
     *     possible object is
     *     {@link CMondT }
     *     
     */
    public CMondT getCMoneCuo() {
        return cMoneCuo;
    }

    /**
     * Define el valor de la propiedad cMoneCuo.
     * 
     * @param value
     *     allowed object is
     *     {@link CMondT }
     *     
     */
    public void setCMoneCuo(CMondT value) {
        this.cMoneCuo = value;
    }

    /**
     * Obtiene el valor de la propiedad ddMoneCuo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDMoneCuo() {
        return ddMoneCuo;
    }

    /**
     * Define el valor de la propiedad ddMoneCuo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDMoneCuo(String value) {
        this.ddMoneCuo = value;
    }

    /**
     * Obtiene el valor de la propiedad dMonCuota.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDMonCuota() {
        return dMonCuota;
    }

    /**
     * Define el valor de la propiedad dMonCuota.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDMonCuota(BigDecimal value) {
        this.dMonCuota = value;
    }

    /**
     * Obtiene el valor de la propiedad dVencCuo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDVencCuo() {
        return dVencCuo;
    }

    /**
     * Define el valor de la propiedad dVencCuo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDVencCuo(String value) {
        this.dVencCuo = value;
    }

}
