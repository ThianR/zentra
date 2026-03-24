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
 * <p>Clase Java para tdDesCondAnt.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesCondAnt"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Anticipo Global"/&gt;
 *     &lt;enumeration value="Anticipo por Ítem"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesCondAnt")
@XmlEnum
public enum TdDesCondAnt {


    /**
     * 
     * 						Corresponde al codigo 1 del campo iCondAnt
     * 					
     * 
     */
    @XmlEnumValue("Anticipo Global")
    ANTICIPO_GLOBAL("Anticipo Global"),

    /**
     * 
     * 						Corresponde al codigo 2 del campo iCondAnt
     * 					
     * 
     */
    @XmlEnumValue("Anticipo por \u00cdtem")
    ANTICIPO_POR_ÍTEM("Anticipo por \u00cdtem");
    private final String value;

    TdDesCondAnt(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesCondAnt fromValue(String v) {
        for (TdDesCondAnt c: TdDesCondAnt.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
