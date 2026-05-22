package com.zentra.middleware.api.controller;

import com.zentra.middleware.api.security.EmpresaContext;
import com.zentra.middleware.core.model.CentroEventosLog;
import com.zentra.middleware.core.repository.CentroEventosLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/eventos-log")
public class EventosLogController {

    private final CentroEventosLogRepository repositorio;

    public EventosLogController(CentroEventosLogRepository repositorio) {
        this.repositorio = repositorio;
    }

    @PostMapping("/reportar")
    public ResponseEntity<Map<String, String>> reportarEvento(@RequestBody Map<String, Object> payload) {
        try {
            String empresaId = EmpresaContext.getEmpresaId();
            String usuarioId = EmpresaContext.getClienteId(); // O el id del usuario real si lo hay en el contexto

            CentroEventosLog log = new CentroEventosLog();
            log.setId(UUID.randomUUID().toString());
            log.setEmpresaId(empresaId);
            log.setUsuarioId(usuarioId);
            log.setFechaHora(LocalDateTime.now());
            
            log.setMensajeAmigable((String) payload.get("mensajeAmigable"));
            log.setDetalleTecnico((String) payload.get("detalleTecnico"));
            
            if (payload.containsKey("datosContexto")) {
                // Convertimos a string si es necesario o guardamos directo
                log.setDatosContexto(String.valueOf(payload.get("datosContexto")));
            }

            repositorio.save(log);

            return ResponseEntity.ok(Map.of("message", "Evento reportado exitosamente."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al reportar el evento."));
        }
    }
}
