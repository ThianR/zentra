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
 * 				Campos que describen los items de la operacion
 * 			
 * 
 * <p>Clase Java para tgCamItem complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgCamItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dCodInt" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCodInt"/&gt;
 *         &lt;element name="dParAranc" type="{http://ekuatia.set.gov.py/sifen/xsd}tdParAranc" minOccurs="0"/&gt;
 *         &lt;element name="dNCM" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNCM" minOccurs="0"/&gt;
 *         &lt;element name="dDncpG" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDncpG" minOccurs="0"/&gt;
 *         &lt;element name="dDncpE" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDncpE" minOccurs="0"/&gt;
 *         &lt;element name="dGtin" type="{http://ekuatia.set.gov.py/sifen/xsd}tdGtin" minOccurs="0"/&gt;
 *         &lt;element name="dGtinPq" type="{http://ekuatia.set.gov.py/sifen/xsd}tdGtin" minOccurs="0"/&gt;
 *         &lt;element name="dDesProSer"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="2000"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cUniMed" type="{http://ekuatia.set.gov.py/sifen/xsd}tcUniMed"/&gt;
 *         &lt;element name="dDesUniMed" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesUniMed"/&gt;
 *         &lt;element name="dCantProSer" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCantProSer"/&gt;
 *         &lt;element name="cPaisOrig" type="{http://ekuatia.set.gov.py/sifen/xsd}paisType" minOccurs="0"/&gt;
 *         &lt;element name="dDesPaisOrig" type="{http://ekuatia.set.gov.py/sifen/xsd}tDesPais" minOccurs="0"/&gt;
 *         &lt;element name="dInfItem" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://ekuatia.set.gov.py/sifen/xsd}noEmptyString"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="500"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cRelMerc" type="{http://ekuatia.set.gov.py/sifen/xsd}tcRelMerc" minOccurs="0"/&gt;
 *         &lt;element name="dDesRelMerc" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesRelMerc" minOccurs="0"/&gt;
 *         &lt;element name="dCanQuiMer" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;totalDigits value="14"/&gt;
 *               &lt;fractionDigits value="4"/&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="9999999999.9999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dPorQuiMer" type="{http://ekuatia.set.gov.py/sifen/xsd}tPorcDesc8" minOccurs="0"/&gt;
 *         &lt;element name="dCDCAnticipo" type="{http://ekuatia.set.gov.py/sifen/xsd}tCDC" minOccurs="0"/&gt;
 *         &lt;element name="gValorItem" type="{http://ekuatia.set.gov.py/sifen/xsd}tgValorItem" minOccurs="0"/&gt;
 *         &lt;element name="gCamIVA" type="{http://ekuatia.set.gov.py/sifen/xsd}tgCamIVA" minOccurs="0"/&gt;
 *         &lt;element name="gRasMerc" type="{http://ekuatia.set.gov.py/sifen/xsd}tgRasMerc" minOccurs="0"/&gt;
 *         &lt;element name="gVehNuevo" type="{http://ekuatia.set.gov.py/sifen/xsd}tgVehNuevo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgCamItem", propOrder = {
    "dCodInt",
    "dParAranc",
    "dncm",
    "dDncpG",
    "dDncpE",
    "dGtin",
    "dGtinPq",
    "dDesProSer",
    "cUniMed",
    "dDesUniMed",
    "dCantProSer",
    "cPaisOrig",
    "dDesPaisOrig",
    "dInfItem",
    "cRelMerc",
    "dDesRelMerc",
    "dCanQuiMer",
    "dPorQuiMer",
    "dcdcAnticipo",
    "gValorItem",
    "gCamIVA",
    "gRasMerc",
    "gVehNuevo"
})
public class TgCamItem {

