//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import java.math.BigDecimal;
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
 * 				Campos inherentes a la operacion comercial
 * 			
 * 
 * <p>Clase Java para tgOpeCom complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgOpeCom"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTipTra" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipTra" minOccurs="0"/&gt;
 *         &lt;element name="dDesTipTra" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTiTran" minOccurs="0"/&gt;
 *         &lt;element name="iTImp" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTImp"/&gt;
 *         &lt;element name="dDesTImp" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesTImp"/&gt;
 *         &lt;element name="cMoneOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}cMondT"/&gt;
 *         &lt;element name="dDesMoneOpe" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDMoneTiPag"/&gt;
 *         &lt;element name="dCondTiCam" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCondTiCam" minOccurs="0"/&gt;
 *         &lt;element name="dTiCam" type="{http://ekuatia.set.gov.py/sifen/xsd}tTipoCambioBase" minOccurs="0"/&gt;
 *         &lt;element name="iCondAnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tiCondAnt" minOccurs="0"/&gt;
 *         &lt;element name="dDesCondAnt" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesCondAnt" minOccurs="0"/&gt;
 *         &lt;element name="gOblAfe" type="{http://ekuatia.set.gov.py/sifen/xsd}tgOblAfe" maxOccurs="12" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgOpeCom", propOrder = {
    "iTipTra",
    "dDesTipTra",
    "itImp",
    "dDesTImp",
    "cMoneOpe",
    "dDesMoneOpe",
    "dCondTiCam",
    "dTiCam",
    "iCondAnt",
    "dDesCondAnt",
    "gOblAfe"
})
public class TgOpeCom {

    @XmlSchemaType(name = "integer")
    protected Integer iTipTra;
    @XmlSchemaType(name = "string")
    protected TdDesTiTran dDesTipTra;
    @XmlElement(name = "iTImp", required = true)
    protected BigInteger itImp;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TdDesTImp dDesTImp;
    @XmlElement(required = true)
    @XmlSchemaType(name = "normalizedString")
    protected CMondT cMoneOpe;
    @XmlElement(required = true)
    protected String dDesMoneOpe;
    protected Short dCondTiCam;
    protected BigDecimal dTiCam;
    protected Short iCondAnt;
    @XmlSchemaType(name = "string")
    protected TdDesCondAnt dDesCondAnt;
    protected List<TgOblAfe> gOblAfe;

    /**
     * Obtiene el valor de la propiedad iTipTra.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getITipTra() {
        return iTipTra;
    }

    /**
     * Define el valor de la propiedad iTipTra.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setITipTra(Integer value) {
        this.iTipTra = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTipTra.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTiTran }
     *     
     */
    public TdDesTiTran getDDesTipTra() {
        return dDesTipTra;
    }

    /**
     * Define el valor de la propiedad dDesTipTra.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTiTran }
     *     
     */
    public void setDDesTipTra(TdDesTiTran value) {
        this.dDesTipTra = value;
    }

    /**
     * Obtiene el valor de la propiedad itImp.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITImp() {
        return itImp;
    }

    /**
     * Define el valor de la propiedad itImp.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITImp(BigInteger value) {
        this.itImp = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesTImp.
     * 
     * @return
     *     possible object is
     *     {@link TdDesTImp }
     *     
     */
    public TdDesTImp getDDesTImp() {
        return dDesTImp;
    }

    /**
     * Define el valor de la propiedad dDesTImp.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesTImp }
     *     
     */
    public void setDDesTImp(TdDesTImp value) {
        this.dDesTImp = value;
    }

    /**
     * Obtiene el valor de la propiedad cMoneOpe.
     * 
     * @return
     *     possible object is
     *     {@link CMondT }
     *     
     */
    public CMondT getCMoneOpe() {
        return cMoneOpe;
    }

    /**
     * Define el valor de la propiedad cMoneOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link CMondT }
     *     
     */
    public void setCMoneOpe(CMondT value) {
        this.cMoneOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesMoneOpe.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesMoneOpe() {
        return dDesMoneOpe;
    }

    /**
     * Define el valor de la propiedad dDesMoneOpe.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesMoneOpe(String value) {
        this.dDesMoneOpe = value;
    }

    /**
     * Obtiene el valor de la propiedad dCondTiCam.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getDCondTiCam() {
        return dCondTiCam;
    }

    /**
     * Define el valor de la propiedad dCondTiCam.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setDCondTiCam(Short value) {
        this.dCondTiCam = value;
    }

    /**
     * Obtiene el valor de la propiedad dTiCam.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDTiCam() {
        return dTiCam;
    }

    /**
     * Define el valor de la propiedad dTiCam.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDTiCam(BigDecimal value) {
        this.dTiCam = value;
    }

    /**
     * Obtiene el valor de la propiedad iCondAnt.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getICondAnt() {
        return iCondAnt;
    }

    /**
     * Define el valor de la propiedad iCondAnt.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setICondAnt(Short value) {
        this.iCondAnt = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesCondAnt.
     * 
     * @return
     *     possible object is
     *     {@link TdDesCondAnt }
     *     
     */
    public TdDesCondAnt getDDesCondAnt() {
        return dDesCondAnt;
    }

    /**
     * Define el valor de la propiedad dDesCondAnt.
     * 
     * @param value
     *     allowed object is
     *     {@link TdDesCondAnt }
     *     
     */
    public void setDDesCondAnt(TdDesCondAnt value) {
        this.dDesCondAnt = value;
    }

    /**
     * Gets the value of the gOblAfe property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the gOblAfe property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGOblAfe().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TgOblAfe }
     * 
     * 
     */
    public List<TgOblAfe> getGOblAfe() {
        if (gOblAfe == null) {
            gOblAfe = new ArrayList<TgOblAfe>();
        }
        return this.gOblAfe;
    }

}
