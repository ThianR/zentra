//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 				Campos que identifican al emisor del Documento Electrónico DE
 * 			
 * 
 * <p>Clase Java para tgEmis complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgEmis"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dRucEm" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc"/&gt;
 *         &lt;element name="dDVEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer"/&gt;
 *         &lt;element name="iTipCont" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipCont"/&gt;
 *         &lt;element name="cTipReg" type="{http://ekuatia.set.gov.py/sifen/xsd}tcTipReg" minOccurs="0"/&gt;
 *         &lt;element name="dNomEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNombre"/&gt;
 *         &lt;element name="dNomFanEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNombre" minOccurs="0"/&gt;
 *         &lt;element name="dDirEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec"/&gt;
 *         &lt;element name="dNumCas" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumCas"/&gt;
 *         &lt;element name="dCompDir1" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *         &lt;element name="dCompDir2" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *         &lt;element name="cDepEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tDepartamentos"/&gt;
 *         &lt;element name="dDesDepEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDepartamento"/&gt;
 *         &lt;element name="cDisEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tcDisEmi" minOccurs="0"/&gt;
 *         &lt;element name="dDesDisEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesDisEmi" minOccurs="0"/&gt;
 *         &lt;element name="cCiuEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tcCiuEmi"/&gt;
 *         &lt;element name="dDesCiuEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesCiuEmi"/&gt;
 *         &lt;element name="dTelEmi" type="{http://ekuatia.set.gov.py/sifen/xsd}tdTel"/&gt;
 *         &lt;element name="dEmailE" type="{http://ekuatia.set.gov.py/sifen/xsd}tEmail"/&gt;
 *         &lt;element name="dDenSuc" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;maxLength value="30"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="gActEco" type="{http://ekuatia.set.gov.py/sifen/xsd}tgActEco" maxOccurs="9"/&gt;
 *         &lt;element name="gRespDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tgRespDE" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgEmis", propOrder = {
    "dRucEm",
    "ddvEmi",
    "iTipCont",
    "cTipReg",
    "dNomEmi",
    "dNomFanEmi",
    "dDirEmi",
    "dNumCas",
    "dCompDir1",
    "dCompDir2",
    "cDepEmi",
    "dDesDepEmi",
    "cDisEmi",
    "dDesDisEmi",
    "cCiuEmi",
    "dDesCiuEmi",
    "dTelEmi",
    "dEmailE",
    "dDenSuc",
    "gActEco",
    "gRespDE"
})
public class TgEmis {

    @XmlElement(required = true)
    protected String dRucEm;
    @XmlElement(name = "dDVEmi", required = true)
    protected BigInteger ddvEmi;
    @XmlElement(required = true)
    protected BigInteger iTipCont;
    protected BigInteger cTipReg;
    @XmlElement(required = true)
    protected String dNomEmi;
    protected String dNomFanEmi;
    @XmlElement(required = true)
    protected String dDirEmi;
    @XmlElement(required = true)
    protected BigInteger dNumCas;
    protected String dCompDir1;
    protected String dCompDir2;
    @XmlElement(required = true)
    protected BigInteger cDepEmi;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TDesDepartamento dDesDepEmi;
    protected BigInteger cDisEmi;
    protected String dDesDisEmi;
    @XmlSchemaType(name = "integer")
    protected int cCiuEmi;
    @XmlElement(required = true)
    protected String dDesCiuEmi;
    @XmlElement(required = true)
    protected String dTelEmi;
    @XmlElement(required = true)
    protected String dEmailE;
    protected String dDenSuc;
    @XmlElement(required = true)
    protected List<TgActEco> gActEco;
    protected TgRespDE gRespDE;

