package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.DocumentoElectronico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoElectronicoRepository extends JpaRepository<DocumentoElectronico, String> {
    boolean existsByNumeroComprobanteAndTipoDocumento(String numeroComprobante, String tipoDocumento);
    java.util.Optional<DocumentoElectronico> findByNumeroComprobanteAndTipoDocumento(String numeroComprobante, String tipoDocumento);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(d) > 0 FROM DocumentoElectronico d WHERE d.timbrado = :timbrado AND d.numeroComprobante >= :rangoDesdeStr AND d.numeroComprobante <= :rangoHastaStr")
    boolean existsDteInRange(@org.springframework.data.repository.query.Param("timbrado") String timbrado,
                             @org.springframework.data.repository.query.Param("rangoDesdeStr") String rangoDesdeStr,
                             @org.springframework.data.repository.query.Param("rangoHastaStr") String rangoHastaStr);

    java.util.List<DocumentoElectronico> findAllByOrderByFechaCreacionDesc();

    java.util.Optional<DocumentoElectronico> findByCdc(String cdc);
}
