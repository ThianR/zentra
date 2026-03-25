# Plan maestro de implementación para una plataforma SaaS de facturación electrónica SIFEN en Paraguay

## 1. Propósito del documento

Este documento define un plan integral para diseñar, construir, validar y lanzar una plataforma de facturación electrónica para Paraguay basada en SIFEN, con enfoque inicial en un producto SaaS middleware de integración universal. El sistema debe permitir conectar ERPs, POS, sistemas legacy, bases de datos y cargas manuales con los servicios oficiales de SIFEN, resolviendo generación XML, firma digital, transmisión, consulta de estado, KuDE, auditoría y operación multiempresa.

El plan se organiza en tres niveles de evolución:

- Plan A: MVP comercial rápido.
- Plan B: plataforma SaaS escalable y diferenciada.
- Plan C: edición enterprise, white-label y on-premise.

Además, el documento incluye:

- stack recomendado;
- arquitectura por capas;
- plan detallado de implementación;
- plan de control de calidad y pruebas;
- criterios de conclusión y cierre por etapa;
- criterios de madurez para avanzar de Plan A a Plan B y de Plan B a Plan C.

---

## 2. Conclusión base y punto de partida recomendado

La estrategia inicial recomendada es construir un modular monolith con límites de dominio claros, preparado para evolucionar luego a arquitectura distribuida solo donde el negocio o la carga lo exijan.

### 2.1 Stack recomendado de arranque

#### Backend
- Java 21 o 25 LTS
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring Batch para tareas masivas y cargas
- Quartz Scheduler para tareas programadas
- Apache CXF o JAX-WS para integración SOAP
- Apache Santuario para XMLDSig
- JAXB / Jakarta XML Binding para construcción XML
- Resilience4j para retry, circuit breaker y rate control

#### Frontend
- React 18
- TypeScript
- Vite
- Tailwind CSS
- shadcn/ui
- React Query
- React Hook Form
- Zod

#### Persistencia y soporte
- PostgreSQL como base principal
- Redis para idempotencia, caché, locks y soporte operativo
- RabbitMQ para mensajería asíncrona inicial
- MinIO o S3 compatible para almacenamiento de XML, PDF, KuDE y evidencias

#### DevOps y observabilidad
- Docker
- Docker Compose para arranque inicial
- GitHub Actions
- Nginx
- Prometheus
- Grafana
- Loki o ELK
- OpenTelemetry

### 2.2 Principio arquitectónico inicial

No empezar con microservicios completos. Empezar con un monolito modular con separación estricta de responsabilidades:

- modulo-auth
- modulo-tenant
- modulo-clientes
- modulo-certificados
- modulo-fiscal-core
- modulo-xml
- modulo-firma
- modulo-sifen
- modulo-kude
- modulo-documentos
- modulo-eventos-dte
- modulo-integraciones
- modulo-auditoria
- modulo-monitoring
- modulo-backoffice
- modulo-onboarding

### 2.3 Razón estratégica de este enfoque

Este enfoque permite:

- reducir complejidad inicial;
- acelerar salida comercial;
- soportar integración SOAP/XML con mayor robustez;
- mantener control fuerte de compliance técnico;
- introducir colas y procesos async de forma ordenada;
- evolucionar a multi-tenant escalable sin rehacer el core.

### 2.4 Estado Actual de Implementación (Hito MVP v1.5.0)

Actualmente el proyecto se encuentra consolidando las **Fases A3 y A6**, con incursiones operativas en la **Fase A4**. 
Se han introducido las siguientes determinaciones técnicas ausentes en el diseño original pero vitales para el ecosistema paraguayo:

- **SIFEN v150 estricto**: Todo el modelo de dominio core (`modulo-xml` y `modulo-sifen`) opera nativamente bajo el namespace y validaciones del Manual Técnico v1.5.0.
- **Micro-caché de Referencias Fiscales**: Se implementó el `SifenReferenciaService` para la gestión dinámica de los catálogos de la SET (departamentos, ciudades, monedas, tipos de operación) alimentando al frontend directamente e impidiendo rechazos por datos maestros obsoletos.
- **Soporte KuDE Multi-Formato**: El generador soporta renderizado adaptativo para A4 y Ticket Térmico mediante Apache FOP desde la Fase A.
- **Espectro de DTEs prioritarios**: Se adelantó la compatibilidad de generación XML para Factura, Autofactura, Nota de Crédito, Nota de Débito y Remisión, cubriendo el 95% de las necesidades del mercado local en el MVP.

---

## 3. Principios de diseño obligatorios

1. Compliance first.
2. Idempotencia estricta.
3. Auditabilidad total.
4. Evolución por capas.
5. Feature flags por versión técnica.
6. Seguridad por diseño.
7. Observabilidad desde el inicio.
8. Automatización de pruebas desde la etapa 1.

---

## 4. Arquitectura funcional por capas

### 4.1 Capa de experiencia y operación
Responsable de portales, paneles, onboarding, administración y experiencia operativa.

