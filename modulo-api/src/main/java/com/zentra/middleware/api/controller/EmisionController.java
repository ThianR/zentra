package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.ItemDocumento;
import com.zentra.middleware.core.model.Cuota;
import com.zentra.middleware.core.model.Transporte;
import com.zentra.middleware.core.service.DocumentoService;
import com.zentra.middleware.core.service.DteValidatorService;
import com.zentra.middleware.xml.DteXmlGenerator;
import com.zentra.middleware.xml.XsdValidatorService;
import com.zentra.middleware.crypto.service.XmlSignerService;
import com.zentra.middleware.sifen.SifenSoapClient;
import com.zentra.middleware.kude.KudeGenerator;
import com.zentra.middleware.core.repository.SifenReferenciaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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

    private final DteXmlGenerator xmlGenerator;
    private final XmlSignerService signerService;
    private final SifenSoapClient sifenClient;
    private final KudeGenerator kudeGenerator;
    private final DocumentoService documentoService;
    private final DteValidatorService validatorService;
    private final SifenReferenciaRepository referenciaRepository;
    private final HttpClient httpClient;
    private final XsdValidatorService xsdValidatorService; // Added field

    public EmisionController(
            DteXmlGenerator xmlGenerator,
            XmlSignerService signerService,
            SifenSoapClient sifenClient,
            KudeGenerator kudeGenerator,
            DocumentoService documentoService,
            DteValidatorService validatorService,
            SifenReferenciaRepository referenciaRepository,
            XsdValidatorService xsdValidatorService) { // Added to constructor parameters
        this.xmlGenerator = xmlGenerator;
        this.signerService = signerService;
        this.sifenClient = sifenClient;
        this.kudeGenerator = kudeGenerator;
        this.documentoService = documentoService;
        this.validatorService = validatorService;
        this.referenciaRepository = referenciaRepository;
        this.xsdValidatorService = xsdValidatorService; // Initialized field
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generarDte(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("Recibida solicitud de generación DTE...");
            
@SuppressWarnings("unchecked")
            Map<String, Object> emiRaw = (Map<String, Object>) payload.get("emisor");
            String rucEmisor = "8394835"; // RUC por defecto del import.sql sincronizado
            if (emiRaw != null && emiRaw.get("ruc") != null) {
                rucEmisor = String.valueOf(emiRaw.get("ruc")).split("-")[0].replace(".", "");
            }
            
            Empresa emisor = documentoService.obtenerEmpresaPorRuc(java.util.Objects.requireNonNull(rucEmisor));
            
            if (emiRaw != null) {
                // Actualizar campos del emisor con los datos enviados desde el frontend si están presentes
                if (emiRaw.get("dv") != null) emisor.setDv(String.valueOf(emiRaw.get("dv")));
                if (emiRaw.get("tipoContribuyente") != null) emisor.setTipoContribuyente(safeInt(emiRaw.get("tipoContribuyente"), emisor.getTipoContribuyente()));
                if (emiRaw.get("razonSocial") != null) emisor.setRazonSocial(String.valueOf(emiRaw.get("razonSocial")));
                if (emiRaw.get("actividadEconomica") != null) emisor.setActividadEconomica(String.valueOf(emiRaw.get("actividadEconomica")));
                if (emiRaw.get("direccion") != null) emisor.setDireccion(String.valueOf(emiRaw.get("direccion")));
                if (emiRaw.get("telefono") != null) emisor.setTelefono(String.valueOf(emiRaw.get("telefono")));
                if (emiRaw.get("email") != null) emisor.setEmail(String.valueOf(emiRaw.get("email")));
                
                if (emiRaw.get("codDepartamento") != null) {
                    int codDepto = safeInt(emiRaw.get("codDepartamento"), emisor.getCodDepartamento());
                    emisor.setCodDepartamento(codDepto);
                    referenciaRepository.findByTipoAndCodigo("DEPARTAMENTO", String.valueOf(codDepto))
                        .ifPresent(ref -> emisor.setDepartamento(ref.getDescripcion()));
                }
                
                if (emiRaw.get("codCiudad") != null) {
                    int codCiud = safeInt(emiRaw.get("codCiudad"), emisor.getCodCiudad());
                    emisor.setCodCiudad(codCiud);
                    // Buscamos la ciudad filtrando por su departamento padre para evitar ambigüedad de códigos (ej: codigo 1 existe en todos los deptos)
                    referenciaRepository.findByTipoAndPadreCodigoAndActivoOrderByOrdenAscDescripcionAsc("CIUDAD", String.valueOf(emisor.getCodDepartamento()), true)
                        .stream()
                        .filter(ref -> ref.getCodigo().equals(String.valueOf(codCiud)))
                        .findFirst()
                        .ifPresent(ref -> emisor.setCiudad(ref.getDescripcion()));
                }
            }

            DocumentoElectronico dte = new DocumentoElectronico();
            dte.setEmisor(emisor);
            dte.setTipoDocumento(String.valueOf(payload.getOrDefault("tipoDocumento", "1")));
            
            // Mapeo detallado de Emisor sincronizado
            dte.setRucEmisor(emisor.getRuc());
            dte.setRazonSocialEmisor(emisor.getRazonSocial());
            dte.setActividadEconomicaEmisor(emisor.getActividadEconomica());
            dte.setDireccionEmisor(emisor.getDireccion());
            dte.setTelefonoEmisor(emisor.getTelefono());

            // Extracción de receptor anidado y datos complementarios
            @SuppressWarnings("unchecked")
            Map<String, Object> recRaw = (Map<String, Object>) payload.get("receptor");
            if (recRaw != null) {
                String rucRaw = String.valueOf(recRaw.getOrDefault("ruc", "Varios"));
                dte.setRucReceptor(rucRaw.split("-")[0].replace(".", ""));
                dte.setReceptorRazonSocial(String.valueOf(recRaw.getOrDefault("razonSocial", "Sin Nombre")));
                dte.setReceptorDireccion(String.valueOf(recRaw.getOrDefault("direccion", "")));
                dte.setReceptorTelefono(String.valueOf(recRaw.getOrDefault("telefono", "")));
                dte.setReceptorEmail(String.valueOf(recRaw.getOrDefault("email", "")));
                dte.setCPaisReceptor(String.valueOf(recRaw.getOrDefault("cPaisReceptor", "PRY")));
            } else {
                String rucRaw = String.valueOf(payload.getOrDefault("rucReceptor", "Varios"));
                dte.setRucReceptor(rucRaw.split("-")[0].replace(".", ""));
                dte.setReceptorRazonSocial(String.valueOf(payload.getOrDefault("razonSocialReceptor", "Sin Nombre")));
                dte.setReceptorDireccion(String.valueOf(payload.getOrDefault("direccionReceptor", "")));
                dte.setReceptorTelefono(String.valueOf(payload.getOrDefault("telefonoReceptor", "")));
                dte.setReceptorEmail(String.valueOf(payload.getOrDefault("emailReceptor", "")));
            }

            dte.setTipoDocumento(String.valueOf(payload.getOrDefault("tipoDocumento", "1")));
            
            String estab = String.valueOf(payload.getOrDefault("establecimiento", "001"));
            String punto = String.valueOf(payload.getOrDefault("puntoExpedicion", "001"));
            String nro = String.valueOf(payload.getOrDefault("numero", "0000001"));
            String fulNum = estab + "-" + punto + "-" + nro;

            String tipoDoc = dte.getTipoDocumento() != null ? dte.getTipoDocumento() : "1";
            // Validación de Duplicados (Permitir re-intentar si falló anteriormente)
            documentoService.eliminarSiEstaRechazado(java.util.Objects.requireNonNull(fulNum), tipoDoc);
            
            if (documentoService.existePorNumero(fulNum, tipoDoc)) {
                return ResponseEntity.status(409).body(Map.of("message", "El documento con número " + fulNum + " ya existe y fue APROBADO anteriormente."));
            }

            dte.setNumeroComprobante(fulNum);
            dte.setTimbrado(String.valueOf(payload.getOrDefault("timbrado", "12345678")));
            
            Object env = payload.get("ambiente");
            dte.setAmbiente(safeInt(env, 1));
            dte.setFormatoKuDE(String.valueOf(payload.getOrDefault("formatoKuDE", "A4")));

            Object cond = payload.get("condicionOperacion");
            dte.setCondicionOperacion(safeInt(cond, 1));

            dte.setTipoOperacion(safeInt(payload.get("iTiOpe"), 1));
            dte.setIndicadorPresencia(safeInt(payload.get("iIndPres"), 1));
            dte.setNaturalezaVendedor(safeInt(payload.get("naturalezaVendedor"), null));
            
            dte.setCdcDocumentoAsociado(String.valueOf(payload.getOrDefault("cdcDocumentoAsociado", "")));
            dte.setMotivoEmision(String.valueOf(payload.getOrDefault("motivoEmision", "")));
            
            // Procesamiento de transporte (si existe)
            if (payload.containsKey("transporte")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tRaw = (Map<String, Object>) payload.get("transporte");
                if (tRaw != null) {
                    Transporte t = new Transporte();
                    t.setNombreChofer(String.valueOf(tRaw.get("nombreChofer")));
                    t.setNumeroDocumentoChofer(String.valueOf(tRaw.get("numeroDocumentoChofer")));
                    t.setMatriculaVehiculo(String.valueOf(tRaw.get("matriculaVehiculo")));
                    Object motTr = tRaw.get("motivoTraslado");
                    t.setMotivoTraslado(motTr != null ? Integer.parseInt(motTr.toString()) : 1);
                    t.setKmsRecorrido(safeInt(tRaw.get("kmsRecorrido"), 10));
                    dte.setTransporte(t);
                }
            }
            
            List<?> itemsRaw = (List<?>) payload.get("items");
            if (itemsRaw != null) {
                List<ItemDocumento> items = new ArrayList<>();
                double totalOpe = 0;
                double grav10 = 0, grav5 = 0, exenta = 0;
                double iva10 = 0, iva5 = 0;

                for (Object itemObj : itemsRaw) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> iRaw = (Map<String, Object>) itemObj;
                    ItemDocumento item = new ItemDocumento();
                    item.setDocumento(dte);
                    item.setCodigo(String.valueOf(iRaw.get("codigo") != null ? iRaw.get("codigo") : ""));
                    item.setDescripcion(String.valueOf(iRaw.get("descripcion") != null ? iRaw.get("descripcion") : "Ítem sin nombre"));
                    item.setCantidad(safeInt(iRaw.get("cantidad"), 1));
                    item.setPrecioUnitario(safeDouble(iRaw.get("precioUnitario"), 0.0));
                    item.setTasaIva(safeDouble(iRaw.get("tasaIva"), 10.0));
                    
                    double subtotal = item.getCantidad() * item.getPrecioUnitario();
                    item.setMontoTotalItem(subtotal);
                    item.setMontoDescuento(0.0);
                    
                    if (item.getTasaIva() == 10.0) {
                        double liqIva = subtotal / 11.0;
                        item.setMontoIvaItem(liqIva);
                        grav10 += subtotal;
                        iva10 += liqIva;
                    } else if (item.getTasaIva() == 5.0) {
                        double liqIva = subtotal / 21.0;
                        item.setMontoIvaItem(liqIva);
                        grav5 += subtotal;
                        iva5 += liqIva;
                    } else {
                        item.setMontoIvaItem(0.0);
                        exenta += subtotal;
                    }
                    
                    items.add(item);
                    totalOpe += subtotal;
                }
                dte.setItems(items);
                dte.setTotalOperacion(totalOpe);
                dte.setTotalGravada10(grav10);
                dte.setTotalGravada5(grav5);
                dte.setTotalExenta(exenta);
                dte.setTotalIva10(iva10);
                dte.setTotalIva5(iva5);
                dte.setTotalIva(iva10 + iva5);
            }

            // Procesamiento de cuotas (si es crédito)
            if (dte.getCondicionOperacion() == 2 && payload.containsKey("cuotas")) {
                List<?> cuotasRaw = (List<?>) payload.get("cuotas");
                if (cuotasRaw != null) {
                    List<Cuota> cuotas = new ArrayList<>();
                    for (Object cuotaObj : cuotasRaw) {
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> cRaw = (Map<String, Object>) cuotaObj;
                            Cuota cuota = new Cuota();
                            cuota.setDocumento(dte);
                            cuota.setNumeroCuota(safeInt(cRaw.get("numero"), 1));
                            cuota.setMonto(safeDouble(cRaw.get("monto"), 0.0));
                            String vStr = String.valueOf(cRaw.get("vencimiento"));
                            if (vStr != null && !vStr.isEmpty() && !"null".equals(vStr)) {
                                cuota.setFechaVencimiento(java.time.LocalDate.parse(vStr));
                            } else {
                                cuota.setFechaVencimiento(java.time.LocalDate.now().plusDays(30));
                            }
                            cuotas.add(cuota);
                        } catch (Exception e) {
                            logger.warning("Error parseando cuota: " + e.getMessage());
                        }
                    }
                    dte.setCuotas(cuotas);
                }
            }

            // --- VALIDACIÓN PREVIA AL GENERADOR XML ---
            DteValidatorService.ResultadoValidacion validacion = validatorService.validar(dte);
            if (!validacion.esValido()) {
                logger.warning("Validación DTE fallida: " + validacion.getMensajeResumen());
                return ResponseEntity.unprocessableEntity().body(
                    Map.of("message", "El documento no cumple con las validaciones SIFEN.",
                           "errores", validacion.getErrores())
                );
            }

            xmlGenerator.generarXml(dte);
            signerService.firmarXml(dte);
            
            // --- VALIDACIÓN XSD LUEGO DE FIRMAR ---
            xsdValidatorService.validarXml(dte.getXmlFirmado());
            
            boolean aprobado = sifenClient.enviarDteSincrono(dte);
            dte.setEstado(aprobado ? EstadoDte.APROBADO : EstadoDte.RECHAZADO);

            documentoService.guardar(dte);
            logger.info("Documento guardado con CDC: " + dte.getCdc());

            // Preparar respuesta con resultado oficial de SIFEN
            Map<String, Object> respuesta = new java.util.HashMap<>();
            respuesta.put("id", dte.getId());
            respuesta.put("cdc", dte.getCdc() != null ? dte.getCdc() : "");
            respuesta.put("estado", dte.getEstado().name());
            respuesta.put("codigoSifen", dte.getCodigoEstadoSifen() != null ? dte.getCodigoEstadoSifen() : "");
            respuesta.put("mensajeSifen", dte.getMensajeSifen() != null ? dte.getMensajeSifen() : "");
            respuesta.put("mensajeUsuario", dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : "");

            if (aprobado) {
                respuesta.put("message", dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : "Documento aprobado por SIFEN");
                return ResponseEntity.ok(respuesta);
            } else {
                // Rechazado por SIFEN (ej: 0401) o falló conexión
                String msg = dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : ("SIFEN devolvió estado: " + dte.getEstado().name() + " (Código: " + respuesta.get("codigoSifen") + ")");
                respuesta.put("message", msg);
                return ResponseEntity.status(422).body(respuesta);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en /generar: " + e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Error interno: " + e.getMessage()));
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/ultimo-error")
    public ResponseEntity<String> getUltimoError() {
        return ResponseEntity.ok(
            documentoService.obtenerTodos().stream()
                .findFirst()
                .map(com.zentra.middleware.core.model.DocumentoElectronico::getXmlRespuestaSifen)
                .orElse("No hay documentos")
        );
    }

    @org.springframework.web.bind.annotation.GetMapping("/ultimo-documento")
    public ResponseEntity<String> getUltimoDocumento() {
        return ResponseEntity.ok(
            documentoService.obtenerTodos().stream()
                .findFirst()
                .map(com.zentra.middleware.core.model.DocumentoElectronico::getXmlFirmado)
                .orElse("No hay documentos firmado")
        );
    }


    @GetMapping("/documentos")
    public ResponseEntity<?> listarDocumentos() {
        try {
            List<Map<String, Object>> list = documentoService.obtenerTodos().stream().map(d -> {
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
                return m;
            }).toList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en /documentos: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    @GetMapping("/kude/{id}")
    public ResponseEntity<byte[]> obtenerKude(@PathVariable String id, @RequestParam(defaultValue = "A4") String formato) {
        try {
            if (id == null) return ResponseEntity.badRequest().build();
            DocumentoElectronico dte = documentoService.obtenerPorId(java.util.Objects.requireNonNull(id));
            byte[] pdf = kudeGenerator.generarKudePdf(dte, formato);
            
            String filename = "kude_" + (dte.getCdc() != null ? dte.getCdc() : dte.getId()) + "_" + formato.toLowerCase() + ".pdf";
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
            if (id == null) return ResponseEntity.badRequest().build();
            DocumentoElectronico dte = documentoService.obtenerPorId(java.util.Objects.requireNonNull(id));
            String xml = dte.getXmlFirmado();
            if (xml == null) xml = dte.getXmlGenerado();
            
            if (xml == null) return ResponseEntity.notFound().build();
            
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
            if (ruc == null || ruc.trim().isEmpty()) return ResponseEntity.badRequest().body("RUC vacío");
            
            if ("80000001".equals(ruc)) {
                return ResponseEntity.ok(Map.of("razonSocial", "EMPRESA DE PRUEBA SIFEN DEMO", "ruc", "80000001", "dv", "7"));
            }

            String url = "https://consultaruc.com.py/api/consulta/" + ruc;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return ResponseEntity.ok(response.body());
                }
            } catch (Exception netEx) {
                logger.warning("No se pudo conectar con el servicio externo de RUC: " + netEx.getMessage());
            }
            
            return ResponseEntity.ok(Map.of("razonSocial", "CLIENTE RECUPERADO MOCK (" + ruc + ")", "ruc", ruc, "dv", "0"));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fatal en consulta RUC: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error interno"));
        }
    }

    private int safeInt(Object val, int def) {
        if (val == null || String.valueOf(val).isEmpty() || "null".equals(String.valueOf(val))) return def;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            String s = String.valueOf(val).trim();
            if (s.contains(".")) return (int) Double.parseDouble(s);
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private Integer safeInt(Object val, Integer def) {
        if (val == null || String.valueOf(val).isEmpty() || "null".equals(String.valueOf(val))) return def;
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            String s = String.valueOf(val).trim();
            if (s.contains(".")) return (int) Double.parseDouble(s);
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private double safeDouble(Object val, double def) {
        if (val == null || String.valueOf(val).isEmpty()) return def;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            String s = String.valueOf(val).trim();
            // Si tiene coma, asumimos formato manual (ej: 1.000,50)
            if (s.contains(",")) {
                s = s.replace(".", "").replace(",", ".");
            }
            return Double.parseDouble(s);
        } catch (Exception e) {
            return def;
        }
    }
}
