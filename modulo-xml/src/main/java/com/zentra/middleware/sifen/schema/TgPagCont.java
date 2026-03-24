//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que describen la forma de pago al contado
 * 			
 * 
 * <p>Clase Java para tgPagCont complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgPagCont"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTiPago" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTiPago"/&gt;
 *         &lt;element name="dDesTiPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTiPag"/&gt;
 *         &lt;element name="dMonTiPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase4"/&gt;
 *         &lt;element name="cMoneTiPag" type="{http://ekuatia.set.gov.py/sifen/xsd}cMondT"/&gt;
 *         &lt;element name="dDMoneTiPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDMoneTiPag"/&gt;
 *         &lt;element name="dTiCamTiPag" type="{http://ekuatia.set.gov.py/sifen/xsd}tTipoCambioBase" minOccurs="0"/&gt;
 *         &lt;element name="gPagTarCD" type="{http://ekuatia.set.gov.py/sifen/xsd}tgPagTarCD" minOccurs="0"/&gt;
 *         &lt;element name="gPagCheq" type="{http://ekuatia.set.gov.py/sifen/xsd}tgPagCheq" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgPagCont", propOrder = {
    "iTiPago",
    "dDesTiPag",
    "dMonTiPag",
    "cMoneTiPag",
    "ddMoneTiPag",
    "dTiCamTiPag",
    "gPagTarCD",
    "gPagCheq"
})
public class TgPagCont {

    @XmlElement(required = true)
    protected BigInteger iTiPago;
    @XmlElement(required = true)
    protected String dDesTiPag;
    @XmlElement(required = true)
    protected BigDecimal dMonTiPag;
    @XmlElement(required = true)
    @XmlSchemaType(name = "normalizedString")
    protected CMondT cMoneTiPag;
    @XmlElement(name = "dDMoneTiPag", required = true)
    protected String ddMoneTiPag;
    protected BigDecimal dTiCamTiPag;
    protected TgPagTarCD gPagTarCD;
    protected TgPagCheq gPagCheq;

    /**
     * Obtiene el valor de la propiedad iTiPago.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITiPago() {
        return iTiPago;
    }

    /**
     * Define el valor de la propiedad iTiPago.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITiPago(BigInteger value) {
        this.iTiPago = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTiPag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesTiPag() {
        return dDesTiPag;
    }

    /**
     * Define el valor de la propiedad dDesTiPag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesTiPag(String value) {
        this.dDesTiPag = value;
    }

    /**
     * Obtiene el valor de la propiedad dMonTiPag.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDMonTiPag() {
        return dMonTiPag;
    }

    /**
     * Define el valor de la propiedad dMonTiPag.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDMonTiPag(BigDecimal value) {
        this.dMonTiPag = value;
    }

    /**
     * Obtiene el valor de la propiedad cMoneTiPag.
     * 
     * @return
     *     possible object is
     *     {@link CMondT }
     *     
     */
    public CMondT getCMoneTiPag() {
        return cMoneTiPag;
    }

    /**
     * Define el valor de la propiedad cMoneTiPag.
     * 
     * @param value
     *     allowed object is
     *     {@link CMondT }
     *     
     */
    public void setCMoneTiPag(CMondT value) {
        this.cMoneTiPag = value;
    }

    /**
     * Obtiene el valor de la propiedad ddMoneTiPag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDMoneTiPag() {
        return ddMoneTiPag;
    }

    /**
     * Define el valor de la propiedad ddMoneTiPag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDMoneTiPag(String value) {
        this.ddMoneTiPag = value;
    }

    /**
     * Obtiene el valor de la propiedad dTiCamTiPag.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTiCamTiPag() {
        return dTiCamTiPag;
    }

    /**
     * Define el valor de la propiedad dTiCamTiPag.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTiCamTiPag(BigDecimal value) {
        this.dTiCamTiPag = value;
    }

    /**
     * Obtiene el valor de la propiedad gPagTarCD.
     * 
     * @return
     *     possible object is
     *     {@link TgPagTarCD }
     *     
     */
    public TgPagTarCD getGPagTarCD() {
        return gPagTarCD;
    }

    /**
     * Define el valor de la propiedad gPagTarCD.
     * 
     * @param value
     *     allowed object is
     *     {@link TgPagTarCD }
     *     
     */
    public void setGPagTarCD(TgPagTarCD value) {
        this.gPagTarCD = value;
    }

    /**
     * Obtiene el valor de la propiedad gPagCheq.
     * 
     * @return
     *     possible object is
     *     {@link TgPagCheq }
     *     
     */
    public TgPagCheq getGPagCheq() {
        return gPagCheq;
    }

    /**
     * Define el valor de la propiedad gPagCheq.
     * 
     * @param value
     *     allowed object is
     *     {@link TgPagCheq }
     *     
     */
    public void setGPagCheq(TgPagCheq value) {
        this.gPagCheq = value;
    }

}
