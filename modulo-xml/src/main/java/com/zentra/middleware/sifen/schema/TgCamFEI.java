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
 * 				Campos que componen la factura electronica de importacion FEI
 * 			
 * 
 * <p>Clase Java para tgCamFEI complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamFEI"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cTipRegImp" type="{http://ekuatia.set.gov.py/sifen/xsd}tcTipRegAdu"/&gt;
 *         &lt;element name="dPuLleg"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="5"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNuDesp"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="16"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dFecDesp" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecAAAAMMDDguion"/&gt;
 *         &lt;element name="dNomDesp"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="60"/&gt;
 *               &lt;minLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dRucDesp" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc"/&gt;
 *         &lt;element name="dDVDesp" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer"/&gt;
 *         &lt;element name="cPaisProd" type="{http://ekuatia.set.gov.py/sifen/xsd}paisType"/&gt;
 *         &lt;element name="dDesPaisProd" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesPais"/&gt;
 *         &lt;element name="dVend"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="30"/&gt;
 *               &lt;minLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dValorInv"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="12"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dValorFle"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="10"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dValorSeg"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="10"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *               &lt;minExclusive value="0"/&gt;
 *               &lt;maxInclusive value="9999999999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dValorImpGs"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;minExclusive value="0"/&gt;
 *               &lt;maxInclusive value="9999999999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dDerAdu"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="12"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *               &lt;minExclusive value="0"/&gt;
 *               &lt;maxInclusive value="99999999.9999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dIndi"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="12"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *               &lt;minExclusive value="0"/&gt;
 *               &lt;maxInclusive value="99999999.9999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dSerValor"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="12"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *               &lt;minExclusive value="0"/&gt;
 *               &lt;maxInclusive value="99999999.9999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dIVAImp"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="12"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *               &lt;minExclusive value="0"/&gt;
 *               &lt;maxInclusive value="99999999.9999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dTasaIntAd"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *               &lt;maxInclusive value="99"/&gt;
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
@XmlType(name = "tgCamFEI", propOrder = {
    "cTipRegImp",
    "dPuLleg",
    "dNuDesp",
    "dFecDesp",
    "dNomDesp",
    "dRucDesp",
    "ddvDesp",
    "cPaisProd",
    "dDesPaisProd",
    "dVend",
    "dValorInv",
    "dValorFle",
    "dValorSeg",
    "dValorImpGs",
    "dDerAdu",
    "dIndi",
    "dSerValor",
    "divaImp",
    "dTasaIntAd"
})
public class TgCamFEI {

    @XmlElement(required = true)
    protected BigInteger cTipRegImp;
    @XmlElement(required = true)
    protected String dPuLleg;
    @XmlElement(required = true)
    protected String dNuDesp;
    @XmlElement(required = true)
    protected String dFecDesp;
    @XmlElement(required = true)
    protected String dNomDesp;
    @XmlElement(required = true)
    protected String dRucDesp;
    @XmlElement(name = "dDVDesp", required = true)
    protected BigInteger ddvDesp;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected PaisType cPaisProd;
    @XmlElement(required = true)
    protected String dDesPaisProd;
    @XmlElement(required = true)
    protected String dVend;
    @XmlElement(required = true)
    protected BigDecimal dValorInv;
    @XmlElement(required = true)
    protected BigDecimal dValorFle;
    @XmlElement(required = true)
    protected BigDecimal dValorSeg;
    protected long dValorImpGs;
    @XmlElement(required = true)
    protected BigDecimal dDerAdu;
    @XmlElement(required = true)
    protected BigDecimal dIndi;
    @XmlElement(required = true)
    protected BigDecimal dSerValor;
    @XmlElement(name = "dIVAImp", required = true)
    protected BigDecimal divaImp;
    protected int dTasaIntAd;

