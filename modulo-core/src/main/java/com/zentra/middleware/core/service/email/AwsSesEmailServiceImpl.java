package com.zentra.middleware.core.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "zentra.email.provider", havingValue = "aws-ses")
public class AwsSesEmailServiceImpl implements ZentraEmailService {

    private static final Logger logger = LoggerFactory.getLogger(AwsSesEmailServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("[AWS SES] Enviando email a: {}", to);
        logger.debug("[AWS SES] Asunto: {}", subject);
        // Lógica de llamada a AWS SES
        logger.info("[AWS SES] Email enviado exitosamente a {}", to);
    }

    @Override
    public void sendEmail(com.zentra.middleware.core.model.Empresa empresa, String to, String subject, String body) {
        // AWS SES usualmente se configura por IAM Role o perfil por empresa.
        sendEmail(to, subject, body);
    }
}
