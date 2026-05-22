package com.zentra.middleware.api.controller;

import com.zentra.middleware.api.security.JwtService;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.Usuario;
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador de autenticación.
 * Gestiona login, selección de empresa y perfil del usuario.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository,
                          EmpresaRepository empresaRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autenticación de usuario.
     * Retorna token JWT + lista de empresas del cliente.
     */
    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario y contraseña son requeridos"));
        }

        Optional<Usuario> optUsuario = usuarioRepository.findByUsername(username.trim().toLowerCase());
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        Usuario usuario = optUsuario.get();

        if (!usuario.getActivo()) {
            return ResponseEntity.status(403).body(Map.of("error", "Usuario desactivado. Contacte al administrador."));
        }

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Obtener empresas del cliente
        String clienteId = usuario.getCliente() != null ? usuario.getCliente().getId() : null;
        List<Map<String, Object>> empresas = List.of();
        if (clienteId != null) {
            empresas = empresaRepository.findByClienteId(clienteId).stream()
                .map(this::empresaToMap)
                .collect(Collectors.toList());
        }

        // Generar token (sin empresaId aún — se agrega al seleccionar empresa)
        Map<String, Object> claims = new HashMap<>();
        claims.put("clienteId", clienteId);
        claims.put("rol", usuario.getRol());
        claims.put("nombreCompleto", usuario.getNombreCompleto());

        // Si tiene empresa por defecto, incluirla en el token
        String empresaDefaultId = null;
        if (usuario.getEmpresaDefault() != null) {
            empresaDefaultId = usuario.getEmpresaDefault().getId();
            claims.put("empresaId", empresaDefaultId);
        }

        String token = jwtService.generateToken(username, claims);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
            "id", usuario.getId(),
            "username", usuario.getUsername(),
            "nombreCompleto", usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : "",
            "rol", usuario.getRol(),
            "debeCambiarPassword", usuario.getDebeCambiarPassword()
        ));
        response.put("empresas", empresas);
        response.put("empresaDefaultId", empresaDefaultId);
        response.put("estadoSuscripcion", usuario.getCliente() != null ? usuario.getCliente().getEstadoSuscripcion() : "AL_DIA");

        return ResponseEntity.ok(response);
    }

    /**
     * Selecciona una empresa para la sesión activa.
     * Emite un nuevo token con el empresaId embebido.
     */
    @PostMapping("/seleccionar-empresa")
    @Transactional
    public ResponseEntity<?> seleccionarEmpresa(@RequestBody Map<String, String> body,
                                                 @RequestHeader("Authorization") String authHeader) {
        String empresaId = body.get("empresaId");
        boolean setDefault = "true".equals(body.get("setDefault"));

        if (empresaId == null || empresaId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "empresaId es requerido"));
        }

        // Extraer datos del token actual
        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);
        var claims = jwtService.extractClaims(token);
        String clienteId = claims.get("clienteId", String.class);

        // Verificar que la empresa pertenece al cliente
        Optional<Empresa> optEmpresa = empresaRepository.findById(empresaId);
        if (optEmpresa.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Empresa no encontrada"));
        }

        Empresa empresa = optEmpresa.get();
        if (empresa.getCliente() == null || !empresa.getCliente().getId().equals(clienteId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No tiene acceso a esta empresa"));
        }

        // Si se marca como default, actualizar preferencia del usuario
        if (setDefault) {
            usuarioRepository.findByUsername(username).ifPresent(user -> {
                user.setEmpresaDefault(empresa);
                usuarioRepository.save(user);
            });
        }

        // Generar nuevo token con empresaId
        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("clienteId", clienteId);
        newClaims.put("rol", claims.get("rol", String.class));
        newClaims.put("nombreCompleto", claims.get("nombreCompleto", String.class));
        newClaims.put("empresaId", empresaId);
        newClaims.put("empresaRuc", empresa.getRuc());
        newClaims.put("empresaNombre", empresa.getRazonSocial());

        String newToken = jwtService.generateToken(username, newClaims);

        return ResponseEntity.ok(Map.of(
            "token", newToken,
            "empresa", empresaToMap(empresa)
        ));
    }

    /**
     * Retorna el perfil del usuario autenticado.
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        return usuarioRepository.findByUsername(username)
            .map(user -> {
                Map<String, Object> perfil = new HashMap<>();
                perfil.put("id", user.getId());
                perfil.put("username", user.getUsername());
                perfil.put("nombreCompleto", user.getNombreCompleto());
                perfil.put("email", user.getEmail());
                perfil.put("rol", user.getRol());
                perfil.put("empresaDefaultId", user.getEmpresaDefault() != null ? user.getEmpresaDefault().getId() : null);
                perfil.put("estadoSuscripcion", user.getCliente() != null ? user.getCliente().getEstadoSuscripcion() : "AL_DIA");
                return ResponseEntity.ok(perfil);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Convierte una entidad Empresa a un Map seguro para serialización (sin datos sensibles).
     */
    private Map<String, Object> empresaToMap(Empresa emp) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", emp.getId());
        map.put("ruc", emp.getRuc());
        map.put("dv", emp.getDv());
        map.put("razonSocial", emp.getRazonSocial());
        map.put("ambiente", emp.getAmbiente() != null ? emp.getAmbiente().name() : "TEST");
        map.put("logoBase64", emp.getLogoBase64());
        map.put("timbrado", emp.getTimbrado());
        map.put("hasCertificado", emp.getCertificadoFisico() != null && emp.getCertificadoFisico().length > 0);
        return map;
    }
}