    @XmlElement(required = true)
    protected String dCodInt;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dParAranc;
    @XmlElement(name = "dNCM")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dncm;
    protected String dDncpG;
    protected String dDncpE;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dGtin;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger dGtinPq;
    @XmlElement(required = true)
    protected String dDesProSer;
    @XmlElement(required = true)
    protected BigInteger cUniMed;
    @XmlElement(required = true)
    protected String dDesUniMed;
    @XmlElement(required = true)
    protected BigDecimal dCantProSer;
    @XmlSchemaType(name = "string")
    protected PaisType cPaisOrig;
    protected String dDesPaisOrig;
    protected String dInfItem;
    protected BigInteger cRelMerc;
    @XmlSchemaType(name = "string")
    protected TdDesRelMerc dDesRelMerc;
    protected BigDecimal dCanQuiMer;
    protected BigDecimal dPorQuiMer;
    @XmlElement(name = "dCDCAnticipo")
    protected String dcdcAnticipo;
    protected TgValorItem gValorItem;
    protected TgCamIVA gCamIVA;
    protected TgRasMerc gRasMerc;
    protected TgVehNuevo gVehNuevo;

    /**
     * Obtiene el valor de la propiedad dCodInt.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCodInt() {
        return dCodInt;
    }

    /**
     * Define el valor de la propiedad dCodInt.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCodInt(String value) {
        this.dCodInt = value;
    }

    /**
     * Obtiene el valor de la propiedad dParAranc.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDParAranc() {
        return dParAranc;
    }

    /**
     * Define el valor de la propiedad dParAranc.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDParAranc(BigInteger value) {
        this.dParAranc = value;
    }

    /**
     * Obtiene el valor de la propiedad dncm.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDNCM() {
        return dncm;
    }

    /**
     * Define el valor de la propiedad dncm.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDNCM(BigInteger value) {
        this.dncm = value;
    }

    /**
     * Obtiene el valor de la propiedad dDncpG.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDncpG() {
        return dDncpG;
    }

    /**
     * Define el valor de la propiedad dDncpG.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDncpG(String value) {
        this.dDncpG = value;
    }

    /**
     * Obtiene el valor de la propiedad dDncpE.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDncpE() {
        return dDncpE;
    }

    /**
     * Define el valor de la propiedad dDncpE.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDncpE(String value) {
        this.dDncpE = value;
    }

    /**
     * Obtiene el valor de la propiedad dGtin.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDGtin() {
        return dGtin;
    }

    /**
     * Define el valor de la propiedad dGtin.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDGtin(BigInteger value) {
        this.dGtin = value;
    }

    /**
     * Obtiene el valor de la propiedad dGtinPq.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDGtinPq() {
        return dGtinPq;
    }

    /**
     * Define el valor de la propiedad dGtinPq.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDGtinPq(BigInteger value) {
        this.dGtinPq = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesProSer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesProSer() {
        return dDesProSer;
    }

    /**
     * Define el valor de la propiedad dDesProSer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesProSer(String value) {
        this.dDesProSer = value;
    }

    /**
     * Obtiene el valor de la propiedad cUniMed.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCUniMed() {
        return cUniMed;
    }

    /**
     * Define el valor de la propiedad cUniMed.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCUniMed(BigInteger value) {
        this.cUniMed = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesUniMed.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesUniMed() {
        return dDesUniMed;
    }

    /**
     * Define el valor de la propiedad dDesUniMed.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesUniMed(String value) {
        this.dDesUniMed = value;
    }

    /**
     * Obtiene el valor de la propiedad dCantProSer.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDCantProSer() {
        return dCantProSer;
    }

    /**
     * Define el valor de la propiedad dCantProSer.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDCantProSer(BigDecimal value) {
        this.dCantProSer = value;
    }

    /**
     * Obtiene el valor de la propiedad cPaisOrig.
     * 
     * @return
     *     possible object is
     *     {@link PaisType }
     *     
     */
    public PaisType getCPaisOrig() {
        return cPaisOrig;
    }

