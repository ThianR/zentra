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
}