### 4.2 Capa de APIs y exposición
Responsable de APIs REST, autenticación, rate limiting, webhooks y contratos externos.

### 4.3 Capa de aplicación
Coordina casos de uso, orquestación, validaciones de negocio y reglas transaccionales.

### 4.4 Capa de dominio fiscal
Contiene el modelo canónico de DTE, reglas tributarias, eventos y estados.

### 4.5 Capa de integración SIFEN
Implementa SOAP, mTLS, envío sync/async, consultas, recepción de respuestas y control de reintentos.

### 4.6 Capa de seguridad criptográfica
Administra certificados, firma XML, vigencia, revocación y cadena de confianza.

### 4.7 Capa documental
Genera XML, KuDE, QR, almacenamiento documental y recuperación de evidencias.

### 4.8 Capa de integración externa
Conecta ERPs, POS, archivos, bases de datos, ETL, conectores y transformaciones.

### 4.9 Capa de datos y auditoría
Persistencia transaccional, trazas, retención, evidencias, histórico y bitácoras.

### 4.10 Capa de mensajería y procesamiento asíncrono
Orquesta lotes, reintentos, consultas diferidas, colas y desacoplamiento.

### 4.11 Capa de observabilidad y soporte
Métricas, logs, trazas, alertas, dashboards operativos y runbooks.

### 4.12 Capa de plataforma y DevOps
CI/CD, infraestructura, secretos, backups, despliegues, ambientes y seguridad operativa.

---

# 5. Plan A: MVP comercial rápido

## 5.1 Objetivo del Plan A

Salir al mercado con una solución vendible y operativa para clientes que necesitan emitir DTE, firmarlos, transmitirlos, consultar estados, generar KuDE y operar con trazabilidad suficiente.

## 5.2 Alcance funcional del Plan A

Incluye:

- multiempresa básico;
- emisión de factura electrónica;
- notas de crédito electrónica y débito electrónica prioritarias;
- notas de remision electrónica
- autofacturas electrónica
- factura Electrónica de Exportación
- generación XML;
- firma digital;
- envío a SIFEN;
- consulta de estado;
- KuDE PDF;
- almacenamiento documental;
- panel operativo base;
- API REST de integración;
- auditoría básica;
- observabilidad mínima;
- onboarding técnico inicial.

No incluye aún:

- mapeador visual no-code avanzado;
- marketplace de conectores;
- edition on-premise robusta;
- analítica avanzada;
- high availability multi-región;
- HSM dedicado.

## 5.3 Fases del Plan A

### Fase A1. Descubrimiento regulatorio, modelo operativo y arquitectura base

#### Plan de implementación
- Consolidar repositorio interno de requisitos: manual técnico, XSD, notas técnicas, guías, reglas de obligatoriedad y checklist legal.
- Crear matriz de trazabilidad requisito -> módulo -> caso de prueba -> evidencia.
- Definir tipos de DTE que entran al MVP.
- Diseñar modelo canónico interno de documento tributario.
- Definir estados internos del ciclo de vida del DTE.
- Diseñar arquitectura del modular monolith.
- Definir estrategia de versionado técnico y feature flags.
- Diseñar políticas de retención documental, auditoría y soporte.
- Elaborar mapa de riesgos y mitigaciones iniciales.

#### Plan de control de calidad y pruebas
- Revisión cruzada funcional, técnica y operativa de requisitos.
- Validación de consistencia de la matriz de trazabilidad.
- Revisión de arquitectura con criterios de extensibilidad, seguridad y observabilidad.
- Revisión de amenazas de seguridad sobre certificados, secretos y evidencias.
- Validación de casos borde: reintentos, duplicidades, errores de schema, certificado vencido, caída de SIFEN.

#### Plan de conclusión
La fase se considera cerrada si existe:
- backlog priorizado aprobado;
- arquitectura documentada;
- matriz de trazabilidad completa;
- definición de alcance del MVP;
- catálogo de riesgos y controles;
- definición de criterios de aceptación por módulo.

### Fase A2. Núcleo de seguridad criptográfica y certificados

#### Plan de implementación
- Implementar almacén seguro de certificados.
- Construir servicio de registro y asociación de certificado por empresa y ambiente.
- Implementar validación de vigencia, cadena y estado de revocación.
- Construir servicio de firma XML desacoplado.
- Implementar trazabilidad de operación de firma.
- Construir alertas de vencimiento y panel de salud de certificados.
- Definir estrategia de rotación y renovación.

#### Plan de control de calidad y pruebas
- Pruebas unitarias de carga de certificados.
- Pruebas de firma sobre XML válidos e inválidos.
- Pruebas con certificado vencido.
- Pruebas con certificado revocado o inválido.
- Pruebas de concurrencia para múltiples firmas simultáneas.
- Pruebas de seguridad de acceso a secretos.
- Pruebas de auditoría para asegurar registro de cada firma.

