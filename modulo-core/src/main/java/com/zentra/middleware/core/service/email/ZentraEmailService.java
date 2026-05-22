package com.zentra.middleware.core.service.email;

public interface ZentraEmailService {
    
    /**
     * Envía un correo electrónico.
     *
     * @param to Destinatario
     * @param subject Asunto del correo
     * @param body Cuerpo del correo (puede ser HTML)
     */
    void sendEmail(String to, String subject, String body);

}
