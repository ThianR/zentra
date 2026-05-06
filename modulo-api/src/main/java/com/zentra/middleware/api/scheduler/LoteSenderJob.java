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
    public void agruparYEnviarLotes() {
        // Buscar DTEs pendientes de envío agrupados (FIRMADOS)
        List<DocumentoElectronico> pendientes = dteRepository.findAll().stream()
                .filter(d -> d.getEstado() == EstadoDte.FIRMADO && d.getTipoEmision() == 2) // 2 es Asíncrono
                .collect(Collectors.toList());

        if (pendientes.isEmpty()) {
            return; // Nada que hacer
        }

        Map<Empresa, List<DocumentoElectronico>> porEmpresa = pendientes.stream()
                .filter(d -> d.getEmisor() != null)
                .collect(Collectors.groupingBy(DocumentoElectronico::getEmisor));

        for (Map.Entry<Empresa, List<DocumentoElectronico>> entry : porEmpresa.entrySet()) {
            Empresa empresa = entry.getKey();
            List<DocumentoElectronico> dtesEmpresa = entry.getValue();

            int frecuencia = empresa.getFrecuenciaLoteMinutos() != null ? empresa.getFrecuenciaLoteMinutos() : 15;

            // Verificamos si ya pasó el tiempo desde el último lote enviado
            List<LoteTransmision> ultimosLotes = loteRepository.findByEmpresaIdOrderByFechaEnvioDesc(empresa.getId());
            if (!ultimosLotes.isEmpty()) {
                LoteTransmision ultimo = ultimosLotes.get(0);
                if (ultimo.getFechaEnvio() != null) {
                    LocalDateTime proximoEnvio = ultimo.getFechaEnvio().plusMinutes(frecuencia);
                    if (LocalDateTime.now().isBefore(proximoEnvio) && dtesEmpresa.size() < 50) {
                        // Aún no es tiempo y no llegamos al tope de 50. Esperamos.
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
