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
 * <p>Clase Java para tdDesCatISC.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesCatISC"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SECCION I-(Cigarrillos,Tabacos,Esencias y Otros derivados del Tabaco)"/&gt;
 *     &lt;enumeration value="SECCION II - (Bebidas con y sin alcohol)"/&gt;
 *     &lt;enumeration value="SECCION III - (Alcoholes y Derivados del alcohol)"/&gt;
 *     &lt;enumeration value="SECCION IV - (Combustibles)"/&gt;
 *     &lt;enumeration value="SECCION V - (Artículos considerados de lujo)"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesCatISC")
@XmlEnum
public enum TdDesCatISC {

    @XmlEnumValue("SECCION I-(Cigarrillos,Tabacos,Esencias y Otros derivados del Tabaco)")
    SECCION_I_CIGARRILLOS_TABACOS_ESENCIAS_Y_OTROS_DERIVADOS_DEL_TABACO("SECCION I-(Cigarrillos,Tabacos,Esencias y Otros derivados del Tabaco)"),
    @XmlEnumValue("SECCION II - (Bebidas con y sin alcohol)")
    SECCION_II_BEBIDAS_CON_Y_SIN_ALCOHOL("SECCION II - (Bebidas con y sin alcohol)"),
    @XmlEnumValue("SECCION III - (Alcoholes y Derivados del alcohol)")
    SECCION_III_ALCOHOLES_Y_DERIVADOS_DEL_ALCOHOL("SECCION III - (Alcoholes y Derivados del alcohol)"),
    @XmlEnumValue("SECCION IV - (Combustibles)")
    SECCION_IV_COMBUSTIBLES("SECCION IV - (Combustibles)"),
    @XmlEnumValue("SECCION V - (Art\u00edculos considerados de lujo)")
    SECCION_V_ARTÍCULOS_CONSIDERADOS_DE_LUJO("SECCION V - (Art\u00edculos considerados de lujo)");
    private final String value;

    TdDesCatISC(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesCatISC fromValue(String v) {
        for (TdDesCatISC c: TdDesCatISC.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
