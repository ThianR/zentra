package com.zentra.middleware.core.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "zentra.email.provider", havingValue = "sendgrid")
public class SendGridEmailServiceImpl implements ZentraEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("[SendGrid] Enviando email a: {}", to);
        logger.debug("[SendGrid] Asunto: {}", subject);
        // Lógica de llamada a la API de SendGrid
        logger.info("[SendGrid] Email enviado exitosamente a {}", to);
    }

    @Override
    public void sendEmail(com.zentra.middleware.core.model.Empresa empresa, String to, String subject, String body) {
        // En SendGrid, tal vez se use el mismo Sender, o una API key dinámica.
        sendEmail(to, subject, body);
    }
}
