package com.zentra.middleware.core.service;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.core.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de documentos electrónicos y emisores.
 */
@Service
@Transactional
public class DocumentoService {

    private final DocumentoElectronicoRepository repository;
    private final EmpresaRepository empresaRepository;

    public DocumentoService(DocumentoElectronicoRepository repository, EmpresaRepository empresaRepository) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
    }

    public List<DocumentoElectronico> obtenerTodos() {
        return repository.findAllByOrderByFechaCreacionDesc();
    }

    public DocumentoElectronico obtenerPorId(@org.springframework.lang.NonNull String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Documento no encontrado"));
    }

    public DocumentoElectronico guardar(@org.springframework.lang.NonNull DocumentoElectronico dte) {
        return repository.save(dte);
    }

    public Empresa obtenerEmpresaPorRuc(@org.springframework.lang.NonNull String ruc) {
        return empresaRepository.findByRuc(ruc).orElseThrow(() -> new RuntimeException("Empresa no encontrada: " + ruc));
    }

    public boolean existePorNumero(@org.springframework.lang.NonNull String numeroComprobante, @org.springframework.lang.NonNull String tipoDocumento) {
        return repository.existsByNumeroComprobanteAndTipoDocumento(numeroComprobante, tipoDocumento);
    }

    public void eliminarSiEstaRechazado(String numero, String tipo) {
        repository.findByNumeroComprobanteAndTipoDocumento(numero, tipo).ifPresent(dte -> {
            if (dte.getEstado() == com.zentra.middleware.core.model.EstadoDte.RECHAZADO 
                || dte.getEstado() == com.zentra.middleware.core.model.EstadoDte.ERROR_ENVIO) {
                repository.delete(dte);
                repository.flush();
            }
        });
    }
}
