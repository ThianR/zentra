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
 * <p>Clase Java para tdDesTIpoDoc.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesTIpoDoc"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Factura"/&gt;
 *     &lt;enumeration value="Nota de crédito"/&gt;
 *     &lt;enumeration value="Nota de débito"/&gt;
 *     &lt;enumeration value="Nota de remisión"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesTIpoDoc")
@XmlEnum
public enum TdDesTIpoDoc {

    @XmlEnumValue("Factura")
    FACTURA("Factura"),
    @XmlEnumValue("Nota de cr\u00e9dito")
    NOTA_DE_CRÉDITO("Nota de cr\u00e9dito"),
    @XmlEnumValue("Nota de d\u00e9bito")
    NOTA_DE_DÉBITO("Nota de d\u00e9bito"),
    @XmlEnumValue("Nota de remisi\u00f3n")
    NOTA_DE_REMISIÓN("Nota de remisi\u00f3n");
    private final String value;

    TdDesTIpoDoc(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesTIpoDoc fromValue(String v) {
        for (TdDesTIpoDoc c: TdDesTIpoDoc.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
