-- =============================================================================
-- ZENTRA SIFEN Middleware - Esquema de Base de Datos Completo (PostgreSQL)
-- v1.5.0 compatible con SIFEN Paraguay
-- Archivo unificado e idempotente
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Tablas Base (Multi-Tenant y Autenticación)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    id                          VARCHAR(36)  PRIMARY KEY,
    nombre                      VARCHAR(255),
    identificador               VARCHAR(100) UNIQUE,
    activo                      BOOLEAN      DEFAULT TRUE,
    fecha_creacion              TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS empresas (
    id                          VARCHAR(36)  PRIMARY KEY,
    cliente_id                  VARCHAR(36)  REFERENCES clientes(id),
    ruc                         VARCHAR(20)  UNIQUE,
    dv                          VARCHAR(1),
    razon_social                VARCHAR(255),
    tipo_contribuyente          INTEGER,
    cod_establecimiento         VARCHAR(3),
    punto_expedicion            VARCHAR(3),
    timbrado                    VARCHAR(8),
    fecha_inicio_timbrado       DATE,
    fecha_vencimiento_timbrado  DATE,
    direccion                   VARCHAR(500),
    numero_casa                 VARCHAR(10),
    cod_departamento            INTEGER,
    departamento                VARCHAR(100),
    cod_distrito                INTEGER,
    distrito                    VARCHAR(100),
    cod_ciudad                  INTEGER,
    ciudad                      VARCHAR(100),
    telefono                    VARCHAR(30),
    email                       VARCHAR(150),
    cod_actividad_economica     VARCHAR(20),
    actividad_economica         VARCHAR(500),
    ruta_certificado            VARCHAR(500),
    password_certificado        VARCHAR(255),
    alias_certificado           VARCHAR(100),
    certificado_fisico          BYTEA,
    fecha_vencimiento_certificado DATE,
    ambiente                    INTEGER      DEFAULT 2,
    id_csc                      VARCHAR(10)  DEFAULT '0001',
    valor_csc                   VARCHAR(255),
    logo_base64                 TEXT,
    frecuencia_lote_minutos     INTEGER      DEFAULT 15,
    frecuencia_consulta_ticket_minutos INTEGER DEFAULT 5
);

