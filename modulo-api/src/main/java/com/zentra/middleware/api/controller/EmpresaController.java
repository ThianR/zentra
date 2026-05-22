package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.util.AesEncryptionUtil;
import com.zentra.middleware.api.security.EmpresaContext;
import com.zentra.middleware.core.model.Cliente;
import com.zentra.middleware.core.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/empresas")
@Transactional
public class EmpresaController {

    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;

    public EmpresaController(EmpresaRepository empresaRepository, ClienteRepository clienteRepository) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping("/{id}/certificado")
    public ResponseEntity<?> cargarCertificado(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) {
        try {
            String nonNullId = java.util.Objects.requireNonNull(id);
            Optional<Empresa> empresaOpt = empresaRepository.findById(nonNullId);
            if (empresaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Empresa empresa = empresaOpt.get();

            String clienteId = EmpresaContext.getClienteId();
            if (empresa.getCliente() == null || !empresa.getCliente().getId().equals(clienteId)) {
                return ResponseEntity.status(403).body("No tiene acceso a esta empresa");
            }

            // Validar que el certificado puede ser leído antes de guardar (Fase 4, Regla 3)
            try {
                java.security.KeyStore ks = java.security.KeyStore.getInstance("PKCS12");
                ks.load(file.getInputStream(), password.toCharArray());
                
                // Extraer fecha de vencimiento y alias
                java.util.Enumeration<String> aliases = ks.aliases();
                if (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    java.security.cert.Certificate cert = ks.getCertificate(alias);
                    if (cert instanceof java.security.cert.X509Certificate) {
                        java.security.cert.X509Certificate x509 = (java.security.cert.X509Certificate) cert;
                        java.util.Date notAfter = x509.getNotAfter();
                        empresa.setFechaVencimientoCertificado(
                            java.time.Instant.ofEpochMilli(notAfter.getTime())
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        );
                        empresa.setAliasCertificado(alias);
                    }
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Contraseña incorrecta o archivo P12/PFX corrupto.");
            }

            // Encriptar la contraseña (Fase 1 y 2)
            String encryptedPassword = AesEncryptionUtil.encrypt(password);
            
            empresa.setCertificadoFisico(file.getBytes());
            empresa.setPasswordCertificado(encryptedPassword);
            
            empresaRepository.save(empresa);

            return ResponseEntity.ok().body("Certificado guardado exitosamente para la empresa.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar el certificado: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        String clienteId = EmpresaContext.getClienteId();
        if (clienteId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        return ResponseEntity.ok(empresaRepository.findByClienteId(clienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable String id) {
        String clienteId = EmpresaContext.getClienteId();
        String nonNullId = java.util.Objects.requireNonNull(id);
        return empresaRepository.findById(nonNullId)
                .filter(e -> e.getCliente() != null && e.getCliente().getId().equals(clienteId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearEmpresa(@RequestBody Empresa empresa) {
        String clienteId = EmpresaContext.getClienteId();
        if (clienteId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        if (empresaRepository.findByRuc(empresa.getRuc()).isPresent()) {
            return ResponseEntity.status(409).body("La empresa con este RUC ya se encuentra registrada.");
        }
        if (empresa.getId() == null || empresa.getId().isEmpty()) {
            empresa.setId(java.util.UUID.randomUUID().toString());
        }
        
        Optional<Cliente> optCliente = clienteRepository.findById(clienteId);
        if (optCliente.isPresent()) {
            empresa.setCliente(optCliente.get());
        } else {
            return ResponseEntity.status(404).body("Cliente no encontrado.");
        }
        
        if (empresa.getSmtpPasswordPlain() != null && !empresa.getSmtpPasswordPlain().isEmpty()) {
            empresa.setSmtpPasswordEncrypted(AesEncryptionUtil.encrypt(empresa.getSmtpPasswordPlain()));
        }
        
        return ResponseEntity.ok(empresaRepository.save(empresa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEmpresa(@PathVariable String id, @RequestBody Empresa empresaDetalles) {
        String clienteId = EmpresaContext.getClienteId();
        
        Optional<Empresa> existenteConRuc = empresaRepository.findByRuc(empresaDetalles.getRuc());
        if (existenteConRuc.isPresent() && !existenteConRuc.get().getId().equals(id)) {
            return ResponseEntity.status(409).body("El RUC provisto ya está asociado a otra empresa.");
        }

        String nonNullId = java.util.Objects.requireNonNull(id);
        return empresaRepository.findById(nonNullId).map(empresa -> {
            if (empresa.getCliente() == null || !empresa.getCliente().getId().equals(clienteId)) {
                return ResponseEntity.status(403).body("No tiene acceso a esta empresa");
            }
            empresa.setRuc(empresaDetalles.getRuc());
            empresa.setRazonSocial(empresaDetalles.getRazonSocial());
            empresa.setDv(empresaDetalles.getDv());
            
            // Timbrado
            empresa.setTimbrado(empresaDetalles.getTimbrado());
            empresa.setFechaInicioTimbrado(empresaDetalles.getFechaInicioTimbrado());
            empresa.setFechaVencimientoTimbrado(empresaDetalles.getFechaVencimientoTimbrado());
            
            // Establecimiento y Expedición
            empresa.setCodEstablecimiento(empresaDetalles.getCodEstablecimiento());
            empresa.setPuntoExpedicion(empresaDetalles.getPuntoExpedicion());
            
            // Actividad Económica
            empresa.setCodActividadEconomica(empresaDetalles.getCodActividadEconomica());
            empresa.setActividadEconomica(empresaDetalles.getActividadEconomica());
            
            // Localización
            empresa.setDireccion(empresaDetalles.getDireccion());
            empresa.setNumeroCasa(empresaDetalles.getNumeroCasa());
            empresa.setCodDepartamento(empresaDetalles.getCodDepartamento());
            empresa.setDepartamento(empresaDetalles.getDepartamento());
            empresa.setCodDistrito(empresaDetalles.getCodDistrito());
            empresa.setDistrito(empresaDetalles.getDistrito());
            empresa.setCodCiudad(empresaDetalles.getCodCiudad());
            empresa.setCiudad(empresaDetalles.getCiudad());
            
            // Contacto
            empresa.setTelefono(empresaDetalles.getTelefono());
            empresa.setEmail(empresaDetalles.getEmail());
            
            // Otros
            empresa.setTipoContribuyente(empresaDetalles.getTipoContribuyente());
            empresa.setAmbiente(empresaDetalles.getAmbiente());
            empresa.setIdCsc(empresaDetalles.getIdCsc());
            empresa.setValorCsc(empresaDetalles.getValorCsc());
            
            // SMTP
            empresa.setSmtpHost(empresaDetalles.getSmtpHost());
            empresa.setSmtpPort(empresaDetalles.getSmtpPort());
            empresa.setSmtpUsername(empresaDetalles.getSmtpUsername());
            empresa.setSmtpUseTls(empresaDetalles.getSmtpUseTls());
            if (empresaDetalles.getSmtpPasswordPlain() != null && !empresaDetalles.getSmtpPasswordPlain().isEmpty()) {
                empresa.setSmtpPasswordEncrypted(AesEncryptionUtil.encrypt(empresaDetalles.getSmtpPasswordPlain()));
            }
            
            // Lotes y Personalización
            if (empresaDetalles.getFrecuenciaLoteMinutos() != null) {
                empresa.setFrecuenciaLoteMinutos(empresaDetalles.getFrecuenciaLoteMinutos());
            }
            if (empresaDetalles.getFrecuenciaConsultaTicketMinutos() != null) {
                empresa.setFrecuenciaConsultaTicketMinutos(empresaDetalles.getFrecuenciaConsultaTicketMinutos());
            }
            empresa.setLogoBase64(empresaDetalles.getLogoBase64());
            
            return ResponseEntity.ok(empresaRepository.save(empresa));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEmpresa(@PathVariable String id) {
        String clienteId = EmpresaContext.getClienteId();
        String nonNullId = java.util.Objects.requireNonNull(id);
        return empresaRepository.findById(nonNullId).map(empresa -> {
            if (empresa.getCliente() == null || !empresa.getCliente().getId().equals(clienteId)) {
                return ResponseEntity.status(403).build();
            }
            empresaRepository.delete(empresa);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/test-smtp")
    public ResponseEntity<?> testSmtp(@RequestBody java.util.Map<String, Object> body) {
        try {
            String host = (String) body.get("host");
            Integer port = (Integer) body.get("port");
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            Boolean useTls = body.get("useTls") != null ? (Boolean) body.get("useTls") : true;

            org.springframework.mail.javamail.JavaMailSenderImpl mailSender = new org.springframework.mail.javamail.JavaMailSenderImpl();
            mailSender.setHost(host);
            mailSender.setPort(port);
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            java.util.Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", useTls.toString());
            props.put("mail.debug", "false");

            mailSender.testConnection();

            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Conexión exitosa"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "error", "Error de conexión: " + e.getMessage()));
        }
    }
}
