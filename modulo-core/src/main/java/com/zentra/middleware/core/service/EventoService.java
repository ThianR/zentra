package com.zentra.middleware.core.service;

import com.zentra.middleware.core.enums.EstadoEvento;
import com.zentra.middleware.core.enums.TipoEvento;
import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.model.EventoDocumento;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.repository.EventoDocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para la gestión de Eventos SIFEN v150.
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Validar precondiciones de negocio antes de crear un evento.</li>
 *   <li>Persistir y actualizar el estado del evento en base de datos.</li>
 *   <li>Consultar el historial de eventos por empresa o por DTE.</li>
 * </ul>
 *
 * <p>Este servicio opera sobre el dominio puro (sin lógica XML ni SOAP).
 * La generación de XML y la comunicación con SIFEN se delegan a capas
 * superiores (modulo-xml y modulo-sifen).</p>
 */
@Service
@Transactional
public class EventoService {

    private final EventoDocumentoRepository eventoRepository;
    private final DocumentoElectronicoRepository documentoRepository;
    private final EmpresaRepository empresaRepository;

    public EventoService(EventoDocumentoRepository eventoRepository,
                         DocumentoElectronicoRepository documentoRepository,
                         EmpresaRepository empresaRepository) {
        this.eventoRepository = eventoRepository;
        this.documentoRepository = documentoRepository;
        this.empresaRepository = empresaRepository;
    }

    // =========================================================================
    // Creación de eventos
    // =========================================================================

    /**
     * Crea y persiste el registro inicial de un evento de Cancelación.
     *
     * <p>Valida que:</p>
     * <ol>
     *   <li>El CDC referenciado corresponde a un DTE existente.</li>
     *   <li>El DTE está en estado APROBADO (sólo se puede cancelar lo que aprobó SIFEN).</li>
     *   <li>No existe ya un evento de cancelación APROBADO para ese CDC.</li>
     *   <li>El motivo tiene al menos 5 caracteres.</li>
     * </ol>
     *
     * @param cdc      CDC del DTE a cancelar (44 dígitos).
     * @param motivo   Motivo de cancelación.
     * @param empresa  Empresa emisora.
     * @return El evento persistido en estado PENDIENTE.
     * @throws IllegalArgumentException si alguna validación de negocio falla.
     */
    public EventoDocumento iniciarCancelacion(String cdc, String motivo, Empresa empresa) {
        validarMotivo(motivo);

        // Regla 1: El DTE referenciado debe existir
        String idDte = java.util.Objects.requireNonNull(buscarIdPorCdc(cdc));
        DocumentoElectronico dte = documentoRepository.findById(idDte)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un DTE con CDC: " + cdc));

        // Regla 2: El DTE debe estar APROBADO por SIFEN
        if (dte.getEstado() != EstadoDte.APROBADO) {
            throw new IllegalArgumentException(
                    "Solo se pueden cancelar DTEs en estado APROBADO. Estado actual: " + dte.getEstado());
        }

        // Regla 3: No debe existir ya una cancelación aprobada para este CDC
        Optional<EventoDocumento> cancelacionExistente = eventoRepository
                .findByCdcRelacionadoAndTipoEventoAndEstado(cdc, TipoEvento.CANCELACION, EstadoEvento.APROBADO);
        if (cancelacionExistente.isPresent()) {
            throw new IllegalArgumentException("El DTE con CDC " + cdc + " ya fue cancelado.");
        }

        // Regla 4: Validaciones temporales (Regla de las 72 horas)
        if (dte.getFechaCreacion() != null) {
            java.time.LocalDateTime limite = dte.getFechaCreacion().plusHours(72);
            if (java.time.LocalDateTime.now().isAfter(limite)) {
                throw new IllegalArgumentException(
                        "Operación rechazada por Regla de Negocio: Han transcurrido más de 72 horas desde la emisión del DTE (" + 
                        dte.getFechaCreacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "). SIFEN no permite cancelaciones fuera de este plazo.");
            }
        }

        EventoDocumento evento = new EventoDocumento();
        evento.setTipoEvento(TipoEvento.CANCELACION);
        evento.setEstado(EstadoEvento.PENDIENTE);
        evento.setEmpresa(empresa);
        evento.setDocumentoAsociado(dte);
        evento.setCdcRelacionado(cdc);
        evento.setMotivo(motivo);

        return eventoRepository.save(evento);
    }

