package com.zentra.middleware.api.controller;

import com.zentra.middleware.api.security.EmpresaContext;
import com.zentra.middleware.core.model.Cliente;
import com.zentra.middleware.core.model.Usuario;
import com.zentra.middleware.core.model.UsuarioInvitacion;
import com.zentra.middleware.core.repository.ClienteRepository;
import com.zentra.middleware.core.repository.UsuarioInvitacionRepository;
import com.zentra.middleware.core.repository.UsuarioRepository;
import com.zentra.middleware.core.service.email.ZentraEmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioInvitacionRepository invitacionRepository;
    private final ClienteRepository clienteRepository;
    private final ZentraEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             UsuarioInvitacionRepository invitacionRepository,
                             ClienteRepository clienteRepository,
                             ZentraEmailService emailService,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.invitacionRepository = invitacionRepository;
        this.clienteRepository = clienteRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint protegido para listar los usuarios del cliente actual.
     */
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        String clienteId = EmpresaContext.getClienteId();
        if (clienteId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }

        List<Usuario> usuarios = usuarioRepository.findByClienteId(clienteId);
        
        // Mapear a DTO para no enviar contraseñas u otra info sensible
        List<Map<String, Object>> usuariosDto = usuarios.stream().map(u -> Map.<String, Object>of(
            "id", u.getId(),
            "username", u.getUsername(),
            "nombreCompleto", u.getNombreCompleto() != null ? u.getNombreCompleto() : "",
            "email", u.getEmail(),
            "rol", u.getRol(),
            "verSoloSusDtes", u.getVerSoloSusDtes(),
            "activo", u.getActivo()
        )).toList();

        return ResponseEntity.ok(usuariosDto);
    }

    /**
     * Endpoint protegido para que un ADMIN invite a otro usuario a su mismo Cliente.
     */
    @PostMapping("/invitar")
    @Transactional
    public ResponseEntity<?> invitarUsuario(@RequestBody Map<String, String> payload) {
        String clienteId = EmpresaContext.getClienteId();
        if (clienteId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }

        String email = payload.get("email");
        String rol = payload.get("rol"); // ADMIN o OPERADOR
        boolean verSoloSusDtes = Boolean.parseBoolean(payload.getOrDefault("verSoloSusDtes", "false"));

        if (email == null || email.isBlank() || rol == null || rol.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email y rol son requeridos"));
        }

        // Verificamos si el email ya existe como usuario
        if (usuarioRepository.findByUsername(email.toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado como usuario"));
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cliente no encontrado"));
        }

        // Crear invitación
        UsuarioInvitacion invitacion = new UsuarioInvitacion();
        invitacion.setEmail(email.toLowerCase());
        invitacion.setRol(rol.toUpperCase());
        invitacion.setVerSoloSusDtes(verSoloSusDtes);
        invitacion.setCliente(clienteOpt.get());
        invitacion.setFechaExpiracion(LocalDateTime.now().plusHours(48)); // 48 horas de validez
        invitacionRepository.save(invitacion);

        // Enviar correo
        String urlInvitacion = "http://localhost:8080/aceptar-invitacion.html?token=" + invitacion.getId();
        String body = "Hola,\n\nHas sido invitado a unirte a Zentra con el rol de " + rol + ".\n" +
                      "Por favor ingresa al siguiente enlace para configurar tu cuenta y contraseña:\n\n" +
                      urlInvitacion + "\n\nEste enlace expirará en 48 horas.";
        emailService.sendEmail(email, "Invitación a Zentra", body);

        return ResponseEntity.ok(Map.of("message", "Invitación enviada con éxito"));
    }

    /**
     * Endpoint público para consultar el estado de una invitación por su token.
     */
    @GetMapping("/invitacion/{token}")
    public ResponseEntity<?> verificarInvitacion(@PathVariable String token) {
        Optional<UsuarioInvitacion> invitacionOpt = invitacionRepository.findByIdAndUsadoFalse(token);
        if (invitacionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invitación inválida o ya utilizada"));
        }

        UsuarioInvitacion invitacion = invitacionOpt.get();
        if (invitacion.isExpirada()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La invitación ha expirado"));
        }

        return ResponseEntity.ok(Map.of(
            "email", invitacion.getEmail(),
            "rol", invitacion.getRol(),
            "clienteNombre", invitacion.getCliente().getNombre()
        ));
    }

    /**
     * Endpoint público para aceptar la invitación y crear el usuario.
     */
    @PostMapping("/aceptar-invitacion")
    @Transactional
    public ResponseEntity<?> aceptarInvitacion(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String nombreCompleto = payload.get("nombreCompleto");
        String password = payload.get("password");

        if (token == null || nombreCompleto == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token, nombreCompleto y password son requeridos"));
        }

        Optional<UsuarioInvitacion> invitacionOpt = invitacionRepository.findByIdAndUsadoFalse(token);
        if (invitacionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invitación inválida o ya utilizada"));
        }

        UsuarioInvitacion invitacion = invitacionOpt.get();
        if (invitacion.isExpirada()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La invitación ha expirado"));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
        }

        // Crear el usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(invitacion.getEmail());
        nuevoUsuario.setNombreCompleto(nombreCompleto);
        nuevoUsuario.setEmail(invitacion.getEmail());
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(password));
        nuevoUsuario.setRol(invitacion.getRol());
        nuevoUsuario.setVerSoloSusDtes(invitacion.getVerSoloSusDtes() != null ? invitacion.getVerSoloSusDtes() : false);
        nuevoUsuario.setCliente(invitacion.getCliente());
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setDebeCambiarPassword(false);

        usuarioRepository.save(nuevoUsuario);

        // Invalidar el token
        invitacion.setUsado(true);
        invitacionRepository.save(invitacion);

        return ResponseEntity.ok(Map.of("message", "Usuario registrado exitosamente. Ya puede iniciar sesión."));
    }
}
