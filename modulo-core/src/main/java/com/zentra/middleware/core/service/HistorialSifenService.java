package com.zentra.middleware.core.service;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.HistorialSifen;
import com.zentra.middleware.core.repository.HistorialSifenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.logging.Logger;

@Service
public class HistorialSifenService {

    private static final Logger logger = Logger.getLogger(HistorialSifenService.class.getName());
    private final HistorialSifenRepository historialRepository;

    public HistorialSifenService(HistorialSifenRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Transactional
    public void registrar(DocumentoElectronico dte, String operacion) {
        try {
            if (dte == null || dte.getId() == null) return;
            
            HistorialSifen historial = new HistorialSifen();
            historial.setDocumento(dte);
            historial.setOperacion(operacion);
            historial.setCodigoEstado(dte.getCodigoEstadoSifen());
            historial.setMensajeRespuesta(dte.getMensajeSifen());
            historial.setXmlRespuesta(dte.getXmlRespuestaSifen());
            
            historialRepository.save(historial);
            logger.info("Registrado en bitácora SIFEN. Operación: " + operacion + " | Doc: " + dte.getId());
        } catch (Exception e) {
            logger.severe("Error al registrar historial SIFEN: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<HistorialSifen> obtenerHistorial(String documentoId) {
        return historialRepository.findByDocumentoIdOrderByFechaRegistroDesc(documentoId);
    }
}