    /**
     * Crea y persiste el registro inicial de un evento de Inutilización.
     *
     * <p>Valida que:</p>
     * <ol>
     *   <li>El rango {@code desde}-{@code hasta} es coherente (desde ≤ hasta).</li>
     *   <li>El timbrado tiene exactamente 8 dígitos.</li>
     *   <li>El motivo tiene al menos 5 caracteres.</li>
     * </ol>
     *
     * @param empresa         Empresa emisora.
     * @param timbrado        Número de timbrado (8 dígitos).
     * @param tipoDoc         Código del tipo de documento a inutilizar.
     * @param establecimiento Código de establecimiento (3 dígitos).
     * @param puntoExpedicion Código de punto de expedición (3 dígitos).
     * @param rangoDesde      Número inicial del rango.
     * @param rangoHasta      Número final del rango.
     * @param motivo          Motivo de inutilización.
     * @return El evento persistido en estado PENDIENTE.
     * @throws IllegalArgumentException si alguna validación de negocio falla.
     */
    public EventoDocumento iniciarInutilizacion(Empresa empresa,
                                                String timbrado,
                                                Integer tipoDoc,
                                                String establecimiento,
                                                String puntoExpedicion,
                                                long rangoDesde,
                                                long rangoHasta,
                                                String motivo) {
        validarMotivo(motivo);
        validarTimbrado(timbrado);

        if (rangoDesde > rangoHasta) {
            throw new IllegalArgumentException(
                    "El rango de inutilización es inválido: desde=" + rangoDesde + " debe ser <= hasta=" + rangoHasta);
        }

        String est = String.format("%03d", Integer.parseInt(establecimiento.replaceAll("[^0-9]", "0")));
        String pun = String.format("%03d", Integer.parseInt(puntoExpedicion.replaceAll("[^0-9]", "0")));
        
        String rangoDesdeStr = String.format("%s-%s-%07d", est, pun, rangoDesde);
        String rangoHastaStr = String.format("%s-%s-%07d", est, pun, rangoHasta);

        // Validar que no haya documentos emitidos en ese rango
        if (documentoRepository.existsDteInRange(timbrado, rangoDesdeStr, rangoHastaStr)) {
            throw new IllegalArgumentException("No se puede inutilizar el rango " + rangoDesdeStr + " al " + rangoHastaStr + 
                " porque existen documentos electrónicos emitidos en ese rango para el timbrado " + timbrado);
        }

        EventoDocumento evento = new EventoDocumento();
        evento.setTipoEvento(TipoEvento.INUTILIZACION);
        evento.setEstado(EstadoEvento.PENDIENTE);
        evento.setEmpresa(empresa);
        evento.setMotivo(motivo);
        evento.setTimbrado(timbrado);
        evento.setTipoDocumentoInutilizar(tipoDoc);
        evento.setEstablecimiento(String.format("%03d", Integer.parseInt(establecimiento.replaceAll("[^0-9]", "0"))));
        evento.setPuntoExpedicion(String.format("%03d", Integer.parseInt(puntoExpedicion.replaceAll("[^0-9]", "0"))));
        evento.setRangoDesde(rangoDesde);
        evento.setRangoHasta(rangoHasta);

        return eventoRepository.save(evento);
    }

