package com.zentra.middleware.core.util;

import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Utilidades para el calculo de identificadores segun SIFEN.
 */
@Component
public class SifenUtil {
    private static final Logger logger = Logger.getLogger(SifenUtil.class.getName());
    private static final Random RANDOM = new Random();

    static {
        // Semilla oficial Manual Tecnico SIFEN v150 (Pag 57)
        String ej = "0144444401700100100000012201701251158732609";
        int r = calcularDV(ej);
        System.out.println(">>> [SifenUtil STATIC TEST] calcularDV resultado con algoritmo Der-Izq: " + r + " (esperado en manual: 8, real SIFEN: 6)");
    }

    /**
     * Calcula el digito verificador usando Modulo 11.
     * Algoritmo: Multiplicadores ciclicos 2-11 de DERECHA a IZQUIERDA.
     * (El manual dice izquierda-a-derecha, pero la práctica y el código de la SET 
     * sugieren que usan derecha-a-izquierda igual que para el RUC).
     */
    public static int calcularDV(String cdcBase43) {
        int suma = 0;
        int k = 2;
        for (int i = cdcBase43.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(cdcBase43.charAt(i));
            suma += digito * k;
            k++;
            if (k > 11) {
                k = 2;
            }
        }
        int resto = suma % 11;
        int dv = 11 - resto;
        if (dv >= 10) return 0;
        return dv;
    }

    public static String generarCdc(String tipoDoc, String ruc, String dv, String estab, String punto,
                                     String nro, String tipoCont, String fecha, String tipoEmi, String codSeg) {

        String fechaLimpia = fecha.replaceAll("[^0-9]", "");
        if (fechaLimpia.length() < 8) {
            fechaLimpia = String.format("%08d", Long.parseLong(fechaLimpia));
        } else if (fechaLimpia.length() > 8) {
            fechaLimpia = fechaLimpia.substring(0, 8);
        }

        // SIFEN v150 CDC: TipoDoc(2)+RUC(8)+DV(1)+Estab(3)+Punto(3)+Nro(7)+TipCont(1)+Fecha(8)+TipEmi(1)+CodSeg(9) = 43 chars
        int tipCont = Integer.parseInt(tipoCont.replaceAll("[^0-9]", "2"));

        String cdcSemilla = String.format("%02d%08d%01d%03d%03d%07d%01d%s%01d%09d",
                Integer.parseInt(tipoDoc.replaceAll("[^0-9]", "0")),
                Long.parseLong(ruc.replaceAll("[^0-9]", "0")),
                Integer.parseInt(dv.replaceAll("[^0-9]", "0")),
                Integer.parseInt(estab.replaceAll("[^0-9]", "0")),
                Integer.parseInt(punto.replaceAll("[^0-9]", "0")),
                Long.parseLong(nro.replaceAll("[^0-9]", "0")),
                tipCont, // Posición 25: iTipCont
                fechaLimpia,
                Integer.parseInt(tipoEmi.replaceAll("[^0-9]", "1")), // Posición 34: iTipEmi
                Long.parseLong(codSeg.replaceAll("[^0-9]", "0")));

        logger.info("CDC BASE (debe tener 43 chars): '" + cdcSemilla + "' largo=" + cdcSemilla.length());
        int dvCdc = calcularDV(cdcSemilla);
        String cdcFinal = cdcSemilla + dvCdc;

        logger.info("CDC generado (v150): " + cdcFinal + " (largo=" + cdcFinal.length() + ")");

        if (cdcFinal.length() == 44) {
            logger.info(String.format(
                "CDC breakdown: tiDE=%s ruc=%s dv=%s est=%s pun=%s num=%s tipCont=%s fec=%s tipoEmi=%s seg=%s dv2=%s",
                cdcFinal.substring(0,  2),
                cdcFinal.substring(2,  10),
                cdcFinal.substring(10, 11),
                cdcFinal.substring(11, 14),
                cdcFinal.substring(14, 17),
                cdcFinal.substring(17, 24),
                cdcFinal.substring(24, 25),
                cdcFinal.substring(25, 33),
                cdcFinal.substring(33, 34),
                cdcFinal.substring(34, 43),
                cdcFinal.substring(43, 44)
            ));
        }
        return cdcFinal;
    }

    public static String generarCodigoSeguridad(int numeroDocumento) {
        String numDocPad = String.format("%07d", numeroDocumento);
        int codSeg;
        do {
            codSeg = 1 + RANDOM.nextInt(999999999);
        } while (String.format("%09d", codSeg).substring(2).equals(numDocPad));
        return String.format("%09d", codSeg);
    }
}