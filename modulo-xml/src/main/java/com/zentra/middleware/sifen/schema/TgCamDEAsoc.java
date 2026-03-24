//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que identifican al documento asociado
 * 			
 * 
 * <p>Clase Java para tgCamDEAsoc complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamDEAsoc"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTipDocAso" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipDocAso"/&gt;
 *         &lt;element name="dDesTipDocAso" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTipDocAso"/&gt;
 *         &lt;element name="dCdCDERef" type="{http://ekuatia.set.gov.py/sifen/xsd}tCDC" minOccurs="0"/&gt;
 *         &lt;element name="dNTimDI" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumTim" minOccurs="0"/&gt;
 *         &lt;element name="dEstDocAso" type="{http://ekuatia.set.gov.py/sifen/xsd}tdEst" minOccurs="0"/&gt;
 *         &lt;element name="dPExpDocAso" type="{http://ekuatia.set.gov.py/sifen/xsd}tdPunExp" minOccurs="0"/&gt;
 *         &lt;element name="dNumDocAso" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumDoc" minOccurs="0"/&gt;
 *         &lt;element name="iTipoDocAso" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTIpoDoc" minOccurs="0"/&gt;
 *         &lt;element name="dDTipoDocAso" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTIpoDoc" minOccurs="0"/&gt;
 *         &lt;element name="dFecEmiDI" type="{http://ekuatia.set.gov.py/sifen/xsd}tFecDocImpAAAAMMDDguion" minOccurs="0"/&gt;
 *         &lt;element name="dNumComRet" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;length value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNumResCF" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;length value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="iTipCons" type="{http://ekuatia.set.gov.py/sifen/xsd}tdTipCons" minOccurs="0"/&gt;
 *         &lt;element name="dDesTipCons" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTipCons" minOccurs="0"/&gt;
 *         &lt;element name="dNumCons" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger"&gt;
 *               &lt;pattern value="[0-9]{11}"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dNumControl" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;pattern value="[0-9A-Za-z\-]{8}"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dRucFus" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc" minOccurs="0"/&gt;
 *         &lt;element name="dNumCuoDocAso" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="dImpCuoDocAso" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamDEAsoc", propOrder = {
    "iTipDocAso",
    "dDesTipDocAso",
    "dCdCDERef",
    "dnTimDI",
    "dEstDocAso",
    "dpExpDocAso",
    "dNumDocAso",
    "iTipoDocAso",
    "ddTipoDocAso",
    "dFecEmiDI",
    "dNumComRet",
    "dNumResCF",
    "iTipCons",
    "dDesTipCons",
    "dNumCons",
    "dNumControl",
    "dRucFus",
    "dNumCuoDocAso",
    "dImpCuoDocAso"
})
public class TgCamDEAsoc {

    @XmlElement(required = true)
    protected BigInteger iTipDocAso;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesTipDocAso dDesTipDocAso;
    protected String dCdCDERef;
    @XmlElement(name = "dNTimDI")
    protected String dnTimDI;
    protected String dEstDocAso;
    @XmlElement(name = "dPExpDocAso")
    protected String dpExpDocAso;
    protected String dNumDocAso;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger iTipoDocAso;
    @XmlElement(name = "dDTipoDocAso")
    @XmlSchemaType(name = "string")
    protected TdDesTIpoDoc ddTipoDocAso;
    protected String dFecEmiDI;
    protected String dNumComRet;
    protected String dNumResCF;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger iTipCons;
    @XmlSchemaType(name = "string")
    protected TdDesTipCons dDesTipCons;
    protected BigInteger dNumCons;
    protected String dNumControl;
    protected String dRucFus;
    protected Object dNumCuoDocAso;
    protected Object dImpCuoDocAso;

    /**
     * Obtiene el valor de la propiedad iTipDocAso.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipDocAso() {
        return iTipDocAso;
    }

    /**
     * Define el valor de la propiedad iTipDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipDocAso(BigInteger value) {
        this.iTipDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTipDocAso.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTipDocAso }
     *     
     */
    public TdDesTipDocAso getDDesTipDocAso() {
        return dDesTipDocAso;
    }