#### Plan de conclusión
La fase se cierra cuando:
- el sistema firma XML válidamente;
- detecta certificados inválidos antes del envío;
- genera trazas completas de firma;
- emite alertas de vigencia;
- no existen secretos expuestos en logs ni configuración.

### Fase A3. Modelo fiscal core, generación XML y KuDE

#### Plan de implementación
- Construir modelo canónico del DTE.
- Implementar validaciones de negocio previas a generación.
- Implementar generador XML conforme a estructura requerida.
- Validar XML contra esquemas internos y externos configurables.
- Implementar generación de CDC y correlación interna.
- Implementar generador de QR y KuDE PDF.
- Asociar XML, KuDE y metadatos en almacenamiento documental.
- Diseñar estados internos: creado, validado, firmado, enviado, aprobado, observado, rechazado, reenviado, anulado, contingencia.

#### Plan de control de calidad y pruebas
- Pruebas unitarias del modelo fiscal.
- Pruebas parametrizadas por tipo de documento.
- Validaciones contra schema.
- Pruebas de consistencia entre datos de entrada, XML y KuDE.
- Pruebas de encoding, caracteres especiales y redondeos.
- Pruebas de generación masiva.
- Pruebas de regresión sobre XML previos.

#### Plan de conclusión
La fase se cierra cuando:
- el XML generado es consistente con el modelo interno;
- el KuDE refleja exactamente la información emitida;
- el CDC y los identificadores internos son trazables;
- las reglas mínimas de validación previa funcionan antes de llegar a SIFEN.

### Fase A4. Integración SIFEN sync y async

#### Plan de implementación
- Implementar cliente SOAP con mTLS por ambiente.
- Parametrizar endpoints, timeouts y certificados.
- Implementar envío sincrónico.
- Implementar consulta por CDC.
- Implementar envío asíncrono por lotes.
- Implementar consulta de lote.
- Implementar parser de respuestas y normalización a estados internos.
- Implementar idempotencia estricta por CDC y correlación interna.
- Implementar retry con backoff y circuit breaker.
- Definir runbooks para errores frecuentes.

#### Plan de control de calidad y pruebas
- Pruebas unitarias del parser SOAP.
- Pruebas de integración en ambiente de test.
- Pruebas de timeout, indisponibilidad y respuesta parcial.
- Pruebas de duplicidad y reenvío controlado.
- Pruebas de lotes con tamaño mínimo y máximo soportado.
- Pruebas de consulta diferida.
- Pruebas de bloqueo por comportamiento agresivo evitado por el sistema.

#### Plan de conclusión
La fase se cierra cuando:
- el sistema transmite correctamente en ambiente de test;
- interpreta respuestas sin pérdida de estado;
- evita reenvíos duplicados no controlados;
- puede recuperar el estado de un documento o lote;
- deja evidencia completa de cada transacción técnica.

### Fase A5. Persistencia, auditoría y almacenamiento documental

#### Plan de implementación
- Diseñar base de datos operacional.
- Persistir DTE, estados, eventos, intentos, respuestas técnicas y referencias cruzadas.
- Implementar almacenamiento de XML y KuDE.
- Crear bitácora de auditoría funcional y técnica.
- Definir políticas de retención y recuperación.
- Implementar trazabilidad por empresa, punto, establecimiento y usuario.

#### Plan de control de calidad y pruebas
- Pruebas de integridad referencial.
- Pruebas de consistencia transaccional.
- Pruebas de restauración y recuperación documental.
- Pruebas de búsqueda por múltiples criterios.
- Pruebas de auditoría sobre operaciones críticas.
- Pruebas de volumen sobre tablas de eventos y logs.

#### Plan de conclusión
La fase se cierra cuando:
- existe trazabilidad completa del ciclo de vida;
- XML y KuDE se recuperan sin pérdida;
- auditoría funcional y técnica es consultable;
- la persistencia soporta operaciones reales de pilotaje.

### Fase A6. Portal operativo y API de integración

#### Plan de implementación
- Desarrollar autenticación y autorización base.
- Crear portal para operación diaria: listado de DTE, filtros, detalle, estados, descarga XML/PDF, reintentos controlados.
- Crear pantalla de certificados y salud operacional.
- Crear onboarding básico por empresa.
- Diseñar API REST para crear DTE, consultar estados y recuperar documentos.
- Implementar documentación de API y ejemplos.
- Incorporar webhooks opcionales para notificación de estados.

#### Plan de control de calidad y pruebas
- Pruebas unitarias frontend y backend.
- Pruebas de integración API.
- Pruebas de UX operativa con escenarios reales.
- Pruebas de roles y permisos.
- Pruebas de seguridad API: auth, rate limit, validación de payload.
- Pruebas de accesibilidad básica.
- Pruebas de descarga documental y trazabilidad visual.

#### Plan de conclusión
La fase se cierra cuando:
- un usuario puede operar el ciclo principal sin asistencia técnica constante;
- la API permite integrar al menos un sistema externo real;
- los errores son entendibles y accionables;
- la consola operativa refleja la verdad transaccional del backend.

