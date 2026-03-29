package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.DocumentoElectronico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoElectronicoRepository extends JpaRepository<DocumentoElectronico, String> {
    boolean existsByNumeroComprobanteAndTipoDocumento(String numeroComprobante, String tipoDocumento);
    java.util.Optional<DocumentoElectronico> findByNumeroComprobanteAndTipoDocumento(String numeroComprobante, String tipoDocumento);
    java.util.List<DocumentoElectronico> findAllByOrderByFechaCreacionDesc();
}
