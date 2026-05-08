package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.enums.Ambiente;
import com.zentra.middleware.core.enums.EstadoEvento;
import com.zentra.middleware.core.enums.TipoEvento;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.EventoDocumento;
import com.zentra.middleware.core.service.EventoService;
import com.zentra.middleware.crypto.service.XmlSignerService;
import com.zentra.middleware.sifen.SifenSoapClient;
import com.zentra.middleware.xml.EventoXmlGenerator;
import com.zentra.middleware.xml.XsdValidatorService;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.zentra.middleware.api.security.EmpresaContext;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de Eventos SIFEN v150.
 *
 * <p>Expone los endpoints para Cancelación e Inutilización de DTEs,
 * orquestando el pipeline completo:</p>
 * <ol>
 *   <li>Validación de negocio ({@link EventoService}).</li>
 *   <li>Generación del XML del evento ({@link EventoXmlGenerator}).</li>
 *   <li>Firma XMLDSig ({@link XmlSignerService}).</li>
 *   <li>Transmisión al WS siRecepEvento ({@link SifenSoapClient}).</li>
 *   <li>Persistencia del resultado ({@link EventoService}).</li>
 * </ol>
 *
 * <p>Ruta base: {@code /api/v1/eventos}</p>
 */
@RestController
@RequestMapping("/api/v1/eventos")
@Transactional
public class EventoController {

    private static final Logger logger = Logger.getLogger(EventoController.class.getName());

    private final EventoService eventoService;
    private final EventoXmlGenerator eventoXmlGenerator;
    private final XmlSignerService signerService;
    private final SifenSoapClient sifenClient;
    private final XsdValidatorService xsdValidatorService;

    public EventoController(EventoService eventoService,
                            EventoXmlGenerator eventoXmlGenerator,
                            XmlSignerService signerService,
                            SifenSoapClient sifenClient,
                            XsdValidatorService xsdValidatorService) {
        this.eventoService = eventoService;
        this.eventoXmlGenerator = eventoXmlGenerator;
        this.signerService = signerService;
        this.sifenClient = sifenClient;
        this.xsdValidatorService = xsdValidatorService;
    }

    // =========================================================================
    // Cancelación de DTE
    // =========================================================================