### Fase A7. Suite oficial, pruebas end-to-end y piloto

#### Plan de implementación
- Preparar juego de datos de pruebas.
- Automatizar escenarios críticos end-to-end.
- Ejecutar set de validaciones sobre ambiente de test.
- Documentar evidencias por caso.
- Preparar checklist de salida a piloto.
- Integrar 2 a 3 clientes piloto de perfiles distintos.
- Implementar monitoreo, soporte y feedback loop.

#### Plan de control de calidad y pruebas
- Pruebas E2E completas.
- Pruebas oficiales mínimas requeridas.
- Pruebas de volumen operativo real controlado.
- Pruebas de incidentes: caída temporal, certificado vencido, duplicado, documento rechazado, lote incompleto.
- UAT con clientes piloto.
- Pruebas de soporte operativo: runbooks, tiempos de respuesta y corrección.

#### Plan de conclusión
La fase se cierra cuando:
- se cumplieron los casos de prueba definidos;
- existen evidencias trazables por caso;
- al menos 2 pilotos operan correctamente;
- se estabiliza el flujo emitir -> aprobar -> consultar -> descargar;
- existe checklist de salida a producción aprobado.

### Fase A8. Lanzamiento MVP

#### Plan de implementación
- Congelar versión release candidate.
- Ejecutar hardening de seguridad y performance.
- Implementar backups, monitoreo y alertas.
- Definir SLA inicial, canales de soporte y clasificación de incidentes.
- Preparar documentación operativa, comercial y técnica.
- Habilitar onboarding comercial controlado.

#### Plan de control de calidad y pruebas
- Smoke tests en producción.
- Validación de despliegue y rollback.
- Pruebas de observabilidad y alertas.
- Pruebas de backup y restauración.
- Pruebas de performance base y carga inicial.
- Revisión de seguridad previa al go-live.

#### Plan de conclusión
El Plan A se considera cerrado cuando:
- el MVP está en producción estable;
- existe emisión real con soporte básico;
- la tasa de fallas críticas es baja y controlada;
- la operación tiene dashboards, alertas y runbooks;
- existe capacidad real de vender e implementar clientes.

---

# 6. Plan B: plataforma SaaS escalable y diferenciada

## 6.1 Objetivo del Plan B

Escalar desde el MVP hacia una plataforma multi-tenant más madura, con mejor onboarding, conectores, observabilidad y diferenciación competitiva.

## 6.2 Alcance adicional del Plan B

- multi-tenant robusto;
- gestión avanzada de usuarios, empresas, sucursales y puntos;
- conectores prioritarios ERP/POS;
- importación por archivos y ETL;
- webhooks y callbacks avanzados;
- analítica operativa;
- observabilidad avanzada;
- soporte a más tipos documentales y eventos;
- portal de onboarding guiado;
- compliance calendar;
- SLA y soporte mejorado.

## 6.3 Fases del Plan B

### Fase B1. Reestructuración a SaaS multi-tenant robusto

#### Plan de implementación
- Separar tenant context en todas las capas.
- Diseñar aislamiento lógico por tenant.
- Implementar configuración por empresa: certificados, endpoints, reglas, branding básico de documentos, retención, notificaciones.
- Refactorizar auditoría con tenant-awareness.
- Implementar RBAC granular.
- Incorporar cuotas, límites y consumo por plan.

#### Plan de control de calidad y pruebas
- Pruebas de aislamiento entre tenants.
- Pruebas de fuga de datos.
- Pruebas de permisos granulares.
- Pruebas de configuración independiente por tenant.
- Pruebas de concurrencia multiempresa.

#### Plan de conclusión
Se cierra cuando:
- existe separación efectiva de datos y configuración;
- usuarios no acceden a información fuera de su tenant;
- la plataforma soporta varios clientes en paralelo sin inconsistencias.

### Fase B2. Capa de integración universal

#### Plan de implementación
- Diseñar adaptadores de entrada estandarizados.
- Incorporar conectores prioritarios para REST, CSV, Excel, JSON batch, DB polling y JDBC/ODBC controlado.
- Construir pipeline de transformación hacia modelo canónico.
- Implementar validadores previos por conector.
- Definir catálogo de conectores certificados.

#### Plan de control de calidad y pruebas
- Pruebas por conector.
- Pruebas de mapeo de campos.
- Pruebas con datos incompletos o sucios.
- Pruebas de tolerancia a errores y cola de rechazados.
- Pruebas de performance de carga.

#### Plan de conclusión
Se cierra cuando:
- existen al menos 3 mecanismos de integración operativos;
- el pipeline transforma correctamente al modelo canónico;
- los errores de datos son detectables y explicables.

### Fase B3. Portal de onboarding y compliance operativo

#### Plan de implementación
- Construir wizard de alta de empresa.
- Implementar checklist de prerequisitos: certificados, datos fiscales, puntos de expedición, establecimiento, pruebas, estado de habilitación.
- Crear compliance calendar por empresa.
- Crear panel de salud de emisión, certificados y documentos rechazados.
- Crear centro de ayuda contextual.

