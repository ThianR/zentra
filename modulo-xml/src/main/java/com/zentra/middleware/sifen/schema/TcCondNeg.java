//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.03.19 a las 11:22:40 PM PYT 
//


package com.zentra.middleware.sifen.schema;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tcCondNeg.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tcCondNeg"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CFR"/&gt;
 *     &lt;enumeration value="CIF"/&gt;
 *     &lt;enumeration value="CIP"/&gt;
 *     &lt;enumeration value="CPT"/&gt;
 *     &lt;enumeration value="DAP"/&gt;
 *     &lt;enumeration value="DAT"/&gt;
 *     &lt;enumeration value="DDP"/&gt;
 *     &lt;enumeration value="EXW"/&gt;
 *     &lt;enumeration value="FAS"/&gt;
 *     &lt;enumeration value="FCA"/&gt;
 *     &lt;enumeration value="FOB"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tcCondNeg")
@XmlEnum
public enum TcCondNeg {


    /**
     * 
     * 						Costo y flete
     * 					
     * 
     */
    CFR,

    /**
     * 
     * 						Costo, seguro y flete
     * 					
     * 
     */
    CIF,

    /**
     * 
     * 						Transporte y seguros pagados hasta
     * 					
     * 
     */
    CIP,

    /**
     * 
     * 						Transporte pagado hasta
     * 					
     * 
     */
    CPT,

    /**
     * 
     * 						Entregada en el lugar convenido
     * 					
     * 
     */
    DAP,

    /**
     * 
     * 						Entregada en terminal
     * 					
     * 
     */
    DAT,

    /**
     * 
     * 						Entregada derechos pagados
     * 					
     * 
     */
    DDP,

    /**
     * 
     * 						En fabrica
     * 					
     * 
     */
    EXW,

    /**
     * 
     * 						Franco al costado del buque
     * 					
     * 
     */
    FAS,

    /**
     * 
     * 						Franco transportista
     * 					
     * 
     */
    FCA,

    /**
     * 
     * 						Franco a bordo
     * 					
     * 
     */
    FOB;

    public String value() {
        return name();
    }

    public static TcCondNeg fromValue(String v) {
        return valueOf(v);
    }

}
