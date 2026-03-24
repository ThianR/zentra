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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				F. Campos que describen los subtotales y totales de la transacción
 * 				documentada
 * 			
 * 
 * <p>Clase Java para tgTotSub complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgTotSub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dSubExe" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dSubExo" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dSub5" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dSub10" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dTotOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dTotDesc" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dTotDescGlotem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dTotAntItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dTotAnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dPorcDescTotal" type="{http://ekuatia.set.gov.py/sifen/xsd}tPorcDesc8"/&gt;
 *         &lt;element name="dDescTotal" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dAnticipo" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dRedon" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCRed"/&gt;
 *         &lt;element name="dComi" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dTotGralOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase"/&gt;
 *         &lt;element name="dIVA5" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dIVA10" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dLiqTotIVA5" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dLiqTotIVA10" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dIVAComi" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dTotIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dBaseGrav5" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dBaseGrav10" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dTBasGraIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *         &lt;element name="dTotalGs" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgTotSub", propOrder = {
    "dSubExe",
    "dSubExo",
    "dSub5",
    "dSub10",
    "dTotOpe",
    "dTotDesc",
    "dTotDescGlotem",
    "dTotAntItem",
    "dTotAnt",
    "dPorcDescTotal",
    "dDescTotal",
    "dAnticipo",
    "dRedon",
    "dComi",
    "dTotGralOpe",
    "diva5",
    "diva10",
    "dLiqTotIVA5",
    "dLiqTotIVA10",
    "divaComi",
    "dTotIVA",
    "dBaseGrav5",
    "dBaseGrav10",
    "dtBasGraIVA",
    "dTotalGs"
})
public class TgTotSub {

    protected BigDecimal dSubExe;
    protected BigDecimal dSubExo;
    protected BigDecimal dSub5;
    protected BigDecimal dSub10;
    @XmlElement(required = true)
    protected BigDecimal dTotOpe;
    @XmlElement(required = true)
    protected BigDecimal dTotDesc;
    @XmlElement(required = true)
    protected BigDecimal dTotDescGlotem;
    @XmlElement(required = true)
    protected BigDecimal dTotAntItem;
    @XmlElement(required = true)
    protected BigDecimal dTotAnt;
    @XmlElement(required = true)
    protected BigDecimal dPorcDescTotal;
    @XmlElement(required = true)
    protected BigDecimal dDescTotal;
    @XmlElement(required = true)
    protected BigDecimal dAnticipo;
    @XmlElement(required = true)
    protected BigDecimal dRedon;
    protected BigDecimal dComi;
    @XmlElement(required = true)
    protected BigDecimal dTotGralOpe;
    @XmlElement(name = "dIVA5")
    protected BigDecimal diva5;
    @XmlElement(name = "dIVA10")
    protected BigDecimal diva10;
    protected BigDecimal dLiqTotIVA5;
    protected BigDecimal dLiqTotIVA10;
    @XmlElement(name = "dIVAComi")
    protected BigDecimal divaComi;
    protected BigDecimal dTotIVA;
    protected BigDecimal dBaseGrav5;
    protected BigDecimal dBaseGrav10;
    @XmlElement(name = "dTBasGraIVA")
    protected BigDecimal dtBasGraIVA;
    protected BigDecimal dTotalGs;

