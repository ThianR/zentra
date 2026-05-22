package com.zentra.middleware.core.service;

import java.util.Map;

/**
 * Interfaz para proveer el diccionario dinámico de SIFEN desde otros módulos.
 */
public interface SifenDictionaryProvider {
    Map<String, String> getDictionary();
}
