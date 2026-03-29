package com.zentra.middleware.core.model;

public enum EstadoDte {
    CREADO,
    FIRMADO,
    ENVIADO,
    APROBADO,
    RECHAZADO,
    OBSERVADO,
    ANULADO,
    ERROR_ENVIO   // Fallo técnico de transmisión (red, certificado, timeout)
}
