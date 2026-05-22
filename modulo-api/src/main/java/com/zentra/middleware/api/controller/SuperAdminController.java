package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.model.Cliente;
import com.zentra.middleware.core.model.UsuarioInvitacion;
import com.zentra.middleware.core.repository.ClienteRepository;
import com.zentra.middleware.core.repository.UsuarioInvitacionRepository;
import com.zentra.middleware.core.repository.UsuarioRepository;
import com.zentra.middleware.core.service.email.ZentraEmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/superadmin")
public class SuperAdminController {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioInvitacionRepository invitacionRepository;
    private final ZentraEmailService emailService;

    // TODO: En producción, proteger este endpoint con un rol ROLE_SUPERADMIN o un API Key específico interno.

    public SuperAdminController(ClienteRepository clienteRepository,
                                UsuarioRepository usuarioRepository,
                                UsuarioInvitacionRepository invitacionRepository,
                                ZentraEmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.invitacionRepository = invitacionRepository;
        this.emailService = emailService;
    }

    /**
     * Endpoint exclusivo para el dueño de Zentra.
     * Crea un nuevo Cliente (Tenant) y envía la primera invitación de administrador.
     */
    @PostMapping("/clientes")
    @Transactional
    public ResponseEntity<?> crearClienteYEnviarInvitacion(@RequestBody Map<String, String> payload) {
        String identificador = payload.get("identificador");
        String nombre = payload.get("nombre");
        String emailAdmin = payload.get("emailAdmin");

        if (identificador == null || nombre == null || emailAdmin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "identificador, nombre y emailAdmin son requeridos"));
        }

        // Verificar si el identificador o correo ya existen
        if (clienteRepository.findByIdentificador(identificador).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El identificador del cliente ya existe"));
        }

        if (usuarioRepository.findByUsername(emailAdmin.toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado en el sistema"));
        }

        // 1. Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setIdentificador(identificador);
        cliente.setNombre(nombre);
        cliente.setEstadoSuscripcion("AL_DIA");
        cliente.setLimiteDiarioEmisiones(-1);
        clienteRepository.save(cliente);

        // 2. Crear Invitación para el Administrador
        UsuarioInvitacion invitacion = new UsuarioInvitacion();
        invitacion.setEmail(emailAdmin.toLowerCase());
        invitacion.setRol("ADMIN");
        invitacion.setCliente(cliente);
        invitacion.setFechaExpiracion(LocalDateTime.now().plusDays(7)); // Damos 7 días para el primer admin
        invitacionRepository.save(invitacion);

        // 3. Enviar Correo de Bienvenida e Invitación
        String urlInvitacion = "http://localhost:8080/aceptar-invitacion.html?token=" + invitacion.getId();
        String body = "¡Bienvenido a Zentra!\n\n" +
                      "Se ha creado tu cuenta corporativa para '" + nombre + "'.\n" +
                      "Eres el Administrador principal de esta cuenta.\n" +
                      "Por favor, ingresa al siguiente enlace para configurar tu contraseña y acceder al sistema:\n\n" +
                      urlInvitacion + "\n\nEste enlace expirará en 7 días.";
        emailService.sendEmail(emailAdmin, "Bienvenido a Zentra - Configura tu cuenta", body);

        return ResponseEntity.ok(Map.of(
            "message", "Cliente creado exitosamente. Invitación enviada al administrador.",
            "clienteId", cliente.getId()
        ));
    }
}
