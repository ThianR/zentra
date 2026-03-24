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
 * <p>Clase Java para tdDesRelMerc.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesRelMerc"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;minLength value="19"/&gt;
 *     &lt;maxLength value="21"/&gt;
 *     &lt;enumeration value="Tolerancia de quiebra"/&gt;
 *     &lt;enumeration value="Tolerancia de merma"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesRelMerc")
@XmlEnum
public enum TdDesRelMerc {

    @XmlEnumValue("Tolerancia de quiebra")
    TOLERANCIA_DE_QUIEBRA("Tolerancia de quiebra"),
    @XmlEnumValue("Tolerancia de merma")
    TOLERANCIA_DE_MERMA("Tolerancia de merma");
    private final String value;

    TdDesRelMerc(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesRelMerc fromValue(String v) {
        for (TdDesRelMerc c: TdDesRelMerc.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