CREATE TABLE IF NOT EXISTS usuarios (
    id                          VARCHAR(36)  PRIMARY KEY,
    username                    VARCHAR(100) UNIQUE NOT NULL,
    password_hash               VARCHAR(255),
    nombre_completo             VARCHAR(255),
    email                       VARCHAR(150),
    rol                         VARCHAR(50)  DEFAULT 'OPERADOR',
    activo                      BOOLEAN      DEFAULT TRUE,
    debe_cambiar_password       BOOLEAN      DEFAULT FALSE,
    cliente_id                  VARCHAR(36)  REFERENCES clientes(id),
    empresa_default_id          VARCHAR(36)  REFERENCES empresas(id),
    ultimo_acceso               TIMESTAMP,
    fecha_creacion              TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 2. Catálogos SIFEN y Datos Maestros
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sifen_referencia (
    id              BIGSERIAL    PRIMARY KEY,
    tipo            VARCHAR(60)  NOT NULL,
    codigo          VARCHAR(20)  NOT NULL,
    descripcion     VARCHAR(255) NOT NULL,
    padre_codigo    VARCHAR(20),
    valor_aux       VARCHAR(100),
    descripcion_aux VARCHAR(255),
    orden           INTEGER      NOT NULL DEFAULT 0,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS padron_ruc (
    ruc                         VARCHAR(20)  PRIMARY KEY,
    dv                          VARCHAR(1),
    razon_social                VARCHAR(255),
    estado                      VARCHAR(50)
);

-- -----------------------------------------------------------------------------
-- 3. Emisión de Documentos y Lotes
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS lotes_transmision (
    id                          VARCHAR(36)  PRIMARY KEY,
    numero_ticket               VARCHAR(50),
    estado                      VARCHAR(50)  DEFAULT 'PENDIENTE',
    fecha_envio                 TIMESTAMP,
    fecha_ultima_consulta       TIMESTAMP,
    intentos_consulta           INTEGER      DEFAULT 0,
    empresa_id                  VARCHAR(36)  REFERENCES empresas(id)
);

CREATE TABLE IF NOT EXISTS documentos_electronicos (
    id                          VARCHAR(36)  PRIMARY KEY,
    cdc                         VARCHAR(44)  UNIQUE,
    tipo_documento              VARCHAR(2),
    tipo_emision                INTEGER,
    tipo_operacion              INTEGER,
    tipo_receptor               INTEGER,
    tipo_transaccion            INTEGER,
    estado                      VARCHAR(20),
    ambiente                    INTEGER,
    emisor_id                   VARCHAR(36)  REFERENCES empresas(id),
    ruc_emisor                  VARCHAR(20),
    dv_emisor                   VARCHAR(1),
    razon_social_emisor         VARCHAR(255),
    actividad_economica_emisor  VARCHAR(500),
    timbrado                    VARCHAR(8),
    numero_comprobante          VARCHAR(20),
    numero_ticket_lote          VARCHAR(20),
    codigo_seguridad            VARCHAR(9),
    codigo_estado_sifen         VARCHAR(10),
    indicador_presencia         INTEGER,
    motivo_emision              INTEGER,
    naturaleza_vendedor         INTEGER,
    condicion_operacion         INTEGER,
    formato_kude                INTEGER,
    direccion_emisor            VARCHAR(500),
    telefono_emisor             VARCHAR(30),
    ruc_receptor                VARCHAR(20),
    receptor_razon_social       VARCHAR(255),
    receptor_email              VARCHAR(150),
    receptor_telefono           VARCHAR(30),
    receptor_direccion          VARCHAR(500),
    c_pais_receptor             VARCHAR(3),
    total_operacion             NUMERIC(18,2),
    total_gravada10             NUMERIC(18,2),
    total_gravada5              NUMERIC(18,2),
    total_exenta                NUMERIC(18,2),
    total_iva                   NUMERIC(18,2),
    total_iva10                 NUMERIC(18,2),
    total_iva5                  NUMERIC(18,2),
    descuento_global            NUMERIC(18,2),
    porcentaje_descuento_global NUMERIC(5,2),
    motivo_traslado             INTEGER,
    descripcion_motivo_traslado VARCHAR(255),
    nombre_transportista        VARCHAR(255),
    ruc_transportista           VARCHAR(20),
    dv_transportista            VARCHAR(1),
    naturaleza_transportista    INTEGER,
    nombre_chofer               VARCHAR(255),
    numero_documento_chofer     VARCHAR(30),
    direccion_chofer            VARCHAR(255),
    matricula_vehiculo          VARCHAR(20),
    kms_recorrido               NUMERIC(10,2),
    precio_flete                NUMERIC(18,2),
    responsable_emision         VARCHAR(255),
    modalidad_contratacion      INTEGER,
    entidad_contratante         VARCHAR(255),
    anio_contratacion           INTEGER,
    secuencial_contrato         VARCHAR(20),
    fecha_codigo_contrato       DATE,
    tipo_documento_asociado     INTEGER,
    cdc_documento_asociado      VARCHAR(44),
    lote_transmision_id         VARCHAR(36)  REFERENCES lotes_transmision(id),
    xml_generado                TEXT,
    xml_firmado                 TEXT,
    xml_respuesta_sifen         TEXT,
    mensaje_sifen               VARCHAR(500),
    mensaje_usuario             VARCHAR(500),
    fecha_creacion              TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documentos_items (
    id              VARCHAR(36)  PRIMARY KEY,
    documento_id    VARCHAR(36)  REFERENCES documentos_electronicos(id) ON DELETE CASCADE,
    codigo          VARCHAR(50),
    descripcion     VARCHAR(500),
    cantidad        NUMERIC(14,4),
    unidad_medida   INTEGER,
    precio_unitario NUMERIC(18,2),
    monto_descuento NUMERIC(18,2),
    monto_total_item NUMERIC(18,2),
    tasa_iva        NUMERIC(5,2),
    monto_iva_item  NUMERIC(18,2)
);

CREATE TABLE IF NOT EXISTS documento_cuotas (
    id                  VARCHAR(36)  PRIMARY KEY,
    documento_id        VARCHAR(36)  REFERENCES documentos_electronicos(id) ON DELETE CASCADE,
    numero_cuota        INTEGER,
    fecha_vencimiento   DATE,
    monto               NUMERIC(18,2)
);

CREATE TABLE IF NOT EXISTS documento_pagos (
    id                          BIGSERIAL    PRIMARY KEY,
    documento_id                VARCHAR(36)  REFERENCES documentos_electronicos(id) ON DELETE CASCADE,
    tipo_pago                   INTEGER,
    monto                       NUMERIC(18,2),
    safe_secure                 BOOLEAN      DEFAULT TRUE,
    tarjeta_denominacion        INTEGER,
    tarjeta_descripcion         VARCHAR(100),
    tarjeta_forma_procesamiento INTEGER,
    cheque_numero               VARCHAR(30),
    cheque_banco                VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS documento_historial_sifen (
    id                  VARCHAR(36)  PRIMARY KEY,
    documento_id        VARCHAR(36)  NOT NULL REFERENCES documentos_electronicos(id) ON DELETE CASCADE,
    operacion           VARCHAR(50)  NOT NULL,
    codigo_estado       VARCHAR(10),
    mensaje_respuesta   VARCHAR(500),
    xml_respuesta       TEXT,
    fecha_registro      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS eventos_sifen (
    id                          VARCHAR(36)  PRIMARY KEY,
    tipo_evento                 VARCHAR(50),
    estado                      VARCHAR(50)  DEFAULT 'PENDIENTE',
    empresa_id                  VARCHAR(36)  REFERENCES empresas(id),
    documento_asociado_id       VARCHAR(36)  REFERENCES documentos_electronicos(id),
    cdc_relacionado             VARCHAR(44),
    motivo                      VARCHAR(500),
    timbrado                    VARCHAR(8),
    establecimiento             VARCHAR(3),
    punto_expedicion            VARCHAR(3),
    tipo_documento_inutilizar   INTEGER,
    rango_desde                 BIGINT,
    rango_hasta                 BIGINT,
    xml_generado                TEXT,
    xml_firmado                 TEXT,
    xml_respuesta_sifen         TEXT,
    codigo_sifen                VARCHAR(10),
    mensaje_sifen               VARCHAR(500),
    mensaje_usuario             VARCHAR(500),
    fecha_creacion              TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    fecha_envio                 TIMESTAMP,
    fecha_respuesta             TIMESTAMP,
    id_firma                    VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS documentos_papelera (
    id                      VARCHAR(36)  PRIMARY KEY,
    documento_id_original   VARCHAR(36),
    cdc                     VARCHAR(44),
    numero_comprobante      VARCHAR(20),
    tipo_documento          VARCHAR(2),
    ruc_receptor            VARCHAR(20),
    razon_social_receptor   VARCHAR(255),
    monto_total             NUMERIC(18,2),
    estado_original         VARCHAR(255),
    xml_generado            TEXT,
    xml_firmado             TEXT,
    xml_respuesta_sifen     TEXT,
    motivo_eliminacion      VARCHAR(255),
    fecha_eliminacion       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 4. Índices para Optimización y Búsqueda (Server-Side)
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_documento_ambiente_fecha ON documentos_electronicos (ambiente, fecha_creacion DESC);
CREATE INDEX IF NOT EXISTS idx_documento_estado ON documentos_electronicos (estado);
CREATE INDEX IF NOT EXISTS idx_documento_tipo ON documentos_electronicos (tipo_documento);
CREATE INDEX IF NOT EXISTS idx_documento_ruc ON documentos_electronicos (ruc_receptor);

-- Extensión y búsqueda trigram para búsqueda global parcial e instantánea
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_documento_cdc_trgm ON documentos_electronicos USING gin (cdc gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_documento_nro_trgm ON documentos_electronicos USING gin (numero_comprobante gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_documento_razon_trgm ON documentos_electronicos USING gin (receptor_razon_social gin_trgm_ops);