    /**
     * Cancela un DTE aprobado en SIFEN.
     *
     * <p>Payload esperado:</p>
     * <pre>
     * {
     *   "cdc"       : "01800014603400110010011234567000000101194310001",  // 44 dígitos
     *   "motivo"    : "Se emitió con datos incorrectos",
     *   "empresaId" : "uuid-de-la-empresa"
     * }
     * </pre>
     *
     * <p>Respuesta exitosa (HTTP 200):</p>
     * <pre>
     * {
     *   "estado"         : "APROBADO",
     *   "codigoSifen"    : "0300",
     *   "mensajeSifen"   : "...",
     *   "mensajeUsuario" : "Evento aprobado exitosamente por SIFEN.",
     *   "eventoId"       : "uuid-del-evento"
     * }
     * </pre>
     *
     * @param payload JSON con cdc, motivo y empresaId.
     * @return Resultado del procesamiento del evento.
     */
    @PostMapping("/cancelacion")
    public ResponseEntity<Map<String, Object>> cancelarDte(@RequestBody Map<String, Object> payload) {
        logger.info("[EventoController.cancelarDte] Solicitud recibida.");

        String cdc       = extraerString(payload, "cdc");
        String motivo    = extraerString(payload, "motivo");
        
        // Priorizar empresaId del contexto, fallback al payload si no existe
        String empresaId = EmpresaContext.getEmpresaId();
        if (empresaId == null || empresaId.isBlank()) {
            empresaId = extraerString(payload, "empresaId");
        }

        // — Validación de campos obligatorios —
        if (cdc == null || cdc.length() != 44) {
            return badRequest("El campo 'cdc' es obligatorio y debe tener exactamente 44 dígitos.");
        }
        if (motivo == null || motivo.trim().length() < 5) {
            return badRequest("El campo 'motivo' es obligatorio (mínimo 5 caracteres).");
        }
        if (empresaId == null || empresaId.isBlank()) {
            return badRequest("El campo 'empresaId' es obligatorio.");
        }

        try {
            // 1. Obtener empresa y resolver ambiente
            Empresa empresa = eventoService.obtenerEmpresaPorId(empresaId);
            Ambiente ambiente = resolverAmbiente(empresa);

            // 2. Validar reglas de negocio y crear el registro del evento
            EventoDocumento evento = eventoService.iniciarCancelacion(cdc, motivo.trim(), empresa);

            // 3. Generar el ID único para la firma XMLDSig del evento
            String idFirma = "CAN-" + evento.getId().replace("-", "").substring(0, 16).toUpperCase();

            // 4. Generar el XML del evento con EventoXmlGenerator
            String xmlGenerado = eventoXmlGenerator.generarXmlCancelacion(
                cdc,
                motivo.trim(),
                empresa.getRuc(),
                empresa.getDv() != null ? empresa.getDv() : "0",
                idFirma
            );
            logger.info("[EventoController.cancelarDte] XML generado (" + xmlGenerado.length() + " chars).");

            // 5. Firmar el XML con la clave del certificado P12 de la empresa
            String xmlFirmado = signerService.firmarEventoXml(
                xmlGenerado,
                idFirma,
                empresa.getCertificadoFisico(),
                empresa.getRutaCertificado(),
                empresa.getPasswordCertificado()
            );
            logger.info("[EventoController.cancelarDte] XML firmado (" + xmlFirmado.length() + " chars).");

            // 6. Validación XSD Rigurosa
            xsdValidatorService.validarXml(xmlFirmado);

            // 7. Registrar firma y marcar envío
            eventoService.registrarFirma(evento, xmlGenerado, xmlFirmado, idFirma);

            // 7. Marcar como ENVIADO antes de la transmisión
            eventoService.registrarEnvio(evento);

            // 8. Transmitir al WS siRecepEvento de SIFEN
            boolean aprobado = sifenClient.enviarEvento(evento, ambiente);

            // 9. Persistir la respuesta de SIFEN (ya actualizada en el objeto evento por SifenSoapClient)
            eventoService.registrarRespuesta(
                evento,
                evento.getCodigoSifen(),
                evento.getMensajeSifen(),
                evento.getMensajeUsuario(),
                evento.getXmlRespuestaSifen()
            );

            logger.info("[EventoController.cancelarDte] Resultado: " + evento.getEstado()
                + " | Código SIFEN: " + evento.getCodigoSifen());

            return ResponseEntity.ok(construirRespuesta(evento));

        } catch (IllegalArgumentException e) {
            // Errores de validación de negocio (DTE no existe, ya cancelado, etc.)
            logger.warning("[EventoController.cancelarDte] Validación: " + e.getMessage());
            return badRequest(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "[EventoController.cancelarDte] Error inesperado: " + e.getMessage(), e);
            return serverError("Error procesando la cancelación: " + e.getMessage());
        }
    }

    // =========================================================================
    // Inutilización de numeración
    // =========================================================================

