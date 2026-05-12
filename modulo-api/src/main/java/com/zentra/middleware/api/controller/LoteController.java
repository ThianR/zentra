package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.LoteTransmision;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.core.repository.LoteTransmisionRepository;
import com.zentra.middleware.sifen.SifenSoapClient;
import com.zentra.middleware.core.service.HistorialSifenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zentra.middleware.api.security.EmpresaContext;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private static final Logger logger = Logger.getLogger(LoteController.class.getName());

    @Autowired
    private LoteTransmisionRepository loteRepository;

    @Autowired
    private DocumentoElectronicoRepository dteRepository;

    @Autowired
    private SifenSoapClient sifenSoapClient;

    @Autowired
    private HistorialSifenService historialSifenService;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<?> listarLotes() {
        String empresaId = EmpresaContext.getEmpresaId();
        if (empresaId == null) return ResponseEntity.status(403).body(Map.of("error", "Empresa no seleccionada"));
        
        List<LoteTransmision> lotes = loteRepository.findByEmpresaIdOrderByFechaEnvioDesc(empresaId);
        List<Map<String, Object>> result = lotes.stream().map(lote -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", lote.getId());
            map.put("estado", lote.getEstado().name());
            map.put("fechaEnvio", lote.getFechaEnvio());
            map.put("numeroTicket", lote.getNumeroTicket());
            map.put("fechaUltimaConsulta", lote.getFechaUltimaConsulta());
            map.put("intentosConsulta", lote.getIntentosConsulta());
            if (lote.getEmpresa() != null) {
                map.put("empresa", Map.of("ruc", lote.getEmpresa().getRuc()));
            }
            List<Map<String, Object>> docs = lote.getDocumentos().stream().map(d -> {
                Map<String, Object> dMap = new java.util.HashMap<>();
                dMap.put("id", d.getId());
                dMap.put("cdc", d.getCdc());
                dMap.put("estado", d.getEstado().name());
                dMap.put("numeroComprobante", d.getNumeroComprobante());
                dMap.put("codigoEstadoSifen", d.getCodigoEstadoSifen());
                
                String qrUrl = "";
                if (d.getXmlFirmado() != null && d.getXmlFirmado().contains("<dCarQR>")) {
                    qrUrl = d.getXmlFirmado().substring(d.getXmlFirmado().indexOf("<dCarQR>") + 8, d.getXmlFirmado().indexOf("</dCarQR>")).replace("&amp;", "&");
                }
                dMap.put("qrUrl", qrUrl);
                return dMap;
            }).toList();
            map.put("documentos", docs);
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de recuperación manual para DTEs "huérfanos".
     * Permite consultar el estado real de un DTE directamente en SIFEN usando su CDC,
     * sin necesitar el Ticket de Lote. Útil cuando un lote superó el timeout de 24h.
     *
     * @param cdc Código de Control del DTE a consultar (44 caracteres).
     */
    @GetMapping("/consultar-cdc/{cdc}")
    public ResponseEntity<?> consultarPorCdc(@PathVariable String cdc) {
        logger.info("Solicitud de consulta manual por CDC: " + cdc);

        DocumentoElectronico dte = dteRepository.findByCdc(cdc).orElse(null);
        if (dte == null) {
            return ResponseEntity.notFound().build();
        }

        String empresaId = EmpresaContext.getEmpresaId();
        if (empresaId == null || dte.getEmisor() == null || !dte.getEmisor().getId().equals(empresaId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No tiene acceso a este documento"));
        }

        try {
            boolean aprobado = sifenSoapClient.consultarDtePorCdc(dte);
            dteRepository.save(dte);
            historialSifenService.registrar(dte, "CONSULTA_CDC");

            Map<String, Object> respuesta = new java.util.HashMap<>();
            respuesta.put("cdc", dte.getCdc());
            respuesta.put("id", dte.getId());
            respuesta.put("estado", dte.getEstado().name());
            respuesta.put("codigoSifen", dte.getCodigoEstadoSifen());
            respuesta.put("mensajeUsuario", dte.getMensajeUsuario());

            if (aprobado) {
                return ResponseEntity.ok(respuesta);
            } else {
                return ResponseEntity.status(422).body(respuesta);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en consulta por CDC: " + e.getMessage(), e);
            return ResponseEntity.status(500).body(
                    Map.of("message", "Error técnico al consultar en SIFEN: " + e.getMessage()));
        }
    }
}
