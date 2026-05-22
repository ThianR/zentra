package com.zentra.middleware.core.service;

import com.zentra.middleware.core.model.SifenDiccionarioError;
import com.zentra.middleware.core.repository.SifenDiccionarioErrorRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SifenDiccionarioService implements SifenDictionaryProvider {

    private static final Logger logger = LoggerFactory.getLogger(SifenDiccionarioService.class);

    private final SifenDiccionarioErrorRepository repositorio;
    private final Map<String, String> cacheDiccionario = new ConcurrentHashMap<>();

    public SifenDiccionarioService(SifenDiccionarioErrorRepository repositorio) {
        this.repositorio = repositorio;
    }

    @PostConstruct
    public void init() {
        recargarDiccionario();
    }

    /**
     * Tarea programada para recargar el diccionario cada 15 minutos.
     */
    @Scheduled(fixedRate = 900000)
    public void recargarDiccionario() {
        logger.debug("Recargando diccionario de errores SIFEN desde la base de datos...");
        try {
            List<SifenDiccionarioError> registros = repositorio.findByActivoTrue();
            Map<String, String> nuevoCache = new ConcurrentHashMap<>();
            
            for (SifenDiccionarioError error : registros) {
                nuevoCache.put(error.getCodigoSifen(), error.getEtiquetaHumana());
            }
            
            cacheDiccionario.clear();
            cacheDiccionario.putAll(nuevoCache);
            logger.debug("Diccionario de errores SIFEN recargado. Total de registros: {}", cacheDiccionario.size());
        } catch (Exception e) {
            logger.error("Error al recargar el diccionario de errores SIFEN: {}", e.getMessage());
        }
    }

    /**
     * Obtiene el diccionario en memoria.
     * @return Mapa con la traducción de codigoSifen a etiquetaHumana.
     */
    @Override
    public Map<String, String> getDictionary() {
        return cacheDiccionario;
    }

    /**
     * Obtiene una etiqueta específica, o null si no existe.
     * @param codigoSifen El código técnico (ej. tdDirec).
     * @return La etiqueta humana o null.
     */
    public String getEtiqueta(String codigoSifen) {
        return cacheDiccionario.get(codigoSifen);
    }
}