    /**
     * Obtiene el valor de la propiedad dRucEm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRucEm() {
        return dRucEm;
    }

    /**
     * Define el valor de la propiedad dRucEm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRucEm(String value) {
        this.dRucEm = value;
    }

    /**
     * Obtiene el valor de la propiedad ddvEmi.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDVEmi() {
        return ddvEmi;
    }

    /**
     * Define el valor de la propiedad ddvEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDVEmi(BigInteger value) {
        this.ddvEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad iTipCont.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipCont() {
        return iTipCont;
    }

    /**
     * Define el valor de la propiedad iTipCont.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipCont(BigInteger value) {
        this.iTipCont = value;
    }

    /**
     * Obtiene el valor de la propiedad cTipReg.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCTipReg() {
        return cTipReg;
    }

    /**
     * Define el valor de la propiedad cTipReg.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCTipReg(BigInteger value) {
        this.cTipReg = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomEmi() {
        return dNomEmi;
    }

    /**
     * Define el valor de la propiedad dNomEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomEmi(String value) {
        this.dNomEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomFanEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomFanEmi() {
        return dNomFanEmi;
    }

    /**
     * Define el valor de la propiedad dNomFanEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomFanEmi(String value) {
        this.dNomFanEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dDirEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirEmi() {
        return dDirEmi;
    }

    /**
     * Define el valor de la propiedad dDirEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirEmi(String value) {
        this.dDirEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumCas.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNumCas() {
        return dNumCas;
    }

    /**
     * Define el valor de la propiedad dNumCas.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNumCas(BigInteger value) {
        this.dNumCas = value;
    }

    /**
     * Obtiene el valor de la propiedad dCompDir1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCompDir1() {
        return dCompDir1;
    }

    /**
     * Define el valor de la propiedad dCompDir1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCompDir1(String value) {
        this.dCompDir1 = value;
    }

    /**
     * Obtiene el valor de la propiedad dCompDir2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCompDir2() {
        return dCompDir2;
    }

    /**
     * Define el valor de la propiedad dCompDir2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCompDir2(String value) {
        this.dCompDir2 = value;
    }

    /**
     * Obtiene el valor de la propiedad cDepEmi.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDepEmi() {
        return cDepEmi;
    }

    /**
     * Define el valor de la propiedad cDepEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDepEmi(BigInteger value) {
        this.cDepEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDepEmi.
     * 
     * @return
     *     possible object is
     *     {@link TDesDepartamento }
     *     
     */
    public TDesDepartamento getDDesDepEmi() {
        return dDesDepEmi;
    }

    /**
     * Define el valor de la propiedad dDesDepEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link TDesDepartamento }
     *     
     */
    public void setDDesDepEmi(TDesDepartamento value) {
        this.dDesDepEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad cDisEmi.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDisEmi() {
        return cDisEmi;
    }

    /**
     * Define el valor de la propiedad cDisEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDisEmi(BigInteger value) {
        this.cDisEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDisEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDisEmi() {
        return dDesDisEmi;
    }

    /**
     * Define el valor de la propiedad dDesDisEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDisEmi(String value) {
        this.dDesDisEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad cCiuEmi.
     * 
     */
    public int getCCiuEmi() {
        return cCiuEmi;
    }

    /**
     * Define el valor de la propiedad cCiuEmi.
     * 
     */
    public void setCCiuEmi(int value) {
        this.cCiuEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCiuEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesCiuEmi() {
        return dDesCiuEmi;
    }

    /**
     * Define el valor de la propiedad dDesCiuEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesCiuEmi(String value) {
        this.dDesCiuEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dTelEmi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDTelEmi() {
        return dTelEmi;
    }

    /**
     * Define el valor de la propiedad dTelEmi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDTelEmi(String value) {
        this.dTelEmi = value;
    }

    /**
     * Obtiene el valor de la propiedad dEmailE.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEmailE() {
        return dEmailE;
    }

    /**
     * Define el valor de la propiedad dEmailE.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEmailE(String value) {
        this.dEmailE = value;
    }

    /**
     * Obtiene el valor de la propiedad dDenSuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDenSuc() {
        return dDenSuc;
    }

    /**
     * Define el valor de la propiedad dDenSuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDenSuc(String value) {
        this.dDenSuc = value;
    }

    /**
     * Gets the value of the gActEco property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gActEco property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGActEco().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgActEco }
     * 
     * 
     */
    public List<TgActEco> getGActEco() {
        if (gActEco == null) {
            gActEco = new ArrayList<TgActEco>();
        }
        return this.gActEco;
    }

    /**
     * Obtiene el valor de la propiedad gRespDE.
     * 
     * @return
     *     possible object is
     *     {@link TgRespDE }
     *     
     */
    public TgRespDE getGRespDE() {
        return gRespDE;
    }

    /**
     * Define el valor de la propiedad gRespDE.
     * 
     * @param value
     *     allowed object is
     *     {@link TgRespDE }
     *     
     */
    public void setGRespDE(TgRespDE value) {
        this.gRespDE = value;
    }

}