    /**
     * Define el valor de la propiedad dDesTipDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTipDocAso }
     *     
     */
    public void setDDesTipDocAso(TdDesTipDocAso value) {
        this.dDesTipDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad dCdCDERef.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCdCDERef() {
        return dCdCDERef;
    }

    /**
     * Define el valor de la propiedad dCdCDERef.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCdCDERef(String value) {
        this.dCdCDERef = value;
    }

    /**
     * Obtiene el valor de la propiedad dnTimDI.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNTimDI() {
        return dnTimDI;
    }

    /**
     * Define el valor de la propiedad dnTimDI.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNTimDI(String value) {
        this.dnTimDI = value;
    }

    /**
     * Obtiene el valor de la propiedad dEstDocAso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEstDocAso() {
        return dEstDocAso;
    }

    /**
     * Define el valor de la propiedad dEstDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEstDocAso(String value) {
        this.dEstDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad dpExpDocAso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDPExpDocAso() {
        return dpExpDocAso;
    }

    /**
     * Define el valor de la propiedad dpExpDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDPExpDocAso(String value) {
        this.dpExpDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumDocAso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumDocAso() {
        return dNumDocAso;
    }

    /**
     * Define el valor de la propiedad dNumDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumDocAso(String value) {
        this.dNumDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad iTipoDocAso.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipoDocAso() {
        return iTipoDocAso;
    }

    /**
     * Define el valor de la propiedad iTipoDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipoDocAso(BigInteger value) {
        this.iTipoDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad ddTipoDocAso.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTIpoDoc }
     *     
     */
    public TdDesTIpoDoc getDDTipoDocAso() {
        return ddTipoDocAso;
    }

    /**
     * Define el valor de la propiedad ddTipoDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTIpoDoc }
     *     
     */
    public void setDDTipoDocAso(TdDesTIpoDoc value) {
        this.ddTipoDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad dFecEmiDI.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFecEmiDI() {
        return dFecEmiDI;
    }

    /**
     * Define el valor de la propiedad dFecEmiDI.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFecEmiDI(String value) {
        this.dFecEmiDI = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumComRet.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumComRet() {
        return dNumComRet;
    }

    /**
     * Define el valor de la propiedad dNumComRet.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumComRet(String value) {
        this.dNumComRet = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumResCF.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumResCF() {
        return dNumResCF;
    }

    /**
     * Define el valor de la propiedad dNumResCF.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumResCF(String value) {
        this.dNumResCF = value;
    }

    /**
     * Obtiene el valor de la propiedad iTipCons.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipCons() {
        return iTipCons;
    }

    /**
     * Define el valor de la propiedad iTipCons.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipCons(BigInteger value) {
        this.iTipCons = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTipCons.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTipCons }
     *     
     */
    public TdDesTipCons getDDesTipCons() {
        return dDesTipCons;
    }

    /**
     * Define el valor de la propiedad dDesTipCons.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTipCons }
     *     
     */
    public void setDDesTipCons(TdDesTipCons value) {
        this.dDesTipCons = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumCons.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNumCons() {
        return dNumCons;
    }

    /**
     * Define el valor de la propiedad dNumCons.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNumCons(BigInteger value) {
        this.dNumCons = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumControl.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumControl() {
        return dNumControl;
    }

    /**
     * Define el valor de la propiedad dNumControl.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumControl(String value) {
        this.dNumControl = value;
    }

    /**
     * Obtiene el valor de la propiedad dRucFus.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRucFus() {
        return dRucFus;
    }

    /**
     * Define el valor de la propiedad dRucFus.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRucFus(String value) {
        this.dRucFus = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumCuoDocAso.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDNumCuoDocAso() {
        return dNumCuoDocAso;
    }

    /**
     * Define el valor de la propiedad dNumCuoDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDNumCuoDocAso(Object value) {
        this.dNumCuoDocAso = value;
    }

    /**
     * Obtiene el valor de la propiedad dImpCuoDocAso.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDImpCuoDocAso() {
        return dImpCuoDocAso;
    }

    /**
     * Define el valor de la propiedad dImpCuoDocAso.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDImpCuoDocAso(Object value) {
        this.dImpCuoDocAso = value;
    }

}
