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
        // Lógica de llamada al SDK de AWS SES
        logger.info("[AWS SES] Email enviado exitosamente a {}", to);
    }
}
