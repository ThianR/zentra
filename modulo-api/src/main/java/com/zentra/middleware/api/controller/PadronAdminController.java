package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.service.PadronRucService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/admin/padron")
public class PadronAdminController {

    private static final Logger logger = Logger.getLogger(PadronAdminController.class.getName());
    private final PadronRucService padronRucService;

    public PadronAdminController(PadronRucService padronRucService) {
        this.padronRucService = padronRucService;
    }

    @PostMapping("/sincronizar-url")
    public ResponseEntity<?> sincronizarDesdeUrl() {
        try {
            logger.info("Solicitada sincronización automática del Padrón DNIT por URL.");
            // Esta operacion puede demorar, pero la mantenemos bloqueante para el demo. 
            // En producción agresiva se podría ejecutar en un hilo asíncrono.
            String resultado = padronRucService.descargarYProcesarPadron();
            return ResponseEntity.ok(Map.of("message", resultado, "status", "SUCCESS"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en sincronización automática de padrón", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error: " + e.getMessage(), "status", "ERROR"));
        }
    }

    @PostMapping("/subir")
    public ResponseEntity<?> subirPadronZip(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Archivo vacío", "status", "ERROR"));
            }
            logger.info("Solicitada carga manual del Padrón DNIT mediante archivo ZIP.");
            String resultado = padronRucService.procesarZipPadron(file.getInputStream());
            return ResponseEntity.ok(Map.of("message", resultado, "status", "SUCCESS"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en carga manual de padrón", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error: " + e.getMessage(), "status", "ERROR"));
        }
    }
}
