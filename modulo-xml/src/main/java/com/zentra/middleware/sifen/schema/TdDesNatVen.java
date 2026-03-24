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
 * <p>Clase Java para tdDesNatVen.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesNatVen"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;minLength value="10"/&gt;
 *     &lt;maxLength value="16"/&gt;
 *     &lt;enumeration value="No contribuyente"/&gt;
 *     &lt;enumeration value="Extranjero"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesNatVen")
@XmlEnum
public enum TdDesNatVen {

    @XmlEnumValue("No contribuyente")
    NO_CONTRIBUYENTE("No contribuyente"),
    @XmlEnumValue("Extranjero")
    EXTRANJERO("Extranjero");
    private final String value;

    TdDesNatVen(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesNatVen fromValue(String v) {
        for (TdDesNatVen c: TdDesNatVen.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
