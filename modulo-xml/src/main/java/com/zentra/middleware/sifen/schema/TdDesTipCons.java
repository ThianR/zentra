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
 * <p>Clase Java para tdDesTipCons.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesTipCons"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Constancia de no ser contribuyente"/&gt;
 *     &lt;enumeration value="Constancia de microproductores"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesTipCons")
@XmlEnum
public enum TdDesTipCons {

    @XmlEnumValue("Constancia de no ser contribuyente")
    CONSTANCIA_DE_NO_SER_CONTRIBUYENTE("Constancia de no ser contribuyente"),
    @XmlEnumValue("Constancia de microproductores")
    CONSTANCIA_DE_MICROPRODUCTORES("Constancia de microproductores");
    private final String value;

    TdDesTipCons(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesTipCons fromValue(String v) {
        for (TdDesTipCons c: TdDesTipCons.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