    /**
     * Obtiene el valor de la propiedad dSubExe.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDSubExe() {
        return dSubExe;
    }

    /**
     * Define el valor de la propiedad dSubExe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDSubExe(BigDecimal value) {
        this.dSubExe = value;
    }

    /**
     * Obtiene el valor de la propiedad dSubExo.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDSubExo() {
        return dSubExo;
    }

    /**
     * Define el valor de la propiedad dSubExo.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDSubExo(BigDecimal value) {
        this.dSubExo = value;
    }

    /**
     * Obtiene el valor de la propiedad dSub5.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDSub5() {
        return dSub5;
    }

    /**
     * Define el valor de la propiedad dSub5.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDSub5(BigDecimal value) {
        this.dSub5 = value;
    }

    /**
     * Obtiene el valor de la propiedad dSub10.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDSub10() {
        return dSub10;
    }

    /**
     * Define el valor de la propiedad dSub10.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDSub10(BigDecimal value) {
        this.dSub10 = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotOpe.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotOpe() {
        return dTotOpe;
    }

    /**
     * Define el valor de la propiedad dTotOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotOpe(BigDecimal value) {
        this.dTotOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotDesc.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotDesc() {
        return dTotDesc;
    }

    /**
     * Define el valor de la propiedad dTotDesc.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotDesc(BigDecimal value) {
        this.dTotDesc = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotDescGlotem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotDescGlotem() {
        return dTotDescGlotem;
    }

    /**
     * Define el valor de la propiedad dTotDescGlotem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotDescGlotem(BigDecimal value) {
        this.dTotDescGlotem = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotAntItem.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotAntItem() {
        return dTotAntItem;
    }

    /**
     * Define el valor de la propiedad dTotAntItem.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotAntItem(BigDecimal value) {
        this.dTotAntItem = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotAnt.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotAnt() {
        return dTotAnt;
    }

    /**
     * Define el valor de la propiedad dTotAnt.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotAnt(BigDecimal value) {
        this.dTotAnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dPorcDescTotal.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDPorcDescTotal() {
        return dPorcDescTotal;
    }

    /**
     * Define el valor de la propiedad dPorcDescTotal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDPorcDescTotal(BigDecimal value) {
        this.dPorcDescTotal = value;
    }

    /**
     * Obtiene el valor de la propiedad dDescTotal.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDDescTotal() {
        return dDescTotal;
    }

    /**
     * Define el valor de la propiedad dDescTotal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDDescTotal(BigDecimal value) {
        this.dDescTotal = value;
    }

    /**
     * Obtiene el valor de la propiedad dAnticipo.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDAnticipo() {
        return dAnticipo;
    }

    /**
     * Define el valor de la propiedad dAnticipo.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDAnticipo(BigDecimal value) {
        this.dAnticipo = value;
    }

    /**
     * Obtiene el valor de la propiedad dRedon.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDRedon() {
        return dRedon;
    }

    /**
     * Define el valor de la propiedad dRedon.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDRedon(BigDecimal value) {
        this.dRedon = value;
    }

    /**
     * Obtiene el valor de la propiedad dComi.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDComi() {
        return dComi;
    }

    /**
     * Define el valor de la propiedad dComi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDComi(BigDecimal value) {
        this.dComi = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotGralOpe.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotGralOpe() {
        return dTotGralOpe;
    }

    /**
     * Define el valor de la propiedad dTotGralOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotGralOpe(BigDecimal value) {
        this.dTotGralOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad diva5.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDIVA5() {
        return diva5;
    }

    /**
     * Define el valor de la propiedad diva5.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDIVA5(BigDecimal value) {
        this.diva5 = value;
    }

    /**
     * Obtiene el valor de la propiedad diva10.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDIVA10() {
        return diva10;
    }

    /**
     * Define el valor de la propiedad diva10.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDIVA10(BigDecimal value) {
        this.diva10 = value;
    }

    /**
     * Obtiene el valor de la propiedad dLiqTotIVA5.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDLiqTotIVA5() {
        return dLiqTotIVA5;
    }

    /**
     * Define el valor de la propiedad dLiqTotIVA5.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDLiqTotIVA5(BigDecimal value) {
        this.dLiqTotIVA5 = value;
    }

    /**
     * Obtiene el valor de la propiedad dLiqTotIVA10.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDLiqTotIVA10() {
        return dLiqTotIVA10;
    }

    /**
     * Define el valor de la propiedad dLiqTotIVA10.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDLiqTotIVA10(BigDecimal value) {
        this.dLiqTotIVA10 = value;
    }

    /**
     * Obtiene el valor de la propiedad divaComi.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDIVAComi() {
        return divaComi;
    }

    /**
     * Define el valor de la propiedad divaComi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDIVAComi(BigDecimal value) {
        this.divaComi = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotIVA.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotIVA() {
        return dTotIVA;
    }

    /**
     * Define el valor de la propiedad dTotIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotIVA(BigDecimal value) {
        this.dTotIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad dBaseGrav5.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDBaseGrav5() {
        return dBaseGrav5;
    }

    /**
     * Define el valor de la propiedad dBaseGrav5.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDBaseGrav5(BigDecimal value) {
        this.dBaseGrav5 = value;
    }

    /**
     * Obtiene el valor de la propiedad dBaseGrav10.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDBaseGrav10() {
        return dBaseGrav10;
    }

    /**
     * Define el valor de la propiedad dBaseGrav10.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDBaseGrav10(BigDecimal value) {
        this.dBaseGrav10 = value;
    }

    /**
     * Obtiene el valor de la propiedad dtBasGraIVA.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTBasGraIVA() {
        return dtBasGraIVA;
    }

    /**
     * Define el valor de la propiedad dtBasGraIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTBasGraIVA(BigDecimal value) {
        this.dtBasGraIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad dTotalGs.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTotalGs() {
        return dTotalGs;
    }

    /**
     * Define el valor de la propiedad dTotalGs.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTotalGs(BigDecimal value) {
        this.dTotalGs = value;
    }

}
