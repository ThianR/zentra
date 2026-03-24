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
 * <p>Clase Java para tdDesTiDE.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesTiDE"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Factura electrónica"/&gt;
 *     &lt;enumeration value="Autofactura electrónica"/&gt;
 *     &lt;enumeration value="Nota de crédito electrónica"/&gt;
 *     &lt;enumeration value="Nota de débito electrónica"/&gt;
 *     &lt;enumeration value="Nota de remisión electrónica"/&gt;
 *     &lt;enumeration value="Boleta de venta electrónica"/&gt;
 *     &lt;enumeration value="Boleta resimple electrónica"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesTiDE")
@XmlEnum
public enum TdDesTiDE {

    @XmlEnumValue("Factura electr\u00f3nica")
    FACTURA_ELECTRÓNICA("Factura electr\u00f3nica"),
    @XmlEnumValue("Autofactura electr\u00f3nica")
    AUTOFACTURA_ELECTRÓNICA("Autofactura electr\u00f3nica"),
    @XmlEnumValue("Nota de cr\u00e9dito electr\u00f3nica")
    NOTA_DE_CRÉDITO_ELECTRÓNICA("Nota de cr\u00e9dito electr\u00f3nica"),
    @XmlEnumValue("Nota de d\u00e9bito electr\u00f3nica")
    NOTA_DE_DÉBITO_ELECTRÓNICA("Nota de d\u00e9bito electr\u00f3nica"),
    @XmlEnumValue("Nota de remisi\u00f3n electr\u00f3nica")
    NOTA_DE_REMISIÓN_ELECTRÓNICA("Nota de remisi\u00f3n electr\u00f3nica"),
    @XmlEnumValue("Boleta de venta electr\u00f3nica")
    BOLETA_DE_VENTA_ELECTRÓNICA("Boleta de venta electr\u00f3nica"),
    @XmlEnumValue("Boleta resimple electr\u00f3nica")
    BOLETA_RESIMPLE_ELECTRÓNICA("Boleta resimple electr\u00f3nica");
    private final String value;

    TdDesTiDE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesTiDE fromValue(String v) {
        for (TdDesTiDE c: TdDesTiDE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
