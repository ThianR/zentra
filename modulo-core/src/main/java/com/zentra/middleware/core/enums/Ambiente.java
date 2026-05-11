package com.zentra.middleware.core.enums;

import java.util.Arrays;

/**
 * Representa los ambientes de operación habilitados por SIFEN.
 * Centraliza la lógica de resolución para evitar integers mágicos dispersos.
 * TEST = 1, PRODUCCION = 2 (según Manual Técnico SIFEN v150).
 */
public enum Ambiente {

    TEST(1),
    PRODUCCION(2);

    private final int codigo;

    Ambiente(int codigo) {
        this.codigo = codigo;
    }

    /** Retorna el código numérico oficial SIFEN (1=Producción, 2=Test según la SET). */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Resuelve el enum desde un código entero.
     * @param codigo Código numérico del ambiente.
     * @return El enum correspondiente.
     * @throws IllegalArgumentException si el código no es 1 ni 2.
     */
    public static Ambiente fromCodigo(int codigo) {
        return Arrays.stream(values())
                .filter(a -> a.codigo == codigo)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ambiente inválido: " + codigo));
    }
}
