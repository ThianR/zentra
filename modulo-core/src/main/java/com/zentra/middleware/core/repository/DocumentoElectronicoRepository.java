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

    java.util.List<DocumentoElectronico> findByEstadoAndTipoEmision(com.zentra.middleware.core.model.EstadoDte estado, Integer tipoEmision);

    java.util.List<DocumentoElectronico> findAllByOrderByFechaCreacionDesc();

    java.util.List<DocumentoElectronico> findByEmisorIdOrderByFechaCreacionDesc(String empresaId);

    java.util.Optional<DocumentoElectronico> findByCdc(String cdc);

    // --- Consultas para Estadísticas ---

    @org.springframework.data.jpa.repository.Query("SELECT CAST(d.fechaCreacion AS date) as fecha, d.estado, COUNT(d), COALESCE(SUM(d.totalOperacion),0) " +
           "FROM DocumentoElectronico d WHERE d.emisor.id = :empresaId AND d.fechaCreacion >= :desde GROUP BY CAST(d.fechaCreacion AS date), d.estado ORDER BY fecha DESC")
    java.util.List<Object[]> resumenDiario(@org.springframework.data.repository.query.Param("empresaId") String empresaId, @org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde);

    @org.springframework.data.jpa.repository.Query("SELECT d.rucReceptor, d.receptorRazonSocial, COUNT(d), COALESCE(SUM(d.totalOperacion),0) " +
           "FROM DocumentoElectronico d WHERE d.emisor.id = :empresaId AND d.estado = 'APROBADO' GROUP BY d.rucReceptor, d.receptorRazonSocial ORDER BY COUNT(d) DESC")
    java.util.List<Object[]> topReceptores(@org.springframework.data.repository.query.Param("empresaId") String empresaId, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT EXTRACT(YEAR FROM d.fechaCreacion), EXTRACT(MONTH FROM d.fechaCreacion), COUNT(d), " +
           "COALESCE(SUM(d.totalOperacion),0), COALESCE(SUM(d.totalIva),0) " +
       "FROM DocumentoElectronico d WHERE d.emisor.id = :empresaId AND d.fechaCreacion >= :desde GROUP BY EXTRACT(YEAR FROM d.fechaCreacion), EXTRACT(MONTH FROM d.fechaCreacion) ORDER BY 1 DESC, 2 DESC")
    java.util.List<Object[]> facturacionMensual(@org.springframework.data.repository.query.Param("empresaId") String empresaId, @org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde);

    @org.springframework.data.jpa.repository.Query("SELECT d.estado, COUNT(d) FROM DocumentoElectronico d WHERE d.emisor.id = :empresaId GROUP BY d.estado")
    java.util.List<Object[]> resumenEstadoGlobal(@org.springframework.data.repository.query.Param("empresaId") String empresaId);
}
