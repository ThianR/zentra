package com.zentra.middleware.api.scheduler;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.model.EstadoLote;
import com.zentra.middleware.core.model.LoteTransmision;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.core.repository.LoteTransmisionRepository;
import com.zentra.middleware.sifen.SifenSoapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TicketPollerJob {

    private static final Logger logger = Logger.getLogger(TicketPollerJob.class.getName());

    @Autowired
    private LoteTransmisionRepository loteRepository;

    @Autowired
    private DocumentoElectronicoRepository dteRepository;

    @Autowired
    private SifenSoapClient sifenSoapClient;

    // Tiempo máximo de espera de respuesta para un lote (en horas) antes de marcarlo como TIMEOUT
    private static final int HORAS_TIMEOUT_LOTE = 24;

    // Ejecuta cada 1 minuto
    @Scheduled(fixedDelay = 60000)
    public void consultarTicketsPendientes() {
        List<LoteTransmision> lotesEnviados = loteRepository.findByEstado(EstadoLote.ENVIADO);

        for (LoteTransmision lote : lotesEnviados) {
            Empresa empresa = lote.getEmpresa();

            // --- Medida Preventiva: Timeout de Lotes Huérfanos ---
            // Si el lote lleva más de 24 horas en estado ENVIADO sin respuesta definitiva,
            // se lo marca como ERROR para que el administrador lo gestione manualmente.
            LocalDateTime fechaEnvio = lote.getFechaEnvio();
            if (fechaEnvio != null &&
                    LocalDateTime.now().isAfter(fechaEnvio.plusHours(HORAS_TIMEOUT_LOTE))) {
                logger.warning("TIMEOUT: Lote " + lote.getId() + " lleva más de " +
                        HORAS_TIMEOUT_LOTE + "h sin respuesta. Marcando como ERROR.");
                lote.setEstado(EstadoLote.ERROR);
                for (DocumentoElectronico dte : lote.getDocumentos()) {
                    dte.setEstado(EstadoDte.ERROR_ENVIO);
                    dte.setMensajeUsuario("Lote sin respuesta por más de " + HORAS_TIMEOUT_LOTE +
                            "h. Usar 'Consultar por CDC' para recuperar el estado desde SIFEN.");
                    dteRepository.save(dte);
                }
                loteRepository.save(lote);
                continue;
            }

            int frecuencia = empresa.getFrecuenciaConsultaTicketMinutos() != null ? empresa.getFrecuenciaConsultaTicketMinutos() : 5;

            LocalDateTime baseTime = lote.getFechaUltimaConsulta() != null ? lote.getFechaUltimaConsulta() : lote.getFechaEnvio();
            
            // Si no ha pasado el tiempo configurado, lo ignoramos este ciclo
            if (baseTime != null && LocalDateTime.now().isBefore(baseTime.plusMinutes(frecuencia))) {
                continue;
            }

            logger.info("Polling Ticket " + lote.getNumeroTicket() + " para empresa " + empresa.getRuc());
            lote.setFechaUltimaConsulta(LocalDateTime.now());
            lote.setIntentosConsulta(lote.getIntentosConsulta() + 1);

            String xmlRespuesta = sifenSoapClient.consultarTicketLoteMultiple(lote);
            
            if (xmlRespuesta == null) {
                loteRepository.save(lote);
                continue;
            }

            String dCodResLot = extraerEtiqueta(xmlRespuesta, "dCodResLot", "DESCONOCIDO");
            
            if ("0304".equals(dCodResLot)) {
                // Sigue en proceso, guardamos fecha y esperamos al siguiente ciclo
                loteRepository.save(lote);
                continue;
            }

            // Si es 0300 (Procesado) o algun otro estado definitivo, procesamos DTEs
            lote.setEstado(EstadoLote.PROCESADO);
            loteRepository.save(lote);

            procesarResultadosIndividuales(lote, xmlRespuesta);
        }
    }

    private void procesarResultadosIndividuales(LoteTransmision lote, String xmlRespuesta) {
        List<DocumentoElectronico> dtes = lote.getDocumentos();

        for (DocumentoElectronico dte : dtes) {
            String cdc = dte.getCdc();
            if (cdc == null) continue;

            // Buscamos el nodo <gResProc> que contiene <id>CDC</id>
            Pattern pattern = Pattern.compile("<(?:.*?:)?gResProc>.*?<(?:.*?:)?id>" + cdc + "</(?:.*?:)?id>.*?</(?:.*?:)?gResProc>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(xmlRespuesta);

            if (matcher.find()) {
                String gResProc = matcher.group(0);
                String dCodRes = extraerEtiqueta(gResProc, "dCodRes", "DESCONOCIDO");
                String dMsgRes = extraerEtiqueta(gResProc, "dMsgRes", "Sin mensaje");

                dte.setCodigoEstadoSifen(dCodRes);
                dte.setXmlRespuestaSifen(gResProc); // Guardamos la fraccion del acuse individual
                
                if ("0300".equals(dCodRes)) {
                    dte.setEstado(EstadoDte.APROBADO);
                    dte.setMensajeUsuario("DTE aprobado exitosamente: " + dMsgRes);
                } else {
                    dte.setEstado(EstadoDte.RECHAZADO);
                    dte.setMensajeSifen(dMsgRes);
                    dte.setMensajeUsuario("DTE rechazado (" + dCodRes + "): " + dMsgRes);
                }
                dteRepository.save(dte);
            } else {
                // SIFEN procesó el lote pero no incluyó este CDC en los resultados. Raro.
                dte.setEstado(EstadoDte.ERROR_ENVIO);
                dte.setMensajeUsuario("SIFEN no devolvió estado para este documento en la respuesta del lote.");
                dteRepository.save(dte);
            }
        }
    }

    private String extraerEtiqueta(String xml, String etiqueta, String valorPorDefecto) {
        if (xml == null || xml.isBlank()) return valorPorDefecto;
        Pattern pattern = Pattern.compile("<(?:.*?:)?" + etiqueta + ">(.*?)</(?:.*?:)?" + etiqueta + ">", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return valorPorDefecto;
    }
}
