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
 * <p>Clase Java para tdDesTipDocAso.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesTipDocAso"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Electrónico"/&gt;
 *     &lt;enumeration value="Impreso"/&gt;
 *     &lt;enumeration value="Constancia Electrónica"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesTipDocAso")
@XmlEnum
public enum TdDesTipDocAso {

    @XmlEnumValue("Electr\u00f3nico")
    ELECTRÓNICO("Electr\u00f3nico"),
    @XmlEnumValue("Impreso")
    IMPRESO("Impreso"),
    @XmlEnumValue("Constancia Electr\u00f3nica")
    CONSTANCIA_ELECTRÓNICA("Constancia Electr\u00f3nica");
    private final String value;

    TdDesTipDocAso(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesTipDocAso fromValue(String v) {
        for (TdDesTipDocAso c: TdDesTipDocAso.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