#### Plan de control de calidad y pruebas
- Pruebas de onboarding completo.
- Pruebas de validaciones guiadas.
- Pruebas de alertas y recordatorios.
- Pruebas de UX con usuarios no técnicos.
- Pruebas de localización y comprensión de mensajes.

#### Plan de conclusión
Se cierra cuando:
- una empresa puede autogestionar su alta en gran parte del proceso;
- el sistema previene errores frecuentes antes de emitir;
- existe reducción del tiempo de onboarding frente al Plan A.

### Fase B4. Mensajería, colas y operación escalable

#### Plan de implementación
- Reforzar diseño event-driven interno.
- Separar colas por criticidad: emisión, consulta, reintentos, notificaciones, generación documental.
- Implementar dead-letter queues.
- Implementar replay controlado.
- Incorporar jobs de reconciliación y compensación.
- Definir prioridades operativas por tipo de tenant o plan.

#### Plan de control de calidad y pruebas
- Pruebas de carga sostenida.
- Pruebas de recuperación ante caída de workers.
- Pruebas de mensajes duplicados.
- Pruebas de orden y consistencia eventual.
- Pruebas de dead-letter y reproceso.

#### Plan de conclusión
Se cierra cuando:
- la cola absorbe variaciones de carga sin pérdida;
- los reprocesos son controlados;
- la reconciliación detecta estados pendientes o inconsistentes.

### Fase B5. Analítica, soporte y observabilidad avanzada

#### Plan de implementación
- Crear dashboards por tenant y por operación global.
- Medir KPI técnicos y de negocio.
- Incorporar trazas distribuidas internas si aplica.
- Implementar clasificación de incidentes y panel de soporte.
- Crear reportes de rechazos, observaciones, latencia y volumen.

#### Plan de control de calidad y pruebas
- Pruebas de exactitud de métricas.
- Pruebas de alertas con umbrales reales.
- Pruebas de correlación logs -> métricas -> trazas.
- Pruebas de soporte con simulación de incidentes.

#### Plan de conclusión
Se cierra cuando:
- existe visibilidad operativa en tiempo real;
- los incidentes se detectan antes de escalar;
- soporte y producto pueden analizar causas con datos suficientes.

### Fase B6. Expansión funcional y endurecimiento del producto

#### Plan de implementación
- Agregar eventos fiscales relevantes posteriores al MVP.
- Extender tipos de DTE según mercado objetivo.
- Mejorar reglas de validación previa.
- Incorporar versionado técnico con pruebas por versión.
- Endurecer seguridad, auditoría y retención.

#### Plan de control de calidad y pruebas
- Pruebas regresivas masivas.
- Pruebas de compatibilidad por versión técnica.
- Pruebas de compliance por nuevos tipos documentales.
- Pruebas de retención y consulta histórica.

#### Plan de conclusión
El Plan B se considera cerrado cuando:
- la plataforma soporta múltiples tenants con operación estable;
- existen conectores reutilizables;
- onboarding y soporte son más escalables;
- las métricas permiten gestionar crecimiento;
- el producto ya tiene diferenciadores reales frente a soluciones básicas.

---

# 7. Plan C: edición enterprise, white-label y on-premise

## 7.1 Objetivo del Plan C

Entrar a cuentas de mayor ticket con requerimientos de seguridad, despliegue y operación especializados.

## 7.2 Alcance adicional del Plan C

- single-tenant dedicado o despliegue on-premise;
- white-label;
- HSM o integración PKCS#11;
- SSO empresarial;
- auditoría ampliada;
- compliance avanzado;
- DR formal;
- HA;
- soporte premium y SLA empresariales;
- políticas avanzadas de retención y custodia.

## 7.3 Fases del Plan C

### Fase C1. Edition single-tenant o dedicada

#### Plan de implementación
- Permitir despliegue aislado por cliente.
- Parametrizar branding, dominios, certificados, políticas y almacenamiento.
- Definir perfil de configuración enterprise.
- Preparar scripts e infraestructura reproducible.

#### Plan de control de calidad y pruebas
- Pruebas de despliegue repetible.
- Pruebas de independencia de configuración.
- Pruebas de upgrade y mantenimiento por cliente.

#### Plan de conclusión
Se cierra cuando:
- el producto puede instalarse en modo dedicado sin modificaciones mayores;
- configuración y operación son reproducibles por plantilla.

### Fase C2. Seguridad empresarial avanzada

#### Plan de implementación
- Integrar SSO/SAML/OIDC.
- Soportar HSM o proveedores compatibles PKCS#11.
- Implementar segregación avanzada de funciones.
- Endurecer manejo de secretos y rotación.
- Agregar auditoría de administración y acceso sensible.

