package com.zentra.middleware.core.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.zentra.middleware.core.util.AesEncryptionUtil;

@Service
@ConditionalOnProperty(name = "zentra.email.provider", havingValue = "smtp", matchIfMissing = true)
public class SmtpEmailServiceImpl implements ZentraEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailServiceImpl.class);

    @Autowired(required = false)
    private JavaMailSender defaultMailSender;

    public SmtpEmailServiceImpl() {
        // Constructor vacío para permitir inyección opcional
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("[SMTP] Enviando email global a: {}", to);
        enviarConSender(defaultMailSender, to, subject, body);
    }

    @Override
    public void sendEmail(com.zentra.middleware.core.model.Empresa empresa, String to, String subject, String body) {
        if (empresa != null && empresa.hasSmtpPassword()) {
            logger.info("[SMTP] Enviando email usando configuración de la empresa {} a: {}", empresa.getRazonSocial(), to);
            try {
                JavaMailSender customSender = buildCustomSender(empresa);
                enviarConSender(customSender, to, subject, body);
            } catch (Exception e) {
                logger.error("[SMTP] Error configurando/enviando con SMTP de empresa, haciendo fallback a global: {}", e.getMessage());
                sendEmail(to, subject, body);
            }
        } else {
            logger.info("[SMTP] Empresa sin configuración SMTP, usando global para: {}", to);
            sendEmail(to, subject, body);
        }
    }

    private void enviarConSender(JavaMailSender sender, String to, String subject, String body) {
        if (sender == null) {
            logger.warn("[SMTP] No se puede enviar el email a {} porque no hay una configuración SMTP válida (sender es null)", to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // El from lo tomará de la propiedad de sesión si se configura, o lo podemos obviar
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            sender.send(message);
            logger.info("[SMTP] Email enviado exitosamente a {}", to);
        } catch (Exception e) {
            logger.error("[SMTP] Fallo al enviar email a {}: {}", to, e.getMessage());
        }
    }

    private JavaMailSender buildCustomSender(com.zentra.middleware.core.model.Empresa empresa) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(empresa.getSmtpHost());
        mailSender.setPort(empresa.getSmtpPort() != null ? empresa.getSmtpPort() : 587);
        mailSender.setUsername(empresa.getSmtpUsername());
        
        String plainPassword = AesEncryptionUtil.decrypt(empresa.getSmtpPasswordEncrypted());
        mailSender.setPassword(plainPassword);

        java.util.Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", empresa.getSmtpUseTls() != null ? empresa.getSmtpUseTls().toString() : "true");
        props.put("mail.debug", "false");

        return mailSender;
    }
}
