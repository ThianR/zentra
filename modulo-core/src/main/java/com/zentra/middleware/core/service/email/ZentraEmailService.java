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

    /**
     * Envía un correo electrónico usando la configuración SMTP de la empresa si existe,
     * o la global por defecto si la empresa no tiene configuración propia.
     */
    void sendEmail(com.zentra.middleware.core.model.Empresa empresa, String to, String subject, String body);

}
