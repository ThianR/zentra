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
