package com.zentra.middleware.core.util;

import org.springframework.stereotype.Component;

/**
 * Utilidades para el calculo de identificadores segun SIFEN.
 */
@Component
public class SifenUtil {

    /**
     * Calcula el digito verificador de una cadena usando Modulo 11.
     * Segun especificaciones oficiales de SIFEN Paraguay.
     */
    public static int calcularDv(String p_numero) {
        int v_total, v_resto, v_digit;
        int i, k;
        String v_numero_al = "";

        for (i = 0; i < p_numero.length(); i++) {
            char c = p_numero.charAt(i);
            if (Character.isDigit(c)) {
                v_numero_al += c;
            } else {
                v_numero_al += (int) c;
            }
        }

        v_total = 0;
        k = 2;
        for (i = v_numero_al.length() - 1; i >= 0; i--) {
            k = (k > 11) ? 2 : k;
            v_total += Character.getNumericValue(v_numero_al.charAt(i)) * k;
            k++;
        }
        v_resto = v_total % 11;
        v_digit = (v_resto > 1) ? 11 - v_resto : 0;

        return v_digit;
    }

    /**
     * Genera el CDC de 44 digitos.
     * Estructura simplificada para el MVP:
     * Tipo(2) + RUC(8) + DV(1) + Estab(3) + Punto(3) + Numero(7) + TipoEmis(1) + Fecha(8) + CodSeg(9) + DV_CDC(1)
     */
    public static String generarCdc(String tipoDoc, String ruc, String dv, String estab, String punto, String nro, int tipoContribuyente, String fecha, String tipoEmis, String codSeg) {
        String cdcSemilla = String.format("%02d%08d%1s%03d%03d%07d%d%08d%1s%09d", 
                Long.parseLong(tipoDoc), 
                Long.parseLong(ruc.replaceAll("[^0-9]", "")), 
                dv, 
                Long.parseLong(estab), 
                Long.parseLong(punto), 
                Long.parseLong(nro), 
                tipoContribuyente,
                Long.parseLong(fecha), 
                tipoEmis, 
                Long.parseLong(codSeg));
        
        int dvCdc = calcularDv(cdcSemilla);
        return cdcSemilla + dvCdc;
    }
}