    /**
     * Inutiliza un rango de numeración de documentos en SIFEN.
     *
     * <p>Payload esperado:</p>
     * <pre>
     * {
     *   "empresaId"       : "uuid-de-la-empresa",
     *   "timbrado"        : "16770994",
     *   "tipoDocumento"   : 1,
     *   "establecimiento" : "001",
     *   "puntoExpedicion" : "001",
     *   "rangoDesde"      : 5,
     *   "rangoHasta"      : 7,
     *   "motivo"          : "Folios impresos defectuosos"
     * }
     * </pre>
     *
     * @param payload JSON con los datos de inutilización.
     * @return Resultado del procesamiento del evento.
     */
    @PostMapping("/inutilizacion")
    public ResponseEntity<Map<String, Object>> inutilizarNumeracion(@RequestBody Map<String, Object> payload) {
        logger.info("[EventoController.inutilizarNumeracion] Solicitud recibida.");

        String  empresaId       = EmpresaContext.getEmpresaId();
        if (empresaId == null || empresaId.isBlank()) {
            empresaId = extraerString(payload, "empresaId");
        }
        String  timbrado        = extraerString(payload, "timbrado");
        Integer tipoDocumento   = extraerInt(payload, "tipoDocumento");
        String  establecimiento = extraerString(payload, "establecimiento");
        String  puntoExpedicion = extraerString(payload, "puntoExpedicion");
        Long    rangoDesde      = extraerLong(payload, "rangoDesde");
        Long    rangoHasta      = extraerLong(payload, "rangoHasta");
        String  motivo          = extraerString(payload, "motivo");

        // — Validaciones básicas —
        if (empresaId == null || empresaId.isBlank())
            return badRequest("El campo 'empresaId' es obligatorio.");
        if (timbrado == null || !timbrado.matches("\\d{8}"))
            return badRequest("El campo 'timbrado' debe tener exactamente 8 dígitos numéricos.");
        if (tipoDocumento == null)
            return badRequest("El campo 'tipoDocumento' es obligatorio.");
        if (establecimiento == null || establecimiento.isBlank())
            return badRequest("El campo 'establecimiento' es obligatorio.");
        if (puntoExpedicion == null || puntoExpedicion.isBlank())
            return badRequest("El campo 'puntoExpedicion' es obligatorio.");
        if (rangoDesde == null || rangoHasta == null)
            return badRequest("Los campos 'rangoDesde' y 'rangoHasta' son obligatorios.");
        if (motivo == null || motivo.trim().length() < 5)
            return badRequest("El campo 'motivo' es obligatorio (mínimo 5 caracteres).");

        try {
            // 1. Obtener empresa y resolver ambiente
            Empresa empresa = eventoService.obtenerEmpresaPorId(empresaId);
            Ambiente ambiente = resolverAmbiente(empresa);

            // 2. Validar y crear el registro inicial
            EventoDocumento evento = eventoService.iniciarInutilizacion(
                empresa, timbrado, tipoDocumento,
                establecimiento, puntoExpedicion,
                rangoDesde, rangoHasta, motivo.trim()
            );

            // 3. ID de firma único para el nodo gGroupGestE
            String idFirma = "INU-" + evento.getId().replace("-", "").substring(0, 16).toUpperCase();

            // 4. Generar el XML de inutilización con el idFirma controlado externamente
            // (garantiza coherencia entre el Id del nodo XML y la Reference URI de la firma)
            String xmlGenerado = eventoXmlGenerator.generarXmlInutilizacion(
                timbrado, tipoDocumento,
                establecimiento, puntoExpedicion,
                rangoDesde, rangoHasta,
                motivo.trim(),
                empresa.getRuc(),
                empresa.getDv() != null ? empresa.getDv() : "0",
                idFirma
            );
            logger.info("[EventoController.inutilizarNumeracion] XML generado (" + xmlGenerado.length() + " chars).");

            // 5. Firmar el XML
            String xmlFirmado = signerService.firmarEventoXml(
                xmlGenerado, idFirma,
                empresa.getCertificadoFisico(),
                empresa.getRutaCertificado(),
                empresa.getPasswordCertificado()
            );
            logger.info("[EventoController.inutilizarNumeracion] XML firmado (" + xmlFirmado.length() + " chars).");

            // 6. Validación XSD Rigurosa
            xsdValidatorService.validarXml(xmlFirmado);

            // 7. Registrar firma y marcar envío
            eventoService.registrarFirma(evento, xmlGenerado, xmlFirmado, idFirma);
            eventoService.registrarEnvio(evento);

            // 7. Transmitir
            boolean aprobado = sifenClient.enviarEvento(evento, ambiente);

            // 8. Persistir resultado
            eventoService.registrarRespuesta(
                evento,
                evento.getCodigoSifen(),
                evento.getMensajeSifen(),
                evento.getMensajeUsuario(),
                evento.getXmlRespuestaSifen()
            );

            logger.info("[EventoController.inutilizarNumeracion] Resultado: " + evento.getEstado()
                + " | Código SIFEN: " + evento.getCodigoSifen());

            return ResponseEntity.ok(construirRespuesta(evento));

        } catch (IllegalArgumentException e) {
            logger.warning("[EventoController.inutilizarNumeracion] Validación: " + e.getMessage());
            return badRequest(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "[EventoController.inutilizarNumeracion] Error: " + e.getMessage(), e);
            return serverError("Error procesando la inutilización: " + e.getMessage());
        }
    }

