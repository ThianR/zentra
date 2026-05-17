package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.SifenReferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de tablas de referencia SIFEN.
 * 
 * @author Antigravity
 */
@Repository
public interface SifenReferenciaRepository extends JpaRepository<SifenReferencia, Long> {

    List<SifenReferencia> findByTipoOrderByOrdenAscDescripcionAsc(String tipo);

    List<SifenReferencia> findByTipoAndActivoOrderByOrdenAscDescripcionAsc(String tipo, Boolean activo);

    List<SifenReferencia> findByTipoAndPadreCodigoAndActivoOrderByOrdenAscDescripcionAsc(String tipo, String padreCodigo, Boolean activo);

    Optional<SifenReferencia> findByTipoAndCodigo(String tipo, String codigo);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM SifenReferencia c WHERE c.tipo = 'CIUDAD' AND c.activo = true AND c.padreCodigo IN (SELECT d.codigo FROM SifenReferencia d WHERE d.tipo = 'DISTRITO' AND d.padreCodigo = :codDepto AND d.activo = true) ORDER BY c.descripcion ASC")
    List<SifenReferencia> findCiudadesByDepartamento(@org.springframework.data.repository.query.Param("codDepto") String codDepto);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM SifenReferencia c WHERE c.tipo = 'CIUDAD' AND c.codigo = :codCiudad AND c.activo = true AND c.padreCodigo IN (SELECT d.codigo FROM SifenReferencia d WHERE d.tipo = 'DISTRITO' AND d.padreCodigo = :codDepto AND d.activo = true)")
    Optional<SifenReferencia> findCiudadByDeptoAndCiudadCod(@org.springframework.data.repository.query.Param("codDepto") String codDepto, @org.springframework.data.repository.query.Param("codCiudad") String codCiudad);
}
