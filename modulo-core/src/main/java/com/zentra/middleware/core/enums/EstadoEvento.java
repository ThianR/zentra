package com.zentra.middleware.core.enums;

/**
 * Estados del ciclo de vida de un Evento SIFEN.
 *
 * <p>El flujo normal es: PENDIENTE → ENVIADO → APROBADO | RECHAZADO.</p>
 * <p>Si ocurre un fallo técnico antes de llegar a SIFEN se pasa a ERROR_ENVIO.</p>
 */
public enum EstadoEvento {

    /** Evento creado y pendiente de firma/envío. */
    PENDIENTE,

    /** XML generado y firmado, pendiente de transmisión. */
    FIRMADO,

    /** Transmitido al WS siRecepEvento, esperando respuesta. */
    ENVIADO,

    /** SIFEN aprobó el evento (dCodRes = 0300). */
    APROBADO,

    /** SIFEN rechazó el evento (dCodRes = 0400 u otro código de error). */
    RECHAZADO,

    /**
     * Fallo técnico de transmisión (timeout, red, certificado).
     * El evento NO llegó a SIFEN; puede reintentarse.
     */
    ERROR_ENVIO
}
