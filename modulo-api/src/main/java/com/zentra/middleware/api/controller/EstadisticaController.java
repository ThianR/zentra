package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * Controlador para la generación de datos analíticos y estadísticas del Dashboard.
 */
@RestController
@RequestMapping("/api/v1/estadisticas")
public class EstadisticaController {

    private static final Logger logger = Logger.getLogger(EstadisticaController.class.getName());

    @Autowired
    private DocumentoElectronicoRepository dteRepository;

    @GetMapping("/resumen-diario")
    public ResponseEntity<?> getResumenDiario(
            @RequestParam(required = false, defaultValue = "30") int dias) {
        try {
            LocalDateTime desde = LocalDateTime.now().minusDays(dias).withHour(0).withMinute(0).withSecond(0);
            List<Object[]> data = dteRepository.resumenDiario(desde);
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] row : data) {
                Map<String, Object> map = new HashMap<>();
                map.put("fecha", row[0]);
                map.put("estado", row[1].toString());
                map.put("cantidad", row[2]);
                map.put("montoTotal", row[3]);
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("Error en resumen-diario: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/resumen-estado")
    public ResponseEntity<?> getResumenEstado() {
        try {
            List<Object[]> data = dteRepository.resumenEstadoGlobal();
            
            long aprobados = 0, rechazados = 0, pendientes = 0, enProceso = 0, anulados = 0;
            
            for (Object[] row : data) {
                String estado = row[0].toString();
                long count = (long) row[1];
                
                if (estado.equals("APROBADO")) aprobados += count;
                else if (estado.contains("ERROR") || estado.equals("RECHAZADO")) rechazados += count;
                else if (estado.equals("CREADO") || estado.equals("FIRMADO") || estado.equals("ENVIADO")) pendientes += count;
                else if (estado.equals("EN_PROCESO")) enProceso += count;
                else if (estado.equals("ANULADO")) anulados += count;
            }

            Map<String, Object> res = new HashMap<>();
            res.put("aprobados", aprobados);
            res.put("rechazados", rechazados);
            res.put("pendientes", pendientes);
            res.put("enProceso", enProceso);
            res.put("anulados", anulados);
            
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            logger.severe("Error en resumen-estado: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/top-receptores")
    public ResponseEntity<?> getTopReceptores(@RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            List<Object[]> data = dteRepository.topReceptores(PageRequest.of(0, limit));
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] row : data) {
                Map<String, Object> map = new HashMap<>();
                map.put("ruc", row[0]);
                map.put("razonSocial", row[1]);
                map.put("cantidad", row[2]);
                map.put("montoTotal", row[3]);
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("Error en top-receptores: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/facturacion-mensual")
    public ResponseEntity<?> getFacturacionMensual(@RequestParam(required = false, defaultValue = "6") int meses) {
        try {
            LocalDateTime desde = LocalDateTime.now().minusMonths(meses).withDayOfMonth(1).withHour(0).withMinute(0);
            List<Object[]> data = dteRepository.facturacionMensual(desde);
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] row : data) {
                Map<String, Object> map = new HashMap<>();
                map.put("anio", row[0]);
                map.put("mes", row[1]);
                map.put("cantidadDtes", row[2]);
                map.put("montoTotal", row[3]);
                map.put("montoIva", row[4]);
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.severe("Error en facturacion-mensual: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }
}