#### Plan de control de calidad y pruebas
- Pentesting.
- Pruebas de hardening.
- Pruebas de acceso privilegiado.
- Pruebas de firma mediante HSM.
- Pruebas de rotación de secretos.

#### Plan de conclusión
Se cierra cuando:
- se cumplen controles de seguridad corporativos definidos;
- la firma y autenticación cumplen con el estándar enterprise acordado.

### Fase C3. Alta disponibilidad, DR y continuidad operativa

#### Plan de implementación
- Diseñar RPO y RTO objetivo.
- Incorporar backups automatizados y réplica según necesidad.
- Implementar plan de recuperación ante desastre.
- Diseñar despliegue HA cuando aplique.
- Definir ejercicios operativos de contingencia.

#### Plan de control de calidad y pruebas
- Simulación de caída de nodo.
- Simulación de restauración completa.
- Pruebas de failover.
- Pruebas de integridad post-recuperación.
- Game days operativos.

#### Plan de conclusión
Se cierra cuando:
- DR está probado con evidencia;
- recuperación cumple objetivos definidos;
- el cliente enterprise acepta resultados de continuidad.

### Fase C4. White-label y operación para partners

#### Plan de implementación
- Desacoplar elementos de marca.
- Crear configuración por reseller o partner.
- Parametrizar comunicación, portal, documentación y branding documental.
- Diseñar esquema de soporte por niveles.

#### Plan de control de calidad y pruebas
- Pruebas de branding completo.
- Pruebas de aislamiento de partners.
- Pruebas de operación y soporte multi-marca.

#### Plan de conclusión
Se cierra cuando:
- un partner puede comercializar una edición propia sin comprometer el core;
- existe operación consistente multi-marca.

### Fase C5. Gobierno enterprise y cumplimiento ampliado

#### Plan de implementación
- Crear trust center y documentación formal de seguridad y operación.
- Crear políticas de retención, auditoría, acceso y soporte premium.
- Diseñar tablero de cumplimiento y evidencias.
- Definir paquetes comerciales enterprise.

#### Plan de control de calidad y pruebas
- Auditoría documental interna.
- Revisión legal y operativa.
- Simulación de proceso de due diligence de cliente enterprise.

#### Plan de conclusión
El Plan C se considera cerrado cuando:
- el producto puede venderse a clientes enterprise con requisitos altos;
- seguridad, despliegue, continuidad y auditoría están formalizados;
- existe base para contratos premium, white-label y on-premise.

---

# 8. Plan detallado por capas

## 8.1 Capa de experiencia y operación

### Implementación
- Portal administrativo.
- Dashboard operativo.
- Gestión de usuarios, roles, empresas y puntos.
- Wizard de onboarding.
- Búsqueda avanzada de DTE.
- Descarga XML/PDF.
- Vista de trazabilidad y errores.
- Centro de ayuda contextual.

### Control de calidad y pruebas
- pruebas de UX con usuarios reales;
- pruebas de accesibilidad;
- pruebas de consistencia entre backend y UI;
- pruebas de mensajes de error;
- pruebas de permisos por rol;
- pruebas responsive básicas.

### Conclusión
La capa se cierra cuando el usuario operativo puede entender qué pasó con un DTE, qué debe hacer y cómo actuar sin depender siempre del equipo técnico.

## 8.2 Capa de APIs y exposición

### Implementación
- API REST pública y privada.
- autenticación por token.
- control de cuota y rate limit.
- documentación OpenAPI.
- webhooks.
- versionado de API.

### Control de calidad y pruebas
- pruebas contractuales;
- pruebas de seguridad;
- pruebas de compatibilidad hacia atrás;
- pruebas de carga;
- pruebas de idempotencia.

### Conclusión
La capa se cierra cuando terceros pueden integrar el sistema con contratos estables, errores comprensibles y seguridad suficiente.

## 8.3 Capa de aplicación

### Implementación
- casos de uso emitir, consultar, reenviar, descargar, anular y conciliar;
- orquestación entre dominio, firma, integración y persistencia;
- validaciones transaccionales;
- políticas de reintento.

### Control de calidad y pruebas
- pruebas unitarias de casos de uso;
- pruebas de integración entre módulos;
- pruebas de rollback y consistencia;
- pruebas de escenarios felices y fallidos.

### Conclusión
La capa se cierra cuando los flujos de negocio funcionan de forma determinista y trazable.

## 8.4 Capa de dominio fiscal

### Implementación
- modelo canónico DTE;
- estados del documento;
- reglas tributarias del alcance definido;
- eventos fiscales;
- validaciones de negocio previas.

### Control de calidad y pruebas
- pruebas unitarias por regla;
- pruebas parametrizadas;
- pruebas de regresión de reglas;
- revisión funcional con negocio.

### Conclusión
La capa se cierra cuando el sistema expresa correctamente el negocio fiscal interno y no depende del formato externo para entender el documento.

## 8.5 Capa de integración SIFEN

### Implementación
- clientes SOAP;
- mTLS;
- envío sync y async;
- consulta de estado y lotes;
- parsing de respuestas;
- correlación técnica;
- control de reintentos.

