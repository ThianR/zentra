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
 * <p>Clase Java para tDesDepartamento.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tDesDepartamento"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CAPITAL"/&gt;
 *     &lt;enumeration value="CONCEPCION"/&gt;
 *     &lt;enumeration value="SAN PEDRO"/&gt;
 *     &lt;enumeration value="CORDILLERA"/&gt;
 *     &lt;enumeration value="GUAIRA"/&gt;
 *     &lt;enumeration value="CAAGUAZU"/&gt;
 *     &lt;enumeration value="CAAZAPA"/&gt;
 *     &lt;enumeration value="ITAPUA"/&gt;
 *     &lt;enumeration value="MISIONES"/&gt;
 *     &lt;enumeration value="PARAGUARI"/&gt;
 *     &lt;enumeration value="ALTO PARANA"/&gt;
 *     &lt;enumeration value="CENTRAL"/&gt;
 *     &lt;enumeration value="NEEMBUCU"/&gt;
 *     &lt;enumeration value="AMAMBAY"/&gt;
 *     &lt;enumeration value="PTE. HAYES"/&gt;
 *     &lt;enumeration value="BOQUERON"/&gt;
 *     &lt;enumeration value="ALTO PARAGUAY"/&gt;
 *     &lt;enumeration value="CANINDEYU"/&gt;
 *     &lt;enumeration value="CHACO"/&gt;
 *     &lt;enumeration value="NUEVA ASUNCION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tDesDepartamento")
@XmlEnum
public enum TDesDepartamento {

    CAPITAL("CAPITAL"),
    CONCEPCION("CONCEPCION"),
    @XmlEnumValue("SAN PEDRO")
    SAN_PEDRO("SAN PEDRO"),
    CORDILLERA("CORDILLERA"),
    GUAIRA("GUAIRA"),
    CAAGUAZU("CAAGUAZU"),
    CAAZAPA("CAAZAPA"),
    ITAPUA("ITAPUA"),
    MISIONES("MISIONES"),
    PARAGUARI("PARAGUARI"),
    @XmlEnumValue("ALTO PARANA")
    ALTO_PARANA("ALTO PARANA"),
    CENTRAL("CENTRAL"),
    NEEMBUCU("NEEMBUCU"),
    AMAMBAY("AMAMBAY"),
    @XmlEnumValue("PTE. HAYES")
    PTE_HAYES("PTE. HAYES"),
    BOQUERON("BOQUERON"),
    @XmlEnumValue("ALTO PARAGUAY")
    ALTO_PARAGUAY("ALTO PARAGUAY"),
    CANINDEYU("CANINDEYU"),
    CHACO("CHACO"),
    @XmlEnumValue("NUEVA ASUNCION")
    NUEVA_ASUNCION("NUEVA ASUNCION");
    private final String value;

    TDesDepartamento(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TDesDepartamento fromValue(String v) {
        for (TDesDepartamento c: TDesDepartamento.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
