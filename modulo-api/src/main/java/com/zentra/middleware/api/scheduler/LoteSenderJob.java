package com.zentra.middleware.api.scheduler;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.model.EstadoLote;
import com.zentra.middleware.core.model.LoteTransmision;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.core.repository.EmpresaRepository;
import com.zentra.middleware.core.repository.LoteTransmisionRepository;
import com.zentra.middleware.sifen.SifenSoapClient;
import com.zentra.middleware.xml.LoteXmlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Component
public class LoteSenderJob {

    private static final Logger logger = Logger.getLogger(LoteSenderJob.class.getName());

    @Autowired
    private DocumentoElectronicoRepository dteRepository;

    @Autowired
    private LoteTransmisionRepository loteRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private LoteXmlGenerator loteXmlGenerator;

    @Autowired
    private SifenSoapClient sifenSoapClient;

    // Ejecuta cada 1 minuto
    @Scheduled(fixedDelay = 60000)
    @org.springframework.transaction.annotation.Transactional
    public void agruparYEnviarLotes() {
        // Buscar DTEs pendientes de envío agrupados (FIRMADOS)
        // tipoEmision 2 es Asíncrono según la lógica del sistema
        List<DocumentoElectronico> pendientes = dteRepository.findByEstadoAndTipoEmision(EstadoDte.FIRMADO, 2);

        if (pendientes.isEmpty()) {
            return;
        }

        // Agrupamos por ID de empresa para evitar problemas con proxies JPA y hashCode
        Map<String, List<DocumentoElectronico>> porEmpresaId = pendientes.stream()
                .filter(d -> d.getEmisor() != null)
                .collect(Collectors.groupingBy(d -> d.getEmisor().getId()));

        for (Map.Entry<String, List<DocumentoElectronico>> entry : porEmpresaId.entrySet()) {
            String empresaId = entry.getKey();
            List<DocumentoElectronico> dtesEmpresa = entry.getValue();

            String nonNullId = java.util.Objects.requireNonNull(empresaId);
            Empresa empresa = empresaRepository.findById(nonNullId).orElse(null);
            if (empresa == null) continue;

            int frecuencia = empresa.getFrecuenciaLoteMinutos() != null ? empresa.getFrecuenciaLoteMinutos() : 15;

            // Verificamos si ya pasó el tiempo desde el último lote enviado
            List<LoteTransmision> ultimosLotes = loteRepository.findByEmpresaIdOrderByFechaEnvioDesc(empresaId);
            if (!ultimosLotes.isEmpty()) {
                LoteTransmision ultimo = ultimosLotes.get(0);
                if (ultimo.getFechaEnvio() != null) {
                    LocalDateTime proximoEnvio = ultimo.getFechaEnvio().plusMinutes(frecuencia);
                    if (LocalDateTime.now().isBefore(proximoEnvio) && dtesEmpresa.size() < 50) {
                        continue;
                    }
                }
            }

            // Procesamos en chunks de a 50
            for (int i = 0; i < dtesEmpresa.size(); i += 50) {
                int end = Math.min(i + 50, dtesEmpresa.size());
                List<DocumentoElectronico> chunk = dtesEmpresa.subList(i, end);
                procesarLote(empresa, chunk);
            }
        }
    }

    private void procesarLote(Empresa empresa, List<DocumentoElectronico> chunk) {
        LoteTransmision lote = new LoteTransmision();
        lote.setEmpresa(empresa);
        lote.setEstado(EstadoLote.PENDIENTE);
        loteRepository.save(lote);

        // Actualizamos los DTEs para marcarlos que ya están en este lote
        for (DocumentoElectronico dte : chunk) {
            dte.setLoteTransmision(lote);
            dte.setEstado(EstadoDte.EN_LOTE);
            dteRepository.save(dte);
        }

        try {
            String xmlLote = loteXmlGenerator.generarXmlLote(chunk);
            boolean exito = sifenSoapClient.enviarLoteAsincrono(lote, xmlLote);

            if (exito) {
                loteRepository.save(lote); // Guarda el estado ENVIADO y el Ticket
                for (DocumentoElectronico dte : chunk) {
                    dte.setEstado(EstadoDte.EN_PROCESO);
                    dte.setNumeroTicketLote(lote.getNumeroTicket());
                    dteRepository.save(dte);
                }
            } else {
                loteRepository.save(lote); // Guarda el estado ERROR
                for (DocumentoElectronico dte : chunk) {
                    dte.setEstado(EstadoDte.ERROR_ENVIO);
                    dteRepository.save(dte);
                }
            }
        } catch (Exception e) {
            logger.severe("Error procesando lote para empresa " + empresa.getRuc() + ": " + e.getMessage());
        }
    }
}
