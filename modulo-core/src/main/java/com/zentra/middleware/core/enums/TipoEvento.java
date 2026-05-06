package com.zentra.middleware.core.enums;

/**
 * Tipos de eventos SIFEN v150 definidos en el Manual Técnico, Sección 11.
 *
 * <ul>
 *   <li>CANCELACION (1) — Anula un DTE previamente aprobado por SIFEN.</li>
 *   <li>INUTILIZACION (3) — Informa la anulación de un rango de numeración
 *       no utilizado, evitando brechas en la secuencia de comprobantes.</li>
 * </ul>
 *
 * <p>Los tipos 2 (Disconformidad) y 4 (Desconocimiento) son eventos del
 * receptor y se incorporarán en fases futuras del módulo de eventos.</p>
 */
public enum TipoEvento {

    /** Cancelación de un DTE aprobado. Código SIFEN: 1. */
    CANCELACION(1, "Cancelación"),

    /** Conformidad (Aceptación de la transacción). Código SIFEN: 2. */
    CONFORMIDAD(2, "Conformidad"),

    /** Inutilización de rango de numeración. Código SIFEN: 3. */
    INUTILIZACION(3, "Inutilización"),

    /** Disconformidad (Rechazo comercial). Código SIFEN: 4. */
    DISCONFORMIDAD(4, "Disconformidad"),

    /** Desconocimiento de la operación. Código SIFEN: 5. */
    DESCONOCIMIENTO(5, "Desconocimiento"),

    /** Notificación de Recepción (Acuse técnico). Código SIFEN: 6. */
    NOTIFICACION_RECEPCION(6, "Notificación de Recepción");

    private final int codigoSifen;
    private final String descripcion;

    TipoEvento(int codigoSifen, String descripcion) {
        this.codigoSifen = codigoSifen;
        this.descripcion = descripcion;
    }

    /** Código numérico oficial en el Manual Técnico SIFEN v150. */
    public int getCodigoSifen() {
        return codigoSifen;
    }

    /** Descripción legible del tipo de evento. */
    public String getDescripcion() {
        return descripcion;
    }
}
