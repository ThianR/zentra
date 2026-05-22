package com.zentra.middleware.core.scheduler;

import com.zentra.middleware.core.model.Cliente;
import com.zentra.middleware.core.repository.ClienteRepository;
import com.zentra.middleware.core.service.email.ZentraEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class SubscriptionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduler.class);

    private final ClienteRepository clienteRepository;
    private final ZentraEmailService emailService;

    public SubscriptionScheduler(ClienteRepository clienteRepository, ZentraEmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.emailService = emailService;
    }

    // Ejecutar todos los días a la 01:00 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void processSubscriptions() {
        logger.info("Iniciando revisión de suscripciones de clientes...");

        List<Cliente> clientes = clienteRepository.findAll();
        LocalDate hoy = LocalDate.now();

        for (Cliente cliente : clientes) {
            if (cliente.getFechaVencimientoPago() == null) {
                continue; // No tiene control de pagos
            }

            long diasAtraso = ChronoUnit.DAYS.between(cliente.getFechaVencimientoPago(), hoy);
            String estadoActual = cliente.getEstadoSuscripcion();
            String nuevoEstado = estadoActual;

            if (diasAtraso <= 0) {
                nuevoEstado = "AL_DIA";
            } else if (diasAtraso > 0 && diasAtraso <= 30) {
                nuevoEstado = "MES_1_AVISO";
            } else if (diasAtraso > 30 && diasAtraso <= 60) {
                nuevoEstado = "MES_2_LIMITADO";
            } else if (diasAtraso > 60) {
                nuevoEstado = "MES_3_BLOQUEADO";
            }

            if (!estadoActual.equals(nuevoEstado)) {
                logger.info("Cliente {} cambia de estado: {} -> {}", cliente.getIdentificador(), estadoActual, nuevoEstado);
                cliente.setEstadoSuscripcion(nuevoEstado);

                // Configurar límites
                if ("MES_2_LIMITADO".equals(nuevoEstado)) {
                    cliente.setLimiteDiarioEmisiones(100); // Límite de ejemplo
                } else if ("AL_DIA".equals(nuevoEstado) || "MES_1_AVISO".equals(nuevoEstado)) {
                    cliente.setLimiteDiarioEmisiones(-1);
                }

                clienteRepository.save(cliente);

                // Enviar notificación (En un entorno real, buscaríamos el email del administrador del cliente)
                String subject = "Zentra - Aviso de Suscripción: " + nuevoEstado;
                String body = "Estimado cliente " + cliente.getNombre() + ",\n\nSu estado de suscripción ha cambiado a " + nuevoEstado + ".\nPor favor regularice su pago.";
                // Usamos un correo genérico para el demo, normalmente sería buscarUsuariosAdmin(cliente).get(0).getEmail()
                emailService.sendEmail("admin@" + cliente.getIdentificador() + ".com", subject, body);
            }
        }

        logger.info("Revisión de suscripciones finalizada.");
    }
}
