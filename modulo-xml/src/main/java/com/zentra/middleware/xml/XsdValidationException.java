package com.zentra.middleware.xml;

public class XsdValidationException extends RuntimeException {

    private final String detalleTecnico;

    public XsdValidationException(String mensajeAmigable, String detalleTecnico, Throwable cause) {
        super(mensajeAmigable, cause);
        this.detalleTecnico = detalleTecnico;
    }

    public String getDetalleTecnico() {
        return detalleTecnico;
    }
}
