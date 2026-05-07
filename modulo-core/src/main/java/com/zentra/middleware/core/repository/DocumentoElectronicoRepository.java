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

    // --- Consultas para Estadísticas ---

    @org.springframework.data.jpa.repository.Query("SELECT CAST(d.fechaCreacion AS date) as fecha, d.estado, COUNT(d), COALESCE(SUM(d.totalOperacion),0) " +
           "FROM DocumentoElectronico d WHERE d.fechaCreacion >= :desde GROUP BY CAST(d.fechaCreacion AS date), d.estado ORDER BY fecha DESC")
    java.util.List<Object[]> resumenDiario(@org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde);

    @org.springframework.data.jpa.repository.Query("SELECT d.rucReceptor, d.receptorRazonSocial, COUNT(d), COALESCE(SUM(d.totalOperacion),0) " +
           "FROM DocumentoElectronico d WHERE d.estado = 'APROBADO' GROUP BY d.rucReceptor, d.receptorRazonSocial ORDER BY COUNT(d) DESC")
    java.util.List<Object[]> topReceptores(org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT FUNCTION('YEAR', d.fechaCreacion), FUNCTION('MONTH', d.fechaCreacion), COUNT(d), " +
           "COALESCE(SUM(d.totalOperacion),0), COALESCE(SUM(d.totalIva),0) " +
       "FROM DocumentoElectronico d WHERE d.fechaCreacion >= :desde GROUP BY FUNCTION('YEAR', d.fechaCreacion), FUNCTION('MONTH', d.fechaCreacion) ORDER BY 1 DESC, 2 DESC")
    java.util.List<Object[]> facturacionMensual(@org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde);

    @org.springframework.data.jpa.repository.Query("SELECT d.estado, COUNT(d) FROM DocumentoElectronico d GROUP BY d.estado")
    java.util.List<Object[]> resumenEstadoGlobal();
}