    // =========================================================================
    // Eventos del Receptor (Conformidad, Disconformidad, etc.)
    // =========================================================================

    /**
     * Procesa un evento del receptor (Comprador).
     *
     * <p>Payload esperado:</p>
     * <pre>
     * {
     *   "cdc"        : "01800014603400110010011234567000000101194310001",
     *   "tipoEvento" : 2, // 2=Conformidad, 4=Disconformidad, 5=Desconocimiento, 6=Notificación
     *   "motivo"     : "Factura correcta", // Opcional para 2 y 6
     *   "empresaId"  : "uuid-de-la-empresa-receptora"
     * }
     * </pre>
     *
     * @param payload JSON con los datos del evento.
     * @return Resultado del procesamiento del evento.
     */
    @PostMapping("/receptor")
    public ResponseEntity<Map<String, Object>> eventoReceptor(@RequestBody Map<String, Object> payload) {
        logger.info("[EventoController.eventoReceptor] Solicitud recibida.");

        String  cdc        = extraerString(payload, "cdc");
        Integer tipoEvtInt = extraerInt(payload, "tipoEvento");
        String  motivo     = extraerString(payload, "motivo");
        String  empresaId  = EmpresaContext.getEmpresaId();
        if (empresaId == null || empresaId.isBlank()) {
            empresaId = extraerString(payload, "empresaId");
        }

        // — Validaciones básicas —
        if (cdc == null || cdc.length() != 44)
            return badRequest("El campo 'cdc' es obligatorio y debe tener exactamente 44 dígitos.");
        if (tipoEvtInt == null)
            return badRequest("El campo 'tipoEvento' es obligatorio.");
        if (empresaId == null || empresaId.isBlank())
            return badRequest("El campo 'empresaId' es obligatorio.");

        TipoEvento tipoEvento = java.util.Arrays.stream(TipoEvento.values())
                .filter(t -> t.getCodigoSifen() == tipoEvtInt)
                .findFirst()
                .orElse(null);

        if (tipoEvento == null || (tipoEvento != TipoEvento.CONFORMIDAD && 
                                   tipoEvento != TipoEvento.DISCONFORMIDAD && 
                                   tipoEvento != TipoEvento.DESCONOCIMIENTO && 
                                   tipoEvento != TipoEvento.NOTIFICACION_RECEPCION)) {
            return badRequest("El tipo de evento " + tipoEvtInt + " no es válido para eventos de receptor.");
        }

        try {
            // 1. Obtener empresa (actuando como receptor) y resolver ambiente
            Empresa empresa = eventoService.obtenerEmpresaPorId(empresaId);
            Ambiente ambiente = resolverAmbiente(empresa);

            // 2. Validar reglas de negocio y crear el registro del evento
            EventoDocumento evento = eventoService.iniciarEventoReceptor(cdc, tipoEvento, motivo, empresa);

            // 3. Generar el ID único para la firma XMLDSig del evento
            // Prefijo RE- para Receptor + UUID recortado
            String idFirma = "RE-" + evento.getId().replace("-", "").substring(0, 16).toUpperCase();

            // 4. Generar el XML del evento de receptor
            String xmlGenerado = eventoXmlGenerator.generarXmlEventoReceptor(
                cdc,
                tipoEvento.getCodigoSifen(),
                motivo,
                empresa.getRuc(),
                empresa.getDv() != null ? empresa.getDv() : "0",
                idFirma
            );
            logger.info("[EventoController.eventoReceptor] XML generado (" + xmlGenerado.length() + " chars).");

            // 5. Firmar el XML con la clave del certificado
            String xmlFirmado = signerService.firmarEventoXml(
                xmlGenerado,
                idFirma,
                empresa.getCertificadoFisico(),
                empresa.getRutaCertificado(),
                empresa.getPasswordCertificado()
            );
            logger.info("[EventoController.eventoReceptor] XML firmado (" + xmlFirmado.length() + " chars).");

            // 6. Validación XSD Rigurosa
            xsdValidatorService.validarXml(xmlFirmado);

            // 7. Persistir XML generado y firmado, actualizar estado a FIRMADO
            eventoService.registrarFirma(evento, xmlGenerado, xmlFirmado, idFirma);

            // 7. Marcar como ENVIADO antes de la transmisión
            eventoService.registrarEnvio(evento);

            // 8. Transmitir al WS siRecepEvento de SIFEN
            boolean aprobado = sifenClient.enviarEvento(evento, ambiente);

            // 9. Persistir la respuesta de SIFEN
            eventoService.registrarRespuesta(
                evento,
                evento.getCodigoSifen(),
                evento.getMensajeSifen(),
                evento.getMensajeUsuario(),
                evento.getXmlRespuestaSifen()
            );

            logger.info("[EventoController.eventoReceptor] Resultado: " + evento.getEstado()
                + " | Código SIFEN: " + evento.getCodigoSifen());

            return ResponseEntity.ok(construirRespuesta(evento));

        } catch (IllegalArgumentException e) {
            logger.warning("[EventoController.eventoReceptor] Validación: " + e.getMessage());
            return badRequest(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "[EventoController.eventoReceptor] Error inesperado: " + e.getMessage(), e);
            return serverError("Error procesando el evento de receptor: " + e.getMessage());
        }
    }

