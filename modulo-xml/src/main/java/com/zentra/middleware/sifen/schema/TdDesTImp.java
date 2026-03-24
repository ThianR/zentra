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
 * <p>Clase Java para tdDesTImp.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tdDesTImp"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="IVA"/&gt;
 *     &lt;enumeration value="ISC"/&gt;
 *     &lt;enumeration value="Renta"/&gt;
 *     &lt;enumeration value="Ninguno"/&gt;
 *     &lt;enumeration value="IVA - Renta"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tdDesTImp")
@XmlEnum
public enum TdDesTImp {


    /**
     * 
     * 						Corresponde al codigo 1 del campo dDesTimp
     * 					
     * 
     */
    IVA("IVA"),

    /**
     * 
     * 						Corresponde al codigo 2 del campo dDesTimp
     * 					
     * 
     */
    ISC("ISC"),

    /**
     * 
     * 						Corresponde al codigo 3 del campo dDesTimp
     * 					
     * 
     */
    @XmlEnumValue("Renta")
    RENTA("Renta"),

    /**
     * 
     * 						Corresponde al codigo 4 del campo dDesTimp
     * 					
     * 
     */
    @XmlEnumValue("Ninguno")
    NINGUNO("Ninguno"),

    /**
     * 
     * 						Corresponde al codigo 5 del campo dDesTimp
     * 					
     * 
     */
    @XmlEnumValue("IVA - Renta")
    IVA_RENTA("IVA - Renta");
    private final String value;

    TdDesTImp(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TdDesTImp fromValue(String v) {
        for (TdDesTImp c: TdDesTImp.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
