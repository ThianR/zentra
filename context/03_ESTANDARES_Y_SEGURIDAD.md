# Estandares de Desarrollo y Seguridad

## 1. Reglas de Codificacion
- No usar emojis ni caracteres especiales en comentarios o documentacion.
- Priorizar simplicidad: Metodos cortos, responsabilidad unica.
- Paquetes: com.zentra.middleware.[modulo].

## 2. Manejo de Errores y Resiliencia
- Usar excepciones personalizadas.
- Circuit Breaker y Reintentos exponenciales para llamadas externas.

## 3. Seguridad y Auditoria
- Prohibido loguear datos sensibles.
- Auditoria obligatoria de cambios de estado.
- Idempotencia estricta por CDC.

## 4. Gestion de Archivos Temporales y de Diagnostico
- Todo archivo generado en tiempo de ejecucion (debug, diagnostico, dump, log estructurado)
  debe escribirse EXCLUSIVAMENTE en el directorio `temp/` ubicado en la raiz del proyecto si no existe directorio temp se debe creaar.
- Prohibido escribir archivos temporales en el directorio de trabajo actual, en directorios
  de modulos (`modulo-*/`) o en cualquier otra ubicacion ad-hoc.
- El directorio `temp/` debe estar excluido del control de versiones (`.gitignore`)
  conservando unicamente el archivo marcador `temp/.gitkeep`.
- Toda clase que genere archivos de diagnostico debe declarar la constante:
    private static final String DIRECTORIO_TEMP = "temp";
  y usar `Files.createDirectories(...)` antes de escribir para garantizar que exista.
- Convencion de nombres para archivos en temp/:
    DEBUG_[descripcion].xml     -> dumps de XML/SOAP
    DEBUG_[descripcion].log     -> trazas de ejecucion puntuales
    [descripcion]_[timestamp].* -> archivos con rotacion temporal

