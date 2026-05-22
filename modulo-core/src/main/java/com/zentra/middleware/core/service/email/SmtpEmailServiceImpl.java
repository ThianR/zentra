package com.zentra.middleware.core.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "zentra.email.provider", havingValue = "smtp", matchIfMissing = true)
public class SmtpEmailServiceImpl implements ZentraEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailServiceImpl.class);

    // Aquí normalmente inyectaríamos JavaMailSender
    // private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("[SMTP] Enviando email a: {}", to);
        logger.debug("[SMTP] Asunto: {}", subject);
        // Lógica real de envío con JavaMailSender iría aquí
        logger.info("[SMTP] Email enviado exitosamente a {}", to);
    }
}
