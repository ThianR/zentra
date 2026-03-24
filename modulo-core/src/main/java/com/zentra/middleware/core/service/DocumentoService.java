package com.zentra.middleware.core.service;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.core.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la gestión de documentos electrónicos y emisores.
 */
@Service
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

    public DocumentoElectronico obtenerPorId(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Documento no encontrado"));
    }

    public DocumentoElectronico guardar(DocumentoElectronico dte) {
        return repository.save(dte);
    }

    public Empresa obtenerEmpresaPorRuc(String ruc) {
        return empresaRepository.findByRuc(ruc).orElseThrow(() -> new RuntimeException("Empresa no encontrada: " + ruc));
    }

    public boolean existePorNumero(String numeroComprobante, String tipoDocumento) {
        return repository.existsByNumeroComprobanteAndTipoDocumento(numeroComprobante, tipoDocumento);
    }
}
