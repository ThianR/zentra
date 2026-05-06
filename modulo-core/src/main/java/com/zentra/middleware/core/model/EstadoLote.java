package com.zentra.middleware.core.model;

public enum EstadoLote {
    PENDIENTE,  // Lote empaquetado, listo para enviar
    ENVIADO,    // Lote enviado a SIFEN, Ticket recibido
    PROCESADO,  // Ticket consultado y respuesta final procesada
    ERROR       // Error de transmisión o rechazo total de estructura del lote
}
