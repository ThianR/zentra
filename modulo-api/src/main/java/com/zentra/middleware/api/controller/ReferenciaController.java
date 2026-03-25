package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.model.SifenReferencia;
import com.zentra.middleware.core.service.SifenReferenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint para obtener las tablas de referencia SIFEN.
 */
@RestController
@RequestMapping("/api/v1/referencia")
@CrossOrigin(origins = "*")
public class ReferenciaController {

    private final SifenReferenciaService referenciaService;

    public ReferenciaController(SifenReferenciaService referenciaService) {
        this.referenciaService = referenciaService;
    }

    @GetMapping("/{tipo}")
    public ResponseEntity<List<SifenReferencia>> listarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(referenciaService.listarPorTipo(tipo.toUpperCase()));
    }

    @GetMapping("/ciudad/{codDepto}")
    public ResponseEntity<List<SifenReferencia>> listarCiudades(@PathVariable String codDepto) {
        return ResponseEntity.ok(referenciaService.listarPorTipoYPadre("CIUDAD", codDepto));
    }

    @GetMapping("/{tipo}/{codigo}")
    public ResponseEntity<SifenReferencia> buscarPorCodigo(
            @PathVariable String tipo, 
            @PathVariable String codigo) {
        return referenciaService.buscarPorCodigo(tipo.toUpperCase(), codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
