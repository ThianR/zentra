package com.zentra.middleware.core.model;

public enum EstadoDte {
    CREADO,
    FIRMADO,
    ENVIADO,
    APROBADO,
    RECHAZADO,
    OBSERVADO,
    ANULADO,
    ERROR_ENVIO,  // Fallo técnico de transmisión (red, certificado, timeout)
    EN_LOTE,      // Empaquetado en un lote ZIP pendiente de transmisión
    EN_PROCESO    // Lote recibido por SIFEN pendiente de consulta
}
