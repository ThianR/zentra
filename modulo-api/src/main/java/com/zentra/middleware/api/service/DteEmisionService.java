package com.zentra.middleware.api.service;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.ItemDocumento;
import com.zentra.middleware.core.model.Cuota;
import com.zentra.middleware.core.model.PagoContado;
import com.zentra.middleware.core.model.Transporte;
import com.zentra.middleware.core.service.DocumentoService;
import com.zentra.middleware.core.service.DteValidatorService;
import com.zentra.middleware.xml.DteXmlGenerator;
import com.zentra.middleware.xml.XsdValidatorService;
import com.zentra.middleware.crypto.service.XmlSignerService;
import com.zentra.middleware.sifen.SifenSoapClient;
import com.zentra.middleware.core.repository.SifenReferenciaRepository;
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.service.HistorialSifenService;
import com.zentra.middleware.api.security.EmpresaContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

/**
 * Servicio de negocio para gestionar el flujo completo de validación, generación, firmado,
 * almacenamiento y envío de Documentos Electrónicos (DTEs) a SIFEN.
 */
@Service
@Transactional
public class DteEmisionService {

    private static final Logger logger = Logger.getLogger(DteEmisionService.class.getName());

    private final DteXmlGenerator xmlGenerator;
    private final XmlSignerService signerService;
    private final SifenSoapClient sifenClient;
    private final DocumentoService documentoService;
    private final DteValidatorService validatorService;
    private final SifenReferenciaRepository referenciaRepository;
    private final XsdValidatorService xsdValidatorService;
    private final EmpresaRepository empresaRepository;
    private final HistorialSifenService historialSifenService;
    private final com.zentra.middleware.core.repository.PadronRucRepository padronRucRepository;
    private final HttpClient httpClient;

