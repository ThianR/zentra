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
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.api.security.EmpresaContext;
import com.zentra.middleware.core.service.HistorialSifenService;
import com.zentra.middleware.core.service.EventoService;
import com.zentra.middleware.core.model.EventoDocumento;

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
    private final XsdValidatorService xsdValidatorService;
    private final EmpresaRepository empresaRepository;
    private final DocumentoElectronicoRepository dteRepository;
    private final HistorialSifenService historialSifenService;
    private final EventoService eventoService;
    private final com.zentra.middleware.core.repository.PadronRucRepository padronRucRepository;

    public EmisionController(
            DteXmlGenerator xmlGenerator,
            XmlSignerService signerService,
            SifenSoapClient sifenClient,
            KudeGenerator kudeGenerator,
            DocumentoService documentoService,
            DteValidatorService validatorService,
            SifenReferenciaRepository referenciaRepository,
            XsdValidatorService xsdValidatorService,
            EmpresaRepository empresaRepository,
            DocumentoElectronicoRepository dteRepository,
            HistorialSifenService historialSifenService,
            EventoService eventoService,
            com.zentra.middleware.core.repository.PadronRucRepository padronRucRepository) { // Added to constructor
                                                                                             // parameters
        this.xmlGenerator = xmlGenerator;
        this.signerService = signerService;
        this.sifenClient = sifenClient;
        this.kudeGenerator = kudeGenerator;
        this.documentoService = documentoService;
        this.validatorService = validatorService;
        this.referenciaRepository = referenciaRepository;
        this.xsdValidatorService = xsdValidatorService; // Initialized field
        this.empresaRepository = empresaRepository;
        this.dteRepository = dteRepository;
        this.historialSifenService = historialSifenService;
        this.eventoService = eventoService;
        this.padronRucRepository = padronRucRepository;

        HttpClient client;
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] {
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .sslContext(sc)
                    .build();
        } catch (Exception e) {
            client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        }
        this.httpClient = client;
    }

    public static class DteValidationException extends RuntimeException {
        private final java.util.List<String> errores;

        public DteValidationException(String message, java.util.List<String> errores) {
            super(message);
            this.errores = errores;
        }

        public java.util.List<String> getErrores() {
            return errores;
        }
    }

    private int safeInt(Object o, Integer defaultVal) {
        if (o == null || o.toString().trim().isEmpty() || "null".equals(o.toString().trim()))
            return defaultVal != null ? defaultVal : 0;
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return defaultVal != null ? defaultVal : 0;
        }
    }

    private double safeDouble(Object o, Double defaultVal) {
        if (o == null || o.toString().trim().isEmpty() || "null".equals(o.toString().trim()))
            return defaultVal != null ? defaultVal : 0.0;
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return defaultVal != null ? defaultVal : 0.0;
        }
    }

    public DocumentoElectronico procesarDteLocal(Map<String, Object> payload) throws Exception {
        String empresaId = EmpresaContext.getEmpresaId();
        if (empresaId == null) {
            throw new RuntimeException("Debe seleccionar una empresa activa para emitir.");
        }

        Empresa emisor = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa activa no encontrada."));

        @SuppressWarnings("unchecked")
        Map<String, Object> emiRaw = (Map<String, Object>) payload.get("emisor");

        if (emiRaw != null) {
            // Actualizar campos del emisor con los datos enviados desde el frontend si
            // están presentes
            if (emiRaw.get("dv") != null)
                emisor.setDv(String.valueOf(emiRaw.get("dv")));
            if (emiRaw.get("tipoContribuyente") != null)
                emisor.setTipoContribuyente(
                        safeInt(emiRaw.get("tipoContribuyente"), emisor.getTipoContribuyente()));
            if (emiRaw.get("razonSocial") != null)
                emisor.setRazonSocial(String.valueOf(emiRaw.get("razonSocial")));
            if (emiRaw.get("actividadEconomica") != null)
                emisor.setActividadEconomica(String.valueOf(emiRaw.get("actividadEconomica")));
            if (emiRaw.get("direccion") != null)
                emisor.setDireccion(String.valueOf(emiRaw.get("direccion")));
            if (emiRaw.get("telefono") != null)
                emisor.setTelefono(String.valueOf(emiRaw.get("telefono")));
            if (emiRaw.get("email") != null)
                emisor.setEmail(String.valueOf(emiRaw.get("email")));
            if (emiRaw.get("codEstablecimiento") != null)
                emisor.setCodEstablecimiento(String.valueOf(emiRaw.get("codEstablecimiento")));
            if (emiRaw.get("puntoExpedicion") != null)
                emisor.setPuntoExpedicion(String.valueOf(emiRaw.get("puntoExpedicion")));

            if (emiRaw.get("codDepartamento") != null) {
                int codDepto = safeInt(emiRaw.get("codDepartamento"), emisor.getCodDepartamento());
                emisor.setCodDepartamento(codDepto);
                referenciaRepository.findByTipoAndCodigo("DEPARTAMENTO", String.valueOf(codDepto))
                        .ifPresent(ref -> emisor.setDepartamento(ref.getDescripcion()));
            }

            if (emiRaw.get("codCiudad") != null) {
                int codCiud = safeInt(emiRaw.get("codCiudad"), emisor.getCodCiudad());
                emisor.setCodCiudad(codCiud);
                // Buscamos la ciudad filtrando por su departamento padre para evitar ambigüedad
                // de códigos (ej: codigo 1 existe en todos los deptos)
                referenciaRepository
                        .findByTipoAndPadreCodigoAndActivoOrderByOrdenAscDescripcionAsc("CIUDAD",
                                String.valueOf(emisor.getCodDepartamento()), true)
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
        String rucParaGuardar = "Varios";
        if (recRaw != null) {
            rucParaGuardar = String.valueOf(recRaw.getOrDefault("ruc", "Varios"));
            dte.setReceptorRazonSocial(String.valueOf(recRaw.getOrDefault("razonSocial", "Sin Nombre")));
            dte.setReceptorDireccion(String.valueOf(recRaw.getOrDefault("direccion", "")));
            dte.setReceptorTelefono(String.valueOf(recRaw.getOrDefault("telefono", "")));
            dte.setReceptorEmail(String.valueOf(recRaw.getOrDefault("email", "")));
            dte.setCPaisReceptor(String.valueOf(recRaw.getOrDefault("cPaisReceptor", "PRY")));
        } else {
            rucParaGuardar = String.valueOf(payload.getOrDefault("rucReceptor", "Varios"));
            dte.setReceptorRazonSocial(String.valueOf(payload.getOrDefault("razonSocialReceptor", "Sin Nombre")));
            dte.setReceptorDireccion(String.valueOf(payload.getOrDefault("direccionReceptor", "")));
            dte.setReceptorTelefono(String.valueOf(payload.getOrDefault("telefonoReceptor", "")));
            dte.setReceptorEmail(String.valueOf(payload.getOrDefault("emailReceptor", "")));
        }

        rucParaGuardar = rucParaGuardar.replace(".", "").trim();

        // Leer tipoReceptor enviado por el frontend (1=Contribuyente, 2=No Contribuyente)
        Integer tipoRecFrontend = null;
        if (recRaw != null && recRaw.get("tipoReceptor") != null) {
            tipoRecFrontend = safeInt(recRaw.get("tipoReceptor"), null);
        }
        // iTiOpe: 1=B2B (Contribuyente a Contribuyente), 2=B2C (Contribuyente a No Contribuyente)
        int iTiOpe = safeInt(payload.get("iTiOpe"), 1);

        String rucSolo = rucParaGuardar.contains("-") ? rucParaGuardar.split("-")[0].trim() : rucParaGuardar;
        boolean esNumerico = rucSolo.matches("\\d+") && !rucSolo.equals("0");

        if (iTiOpe == 2 || (tipoRecFrontend != null && tipoRecFrontend == 2)) {
            // B2C o marcado como No Contribuyente: siempre enviar como Cédula paraguaya
            rucParaGuardar = rucSolo;
            dte.setTipoReceptor(2);
            logger.info("Receptor tratado como No Contribuyente (iTiOpe=" + iTiOpe + ", tipoRecFrontend=" + tipoRecFrontend + ")");
        } else if (esNumerico) {
            // B2B: validar contra padrón para confirmar contribuyente válido
            boolean existeEnPadron = padronRucRepository.existsById(rucSolo);
            logger.info("Validación padrón RUC (B2B): " + rucSolo + " -> " + (existeEnPadron ? "EXISTE" : "NO EXISTE"));
            if (existeEnPadron) {
                dte.setTipoReceptor(1);
                if (!rucParaGuardar.contains("-")) {
                    String dvPadron = padronRucRepository.findById(rucSolo)
                            .map(p -> p.getDv()).orElse("0");
                    rucParaGuardar = rucSolo + "-" + dvPadron;
                }
            } else {
                // No existe en padrón → forzar como No Contribuyente
                rucParaGuardar = rucSolo;
                dte.setTipoReceptor(2);
            }
        }

        dte.setRucReceptor(rucParaGuardar);

        dte.setTipoDocumento(String.valueOf(payload.getOrDefault("tipoDocumento", "1")));

        String estab = String.valueOf(payload.getOrDefault("establecimiento", "001"));
        String punto = String.valueOf(payload.getOrDefault("puntoExpedicion", "001"));
        String nro = String.valueOf(payload.getOrDefault("numero", "0000001"));
        String fulNum = estab + "-" + punto + "-" + nro;

        String tipoDoc = dte.getTipoDocumento() != null ? dte.getTipoDocumento() : "1";
        // Validación de Duplicados (Permitir re-intentar si falló anteriormente)
        String nonNullNum = java.util.Objects.requireNonNull(fulNum);
        String nonNullTipo = java.util.Objects.requireNonNull(tipoDoc);
        documentoService.eliminarSiEstaRechazado(nonNullNum, nonNullTipo);

        if (documentoService.existePorNumero(nonNullNum, nonNullTipo)) {
            throw new RuntimeException(
                    "El documento con número " + fulNum + " ya existe y fue APROBADO anteriormente.");
        }

        dte.setNumeroComprobante(fulNum);
        dte.setTimbrado(emisor.getTimbrado() != null ? emisor.getTimbrado() : "16770994");

        // Tomar el ambiente del UI (si se envía), sino de la empresa emisora
        if (payload.get("ambiente") != null) {
            dte.setAmbiente(com.zentra.middleware.core.enums.Ambiente.valueOf(String.valueOf(payload.get("ambiente"))));
        } else {
            dte.setAmbiente(emisor.getAmbiente());
        }
        dte.setFormatoKuDE(String.valueOf(payload.getOrDefault("formatoKuDE", "A4")));

        if (payload.get("fechaCreacion") != null) {
            try {
                dte.setFechaCreacion(java.time.LocalDateTime.parse(String.valueOf(payload.get("fechaCreacion"))));
            } catch (Exception e) {
                logger.warning("Error parseando fechaCreacion: " + e.getMessage());
            }
        }

        Object cond = payload.get("condicionOperacion");
        dte.setCondicionOperacion(safeInt(cond, 1));

        dte.setTipoOperacion(safeInt(payload.get("iTiOpe"), 1));
        dte.setIndicadorPresencia(safeInt(payload.get("iIndPres"), 1));
        dte.setNaturalezaVendedor(safeInt(payload.get("naturalezaVendedor"), null));
        dte.setTipoEmision(safeInt(payload.get("tipoEmision"), 1)); // 1=Normal, 2=Asíncrono/Contingencia

        dte.setCdcDocumentoAsociado(String.valueOf(payload.getOrDefault("cdcDocumentoAsociado", "")));
        dte.setMotivoEmision(String.valueOf(payload.getOrDefault("motivoEmision", "")));

        // Procesamiento de transporte (si existe y tiene datos mínimos)
        if (payload.containsKey("transporte")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tRaw = (Map<String, Object>) payload.get("transporte");
            if (tRaw != null) {
                String nomChof = String.valueOf(tRaw.get("nombreChofer")).trim();
                String docChof = String.valueOf(tRaw.get("numeroDocumentoChofer")).trim();
                String patent = String.valueOf(tRaw.get("matriculaVehiculo")).trim();

                // Solo crear transporte si al menos hay un nombre o documento de chofer o
                // patente
                if (!nomChof.isEmpty() && !"null".equals(nomChof) ||
                        !docChof.isEmpty() && !"null".equals(docChof) ||
                        !patent.isEmpty() && !"null".equals(patent)) {

                    Transporte t = new Transporte();
                    t.setNombreChofer(nomChof);
                    t.setNumeroDocumentoChofer(docChof);
                    t.setMatriculaVehiculo(patent);
                    Object motTr = tRaw.get("motivoTraslado");
                    t.setMotivoTraslado(motTr != null ? Integer.parseInt(motTr.toString()) : 1);
                    t.setKmsRecorrido(safeInt(tRaw.get("kmsRecorrido"), 10));
                    dte.setTransporte(t);
                }
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
                item.setDescripcion(String
                        .valueOf(iRaw.get("descripcion") != null ? iRaw.get("descripcion") : "Ítem sin nombre"));
                item.setCantidad(safeInt(iRaw.get("cantidad"), 1));
                item.setPrecioUnitario(safeDouble(iRaw.get("precioUnitario"), 0.0));
                item.setTasaIva(safeDouble(iRaw.get("tasaIva"), 10.0));

                double subtotal = item.getCantidad() * item.getPrecioUnitario();
                item.setMontoTotalItem(subtotal);
                item.setMontoDescuento(0.0);

                if (item.getTasaIva() == 10.0) {
                    // Base neta: monto / 1.1 (consistente con generador XML)
                    long baseNeta = Math.round(subtotal / 1.1);
                    long liqIva = Math.round(subtotal) - baseNeta;
                    item.setMontoIvaItem((double) liqIva);
                    item.setMontoTotalItem((double) Math.round(subtotal));
                    grav10 += item.getMontoTotalItem();
                    iva10 += liqIva;
                } else if (item.getTasaIva() == 5.0) {
                    // Base neta: monto / 1.05 (consistente con generador XML)
                    long baseNeta = Math.round(subtotal / 1.05);
                    long liqIva = Math.round(subtotal) - baseNeta;
                    item.setMontoIvaItem((double) liqIva);
                    item.setMontoTotalItem((double) Math.round(subtotal));
                    grav5 += item.getMontoTotalItem();
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
            throw new DteValidationException("Validación fallida: " + validacion.getMensajeResumen(),
                    validacion.getErrores());
        }

        xmlGenerator.generarXml(dte);
        signerService.firmarXml(dte);

        // --- VALIDACIÓN XSD LUEGO DE FIRMAR ---
        xsdValidatorService.validarXml(dte.getXmlFirmado());

        // Forzar estado para emisión asíncrona por defecto o FIRMADO inicial
        dte.setEstado(EstadoDte.FIRMADO);
        documentoService.guardar(dte);
        return dte;
    }

    @PostMapping("/generar")
    public ResponseEntity<Map<String, Object>> generarDte(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("Recibida solicitud de generación DTE unitaria...");
            DocumentoElectronico dte = java.util.Objects.requireNonNull(procesarDteLocal(payload));

            String tipoEnvio = String.valueOf(payload.getOrDefault("tipoEnvio", "SYNC"));
            boolean exitoEnvio;

            if ("ASYNC".equalsIgnoreCase(tipoEnvio)) {
                exitoEnvio = sifenClient.enviarDteAsincrono(dte);
            } else {
                exitoEnvio = sifenClient.enviarDteSincrono(dte);
            }

            documentoService.guardar(dte);
            historialSifenService.registrar(dte, "ENVIO");
            logger.info("Documento emitido con CDC: " + dte.getCdc());

            Map<String, Object> respuesta = new java.util.HashMap<>();
            respuesta.put("id", dte.getId());
            respuesta.put("cdc", dte.getCdc() != null ? dte.getCdc() : "");
            respuesta.put("estado", dte.getEstado().name());
            respuesta.put("codigoSifen", dte.getCodigoEstadoSifen() != null ? dte.getCodigoEstadoSifen() : "");
            respuesta.put("mensajeSifen", dte.getMensajeSifen() != null ? dte.getMensajeSifen() : "");
            respuesta.put("mensajeUsuario", dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : "");

            if ("ASYNC".equalsIgnoreCase(tipoEnvio) && dte.getNumeroTicketLote() != null) {
                respuesta.put("ticket", dte.getNumeroTicketLote());
            }

            if (exitoEnvio) {
                respuesta.put("message",
                        dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : "Operación exitosa con SIFEN");
                return ResponseEntity.ok(respuesta);
            } else {
                String msg = dte.getMensajeUsuario() != null ? dte.getMensajeUsuario()
                        : ("SIFEN devolvió estado: " + dte.getEstado().name());
                respuesta.put("message", msg);
                return ResponseEntity.status(422).body(respuesta);
            }
        } catch (DteValidationException e) {
            logger.warning("Validación DTE fallida: " + e.getMessage());
            return ResponseEntity.unprocessableEntity().body(
                    Map.of("message", "El documento no cumple con las validaciones SIFEN.", "errores", e.getErrores()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error emitiendo DTE", e);
            return ResponseEntity.status(500).body(Map.of("message", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/masivo")
    public ResponseEntity<Map<String, Object>> generarMasivo(@RequestBody List<Map<String, Object>> payloadList) {
        logger.info("Recibida solicitud de emisión MASIVA. Total documentos: " + payloadList.size());
        int procesadosExitosamente = 0;
        int conErrores = 0;
        List<Map<String, Object>> resultados = new ArrayList<>();

        for (int i = 0; i < payloadList.size(); i++) {
            Map<String, Object> payload = payloadList.get(i);
            Map<String, Object> resItem = new java.util.HashMap<>();
            resItem.put("indice", i);
            try {
                // El procesamiento transaccional individual garantiza que si uno falla el resto
                // no hace rollback
                DocumentoElectronico dte = procesarDteLocal(payload);
                dte.setTipoEmision(2); // Forzar a tipo asíncrono/lote para el procesador en background
                documentoService.guardar(dte);

                resItem.put("estado", dte.getEstado().name());
                resItem.put("cdc", dte.getCdc());
                resItem.put("idInterno", dte.getId());
                procesadosExitosamente++;
            } catch (DteValidationException e) {
                resItem.put("estado", "ERROR_VALIDACION");
                resItem.put("error", e.getMessage());
                resItem.put("detalles", e.getErrores());
                conErrores++;
            } catch (Exception e) {
                resItem.put("estado", "ERROR_INTERNO");
                resItem.put("error", e.getMessage());
                conErrores++;
            }
            resultados.add(resItem);
        }

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("totalRecibidos", payloadList.size());
        response.put("procesadosExitosamente", procesadosExitosamente);
        response.put("conErrores", conErrores);
        response.put("resultados", resultados);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/consultar-lote/{id}")
    public ResponseEntity<?> consultarLote(@PathVariable String id) {
        try {
            logger.info("Recibida solicitud de consulta de Lote para DTE con ID: " + id);
            String nonNullId = java.util.Objects.requireNonNull(id);
            DocumentoElectronico dte = documentoService.obtenerPorId(nonNullId);
            if (dte == null) {
                logger.warning("No se encontró el DTE con ID: " + id);
                return ResponseEntity.notFound().build();
            }

            if (dte.getNumeroTicketLote() == null || dte.getNumeroTicketLote().isBlank()) {
                logger.warning("El DTE " + id + " no tiene un Ticket asociado.");
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Este documento no tiene un Ticket de Lote asociado."));
            }

            logger.info("Iniciando consulta a SIFEN para el Ticket: " + dte.getNumeroTicketLote());
            boolean exito = sifenClient.consultarLoteSifen(dte);
            documentoService.guardar(dte);
            historialSifenService.registrar(dte, "CONSULTA_LOTE");
            logger.info("Consulta finalizada. Nuevo estado: " + dte.getEstado() + " | SIFEN Code: "
                    + dte.getCodigoEstadoSifen());

            Map<String, Object> respuesta = new java.util.HashMap<>();
            respuesta.put("id", dte.getId());
            respuesta.put("cdc", dte.getCdc() != null ? dte.getCdc() : "");
            respuesta.put("ticket", dte.getNumeroTicketLote());
            respuesta.put("estado", dte.getEstado().name());
            respuesta.put("codigoSifen", dte.getCodigoEstadoSifen() != null ? dte.getCodigoEstadoSifen() : "");
            respuesta.put("mensajeSifen", dte.getMensajeSifen() != null ? dte.getMensajeSifen() : "");
            respuesta.put("mensajeUsuario", dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : "");

            if (exito) {
                return ResponseEntity.ok(respuesta);
            } else {
                return ResponseEntity.status(422).body(respuesta);
            }

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
    public ResponseEntity<?> listarDocumentos() {
        try {
            String empresaId = EmpresaContext.getEmpresaId();
            List<DocumentoElectronico> documentos;

            if (empresaId != null) {
                documentos = dteRepository.findByEmisorIdOrderByFechaCreacionDesc(empresaId);
            } else {
                return ResponseEntity.status(403).body(Map.of("error", "Debe seleccionar una empresa"));
            }

            List<Map<String, Object>> list = documentos.stream().map(d -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", d.getId());
                m.put("tipoDocumento", d.getTipoDocumento());
                m.put("numeroComprobante", d.getNumeroComprobante() != null ? d.getNumeroComprobante() : "S/N");
                m.put("rucReceptor", d.getRucReceptor() != null ? d.getRucReceptor() : "Varios");
                m.put("receptorRazonSocial",
                        d.getReceptorRazonSocial() != null ? d.getReceptorRazonSocial() : "Varios");
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
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en /documentos: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().body(List.of());
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

            if ("80000001".equals(ruc)) {
                return ResponseEntity
                        .ok(Map.of("razonSocial", "EMPRESA DE PRUEBA SIFEN DEMO", "ruc", "80000001", "dv", "7"));
            }

            String rucBase = ruc.contains("-") ? ruc.substring(0, ruc.indexOf("-")) : ruc;
            rucBase = rucBase.replaceAll("[^0-9]", "").trim();

            java.util.Optional<com.zentra.middleware.core.model.PadronRuc> padronOpt = padronRucRepository
                    .findById(java.util.Objects.requireNonNull(rucBase));
            if (padronOpt.isPresent()) {
                com.zentra.middleware.core.model.PadronRuc p = padronOpt.get();
                return ResponseEntity.ok(Map.of("razonSocial", p.getRazonSocial(), "ruc", p.getRuc() + "-" + p.getDv(),
                        "dv", p.getDv()));
            }

            String dv = ruc.contains("-") ? ruc.substring(ruc.indexOf("-") + 1).trim()
                    : String.valueOf(calcularDV(rucBase));

            String url = "https://consultaruc.com.py/api/consulta/" + rucBase;
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

            return ResponseEntity
                    .ok(Map.of("razonSocial", "CLIENTE RECUPERADO MOCK (" + rucBase + "-" + dv + ")", "ruc", rucBase,
                            "dv", dv));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fatal en consulta RUC: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error interno"));
        }
    }

    private int calcularDV(String ruc) {
        int basemax = 11;
        int total = 0;
        int k = 2;
        for (int i = ruc.length() - 1; i >= 0; i--) {
            char c = ruc.charAt(i);
            if (c >= '0' && c <= '9') {
                total += Character.getNumericValue(c) * k;
                k++;
                if (k > basemax) {
                    k = 2;
                }
            } else {
                return 0;
            }
        }
        int resto = total % 11;
        return resto > 1 ? 11 - resto : 0;
    }

}