    // =========================================================================
    // Consultas
    // =========================================================================

    /**
     * Lista todos los eventos registrados, ordenados por fecha descendente.
     *
     * @return Lista de eventos con sus datos de estado y respuesta SIFEN.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> listarEventos() {
        String empresaId = EmpresaContext.getEmpresaId();
        if (empresaId == null) return ResponseEntity.status(403).body(Map.of("error", "Empresa no seleccionada"));
        
        List<EventoDocumento> eventos = eventoService.obtenerTodos(empresaId);
        List<Map<String, Object>> respuesta = eventos.stream()
            .map(this::construirRespuesta)
            .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Obtiene los eventos registrados para un CDC específico.
     * Permite verificar si un DTE ya fue cancelado.
     *
     * @param cdc CDC del DTE (44 dígitos).
     * @return Lista de eventos asociados al CDC.
     */
    @GetMapping("/cdc/{cdc}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> obtenerEventosPorCdc(@PathVariable String cdc) {
        if (cdc == null || cdc.length() != 44) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El CDC debe tener exactamente 44 dígitos."));
        }

        List<EventoDocumento> eventos = eventoService.obtenerPorCdc(cdc).stream()
            .filter(e -> e.getEmpresa() != null && e.getEmpresa().getId().equals(EmpresaContext.getEmpresaId()))
            .collect(Collectors.toList());
        boolean cancelado = eventoService.estaCancelado(cdc);

