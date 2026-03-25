package com.zentra.middleware.core.service;

import com.zentra.middleware.core.model.SifenReferencia;
import com.zentra.middleware.core.repository.SifenReferenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de tablas de referencia SIFEN centralizadas.
 */
@Service
@Transactional(readOnly = true)
public class SifenReferenciaService {

    private final SifenReferenciaRepository repository;

    public SifenReferenciaService(SifenReferenciaRepository repository) {
        this.repository = repository;
    }

    public List<SifenReferencia> listarPorTipo(String tipo) {
        return repository.findByTipoAndActivoOrderByOrdenAscDescripcionAsc(tipo, true);
    }

    public List<SifenReferencia> listarPorTipoYPadre(String tipo, String padreCodigo) {
        return repository.findByTipoAndPadreCodigoAndActivoOrderByOrdenAscDescripcionAsc(tipo, padreCodigo, true);
    }

    public Optional<SifenReferencia> buscarPorCodigo(String tipo, String codigo) {
        return repository.findByTipoAndCodigo(tipo, codigo);
    }

    public String obtenerDescripcion(String tipo, String codigo) {
        return repository.findByTipoAndCodigo(tipo, codigo)
                .map(SifenReferencia::getDescripcion)
                .orElse(codigo);
    }
}
