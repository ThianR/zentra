package com.zentra.middleware.core.util;

import java.util.Map;

/**
 * Utilitario que mapea el código de tipo de transacción SIFEN a su descripción oficial.
 * Referencia: Manual Técnico SIFEN v150, campo dDesTipTra en gOpeCom.
 */
public class DescripcionTipoTransaccion {

    private static final Map<Integer, String> DESC;
    
    static {
        Map<Integer, String> m = new java.util.HashMap<>();
        m.put(1, "Venta de mercadería");
        m.put(2, "Prestación de servicios");
        m.put(3, "Mixto (Venta de mercadería y servicios)");
        m.put(4, "Venta de activo fijo");
        m.put(5, "Venta de divisas");
        m.put(6, "Compra de productos");
        m.put(7, "Prestación de servicios"); // Fallback para Alquiler si no está en el XSD
        m.put(8, "Venta de crédito fiscal");
        m.put(9, "Donación");
        DESC = java.util.Collections.unmodifiableMap(m);
    }

    private DescripcionTipoTransaccion() {
        // Clase utilitaria: no instanciar
    }

    /**
     * Retorna la descripción oficial SIFEN para un tipo de transacción dado.
     * @param tipo Código de tipo de transacción (1-9).
     * @return Descripción textual, o "Venta de mercadería" si el código no es reconocido.
     */
    public static String getDescripcion(int tipo) {
        return DESC.getOrDefault(tipo, "Venta de mercadería");
    }
}
