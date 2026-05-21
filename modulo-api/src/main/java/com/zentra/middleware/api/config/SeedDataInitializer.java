package com.zentra.middleware.api.config;

import com.zentra.middleware.core.model.Cliente;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.Usuario;
import com.zentra.middleware.core.repository.ClienteRepository;
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Inicializador que se ejecuta al arrancar la aplicación.
 * Crea el cliente semilla, el usuario admin y asocia empresas huérfanas.
 */
@Component
public class SeedDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataInitializer.class);

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public SeedDataInitializer(ClienteRepository clienteRepository,
                                UsuarioRepository usuarioRepository,
                                EmpresaRepository empresaRepository,
                                PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[Seed] Verificando integridad de datos iniciales...");

        // 1. Obtener o crear cliente semilla
        Cliente cliente = clienteRepository.findByIdentificador("zentra-default")
                .orElseGet(() -> {
                    Cliente c = new Cliente();
                    c.setNombre("Zentra Default");
                    c.setIdentificador("zentra-default");
                    Cliente saved = clienteRepository.save(c);
                    log.info("[Seed] Cliente semilla creado: {}", saved.getNombre());
                    return saved;
                });

        // 2. Obtener o crear usuario administrador
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            String adminPassword = System.getenv("ZENTRA_ADMIN_PASSWORD");
            if (adminPassword == null || adminPassword.trim().isEmpty()) {
                adminPassword = "zentra2026";
                log.warn("[Seed] ZENTRA_ADMIN_PASSWORD no configurado en el entorno. Usando contraseña inicial por defecto.");
            }
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setNombreCompleto("Administrador");
            admin.setEmail("admin@zentra.local");
            admin.setRol("ADMIN");
            admin.setCliente(cliente);
            admin.setDebeCambiarPassword(true);
            usuarioRepository.save(admin);
            log.info("[Seed] Usuario admin creado (usuario: admin)");
        }

        // 3. Asociar empresas existentes sin cliente al cliente semilla
        List<Empresa> empresasSinCliente = empresaRepository.findAll().stream()
                .filter(e -> e.getCliente() == null)
                .toList();

        if (!empresasSinCliente.isEmpty()) {
            empresasSinCliente.forEach(empresa -> {
                empresa.setCliente(cliente);
                empresaRepository.save(empresa);
            });
            log.info("[Seed] {} empresa(s) existente(s) asociadas al cliente semilla.", empresasSinCliente.size());
        }

        log.info("[Seed] Verificación de datos completada.");
    }
}
