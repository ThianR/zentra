package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.EstadoLote;
import com.zentra.middleware.core.model.LoteTransmision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoteTransmisionRepository extends JpaRepository<LoteTransmision, String> {
    List<LoteTransmision> findByEstado(EstadoLote estado);
    List<LoteTransmision> findByEstadoAndFechaUltimaConsultaBefore(EstadoLote estado, LocalDateTime fecha);
    List<LoteTransmision> findByEmpresaIdOrderByFechaEnvioDesc(String empresaId);
}