    /**
     * Crea y persiste el registro inicial de un evento de Receptor.
     *
     * <p>Eventos soportados:</p>
     * <ul>
     *   <li>Conformidad (2)</li>
     *   <li>Disconformidad (4) - Requiere motivo</li>
     *   <li>Desconocimiento (5) - Requiere motivo</li>
     *   <li>Notificación de Recepción (6)</li>
     * </ul>
     *
     * @param cdc        CDC del documento recibido (44 dígitos).
     * @param tipoEvento Tipo de evento a generar.
     * @param motivo     Motivo (obligatorio para disconformidad y desconocimiento).
     * @param empresa    Empresa receptora (que genera el evento).
     * @return El evento persistido en estado PENDIENTE.
     * @throws IllegalArgumentException si falla la validación de negocio.
     */
    public EventoDocumento iniciarEventoReceptor(String cdc, TipoEvento tipoEvento, String motivo, Empresa empresa) {
        if (cdc == null || cdc.length() != 44) {
            throw new IllegalArgumentException("El CDC debe tener exactamente 44 dígitos.");
        }

        if (tipoEvento == TipoEvento.DISCONFORMIDAD || tipoEvento == TipoEvento.DESCONOCIMIENTO) {
            validarMotivo(motivo);
        }

        // Regla: No generar el mismo evento exitoso dos veces para el mismo CDC
        Optional<EventoDocumento> eventoExistente = eventoRepository
                .findByCdcRelacionadoAndTipoEventoAndEstado(cdc, tipoEvento, EstadoEvento.APROBADO);
        if (eventoExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un evento de " + tipoEvento.getDescripcion() + " APROBADO para el CDC " + cdc);
        }

        EventoDocumento evento = new EventoDocumento();
        evento.setTipoEvento(tipoEvento);
        evento.setEstado(EstadoEvento.PENDIENTE);
        evento.setEmpresa(empresa);
        evento.setCdcRelacionado(cdc);
        evento.setMotivo(motivo != null ? motivo.trim() : null);

        return eventoRepository.save(evento);
    }

    // =========================================================================
    // Actualización de estado
    // =========================================================================

    /**
     * Registra el XML generado y actualiza el estado a FIRMADO.
     * Llamado por la capa de firma (modulo-crypto) tras firmar exitosamente.
     *
     * @param evento     El evento a actualizar.
     * @param xmlFirmado XML con la firma XMLDSig incluida.
     * @param idFirma    Identificador del nodo firmado (atributo Id de gGroupGestE).
     * @return El evento actualizado.
     */
    public EventoDocumento registrarFirma(EventoDocumento evento, String xmlGenerado,
                                          String xmlFirmado, String idFirma) {
        evento.setXmlGenerado(xmlGenerado);
        evento.setXmlFirmado(xmlFirmado);
        evento.setIdFirma(idFirma);
        evento.setEstado(EstadoEvento.FIRMADO);
        return eventoRepository.save(evento);
    }

    /**
     * Registra el envío a SIFEN y actualiza el estado a ENVIADO.
     *
     * @param evento El evento transmitido.
     * @return El evento actualizado.
     */
    public EventoDocumento registrarEnvio(EventoDocumento evento) {
        evento.setEstado(EstadoEvento.ENVIADO);
        evento.setFechaEnvio(java.time.LocalDateTime.now());
        return eventoRepository.save(evento);
    }

    /**
     * Registra la respuesta de SIFEN y actualiza el estado final del evento.
     *
     * @param evento          El evento respondido.
     * @param codigoSifen     Código de resultado (ej: "0300" = Aprobado).
     * @param mensajeSifen    Mensaje oficial de SIFEN.
     * @param mensajeUsuario  Mensaje amigable para el usuario.
     * @param xmlRespuesta    XML crudo de la respuesta SOAP.
     * @return El evento actualizado con su estado final.
     */
    public EventoDocumento registrarRespuesta(EventoDocumento evento,
                                              String codigoSifen,
                                              String mensajeSifen,
                                              String mensajeUsuario,
                                              String xmlRespuesta) {
        evento.setCodigoSifen(codigoSifen);
        evento.setMensajeSifen(mensajeSifen);
        evento.setMensajeUsuario(mensajeUsuario);
        evento.setXmlRespuestaSifen(xmlRespuesta);
        evento.setFechaRespuesta(java.time.LocalDateTime.now());

        // Códigos de Éxito en SIFEN para Eventos:
        // 0600 = Evento registrado correctamente
        // 0601 = Evento registrado con observación
        // 4003 = CDC ya se encuentra con el mismo evento solicitado (Idempotencia)
        // También incluimos códigos de documentos por si acaso (0300, 0301, 0260)
        boolean aprobado = "0600".equals(codigoSifen) || "0601".equals(codigoSifen) || "4003".equals(codigoSifen)
                        || "0300".equals(codigoSifen) || "0301".equals(codigoSifen) || "0260".equals(codigoSifen);
        evento.setEstado(aprobado ? EstadoEvento.APROBADO : EstadoEvento.RECHAZADO);

        // Si el evento de cancelación fue aprobado, se actualiza el estado del DTE original
        if (aprobado && evento.getTipoEvento() == TipoEvento.CANCELACION
                && evento.getDocumentoAsociado() != null) {
            DocumentoElectronico dte = evento.getDocumentoAsociado();
            dte.setEstado(EstadoDte.ANULADO);
            documentoRepository.save(dte);
        }

        return eventoRepository.save(evento);
    }