### Control de calidad y pruebas
- pruebas contra ambiente de test;
- pruebas de resiliencia;
- pruebas de duplicados;
- pruebas de timeout;
- pruebas con respuestas inesperadas.

### Conclusión
La capa se cierra cuando el producto puede dialogar con SIFEN de manera confiable y recuperar el estado real del documento.

## 8.6 Capa de seguridad criptográfica

### Implementación
- gestión de certificados;
- firma digital;
- validación de vigencia;
- controles de acceso;
- alertas y rotación;
- posible evolución a HSM.

### Control de calidad y pruebas
- pruebas de firma y verificación;
- pruebas de expiración;
- pruebas de acceso indebido;
- revisión de hardening.

### Conclusión
La capa se cierra cuando la confianza criptográfica está resuelta y monitoreada.

## 8.7 Capa documental

### Implementación
- generación XML;
- generación KuDE;
- QR;
- almacenamiento documental;
- recuperación y versionado cuando aplique.

### Control de calidad y pruebas
- validación XML;
- comparación XML vs PDF;
- pruebas de almacenamiento;
- pruebas de descarga masiva.

### Conclusión
La capa se cierra cuando todo documento emitido tiene representación técnica y operativa consistente.

## 8.8 Capa de integración externa

### Implementación
- API input;
- carga batch;
- importación CSV/Excel;
- conectores DB;
- adaptadores POS/ERP;
- pipeline de transformación.

### Control de calidad y pruebas
- pruebas por formato;
- pruebas de datos sucios;
- pruebas de mapeo;
- pruebas de rechazos controlados;
- pruebas de volumen.

### Conclusión
La capa se cierra cuando los clientes pueden conectar sus fuentes sin rehacer sus sistemas principales.

## 8.9 Capa de datos y auditoría

### Implementación
- base operacional;
- tablas de DTE, estados, intentos, errores, eventos y auditoría;
- políticas de retención;
- consultas históricas;
- evidencias.

### Control de calidad y pruebas
- pruebas de integridad;
- pruebas de restauración;
- pruebas de volumen;
- pruebas de auditoría funcional y técnica.

### Conclusión
La capa se cierra cuando existe historia completa del documento y evidencia suficiente para soporte y cumplimiento.

## 8.10 Capa de mensajería y procesamiento asíncrono

### Implementación
- colas por proceso;
- workers;
- retry controlado;
- DLQ;
- reconciliación;
- prioridad operativa.

### Control de calidad y pruebas
- pruebas de carga;
- pruebas de duplicidad;
- pruebas de reproceso;
- pruebas de caída y recuperación.

### Conclusión
La capa se cierra cuando los flujos críticos no dependen de procesamiento síncrono continuo y pueden absorber variabilidad operacional.

## 8.11 Capa de observabilidad y soporte

### Implementación
- métricas;
- logs estructurados;
- trazas;
- alertas;
- dashboards;
- runbooks;
- panel de incidentes.

### Control de calidad y pruebas
- pruebas de alertas;
- pruebas de correlación;
- simulación de incidentes;
- revisión de utilidad real para soporte.

### Conclusión
La capa se cierra cuando se puede detectar, diagnosticar y accionar incidentes rápidamente.

## 8.12 Capa de plataforma y DevOps

### Implementación
- CI/CD;
- gestión de ambientes;
- infraestructura reproducible;
- secretos;
- backups;
- rollback;
- política de releases.

### Control de calidad y pruebas
- pruebas de pipeline;
- pruebas de despliegue;
- pruebas de rollback;
- pruebas de backup/restore;
- revisión de secretos y configuraciones.

### Conclusión
La capa se cierra cuando el sistema puede desplegarse y mantenerse de forma segura, repetible y controlada.

---

# 9. Plan maestro de calidad y pruebas

## 9.1 Estrategia general

La calidad no debe tratarse como etapa final. Debe acompañar todo el ciclo.

### Niveles de pruebas
- pruebas unitarias;
- pruebas de componente;
- pruebas de integración;
- pruebas contractuales;
- pruebas end-to-end;
- pruebas de regresión;
- pruebas de performance;
- pruebas de seguridad;
- pruebas de resiliencia;
- pruebas de aceptación de usuario;
- pruebas de compliance técnico.

## 9.2 Pirámide recomendada
- Base fuerte de pruebas unitarias y de reglas.
- Cobertura amplia de integración entre módulos.
- Pocos E2E pero muy representativos.
- Suite oficial y suite regresiva automatizada.

## 9.3 Ambientes mínimos
- local de desarrollo;
- integración interna;
- QA;
- staging o preproducción;
- test oficial;
- producción.

## 9.4 Datos de prueba
- juego base por tipo documental;
- casos nominales;
- casos con errores;
- lotes;
- escenarios de duplicidad;
- escenarios de certificado inválido;
- escenarios de indisponibilidad;
- escenarios de recuperación y reproceso.

