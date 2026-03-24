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
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Sector de automotores nuevos y usados
 * 			
 * 
 * <p>Clase Java para tgVehNuevo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgVehNuevo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTipOpVN" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipOpVN" minOccurs="0"/&gt;
 *         &lt;element name="dDesTipOpVN" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTipOpVN" minOccurs="0"/&gt;
 *         &lt;element name="dChasis" type="{http://ekuatia.set.gov.py/sifen/xsd}tdChasis" minOccurs="0"/&gt;
 *         &lt;element name="dColor" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dPotencia" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dCapMot" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dPNet" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase6" minOccurs="0"/&gt;
 *         &lt;element name="dPBruto" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase6" minOccurs="0"/&gt;
 *         &lt;element name="iTipCom" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipCom" minOccurs="0"/&gt;
 *         &lt;element name="dDesTipCom" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTipCom" minOccurs="0"/&gt;
 *         &lt;element name="dNroMotor" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="21"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dCapTracc" type="{http://ekuatia.set.gov.py/sifen/xsd}tMontoBase6" minOccurs="0"/&gt;
 *         &lt;element name="dAnoFab" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;pattern value="[1-9][0-9]{3}"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cTipVeh" type="{http://ekuatia.set.gov.py/sifen/xsd}tcTipVeh" minOccurs="0"/&gt;
 *         &lt;element name="dCapac" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;totalDigits value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dCilin" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;pattern value="/d"/&gt;
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
@XmlType(name = "tgVehNuevo", propOrder = {
    "iTipOpVN",
    "dDesTipOpVN",
    "dChasis",
    "dColor",
    "dPotencia",
    "dCapMot",
    "dpNet",
    "dpBruto",
    "iTipCom",
    "dDesTipCom",
    "dNroMotor",
    "dCapTracc",
    "dAnoFab",
    "cTipVeh",
    "dCapac",
    "dCilin"
})
public class TgVehNuevo {

    protected Short iTipOpVN;
    protected String dDesTipOpVN;
    protected String dChasis;
    protected String dColor;
    protected BigInteger dPotencia;
    protected BigInteger dCapMot;
    @XmlElement(name = "dPNet")
    protected BigDecimal dpNet;
    @XmlElement(name = "dPBruto")
    protected BigDecimal dpBruto;
    protected BigInteger iTipCom;
    protected String dDesTipCom;
    protected String dNroMotor;
    protected BigDecimal dCapTracc;
    protected BigInteger dAnoFab;
    protected String cTipVeh;
    protected BigInteger dCapac;
    protected String dCilin;

    /**
     * Obtiene el valor de la propiedad iTipOpVN.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getITipOpVN() {
        return iTipOpVN;
    }

    /**
     * Define el valor de la propiedad iTipOpVN.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setITipOpVN(Short value) {
        this.iTipOpVN = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTipOpVN.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesTipOpVN() {
        return dDesTipOpVN;
    }

    /**
     * Define el valor de la propiedad dDesTipOpVN.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesTipOpVN(String value) {
        this.dDesTipOpVN = value;
    }

    /**
     * Obtiene el valor de la propiedad dChasis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDChasis() {
        return dChasis;
    }

    /**
     * Define el valor de la propiedad dChasis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDChasis(String value) {
        this.dChasis = value;
    }

    /**
     * Obtiene el valor de la propiedad dColor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDColor() {
        return dColor;
    }

    /**
     * Define el valor de la propiedad dColor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDColor(String value) {
        this.dColor = value;
    }

    /**
     * Obtiene el valor de la propiedad dPotencia.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDPotencia() {
        return dPotencia;
    }

    /**
     * Define el valor de la propiedad dPotencia.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDPotencia(BigInteger value) {
        this.dPotencia = value;
    }

    /**
     * Obtiene el valor de la propiedad dCapMot.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDCapMot() {
        return dCapMot;
    }

    /**
     * Define el valor de la propiedad dCapMot.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDCapMot(BigInteger value) {
        this.dCapMot = value;
    }

    /**
     * Obtiene el valor de la propiedad dpNet.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDPNet() {
        return dpNet;
    }

    /**
     * Define el valor de la propiedad dpNet.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDPNet(BigDecimal value) {
        this.dpNet = value;
    }

    /**
     * Obtiene el valor de la propiedad dpBruto.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDPBruto() {
        return dpBruto;
    }

    /**
     * Define el valor de la propiedad dpBruto.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDPBruto(BigDecimal value) {
        this.dpBruto = value;
    }

    /**
     * Obtiene el valor de la propiedad iTipCom.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipCom() {
        return iTipCom;
    }

    /**
     * Define el valor de la propiedad iTipCom.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipCom(BigInteger value) {
        this.iTipCom = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTipCom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesTipCom() {
        return dDesTipCom;
    }

    /**
     * Define el valor de la propiedad dDesTipCom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesTipCom(String value) {
        this.dDesTipCom = value;
    }

    /**
     * Obtiene el valor de la propiedad dNroMotor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNroMotor() {
        return dNroMotor;
    }

    /**
     * Define el valor de la propiedad dNroMotor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNroMotor(String value) {
        this.dNroMotor = value;
    }

    /**
     * Obtiene el valor de la propiedad dCapTracc.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDCapTracc() {
        return dCapTracc;
    }

    /**
     * Define el valor de la propiedad dCapTracc.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDCapTracc(BigDecimal value) {
        this.dCapTracc = value;
    }

    /**
     * Obtiene el valor de la propiedad dAnoFab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDAnoFab() {
        return dAnoFab;
    }

    /**
     * Define el valor de la propiedad dAnoFab.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDAnoFab(BigInteger value) {
        this.dAnoFab = value;
    }

    /**
     * Obtiene el valor de la propiedad cTipVeh.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCTipVeh() {
        return cTipVeh;
    }

    /**
     * Define el valor de la propiedad cTipVeh.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCTipVeh(String value) {
        this.cTipVeh = value;
    }

    /**
     * Obtiene el valor de la propiedad dCapac.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDCapac() {
        return dCapac;
    }

    /**
     * Define el valor de la propiedad dCapac.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDCapac(BigInteger value) {
        this.dCapac = value;
    }

    /**
     * Obtiene el valor de la propiedad dCilin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCilin() {
        return dCilin;
    }

    /**
     * Define el valor de la propiedad dCilin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCilin(String value) {
        this.dCilin = value;
    }

}