    public DteEmisionService(
            DteXmlGenerator xmlGenerator,
            XmlSignerService signerService,
            SifenSoapClient sifenClient,
            DocumentoService documentoService,
            DteValidatorService validatorService,
            SifenReferenciaRepository referenciaRepository,
            XsdValidatorService xsdValidatorService,
            EmpresaRepository empresaRepository,
            HistorialSifenService historialSifenService,
            com.zentra.middleware.core.repository.PadronRucRepository padronRucRepository,
            HttpClient httpClient) {
        this.xmlGenerator = xmlGenerator;
        this.signerService = signerService;
        this.sifenClient = sifenClient;
        this.documentoService = documentoService;
        this.validatorService = validatorService;
        this.referenciaRepository = referenciaRepository;
        this.xsdValidatorService = xsdValidatorService;
        this.empresaRepository = empresaRepository;
        this.historialSifenService = historialSifenService;
        this.padronRucRepository = padronRucRepository;
        this.httpClient = httpClient;
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

    /**
     * Variante nullable de safeInt: retorna Integer (puede ser null si defaultVal es null).
     * Permite persistir campos opcionales sin forzar valor por defecto.
     */
    private Integer safeInteger(Object o, Integer defaultVal) {
        if (o == null || o.toString().trim().isEmpty() || "null".equals(o.toString().trim()))
            return defaultVal;
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return defaultVal;
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

    /**
     * Procesa y mapea el payload de entrada a un objeto de tipo DocumentoElectronico,
     * realiza validaciones y firma el XML final.
     *
     * @param payload Mapa con los datos del DTE enviados por el cliente.
     * @return el DocumentoElectronico generado, firmado y persistido preliminarmente.
     * @throws Exception en caso de errores de negocio o validación.
     */
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

        dte.setRucEmisor(emisor.getRuc());
        dte.setRazonSocialEmisor(emisor.getRazonSocial());
        dte.setActividadEconomicaEmisor(emisor.getActividadEconomica());
        dte.setDireccionEmisor(emisor.getDireccion());
        dte.setTelefonoEmisor(emisor.getTelefono());

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

            if (recRaw.get("numeroCasa") != null) {
                dte.setReceptorNumeroCasa(String.valueOf(recRaw.get("numeroCasa")));
            }
            
            if (recRaw.get("codDepartamento") != null) {
                int codDepto = safeInt(recRaw.get("codDepartamento"), 0);
                if (codDepto > 0) {
                    dte.setReceptorCodigoDepartamento(codDepto);
                    referenciaRepository.findByTipoAndCodigo("DEPARTAMENTO", String.valueOf(codDepto))
                            .ifPresent(ref -> dte.setReceptorDescripcionDepartamento(ref.getDescripcion()));
                }
            }

            if (recRaw.get("codCiudad") != null) {
                int codCiud = safeInt(recRaw.get("codCiudad"), 0);
                if (codCiud > 0) {
                    dte.setReceptorCodigoCiudad(codCiud);
                    int codDepto = dte.getReceptorCodigoDepartamento() != null ? dte.getReceptorCodigoDepartamento() : 1;
                    referenciaRepository.findCiudadByDeptoAndCiudadCod(String.valueOf(codDepto), String.valueOf(codCiud))
                            .ifPresent(ref -> dte.setReceptorDescripcionCiudad(ref.getDescripcion()));
                }
            }
        } else {
            rucParaGuardar = String.valueOf(payload.getOrDefault("rucReceptor", "Varios"));
            dte.setReceptorRazonSocial(String.valueOf(payload.getOrDefault("razonSocialReceptor", "Sin Nombre")));
            dte.setReceptorDireccion(String.valueOf(payload.getOrDefault("direccionReceptor", "")));
            dte.setReceptorTelefono(String.valueOf(payload.getOrDefault("telefonoReceptor", "")));
            dte.setReceptorEmail(String.valueOf(payload.getOrDefault("emailReceptor", "")));
            
            if (payload.get("numeroCasaReceptor") != null) {
                dte.setReceptorNumeroCasa(String.valueOf(payload.get("numeroCasaReceptor")));
            }
        }

        rucParaGuardar = rucParaGuardar.replace(".", "").trim();

        Integer tipoRecFrontend = null;
        if (recRaw != null && recRaw.get("tipoReceptor") != null) {
            tipoRecFrontend = safeInt(recRaw.get("tipoReceptor"), null);
        }
        int iTiOpe = safeInt(payload.get("iTiOpe"), 1);

        String rucSolo = rucParaGuardar.contains("-") ? rucParaGuardar.split("-")[0].trim() : rucParaGuardar;
        boolean esNumerico = rucSolo.matches("\\d+") && !rucSolo.equals("0");

        if (iTiOpe == 2 || (tipoRecFrontend != null && tipoRecFrontend == 2)) {
            rucParaGuardar = rucSolo;
            dte.setTipoReceptor(2);
            logger.info("Receptor tratado como No Contribuyente (iTiOpe=" + iTiOpe + ", tipoRecFrontend=" + tipoRecFrontend + ")");
        } else if (esNumerico) {
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
        String nonNullNum = Objects.requireNonNull(fulNum);
        String nonNullTipo = Objects.requireNonNull(tipoDoc);
        documentoService.eliminarSiEstaRechazado(nonNullNum, nonNullTipo);

        if (documentoService.existePorNumero(nonNullNum, nonNullTipo)) {
            throw new RuntimeException(
                    "El documento con número " + fulNum + " ya existe y fue APROBADO anteriormente.");
        }

        dte.setNumeroComprobante(fulNum);
        dte.setTimbrado(emisor.getTimbrado() != null ? emisor.getTimbrado() : "16770994");

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
        
        if (dte.getCondicionOperacion() == 1 && payload.containsKey("pagos")) {
            List<?> pagosRaw = (List<?>) payload.get("pagos");
            if (pagosRaw != null) {
                List<PagoContado> pagos = new ArrayList<>();
                for (Object pagoObj : pagosRaw) {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> pRaw = (Map<String, Object>) pagoObj;
                        PagoContado pago = new PagoContado();
                        pago.setDocumento(dte);
                        pago.setTipoPago(safeInt(pRaw.get("tipoPago"), 1));
                        pago.setMonto(safeDouble(pRaw.get("monto"), 0.0));
                        // Campos de detalle de tarjeta y cheque
                        Object safeSec = pRaw.get("safeSecure");
                        pago.setSafeSecure(safeSec instanceof Boolean ? (Boolean) safeSec : true);
                        pago.setTarjetaDenominacion(safeInteger(pRaw.get("tarjetaDenominacion"), null));
                        String tarjDesc = pRaw.get("tarjetaDescripcion") != null ? pRaw.get("tarjetaDescripcion").toString().trim() : null;
                        pago.setTarjetaDescripcion(tarjDesc != null && !tarjDesc.isEmpty() ? tarjDesc : null);
                        pago.setTarjetaFormaProcesamiento(safeInteger(pRaw.get("tarjetaFormaProcesamiento"), null));
                        String cheqNum = pRaw.get("chequeNumero") != null ? pRaw.get("chequeNumero").toString().trim() : null;
                        pago.setChequeNumero(cheqNum != null && !cheqNum.isEmpty() ? cheqNum : null);
                        String cheqBco = pRaw.get("chequeBanco") != null ? pRaw.get("chequeBanco").toString().trim() : null;
                        pago.setChequeBanco(cheqBco != null && !cheqBco.isEmpty() ? cheqBco : null);
                        pagos.add(pago);
                    } catch (Exception e) {
                        logger.warning("Error parseando pago: " + e.getMessage());
                    }
                }
                dte.setPagos(pagos);
            }
        }

        dte.setTipoOperacion(safeInt(payload.get("iTiOpe"), 1));
        dte.setIndicadorPresencia(safeInt(payload.get("iIndPres"), 1));
        dte.setNaturalezaVendedor(safeInt(payload.get("naturalezaVendedor"), null));
        dte.setTipoEmision(safeInt(payload.get("tipoEmision"), 1));

        dte.setCdcDocumentoAsociado(String.valueOf(payload.getOrDefault("cdcDocumentoAsociado", "")));
        dte.setMotivoEmision(String.valueOf(payload.getOrDefault("motivoEmision", "")));

        if (payload.containsKey("transporte")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tRaw = (Map<String, Object>) payload.get("transporte");
            if (tRaw != null) {
                String nomChof = String.valueOf(tRaw.get("nombreChofer")).trim();
                String docChof = String.valueOf(tRaw.get("numeroDocumentoChofer")).trim();
                String patent = String.valueOf(tRaw.get("matriculaVehiculo")).trim();

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
                    t.setTipoTransporte(safeInt(tRaw.get("tipoTransporte"), 1));
                    t.setNaturalezaTransportista(safeInt(tRaw.get("naturalezaTransportista"), 1));
                    t.setResponsableEmision(safeInt(tRaw.get("responsableEmision"), 1));
                    t.setNombreTransportista(String.valueOf(tRaw.getOrDefault("nombreTransportista", "")));
                    t.setRucTransportista(String.valueOf(tRaw.getOrDefault("rucTransportista", "")));
                    t.setDvTransportista(String.valueOf(tRaw.getOrDefault("dvTransportista", "")));
                    t.setDireccionChofer(String.valueOf(tRaw.getOrDefault("direccionChofer", "")));
                    t.setFechaInicioTraslado(String.valueOf(tRaw.getOrDefault("fechaInicioTraslado", "")));
                    t.setFechaFinTraslado(String.valueOf(tRaw.getOrDefault("fechaFinTraslado", "")));
                    t.setMarcaVehiculo(String.valueOf(tRaw.getOrDefault("marcaVehiculo", "")));
                    t.setTipoVehiculo(String.valueOf(tRaw.getOrDefault("tipoVehiculo", "")));
                    t.setChasisVehiculo(String.valueOf(tRaw.getOrDefault("chasisVehiculo", "")));
                    t.setLocalSalidaDireccion(String.valueOf(tRaw.getOrDefault("localSalidaDireccion", "")));
                    t.setLocalSalidaNumeroCasa(safeInt(tRaw.get("localSalidaNumeroCasa"), 0));
                    int codDepSal = safeInt(tRaw.get("localSalidaCodigoDepartamento"), 0);
                    if (codDepSal > 0) {
                        t.setLocalSalidaCodigoDepartamento(codDepSal);
                        referenciaRepository.findByTipoAndCodigo("DEPARTAMENTO", String.valueOf(codDepSal))
                                .ifPresent(ref -> t.setLocalSalidaDescripcionDepartamento(ref.getDescripcion()));
                    }
                    int codCiuSal = safeInt(tRaw.get("localSalidaCodigoCiudad"), 0);
                    if (codCiuSal > 0) {
                        t.setLocalSalidaCodigoCiudad(codCiuSal);
                        int depSal = t.getLocalSalidaCodigoDepartamento() != null ? t.getLocalSalidaCodigoDepartamento() : 1;
                        referenciaRepository.findCiudadByDeptoAndCiudadCod(String.valueOf(depSal), String.valueOf(codCiuSal))
                                .ifPresent(ref -> t.setLocalSalidaDescripcionCiudad(ref.getDescripcion()));
                    }
                    t.setLocalEntregaDireccion(String.valueOf(tRaw.getOrDefault("localEntregaDireccion", "")));
                    t.setLocalEntregaNumeroCasa(safeInt(tRaw.get("localEntregaNumeroCasa"), 0));
                    int codDepEnt = safeInt(tRaw.get("localEntregaCodigoDepartamento"), 0);
                    if (codDepEnt > 0) {
                        t.setLocalEntregaCodigoDepartamento(codDepEnt);
                        referenciaRepository.findByTipoAndCodigo("DEPARTAMENTO", String.valueOf(codDepEnt))
                                .ifPresent(ref -> t.setLocalEntregaDescripcionDepartamento(ref.getDescripcion()));
                    }
                    int codCiuEnt = safeInt(tRaw.get("localEntregaCodigoCiudad"), 0);
                    if (codCiuEnt > 0) {
                        t.setLocalEntregaCodigoCiudad(codCiuEnt);
                        int depEnt = t.getLocalEntregaCodigoDepartamento() != null ? t.getLocalEntregaCodigoDepartamento() : 1;
                        referenciaRepository.findCiudadByDeptoAndCiudadCod(String.valueOf(depEnt), String.valueOf(codCiuEnt))
                                .ifPresent(ref -> t.setLocalEntregaDescripcionCiudad(ref.getDescripcion()));
                    }
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
                    long baseNeta = Math.round(subtotal / 1.1);
                    long liqIva = Math.round(subtotal) - baseNeta;
                    item.setMontoIvaItem((double) liqIva);
                    item.setMontoTotalItem((double) Math.round(subtotal));
                    grav10 += item.getMontoTotalItem();
                    iva10 += liqIva;
                } else if (item.getTasaIva() == 5.0) {
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

        DteValidatorService.ResultadoValidacion validacion = validatorService.validar(dte);
        if (!validacion.esValido()) {
            throw new DteValidationException("Validación fallida: " + validacion.getMensajeResumen(),
                    validacion.getErrores());
        }

        xmlGenerator.generarXml(dte);
        signerService.firmarXml(dte);

        xsdValidatorService.validarXml(dte.getXmlFirmado(), dte.getTipoDocumento());

        dte.setEstado(EstadoDte.FIRMADO);
        return documentoService.guardar(dte);
    }

    /**
     * Realiza la generación de un DTE y lo envía sincrónica o asincrónicamente a SIFEN.
     *
     * @param payload Datos del DTE recibidos del frontend.
     * @return un mapa con el resultado de la emisión estructurado para responder al frontend.
     * @throws Exception en caso de errores durante el flujo.
     */
    public Map<String, Object> generarDte(Map<String, Object> payload) throws Exception {
        logger.info("Recibida solicitud de generación DTE unitaria en capa de servicio...");
        DocumentoElectronico dte = Objects.requireNonNull(procesarDteLocal(payload));

        String tipoEnvio = String.valueOf(payload.getOrDefault("tipoEnvio", "SYNC"));
        boolean exitoEnvio;

        if ("ASYNC".equalsIgnoreCase(tipoEnvio)) {
            exitoEnvio = sifenClient.enviarDteAsincrono(dte);
        } else {
            exitoEnvio = sifenClient.enviarDteSincrono(dte);
        }

        dte = documentoService.guardar(dte);
        historialSifenService.registrar(dte, "ENVIO");
        logger.info("Documento emitido con CDC en servicio: " + dte.getCdc());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", dte.getId());
        respuesta.put("cdc", dte.getCdc() != null ? dte.getCdc() : "");
        respuesta.put("estado", dte.getEstado().name());
        respuesta.put("codigoSifen", dte.getCodigoEstadoSifen() != null ? dte.getCodigoEstadoSifen() : "");
        respuesta.put("mensajeSifen", dte.getMensajeSifen() != null ? dte.getMensajeSifen() : "");
        respuesta.put("mensajeUsuario", dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : "");
        respuesta.put("exitoEnvio", exitoEnvio);

        if ("ASYNC".equalsIgnoreCase(tipoEnvio) && dte.getNumeroTicketLote() != null) {
            respuesta.put("ticket", dte.getNumeroTicketLote());
        }

        return respuesta;
    }

    /**
     * Procesa la emisión masiva de una lista de payloads, registrándolos para su envío en background.
     *
     * @param payloadList Lista de mapas con datos de DTEs.
     * @return estadísticas de la emisión y detalle por ítem.
     */
    public Map<String, Object> generarMasivo(List<Map<String, Object>> payloadList) {
        logger.info("Recibida solicitud de emisión MASIVA en servicio. Total: " + payloadList.size());
        int procesadosExitosamente = 0;
        int conErrores = 0;
        List<Map<String, Object>> resultados = new ArrayList<>();

        for (int i = 0; i < payloadList.size(); i++) {
            Map<String, Object> payload = payloadList.get(i);
            Map<String, Object> resItem = new HashMap<>();
            resItem.put("indice", i);
            try {
                DocumentoElectronico dte = procesarDteLocal(payload);
                dte.setTipoEmision(2); // Forzar a tipo asíncrono/lote
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

        Map<String, Object> response = new HashMap<>();
        response.put("totalRecibidos", payloadList.size());
        response.put("procesadosExitosamente", procesadosExitosamente);
        response.put("conErrores", conErrores);
        response.put("resultados", resultados);
        return response;
    }

    /**
     * Consulta el estado de procesamiento de un lote asociado a un DTE específico.
     *
     * @param id identificador único del documento.
     * @return mapa con el estado devuelto por SIFEN.
     * @throws Exception en caso de que el DTE no exista o no contenga un ticket.
     */
    public Map<String, Object> consultarLote(String id) throws Exception {
        logger.info("Consultando Lote en servicio para DTE con ID: " + id);
        String nonNullId = Objects.requireNonNull(id);
        DocumentoElectronico dte = documentoService.obtenerPorId(nonNullId);
        if (dte == null) {
            logger.warning("No se encontró el DTE con ID: " + id);
            throw new NoSuchElementException("No se encontró el DTE con ID: " + id);
        }

        if (dte.getNumeroTicketLote() == null || dte.getNumeroTicketLote().isBlank()) {
            logger.warning("El DTE " + id + " no tiene un Ticket asociado.");
            throw new IllegalArgumentException("Este documento no tiene un Ticket de Lote asociado.");
        }

        logger.info("Iniciando consulta a SIFEN para el Ticket en servicio: " + dte.getNumeroTicketLote());
        boolean exito = sifenClient.consultarLoteSifen(dte);
        documentoService.guardar(dte);
        historialSifenService.registrar(dte, "CONSULTA_LOTE");
        logger.info("Consulta finalizada. Nuevo estado: " + dte.getEstado() + " | SIFEN Code: "
                + dte.getCodigoEstadoSifen());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", dte.getId());
        respuesta.put("cdc", dte.getCdc() != null ? dte.getCdc() : "");
        respuesta.put("ticket", dte.getNumeroTicketLote());
        respuesta.put("estado", dte.getEstado().name());
        respuesta.put("codigoSifen", dte.getCodigoEstadoSifen() != null ? dte.getCodigoEstadoSifen() : "");
        respuesta.put("mensajeSifen", dte.getMensajeSifen() != null ? dte.getMensajeSifen() : "");
        respuesta.put("mensajeUsuario", dte.getMensajeUsuario() != null ? dte.getMensajeUsuario() : "");
        respuesta.put("exito", exito);
        return respuesta;
    }

    /**
     * Consulta el RUC en el padrón local y, si no se encuentra, en el servicio de consulta externo.
     *
     * @param ruc RUC a consultar.
     * @return mapa con el resultado de la consulta.
     * @throws Exception en caso de errores en la red o base de datos.
     */
    public Map<String, Object> consultarRuc(String ruc) throws Exception {
        if (ruc == null || ruc.trim().isEmpty()) {
            throw new IllegalArgumentException("RUC vacío");
        }

        if ("80000001".equals(ruc)) {
            return Map.of("razonSocial", "EMPRESA DE PRUEBA SIFEN DEMO", "ruc", "80000001", "dv", "7");
        }

        String rucBase = ruc.contains("-") ? ruc.substring(0, ruc.indexOf("-")) : ruc;
        rucBase = rucBase.replaceAll("[^0-9]", "").trim();

        var padronOpt = padronRucRepository.findById(Objects.requireNonNull(rucBase));
        if (padronOpt.isPresent()) {
            var p = padronOpt.get();
            return Map.of("razonSocial", p.getRazonSocial(), "ruc", p.getRuc() + "-" + p.getDv(), "dv", p.getDv());
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
                // Devolvemos los datos crudos consultados si la API responde exitosamente
                return Map.of("rawJson", response.body(), "ruc", rucBase, "dv", dv);
            }
        } catch (Exception netEx) {
            logger.warning("No se pudo conectar con el servicio externo de RUC: " + netEx.getMessage());
        }

        return Map.of("razonSocial", "CLIENTE RECUPERADO MOCK (" + rucBase + "-" + dv + ")", "ruc", rucBase, "dv", dv);
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