    /**
     * Registra un error de transmisión y actualiza el estado a ERROR_ENVIO.
     *
     * @param evento          El evento que falló.
     * @param mensajeError    Descripción del error técnico.
     * @return El evento actualizado.
     */
    public EventoDocumento registrarErrorEnvio(EventoDocumento evento, String mensajeError) {
        evento.setEstado(EstadoEvento.ERROR_ENVIO);
        evento.setMensajeUsuario("Error de transmisión: " + mensajeError);
        evento.setFechaRespuesta(java.time.LocalDateTime.now());
        return eventoRepository.save(evento);
    }

    // =========================================================================
    // Consultas
    // =========================================================================

    /**
     * Obtiene un evento por su ID. Lanza excepción si no existe.
     *
     * @param id Identificador UUID del evento.
     * @return El evento encontrado.
     */
    @Transactional(readOnly = true)
    public EventoDocumento obtenerPorId(String id) {
        String nonNullId = java.util.Objects.requireNonNull(id);
        return eventoRepository.findById(nonNullId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado: " + id));
    }

    /**
     * Lista todos los eventos ordenados por fecha descendente.
     *
     * @return Lista de todos los eventos.
     */
    @Transactional(readOnly = true)
    public List<EventoDocumento> obtenerTodos(String empresaId) {
        return eventoRepository.findByEmpresaIdOrderByFechaCreacionDesc(empresaId);
    }

    /**
     * Lista los eventos registrados para un CDC específico.
     *
     * @param cdc CDC del DTE (44 dígitos).
     * @return Lista de eventos asociados.
     */
    @Transactional(readOnly = true)
    public List<EventoDocumento> obtenerPorCdc(String cdc) {
        return eventoRepository.findByCdcRelacionado(cdc);
    }

    /**
     * Verifica si un DTE ya fue cancelado exitosamente en SIFEN.
     *
     * @param cdc CDC del DTE a verificar.
     * @return {@code true} si existe un evento de cancelación APROBADO para ese CDC.
     */
    @Transactional(readOnly = true)
    public boolean estaCancelado(String cdc) {
        return eventoRepository.findByCdcRelacionadoAndTipoEventoAndEstado(
                cdc, TipoEvento.CANCELACION, EstadoEvento.APROBADO).isPresent();
    }

    /**
     * Obtiene la empresa por su ID para usarla en la creación de eventos.
     *
     * @param empresaId ID de la empresa.
     * @return La empresa encontrada.
     */
    @Transactional(readOnly = true)
    public Empresa obtenerEmpresaPorId(String empresaId) {
        String nonNullId = java.util.Objects.requireNonNull(empresaId);
        return empresaRepository.findById(nonNullId)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada: " + empresaId));
    }

    // =========================================================================
    // Validaciones internas
    // =========================================================================

    private void validarMotivo(String motivo) {
        if (motivo == null || motivo.trim().length() < 5) {
            throw new IllegalArgumentException(
                    "El motivo del evento debe tener al menos 5 caracteres.");
        }
        if (motivo.trim().length() > 500) {
            throw new IllegalArgumentException(
                    "El motivo del evento no puede superar los 500 caracteres.");
        }
    }

    private void validarTimbrado(String timbrado) {
        if (timbrado == null || !timbrado.matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El timbrado debe contener exactamente 8 dígitos numéricos.");
        }
    }

    /**
     * Busca el ID de la entidad {@code DocumentoElectronico} a partir de un CDC.
     * El CDC se almacena como campo en la entidad, no como PK (la PK es UUID).
     *
     * @param cdc CDC del DTE (44 dígitos).
     * @return ID (UUID) del documento.
     * @throws IllegalArgumentException si no se encuentra el DTE.
     */
    private String buscarIdPorCdc(String cdc) {
        return documentoRepository.findAll().stream()
                .filter(d -> cdc.equals(d.getCdc()))
                .map(DocumentoElectronico::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un DTE registrado con CDC: " + cdc));
    }
}
