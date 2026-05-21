package com.zentra.middleware.api.service;

import java.util.List;

/**
 * Excepción personalizada para representar fallos en las validaciones previas de un DTE.
 */
public class DteValidationException extends RuntimeException {
    private final List<String> errores;

    /**
     * Constructor con mensaje descriptivo y lista detallada de errores.
     * 
     * @param message mensaje descriptivo del fallo general.
     * @param errores lista con cada error específico de validación.
     */
    public DteValidationException(String message, List<String> errores) {
        super(message);
        this.errores = errores;
    }

    /**
     * Obtiene la lista de errores específicos de validación.
     * 
     * @return lista de errores.
     */
    public List<String> getErrores() {
        return errores;
    }
}
