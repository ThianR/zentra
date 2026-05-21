package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.EventoDocumento;
import com.zentra.middleware.core.service.DocumentoService;
import com.zentra.middleware.kude.KudeGenerator;
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.api.security.EmpresaContext;
import com.zentra.middleware.core.service.HistorialSifenService;
import com.zentra.middleware.core.service.EventoService;
import com.zentra.middleware.api.service.DteEmisionService;
import com.zentra.middleware.api.service.DteValidationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/emision")
public class EmisionController {

    private static final Logger logger = Logger.getLogger(EmisionController.class.getName());

    private final DteEmisionService dteEmisionService;
    private final KudeGenerator kudeGenerator;
    private final DocumentoService documentoService;
    private final EmpresaRepository empresaRepository;
    private final DocumentoElectronicoRepository dteRepository;
    private final HistorialSifenService historialSifenService;
    private final EventoService eventoService;

    public EmisionController(
            DteEmisionService dteEmisionService,
            KudeGenerator kudeGenerator,
            DocumentoService documentoService,
            EmpresaRepository empresaRepository,
            DocumentoElectronicoRepository dteRepository,
            HistorialSifenService historialSifenService,
            EventoService eventoService) {
        this.dteEmisionService = dteEmisionService;
        this.kudeGenerator = kudeGenerator;
        this.documentoService = documentoService;
        this.empresaRepository = empresaRepository;
        this.dteRepository = dteRepository;
        this.historialSifenService = historialSifenService;
        this.eventoService = eventoService;
    }

    public DocumentoElectronico procesarDteLocal(Map<String, Object> payload) throws Exception {
        return dteEmisionService.procesarDteLocal(payload);
    }

