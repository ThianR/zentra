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
 * 				Campos que identifican al receptor del Documento Electrónico DE
 * 			
 * 
 * <p>Clase Java para tgDatRec complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgDatRec"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iNatRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tiNatRec"/&gt;
 *         &lt;element name="iTiOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTiOpe"/&gt;
 *         &lt;element name="cPaisRec" type="{http://ekuatia.set.gov.py/sifen/xsd}paisType"/&gt;
 *         &lt;element name="dDesPaisRe" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesPais"/&gt;
 *         &lt;element name="iTiContRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipCont" minOccurs="0"/&gt;
 *         &lt;element name="dRucRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tRuc" minOccurs="0"/&gt;
 *         &lt;element name="dDVRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tDVer" minOccurs="0"/&gt;
 *         &lt;element name="iTipIDRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipDocRec" minOccurs="0"/&gt;
 *         &lt;element name="dDTipIDRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDtipDocRec" minOccurs="0"/&gt;
 *         &lt;element name="dNumIDRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumDocId" minOccurs="0"/&gt;
 *         &lt;element name="dNomRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNombre"/&gt;
 *         &lt;element name="dNomFanRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNombre" minOccurs="0"/&gt;
 *         &lt;element name="dDirRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDirec" minOccurs="0"/&gt;
 *         &lt;element name="dNumCasRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumCas" minOccurs="0"/&gt;
 *         &lt;element name="cDepRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tDepartamentos" minOccurs="0"/&gt;
 *         &lt;element name="dDesDepRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDepartamento" minOccurs="0"/&gt;
 *         &lt;element name="cDisRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tDistrito" minOccurs="0"/&gt;
 *         &lt;element name="dDesDisRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesDistrito" minOccurs="0"/&gt;
 *         &lt;element name="cCiuRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tCiudad" minOccurs="0"/&gt;
 *         &lt;element name="dDesCiuRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesCiudad" minOccurs="0"/&gt;
 *         &lt;element name="dTelRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdTel" minOccurs="0"/&gt;
 *         &lt;element name="dCelRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCel" minOccurs="0"/&gt;
 *         &lt;element name="dEmailRec" type="{http://ekuatia.set.gov.py/sifen/xsd}tEmail" minOccurs="0"/&gt;
 *         &lt;element name="dCodCliente" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="3"/&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="15"/&gt;
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
@XmlType(name = "tgDatRec", propOrder = {
    "iNatRec",
    "iTiOpe",
    "cPaisRec",
    "dDesPaisRe",
    "iTiContRec",
    "dRucRec",
    "ddvRec",
    "iTipIDRec",
    "ddTipIDRec",
    "dNumIDRec",
    "dNomRec",
    "dNomFanRec",
    "dDirRec",
    "dNumCasRec",
    "cDepRec",
    "dDesDepRec",
    "cDisRec",
    "dDesDisRec",
    "cCiuRec",
    "dDesCiuRec",
    "dTelRec",
    "dCelRec",
    "dEmailRec",
    "dCodCliente"
})
public class TgDatRec {

    @XmlElement(required = true)
    protected BigInteger iNatRec;
    @XmlElement(required = true)
    protected BigInteger iTiOpe;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected PaisType cPaisRec;
    @XmlElement(required = true)
    protected String dDesPaisRe;
    protected BigInteger iTiContRec;
    protected String dRucRec;
    @XmlElement(name = "dDVRec")
    protected BigInteger ddvRec;
    protected BigInteger iTipIDRec;
    @XmlElement(name = "dDTipIDRec")
    protected String ddTipIDRec;
    protected String dNumIDRec;
    @XmlElement(required = true)
    protected String dNomRec;
    protected String dNomFanRec;
    protected String dDirRec;
    protected BigInteger dNumCasRec;
    protected BigInteger cDepRec;
    @XmlSchemaType(name = "string")
    protected TDesDepartamento dDesDepRec;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cDisRec;
    protected String dDesDisRec;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger cCiuRec;
    protected String dDesCiuRec;
    protected String dTelRec;
    protected String dCelRec;
    protected String dEmailRec;
    protected String dCodCliente;

    /**
     * Obtiene el valor de la propiedad iNatRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getINatRec() {
        return iNatRec;
    }

    /**
     * Define el valor de la propiedad iNatRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setINatRec(BigInteger value) {
        this.iNatRec = value;
    }

    /**
     * Obtiene el valor de la propiedad iTiOpe.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITiOpe() {
        return iTiOpe;
    }

    /**
     * Define el valor de la propiedad iTiOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITiOpe(BigInteger value) {
        this.iTiOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad cPaisRec.
     * 
     * @return
     *     possible object is
     *     {@link PaisType }
     *     
     */
    public PaisType getCPaisRec() {
        return cPaisRec;
    }