    /**
     * Obtiene el valor de la propiedad cTipRegImp.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCTipRegImp() {
        return cTipRegImp;
    }

    /**
     * Define el valor de la propiedad cTipRegImp.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCTipRegImp(BigInteger value) {
        this.cTipRegImp = value;
    }

    /**
     * Obtiene el valor de la propiedad dPuLleg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDPuLleg() {
        return dPuLleg;
    }

    /**
     * Define el valor de la propiedad dPuLleg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDPuLleg(String value) {
        this.dPuLleg = value;
    }

    /**
     * Obtiene el valor de la propiedad dNuDesp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNuDesp() {
        return dNuDesp;
    }

    /**
     * Define el valor de la propiedad dNuDesp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNuDesp(String value) {
        this.dNuDesp = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecDesp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFecDesp() {
        return dFecDesp;
    }

    /**
     * Define el valor de la propiedad dFecDesp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFecDesp(String value) {
        this.dFecDesp = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomDesp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomDesp() {
        return dNomDesp;
    }

    /**
     * Define el valor de la propiedad dNomDesp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomDesp(String value) {
        this.dNomDesp = value;
    }

    /**
     * Obtiene el valor de la propiedad dRucDesp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRucDesp() {
        return dRucDesp;
    }

    /**
     * Define el valor de la propiedad dRucDesp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRucDesp(String value) {
        this.dRucDesp = value;
    }

    /**
     * Obtiene el valor de la propiedad ddvDesp.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDVDesp() {
        return ddvDesp;
    }

    /**
     * Define el valor de la propiedad ddvDesp.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDVDesp(BigInteger value) {
        this.ddvDesp = value;
    }

    /**
     * Obtiene el valor de la propiedad cPaisProd.
     * 
     * @return
     *     possible object is
     *     {@link PaisType }
     *     
     */
    public PaisType getCPaisProd() {
        return cPaisProd;
    }

    /**
     * Define el valor de la propiedad cPaisProd.
     * 
     * @param value
     *     allowed object is
     *     {@link PaisType }
     *     
     */
    public void setCPaisProd(PaisType value) {
        this.cPaisProd = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesPaisProd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesPaisProd() {
        return dDesPaisProd;
    }

    /**
     * Define el valor de la propiedad dDesPaisProd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesPaisProd(String value) {
        this.dDesPaisProd = value;
    }

    /**
     * Obtiene el valor de la propiedad dVend.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDVend() {
        return dVend;
    }

    /**
     * Define el valor de la propiedad dVend.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDVend(String value) {
        this.dVend = value;
    }

    /**
     * Obtiene el valor de la propiedad dValorInv.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDValorInv() {
        return dValorInv;
    }

    /**
     * Define el valor de la propiedad dValorInv.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDValorInv(BigDecimal value) {
        this.dValorInv = value;
    }

    /**
     * Obtiene el valor de la propiedad dValorFle.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDValorFle() {
        return dValorFle;
    }

    /**
     * Define el valor de la propiedad dValorFle.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDValorFle(BigDecimal value) {
        this.dValorFle = value;
    }

    /**
     * Obtiene el valor de la propiedad dValorSeg.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDValorSeg() {
        return dValorSeg;
    }

    /**
     * Define el valor de la propiedad dValorSeg.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDValorSeg(BigDecimal value) {
        this.dValorSeg = value;
    }

    /**
     * Obtiene el valor de la propiedad dValorImpGs.
     * 
     */
    public long getDValorImpGs() {
        return dValorImpGs;
    }

    /**
     * Define el valor de la propiedad dValorImpGs.
     * 
     */
    public void setDValorImpGs(long value) {
        this.dValorImpGs = value;
    }

    /**
     * Obtiene el valor de la propiedad dDerAdu.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDDerAdu() {
        return dDerAdu;
    }

    /**
     * Define el valor de la propiedad dDerAdu.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDDerAdu(BigDecimal value) {
        this.dDerAdu = value;
    }

    /**
     * Obtiene el valor de la propiedad dIndi.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDIndi() {
        return dIndi;
    }

    /**
     * Define el valor de la propiedad dIndi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDIndi(BigDecimal value) {
        this.dIndi = value;
    }

    /**
     * Obtiene el valor de la propiedad dSerValor.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDSerValor() {
        return dSerValor;
    }

    /**
     * Define el valor de la propiedad dSerValor.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDSerValor(BigDecimal value) {
        this.dSerValor = value;
    }

    /**
     * Obtiene el valor de la propiedad divaImp.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDIVAImp() {
        return divaImp;
    }

    /**
     * Define el valor de la propiedad divaImp.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDIVAImp(BigDecimal value) {
        this.divaImp = value;
    }

    /**
     * Obtiene el valor de la propiedad dTasaIntAd.
     * 
     */
    public int getDTasaIntAd() {
        return dTasaIntAd;
    }

    /**
     * Define el valor de la propiedad dTasaIntAd.
     * 
     */
    public void setDTasaIntAd(int value) {
        this.dTasaIntAd = value;
    }

}