## 9.5 Cobertura mínima exigida
- reglas críticas de dominio fiscal cubiertas por pruebas unitarias;
- flujos críticos cubiertos por integración;
- emisión completa y consulta cubiertas por E2E;
- suite regresiva ejecutable por pipeline.

## 9.6 Pruebas de seguridad obligatorias
- gestión de secretos;
- control de acceso;
- autorización por rol;
- validación de entradas;
- protección de endpoints;
- hardening de infraestructura;
- revisión de logs y datos sensibles;
- pentesting en etapas avanzadas.

## 9.7 Pruebas de resiliencia obligatorias
- caída temporal de SIFEN;
- caída de red;
- reintento seguro;
- mensajes duplicados;
- corrupción o inconsistencia de estado;
- expiración de certificado;
- worker caído;
- restauración desde backup.

## 9.8 Evidencias mínimas por release
- resultados de pruebas automatizadas;
- checklist de release;
- reporte de defectos abiertos y cerrados;
- evidencia de cumplimiento del criterio de salida;
- registro de riesgos conocidos;
- plan de rollback.

---

# 10. Criterios de cierre por etapa

## 10.1 Criterios generales
Una etapa no se considera cerrada si falta alguno de estos elementos:

- funcionalidad implementada según alcance;
- pruebas ejecutadas y aprobadas;
- defectos críticos cerrados;
- documentación mínima actualizada;
- observabilidad incorporada;
- criterios de aceptación firmados o aceptados por el responsable.

## 10.2 Criterios de salida de Plan A a Plan B
- MVP estable en producción;
- primeros clientes emitiendo;
- soporte operativo funcional;
- arquitectura suficientemente modular;
- backlog validado de conectores y multi-tenant avanzado.

## 10.3 Criterios de salida de Plan B a Plan C
- operación multi-tenant madura;
- onboarding escalable;
- conectores reutilizables en uso;
- métricas y soporte consolidados;
- oportunidades enterprise reales justificadas.

---

# 11. KPIs recomendados

## 11.1 KPIs técnicos
- tasa de aprobación por tipo de DTE;
- tiempo emitir -> aprobado;
- porcentaje de rechazos por causa;
- incidencia por certificados;
- latencia por integración externa;
- tasa de reintentos exitosos;
- backlog de documentos pendientes;
- disponibilidad de plataforma.

## 11.2 KPIs de negocio
- tiempo de onboarding por cliente;
- tiempo hasta primera factura aprobada;
- cantidad de DTE por cliente;
- porcentaje de clientes activos;
- churn mensual;
- adopción por tipo de conector;
- margen por plan o segmento.

## 11.3 KPIs operativos
- volumen de incidentes por severidad;
- MTTR;
- porcentaje de tickets resueltos en SLA;
- porcentaje de errores prevenidos antes del envío.

---

# 12. Orden recomendado real de ejecución

1. Descubrimiento regulatorio y arquitectura.
2. Seguridad criptográfica y certificados.
3. Modelo fiscal core.
4. XML, KuDE y almacenamiento documental.
5. Integración SIFEN sync.
6. Integración async y consultas.
7. Persistencia y auditoría completa.
8. Portal operativo y API pública.
9. Suite oficial, E2E y piloto.
10. Lanzamiento MVP.
11. Multi-tenant reforzado.
12. Conectores y onboarding avanzado.
13. Observabilidad, analítica y expansión.
14. Edition enterprise, HA, DR y white-label.

---

# 13. Recomendación final de gobierno del proyecto

## 13.1 Equipo mínimo para Plan A
- 1 líder técnico / arquitecto
- 2 backend
- 1 frontend
- 1 QA con automatización
- 1 DevOps compartido
- 1 product owner o responsable funcional

## 13.2 Equipo recomendado para Plan B
- 1 líder técnico
- 3 backend
- 2 frontend
- 1 QA automation
- 1 QA funcional
- 1 DevOps/SRE
- 1 product owner
- 1 analista de implementación

## 13.3 Equipo recomendado para Plan C
- 1 arquitecto principal
- 3 a 4 backend
- 2 frontend
- 1 security specialist
- 1 QA automation
- 1 QA funcional
- 1 DevOps/SRE
- 1 implementation lead
- 1 product manager

---

# 14. Cierre ejecutivo

La ruta correcta no es construir primero una solución compleja enterprise, sino consolidar un core fiscal robusto, auditable, integrable y operable. El valor principal del producto estará en:

- permitir integración sin reemplazar el sistema del cliente;
- reducir complejidad técnica del cumplimiento SIFEN;
- ofrecer trazabilidad y operación real, no solo emisión;
- escalar desde un MVP vendible hasta una plataforma enterprise.

La secuencia recomendada es:

- Plan A para validar producto y operación;
- Plan B para escalar y diferenciar;
- Plan C para capturar cuentas de alto valor.

Ese camino maximiza velocidad de salida, control técnico, sostenibilidad operativa y capacidad comercial.
