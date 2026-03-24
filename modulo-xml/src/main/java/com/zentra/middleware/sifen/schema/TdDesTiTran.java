//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tdDesTiTran.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesTiTran"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Venta de mercadería"/&gt;
 *     &lt;enumeration value="Prestación de servicios"/&gt;
 *     &lt;enumeration value="Mixto (Venta de mercadería y servicios)"/&gt;
 *     &lt;enumeration value="Venta de activo fijo"/&gt;
 *     &lt;enumeration value="Venta de divisas"/&gt;
 *     &lt;enumeration value="Compra de divisas"/&gt;
 *     &lt;enumeration value="Promoción o entrega de muestras"/&gt;
 *     &lt;enumeration value="Donación"/&gt;
 *     &lt;enumeration value="Anticipo"/&gt;
 *     &lt;enumeration value="Compra de productos"/&gt;
 *     &lt;enumeration value="Compra de servicios"/&gt;
 *     &lt;enumeration value="Venta de crédito fiscal"/&gt;
 *     &lt;enumeration value="Muestras médicas (Art. 3 RG 24/2014)"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesTiTran")
@XmlEnum
public enum TdDesTiTran {

    @XmlEnumValue("Venta de mercader\u00eda")
    VENTA_DE_MERCADERÍA("Venta de mercader\u00eda"),
    @XmlEnumValue("Prestaci\u00f3n de servicios")
    PRESTACIÓN_DE_SERVICIOS("Prestaci\u00f3n de servicios"),
    @XmlEnumValue("Mixto (Venta de mercader\u00eda y servicios)")
    MIXTO_VENTA_DE_MERCADERÍA_Y_SERVICIOS("Mixto (Venta de mercader\u00eda y servicios)"),
    @XmlEnumValue("Venta de activo fijo")
    VENTA_DE_ACTIVO_FIJO("Venta de activo fijo"),
    @XmlEnumValue("Venta de divisas")
    VENTA_DE_DIVISAS("Venta de divisas"),
    @XmlEnumValue("Compra de divisas")
    COMPRA_DE_DIVISAS("Compra de divisas"),
    @XmlEnumValue("Promoci\u00f3n o entrega de muestras")
    PROMOCIÓN_O_ENTREGA_DE_MUESTRAS("Promoci\u00f3n o entrega de muestras"),
    @XmlEnumValue("Donaci\u00f3n")
    DONACIÓN("Donaci\u00f3n"),
    @XmlEnumValue("Anticipo")
    ANTICIPO("Anticipo"),
    @XmlEnumValue("Compra de productos")
    COMPRA_DE_PRODUCTOS("Compra de productos"),
    @XmlEnumValue("Compra de servicios")
    COMPRA_DE_SERVICIOS("Compra de servicios"),
    @XmlEnumValue("Venta de cr\u00e9dito fiscal")
    VENTA_DE_CRÉDITO_FISCAL("Venta de cr\u00e9dito fiscal"),
    @XmlEnumValue("Muestras m\u00e9dicas (Art. 3 RG 24/2014)")
    MUESTRAS_MÉDICAS_ART_3_RG_24_2014("Muestras m\u00e9dicas (Art. 3 RG 24/2014)");
    private final String value;

    TdDesTiTran(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesTiTran fromValue(String v) {
        for (TdDesTiTran c: TdDesTiTran.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