    @PostMapping("/generar")
    public ResponseEntity<Map<String, Object>> generarDte(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("Recibida solicitud de generación DTE unitaria...");
            Map<String, Object> respuesta = dteEmisionService.generarDte(payload);
            boolean exitoEnvio = (boolean) respuesta.getOrDefault("exitoEnvio", false);

            if (exitoEnvio) {
                String msg = respuesta.get("mensajeUsuario") != null ? (String) respuesta.get("mensajeUsuario") : "Operación exitosa con SIFEN";
                respuesta.put("message", msg);
                return ResponseEntity.ok(respuesta);
            } else {
                String estado = (String) respuesta.getOrDefault("estado", "RECHAZADO");
                String msg = respuesta.get("mensajeUsuario") != null ? (String) respuesta.get("mensajeUsuario") : ("SIFEN devolvió estado: " + estado);
                respuesta.put("message", msg);
                return ResponseEntity.status(422).body(respuesta);
            }
        } catch (DteValidationException e) {
            logger.warning("Validación DTE fallida: " + e.getMessage());
            return ResponseEntity.unprocessableEntity().body(
                    Map.of("message", "El documento no cumple con las validaciones SIFEN.", "errores", e.getErrores()));
        } catch (RuntimeException e) {
            String mensajeUsuario = e.getMessage();
            if (mensajeUsuario == null || mensajeUsuario.isBlank()) {
                mensajeUsuario = "Ocurrió un error al procesar el documento. Por favor verifique los datos.";
            }
            logger.warning("Error controlado en emisión DTE: " + mensajeUsuario);
            return ResponseEntity.status(422).body(Map.of("message", mensajeUsuario));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error interno inesperado en emisión DTE", e);
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Ocurrió un error interno en el sistema. Por favor contacte al soporte técnico."));
        }
    }

    @PostMapping("/masivo")
    public ResponseEntity<Map<String, Object>> generarMasivo(@RequestBody List<Map<String, Object>> payloadList) {
        logger.info("Recibida solicitud de emisión MASIVA. Total documentos: " + payloadList.size());
        Map<String, Object> response = dteEmisionService.generarMasivo(payloadList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/consultar-lote/{id}")
    public ResponseEntity<?> consultarLote(@PathVariable String id) {
        try {
            logger.info("Recibida solicitud de consulta de Lote para DTE con ID: " + id);
            Map<String, Object> respuesta = dteEmisionService.consultarLote(id);
            boolean exito = (boolean) respuesta.getOrDefault("exito", false);
            if (exito) {
                return ResponseEntity.ok(respuesta);
            } else {
                return ResponseEntity.status(422).body(respuesta);
            }
        } catch (java.util.NoSuchElementException e) {
            logger.warning("No se encontró el DTE con ID: " + id);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.warning("Error de argumento al consultar lote: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error consultando lote", e);
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error interno al consultar lote: " + e.getMessage()));
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/ultimo-error")
    public ResponseEntity<String> getUltimoError() {
        return ResponseEntity.ok(
                documentoService.obtenerTodos().stream()
                        .findFirst()
                        .map(com.zentra.middleware.core.model.DocumentoElectronico::getXmlRespuestaSifen)
                        .orElse("No hay documentos"));
    }

    @org.springframework.web.bind.annotation.GetMapping("/ultimo-documento")
    public ResponseEntity<String> getUltimoDocumento() {
        return ResponseEntity.ok(
                documentoService.obtenerTodos().stream()
                        .findFirst()
                        .map(com.zentra.middleware.core.model.DocumentoElectronico::getXmlFirmado)
                        .orElse("No hay documentos firmado"));
    }

    @GetMapping("/documentos")
    public ResponseEntity<?> listarDocumentos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String ambiente) {
        try {
            String empresaId = EmpresaContext.getEmpresaId();
            if (empresaId == null) {
                return ResponseEntity.status(403).body(Map.of("error", "Debe seleccionar una empresa"));
            }

            // 1. Construir filtros dinámicos mediante JPA Specification
            Specification<DocumentoElectronico> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // Filtrar siempre por la empresa actual
                predicates.add(cb.equal(root.get("emisor").get("id"), empresaId));

                // Filtro de Ambiente (TEST / PRODUCCION)
                if (ambiente != null && !ambiente.isBlank()) {
                    predicates.add(cb.equal(root.get("ambiente"), com.zentra.middleware.core.enums.Ambiente.valueOf(ambiente)));
                }

                // Filtro por Estado
                if (estado != null && !estado.isBlank() && !estado.equalsIgnoreCase("TODOS")) {
                    predicates.add(cb.equal(root.get("estado"), com.zentra.middleware.core.model.EstadoDte.valueOf(estado)));
                }

                // Filtro por Tipo de DTE
                if (tipo != null && !tipo.isBlank() && !tipo.equalsIgnoreCase("TODOS")) {
                    predicates.add(cb.equal(root.get("tipoDocumento"), tipo));
                }

                // Rango de Fechas (Desde / Hasta)
                if (desde != null && !desde.isBlank()) {
                    LocalDateTime desdeLdt = LocalDate.parse(desde).atStartOfDay();
                    predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCreacion"), desdeLdt));
                }
                if (hasta != null && !hasta.isBlank()) {
                    LocalDateTime hastaLdt = LocalDate.parse(hasta).atTime(23, 59, 59);
                    predicates.add(cb.lessThanOrEqualTo(root.get("fechaCreacion"), hastaLdt));
                }

                // Búsqueda Global (CDC, Número de Comprobante, o Razón Social del Receptor)
                if (search != null && !search.isBlank()) {
                    String searchLike = "%" + search.toLowerCase() + "%";
                    Predicate cdcPred = cb.like(cb.lower(root.get("cdc")), searchLike);
                    Predicate nroPred = cb.like(cb.lower(root.get("numeroComprobante")), searchLike);
                    Predicate razonPred = cb.like(cb.lower(root.get("receptorRazonSocial")), searchLike);
                    Predicate rucPred = cb.like(cb.lower(root.get("rucReceptor")), searchLike);
                    
                    predicates.add(cb.or(cdcPred, nroPred, razonPred, rucPred));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            };

            // 2. Definir ordenamiento por fechaCreacion descendente por defecto
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());

            // 3. Consultar la página a la base de datos
            Page<DocumentoElectronico> paginaDocs = dteRepository.findAll(spec, pageable);

            // 4. Mapear los resultados a un formato liviano JSON compatible con el Frontend
            List<Map<String, Object>> contentList = paginaDocs.getContent().stream().map(d -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", d.getId());
                m.put("tipoDocumento", d.getTipoDocumento());
                m.put("numeroComprobante", d.getNumeroComprobante() != null ? d.getNumeroComprobante() : "S/N");
                m.put("rucReceptor", d.getRucReceptor() != null ? d.getRucReceptor() : "Varios");
                m.put("receptorRazonSocial", d.getReceptorRazonSocial() != null ? d.getReceptorRazonSocial() : "Varios");
                m.put("totalOperacion", d.getTotalOperacion() != null ? d.getTotalOperacion() : 0.0);
                m.put("estado", d.getEstado() != null ? d.getEstado().name() : "CREADO");
                m.put("fechaCreacion", d.getFechaCreacion() != null ? d.getFechaCreacion().toString() : "");
                m.put("cdc", d.getCdc() != null ? d.getCdc() : "");
                m.put("numeroTicketLote", d.getNumeroTicketLote() != null ? d.getNumeroTicketLote() : "");
                m.put("ambiente", d.getAmbiente() != null ? d.getAmbiente().name() : "TEST");

                String qrUrl = "";
                if (d.getXmlFirmado() != null && d.getXmlFirmado().contains("<dCarQR>")) {
                    qrUrl = d.getXmlFirmado().substring(d.getXmlFirmado().indexOf("<dCarQR>") + 8,
                            d.getXmlFirmado().indexOf("</dCarQR>")).replace("&amp;", "&");
                }
                m.put("qrUrl", qrUrl);

                return m;
            }).toList();

            // 5. Construir y retornar el objeto de respuesta paginado estructurado
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("content", contentList);
            respuesta.put("totalPages", paginaDocs.getTotalPages());
            respuesta.put("totalElements", paginaDocs.getTotalElements());
            respuesta.put("number", paginaDocs.getNumber()); // Página actual
            respuesta.put("size", paginaDocs.getSize());     // Filas por página
            respuesta.put("first", paginaDocs.isFirst());
            respuesta.put("last", paginaDocs.isLast());

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en /documentos: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al procesar los documentos"));
        }
    }

    @GetMapping("/documentos/{id}")
    public ResponseEntity<?> obtenerDocumento(@PathVariable String id) {
        try {
            String nonNullId = java.util.Objects.requireNonNull(id);
            DocumentoElectronico d = documentoService.obtenerPorId(nonNullId);
            if (d == null) {
                return ResponseEntity.notFound().build();
            }
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("tipoDocumento", d.getTipoDocumento());
            m.put("numeroComprobante", d.getNumeroComprobante() != null ? d.getNumeroComprobante() : "S/N");
            m.put("rucReceptor", d.getRucReceptor() != null ? d.getRucReceptor() : "");
            m.put("receptorRazonSocial", d.getReceptorRazonSocial() != null ? d.getReceptorRazonSocial() : "");
            m.put("totalOperacion", d.getTotalOperacion() != null ? d.getTotalOperacion() : 0.0);
            m.put("estado", d.getEstado() != null ? d.getEstado().name() : "CREADO");
            m.put("fechaCreacion", d.getFechaCreacion() != null ? d.getFechaCreacion().toString() : "");
            m.put("cdc", d.getCdc() != null ? d.getCdc() : "");
            m.put("numeroTicketLote", d.getNumeroTicketLote() != null ? d.getNumeroTicketLote() : "");
            m.put("codigoEstadoSifen", d.getCodigoEstadoSifen() != null ? d.getCodigoEstadoSifen() : "");
            m.put("mensajeSifen", d.getMensajeSifen() != null ? d.getMensajeSifen() : "");
            m.put("mensajeUsuario", d.getMensajeUsuario() != null ? d.getMensajeUsuario() : "");
            m.put("xmlRespuestaSifen", d.getXmlRespuestaSifen() != null ? d.getXmlRespuestaSifen() : "");
            return ResponseEntity.ok(m);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo documento " + id, e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/consultar-referencia/{cdc}")
    public ResponseEntity<?> consultarReferencia(@PathVariable String cdc) {
        try {
            logger.info("Consultando documento de referencia con CDC: " + cdc);
            return documentoService.obtenerPorCdc(cdc)
                .map(d -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("cdc", d.getCdc());
                    res.put("tipoDocumento", d.getTipoDocumento());
                    
                    // Receptor
                    Map<String, Object> receptor = new HashMap<>();
                    receptor.put("ruc", d.getRucReceptor());
                    receptor.put("razonSocial", d.getReceptorRazonSocial());
                    receptor.put("direccion", d.getReceptorDireccion());
                    receptor.put("telefono", d.getReceptorTelefono());
                    receptor.put("email", d.getReceptorEmail());
                    receptor.put("tipoReceptor", d.getTipoReceptor());
                    res.put("receptor", receptor);
                    
                    // Items
                    List<Map<String, Object>> items = d.getItems().stream().map(i -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("codigo", i.getCodigo());
                        item.put("descripcion", i.getDescripcion());
                        item.put("cantidad", i.getCantidad());
                        item.put("precioUnitario", i.getPrecioUnitario());
                        item.put("tasaIva", i.getTasaIva());
                        item.put("unidadMedida", i.getUnidadMedida());
                        return item;
                    }).toList();
                    res.put("items", items);
                    
                    return ResponseEntity.ok(res);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error consultando referencia " + cdc, e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/documentos/{id}/historial")
    public ResponseEntity<?> obtenerHistorial(@PathVariable String id) {
        try {
            String nonNullId = java.util.Objects.requireNonNull(id);
            DocumentoElectronico d = documentoService.obtenerPorId(nonNullId);
            if (d == null) {
                return ResponseEntity.notFound().build();
            }
            // Verificar acceso por empresa
            String empresaId = EmpresaContext.getEmpresaId();
            if (empresaId == null || d.getEmisor() == null || !d.getEmisor().getId().equals(empresaId)) {
                return ResponseEntity.status(403).body(Map.of("error", "No tiene acceso a este documento"));
            }
            var historial = historialSifenService.obtenerHistorial(id);
            List<Map<String, Object>> result = new ArrayList<>();
            
            // 1. Agregar historial de transmisión de documentos
            historial.forEach(h -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", h.getId());
                m.put("fechaRegistro", h.getFechaRegistro() != null ? h.getFechaRegistro().toString() : "");
                m.put("operacion", h.getOperacion());
                m.put("codigoEstado", h.getCodigoEstado() != null ? h.getCodigoEstado() : "");
                m.put("mensajeRespuesta", h.getMensajeRespuesta() != null ? h.getMensajeRespuesta() : "");
                m.put("xmlRespuesta", h.getXmlRespuesta() != null ? h.getXmlRespuesta() : "");
                result.add(m);
            });

            // 2. Agregar eventos de SIFEN asociados (Cancelaciones, Conformidades, etc.)
            if (d.getCdc() != null && !d.getCdc().isBlank()) {
                List<EventoDocumento> eventos = eventoService.obtenerPorCdc(d.getCdc());
                eventos.forEach(ev -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", ev.getId());
                    m.put("fechaRegistro", ev.getFechaRespuesta() != null ? ev.getFechaRespuesta().toString() : 
                                         (ev.getFechaCreacion() != null ? ev.getFechaCreacion().toString() : ""));
                    m.put("operacion", "EVENTO_" + ev.getTipoEvento().name());
                    m.put("codigoEstado", ev.getCodigoSifen() != null ? ev.getCodigoSifen() : "");
                    m.put("mensajeRespuesta", ev.getMensajeSifen() != null ? ev.getMensajeSifen() : "");
                    m.put("xmlRespuesta", ev.getXmlRespuestaSifen() != null ? ev.getXmlRespuestaSifen() : "");
                    result.add(m);
                });
            }

            // Ordenar por fecha descendente
            result.sort((a, b) -> String.valueOf(b.get("fechaRegistro")).compareTo(String.valueOf(a.get("fechaRegistro"))));
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo historial del documento " + id, e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/empresas")
    public ResponseEntity<?> listarEmpresas() {
        try {
            String clienteId = EmpresaContext.getClienteId();
            if (clienteId == null) {
                return ResponseEntity.status(401).body("No autorizado");
            }
            List<Map<String, Object>> list = empresaRepository.findByClienteId(clienteId).stream().map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", e.getId());
                m.put("ruc", e.getRuc());
                m.put("dv", e.getDv());
                m.put("razonSocial", e.getRazonSocial());
                m.put("codActividadEconomica", e.getCodActividadEconomica());
                m.put("actividadEconomica", e.getActividadEconomica());
                m.put("direccion", e.getDireccion());
                m.put("telefono", e.getTelefono());
                m.put("email", e.getEmail());
                m.put("codDepartamento", e.getCodDepartamento());
                m.put("codCiudad", e.getCodCiudad());
                m.put("puntoExpedicion", e.getPuntoExpedicion());
                m.put("codEstablecimiento", e.getCodEstablecimiento());
                m.put("timbrado", e.getTimbrado());
                m.put("fechaInicioTimbrado",
                        e.getFechaInicioTimbrado() != null ? e.getFechaInicioTimbrado().toString() : null);
                m.put("fechaVencimientoTimbrado",
                        e.getFechaVencimientoTimbrado() != null ? e.getFechaVencimientoTimbrado().toString() : null);
                m.put("tipoContribuyente", e.getTipoContribuyente());
                m.put("ambiente", e.getAmbiente() != null ? e.getAmbiente().name() : null);
                m.put("hasCertificado", e.getCertificadoFisico() != null);
                m.put("fechaVencimientoCertificado",
                        e.getFechaVencimientoCertificado() != null ? e.getFechaVencimientoCertificado().toString()
                                : null);
                m.put("idCsc", e.getIdCsc());
                m.put("valorCsc", e.getValorCsc());
                return m;
            }).toList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en /empresas: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    @GetMapping("/kude/{id}")
    public ResponseEntity<byte[]> obtenerKude(@PathVariable String id,
            @RequestParam(defaultValue = "A4") String formato) {
        try {
            if (id == null)
                return ResponseEntity.badRequest().build();
            DocumentoElectronico dte = documentoService.obtenerPorId(java.util.Objects.requireNonNull(id));
            byte[] pdf = kudeGenerator.generarKudePdf(dte, formato);

            String filename = "kude_" + (dte.getCdc() != null ? dte.getCdc() : dte.getId()) + "_"
                    + formato.toLowerCase() + ".pdf";
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(pdf);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en /kude: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/xml/{id}")
    public ResponseEntity<byte[]> descargarXml(@PathVariable String id) {
        try {
            if (id == null)
                return ResponseEntity.badRequest().build();
            DocumentoElectronico dte = documentoService.obtenerPorId(java.util.Objects.requireNonNull(id));
            String xml = dte.getXmlFirmado();
            if (xml == null)
                xml = dte.getXmlGenerado();

            if (xml == null)
                return ResponseEntity.notFound().build();

            byte[] bytes = xml.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String filename = "dte_" + (dte.getCdc() != null ? dte.getCdc() : dte.getId()) + ".xml";

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(bytes);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error descargando XML: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/consultar-ruc")
    public ResponseEntity<?> consultarRuc(@RequestParam String ruc) {
        try {
            if (ruc == null || ruc.trim().isEmpty())
                return ResponseEntity.badRequest().body("RUC vacío");

            Map<String, Object> resultado = dteEmisionService.consultarRuc(ruc);
            if (resultado.containsKey("rawJson")) {
                return ResponseEntity.ok(resultado.get("rawJson"));
            }
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fatal en consulta RUC: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error interno"));
        }
    }

}
