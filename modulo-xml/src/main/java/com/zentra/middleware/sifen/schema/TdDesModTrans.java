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
 * <p>Clase Java para tdDesModTrans.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesModTrans"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Terrestre"/&gt;
 *     &lt;enumeration value="Fluvial"/&gt;
 *     &lt;enumeration value="Aéreo"/&gt;
 *     &lt;enumeration value="Multimodal"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesModTrans")
@XmlEnum
public enum TdDesModTrans {

    @XmlEnumValue("Terrestre")
    TERRESTRE("Terrestre"),
    @XmlEnumValue("Fluvial")
    FLUVIAL("Fluvial"),
    @XmlEnumValue("A\u00e9reo")
    AÉREO("A\u00e9reo"),
    @XmlEnumValue("Multimodal")
    MULTIMODAL("Multimodal");
    private final String value;

    TdDesModTrans(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesModTrans fromValue(String v) {
        for (TdDesModTrans c: TdDesModTrans.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