    /**
     * Define el valor de la propiedad cPaisRec.
     * 
     * @param value
     *     allowed object is
     *     {@link PaisType }
     *     
     */
    public void setCPaisRec(PaisType value) {
        this.cPaisRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesPaisRe.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesPaisRe() {
        return dDesPaisRe;
    }

    /**
     * Define el valor de la propiedad dDesPaisRe.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesPaisRe(String value) {
        this.dDesPaisRe = value;
    }

    /**
     * Obtiene el valor de la propiedad iTiContRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITiContRec() {
        return iTiContRec;
    }

    /**
     * Define el valor de la propiedad iTiContRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITiContRec(BigInteger value) {
        this.iTiContRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dRucRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRucRec() {
        return dRucRec;
    }

    /**
     * Define el valor de la propiedad dRucRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRucRec(String value) {
        this.dRucRec = value;
    }

    /**
     * Obtiene el valor de la propiedad ddvRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDDVRec() {
        return ddvRec;
    }

    /**
     * Define el valor de la propiedad ddvRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDDVRec(BigInteger value) {
        this.ddvRec = value;
    }

    /**
     * Obtiene el valor de la propiedad iTipIDRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipIDRec() {
        return iTipIDRec;
    }

    /**
     * Define el valor de la propiedad iTipIDRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipIDRec(BigInteger value) {
        this.iTipIDRec = value;
    }

    /**
     * Obtiene el valor de la propiedad ddTipIDRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDTipIDRec() {
        return ddTipIDRec;
    }

    /**
     * Define el valor de la propiedad ddTipIDRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDTipIDRec(String value) {
        this.ddTipIDRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumIDRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumIDRec() {
        return dNumIDRec;
    }

    /**
     * Define el valor de la propiedad dNumIDRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumIDRec(String value) {
        this.dNumIDRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomRec() {
        return dNomRec;
    }

    /**
     * Define el valor de la propiedad dNomRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomRec(String value) {
        this.dNomRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomFanRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomFanRec() {
        return dNomFanRec;
    }

    /**
     * Define el valor de la propiedad dNomFanRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomFanRec(String value) {
        this.dNomFanRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dDirRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDirRec() {
        return dDirRec;
    }

    /**
     * Define el valor de la propiedad dDirRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDirRec(String value) {
        this.dDirRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumCasRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNumCasRec() {
        return dNumCasRec;
    }

    /**
     * Define el valor de la propiedad dNumCasRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNumCasRec(BigInteger value) {
        this.dNumCasRec = value;
    }

    /**
     * Obtiene el valor de la propiedad cDepRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDepRec() {
        return cDepRec;
    }

    /**
     * Define el valor de la propiedad cDepRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDepRec(BigInteger value) {
        this.cDepRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDepRec.
     * 
     * @return
     *     possible object is
     *     {@link TDesDepartamento }
     *     
     */
    public TDesDepartamento getDDesDepRec() {
        return dDesDepRec;
    }

    /**
     * Define el valor de la propiedad dDesDepRec.
     * 
     * @param value
     *     allowed object is
     *     {@link TDesDepartamento }
     *     
     */
    public void setDDesDepRec(TDesDepartamento value) {
        this.dDesDepRec = value;
    }

    /**
     * Obtiene el valor de la propiedad cDisRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCDisRec() {
        return cDisRec;
    }

    /**
     * Define el valor de la propiedad cDisRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCDisRec(BigInteger value) {
        this.cDisRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesDisRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesDisRec() {
        return dDesDisRec;
    }

    /**
     * Define el valor de la propiedad dDesDisRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesDisRec(String value) {
        this.dDesDisRec = value;
    }

    /**
     * Obtiene el valor de la propiedad cCiuRec.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCCiuRec() {
        return cCiuRec;
    }

    /**
     * Define el valor de la propiedad cCiuRec.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCCiuRec(BigInteger value) {
        this.cCiuRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCiuRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesCiuRec() {
        return dDesCiuRec;
    }

    /**
     * Define el valor de la propiedad dDesCiuRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesCiuRec(String value) {
        this.dDesCiuRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dTelRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDTelRec() {
        return dTelRec;
    }

    /**
     * Define el valor de la propiedad dTelRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDTelRec(String value) {
        this.dTelRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dCelRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCelRec() {
        return dCelRec;
    }

    /**
     * Define el valor de la propiedad dCelRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCelRec(String value) {
        this.dCelRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dEmailRec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEmailRec() {
        return dEmailRec;
    }

    /**
     * Define el valor de la propiedad dEmailRec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEmailRec(String value) {
        this.dEmailRec = value;
    }

    /**
     * Obtiene el valor de la propiedad dCodCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCodCliente() {
        return dCodCliente;
    }

    /**
     * Define el valor de la propiedad dCodCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCodCliente(String value) {
        this.dCodCliente = value;
    }

}