        return ResponseEntity.ok(Map.of(
            "cdc", cdc,
            "cancelado", cancelado,
            "totalEventos", eventos.size(),
            "eventos", eventos.stream().map(this::construirRespuesta).collect(Collectors.toList())
        ));
    }

    /**
     * Obtiene el detalle de un evento por su ID.
     *
     * @param id ID UUID del evento.
     * @return Detalle del evento.
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> obtenerEvento(@PathVariable String id) {
        try {
            EventoDocumento evento = eventoService.obtenerPorId(id);
            if (evento.getEmpresa() == null || !evento.getEmpresa().getId().equals(EmpresaContext.getEmpresaId())) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(construirRespuestaDetalle(evento));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verifica si un DTE está cancelado consultando el historial de eventos.
     *
     * @param cdc CDC del DTE a verificar.
     * @return JSON con el campo {@code cancelado} (boolean).
     */
    @GetMapping("/estado/{cdc}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> verificarEstadoCancelacion(@PathVariable String cdc) {
        boolean cancelado = eventoService.estaCancelado(cdc);
        return ResponseEntity.ok(Map.of(
            "cdc", cdc,
            "cancelado", cancelado,
            "mensaje", cancelado
                ? "El DTE ha sido cancelado exitosamente en SIFEN."
                : "El DTE no tiene una cancelación aprobada en el sistema."
        ));
    }

    // =========================================================================
    // Métodos auxiliares
    // =========================================================================

    /**
     * Construye el mapa de respuesta estándar para un evento.
     * Incluye los datos de auditoría y el resultado de SIFEN.
     */
    private Map<String, Object> construirRespuesta(EventoDocumento evento) {
        return Map.of(
            "eventoId",       evento.getId(),
            "tipoEvento",     evento.getTipoEvento() != null ? evento.getTipoEvento().name() : "",
            "estado",         evento.getEstado() != null ? evento.getEstado().name() : "",
            "codigoSifen",    evento.getCodigoSifen()    != null ? evento.getCodigoSifen()    : "",
            "mensajeSifen",   evento.getMensajeSifen()   != null ? evento.getMensajeSifen()   : "",
            "mensajeUsuario", evento.getMensajeUsuario() != null ? evento.getMensajeUsuario() : "",
            "aprobado",       EstadoEvento.APROBADO == evento.getEstado(),
            "cdcRelacionado", evento.getCdcRelacionado() != null ? evento.getCdcRelacionado() : "",
            "fechaCreacion",  evento.getFechaCreacion()  != null ? evento.getFechaCreacion().toString() : ""
        );
    }

    /**
     * Construye el mapa de respuesta detallado (incluye fechas de envío/respuesta).
     */
    private Map<String, Object> construirRespuestaDetalle(EventoDocumento evento) {
        java.util.Map<String, Object> base = new java.util.LinkedHashMap<>(construirRespuesta(evento));
        base.put("fechaEnvio",     evento.getFechaEnvio()     != null ? evento.getFechaEnvio().toString() : "");
        base.put("fechaRespuesta", evento.getFechaRespuesta() != null ? evento.getFechaRespuesta().toString() : "");
        base.put("tipoEvento",     evento.getTipoEvento()     != null ? evento.getTipoEvento().name() : "");
        // Datos de inutilización (si aplica)
        if (evento.getTipoEvento() == TipoEvento.INUTILIZACION) {
            base.put("timbrado",        evento.getTimbrado());
            base.put("establecimiento", evento.getEstablecimiento());
            base.put("puntoExpedicion", evento.getPuntoExpedicion());
            base.put("rangoDesde",      evento.getRangoDesde());
            base.put("rangoHasta",      evento.getRangoHasta());
        }
        return base;
    }

    /**
     * Resuelve el ambiente de operación desde la configuración de la empresa.
     * Por defecto usa TEST si la empresa no tiene ambiente configurado.
     */
    private Ambiente resolverAmbiente(Empresa empresa) {
        if (empresa.getAmbiente() != null) {
            return empresa.getAmbiente();
        }
        logger.warning("[EventoController] Empresa sin ambiente configurado. Usando TEST por defecto.");
        return Ambiente.TEST;
    }

    // — Helpers de extracción de payload —

    private String extraerString(Map<String, Object> payload, String clave) {
        Object val = payload.get(clave);
        return val != null ? String.valueOf(val).trim() : null;
    }

    private Integer extraerInt(Map<String, Object> payload, String clave) {
        Object val = payload.get(clave);
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); }
        catch (NumberFormatException e) { return null; }
    }

    private Long extraerLong(Map<String, Object> payload, String clave) {
        Object val = payload.get(clave);
        if (val == null) return null;
        try { return Long.parseLong(String.valueOf(val)); }
        catch (NumberFormatException e) { return null; }
    }

    // — Constructores de respuestas de error estándar —

    private ResponseEntity<Map<String, Object>> badRequest(String mensaje) {
        return ResponseEntity.badRequest().body(Map.of(
            "error",   "VALIDACION",
            "message", mensaje
        ));
    }

    private ResponseEntity<Map<String, Object>> serverError(String mensaje) {
        return ResponseEntity.internalServerError().body(Map.of(
            "error",   "ERROR_INTERNO",
            "message", mensaje
        ));
    }
}