    /**
     * Define el valor de la propiedad cPaisOrig.
     * 
     * @param value
     *     allowed object is
     *     {@link PaisType }
     *     
     */
    public void setCPaisOrig(PaisType value) {
        this.cPaisOrig = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesPaisOrig.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesPaisOrig() {
        return dDesPaisOrig;
    }

    /**
     * Define el valor de la propiedad dDesPaisOrig.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesPaisOrig(String value) {
        this.dDesPaisOrig = value;
    }

    /**
     * Obtiene el valor de la propiedad dInfItem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDInfItem() {
        return dInfItem;
    }

    /**
     * Define el valor de la propiedad dInfItem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDInfItem(String value) {
        this.dInfItem = value;
    }

    /**
     * Obtiene el valor de la propiedad cRelMerc.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCRelMerc() {
        return cRelMerc;
    }

    /**
     * Define el valor de la propiedad cRelMerc.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCRelMerc(BigInteger value) {
        this.cRelMerc = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesRelMerc.
     * 
     * @return
     *     possible object is
     *     {@link TdDesRelMerc }
     *     
     */
    public TdDesRelMerc getDDesRelMerc() {
        return dDesRelMerc;
    }

    /**
     * Define el valor de la propiedad dDesRelMerc.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesRelMerc }
     *     
     */
    public void setDDesRelMerc(TdDesRelMerc value) {
        this.dDesRelMerc = value;
    }

    /**
     * Obtiene el valor de la propiedad dCanQuiMer.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDCanQuiMer() {
        return dCanQuiMer;
    }

    /**
     * Define el valor de la propiedad dCanQuiMer.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDCanQuiMer(BigDecimal value) {
        this.dCanQuiMer = value;
    }

    /**
     * Obtiene el valor de la propiedad dPorQuiMer.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDPorQuiMer() {
        return dPorQuiMer;
    }

    /**
     * Define el valor de la propiedad dPorQuiMer.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDPorQuiMer(BigDecimal value) {
        this.dPorQuiMer = value;
    }

    /**
     * Obtiene el valor de la propiedad dcdcAnticipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCDCAnticipo() {
        return dcdcAnticipo;
    }

    /**
     * Define el valor de la propiedad dcdcAnticipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCDCAnticipo(String value) {
        this.dcdcAnticipo = value;
    }

    /**
     * Obtiene el valor de la propiedad gValorItem.
     * 
     * @return
     *     possible object is
     *     {@link TgValorItem }
     *     
     */
    public TgValorItem getGValorItem() {
        return gValorItem;
    }

    /**
     * Define el valor de la propiedad gValorItem.
     * 
     * @param value
     *     allowed object is
     *     {@link TgValorItem }
     *     
     */
    public void setGValorItem(TgValorItem value) {
        this.gValorItem = value;
    }

    /**
     * Obtiene el valor de la propiedad gCamIVA.
     * 
     * @return
     *     possible object is
     *     {@link TgCamIVA }
     *     
     */
    public TgCamIVA getGCamIVA() {
        return gCamIVA;
    }

    /**
     * Define el valor de la propiedad gCamIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link TgCamIVA }
     *     
     */
    public void setGCamIVA(TgCamIVA value) {
        this.gCamIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad gRasMerc.
     * 
     * @return
     *     possible object is
     *     {@link TgRasMerc }
     *     
     */
    public TgRasMerc getGRasMerc() {
        return gRasMerc;
    }

    /**
     * Define el valor de la propiedad gRasMerc.
     * 
     * @param value
     *     allowed object is
     *     {@link TgRasMerc }
     *     
     */
    public void setGRasMerc(TgRasMerc value) {
        this.gRasMerc = value;
    }

    /**
     * Obtiene el valor de la propiedad gVehNuevo.
     * 
     * @return
     *     possible object is
     *     {@link TgVehNuevo }
     *     
     */
    public TgVehNuevo getGVehNuevo() {
        return gVehNuevo;
    }

    /**
     * Define el valor de la propiedad gVehNuevo.
     * 
     * @param value
     *     allowed object is
     *     {@link TgVehNuevo }
     *     
     */
    public void setGVehNuevo(TgVehNuevo value) {
        this.gVehNuevo = value;
    }

}
