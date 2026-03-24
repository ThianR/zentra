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
 * <p>Clase Java para tdDesAfecIVA.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesAfecIVA"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Gravado IVA"/&gt;
 *     &lt;enumeration value="Exonerado (Art. 100 - Ley 6380/2019)"/&gt;
 *     &lt;enumeration value="Exento"/&gt;
 *     &lt;enumeration value="Gravado parcial (Grav- Exento)"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesAfecIVA")
@XmlEnum
public enum TdDesAfecIVA {

    @XmlEnumValue("Gravado IVA")
    GRAVADO_IVA("Gravado IVA"),
    @XmlEnumValue("Exonerado (Art. 100 - Ley 6380/2019)")
    EXONERADO_ART_100_LEY_6380_2019("Exonerado (Art. 100 - Ley 6380/2019)"),
    @XmlEnumValue("Exento")
    EXENTO("Exento"),
    @XmlEnumValue("Gravado parcial (Grav- Exento)")
    GRAVADO_PARCIAL_GRAV_EXENTO("Gravado parcial (Grav- Exento)");
    private final String value;

    TdDesAfecIVA(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesAfecIVA fromValue(String v) {
        for (TdDesAfecIVA c: TdDesAfecIVA.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
