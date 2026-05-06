package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.enums.EstadoEvento;
import com.zentra.middleware.core.enums.TipoEvento;
import com.zentra.middleware.core.model.EventoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link EventoDocumento}.
 *
 * <p>Provee las operaciones de persistencia estándar más consultas
 * específicas para el módulo de gestión de eventos SIFEN.</p>
 */
@Repository
public interface EventoDocumentoRepository extends JpaRepository<EventoDocumento, String> {

    /**
     * Busca todos los eventos asociados a un CDC específico.
     * Permite verificar si un DTE ya fue cancelado.
     *
     * @param cdcRelacionado CDC del DTE afectado (44 dígitos).
     * @return Lista de eventos registrados para ese CDC.
     */
    List<EventoDocumento> findByCdcRelacionado(String cdcRelacionado);

    /**
     * Busca el evento de cancelación aprobado para un CDC específico.
     * Utilizado para verificar el estado de cancelación de un DTE.
     *
     * @param cdcRelacionado CDC del DTE.
     * @param tipoEvento     Tipo de evento (CANCELACION).
     * @param estado         Estado buscado (APROBADO).
     * @return El evento si existe.
     */
    Optional<EventoDocumento> findByCdcRelacionadoAndTipoEventoAndEstado(
            String cdcRelacionado, TipoEvento tipoEvento, EstadoEvento estado);

    /**
     * Lista todos los eventos de una empresa, ordenados por fecha descendente.
     *
     * @param empresaId ID de la empresa emisora.
     * @return Lista de eventos de la empresa.
     */
    List<EventoDocumento> findByEmpresaIdOrderByFechaCreacionDesc(String empresaId);

    /**
     * Lista todos los eventos de un tipo dado, ordenados por fecha descendente.
     * Útil para el historial de cancelaciones o inutilizaciones.
     *
     * @param tipoEvento Tipo de evento a filtrar.
     * @return Lista de eventos.
     */
    List<EventoDocumento> findByTipoEventoOrderByFechaCreacionDesc(TipoEvento tipoEvento);

    /**
     * Lista todos los eventos ordenados por fecha de creación descendente.
     *
     * @return Lista completa de eventos.
     */
    List<EventoDocumento> findAllByOrderByFechaCreacionDesc();
}
