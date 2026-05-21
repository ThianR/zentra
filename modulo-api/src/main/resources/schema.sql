-- =============================================================================
-- ZENTRA SIFEN Middleware - Schema DDL Idempotente
-- Ejecutado por Spring Boot al iniciar si spring.sql.init.mode=always
-- Cada sentencia usa IF NOT EXISTS para garantizar idempotencia total.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: empresas
-- Almacena la configuraciÃ³n del emisor (RUC, certificado, timbrado, etc.)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS empresas (
    id                          VARCHAR(36)  PRIMARY KEY,
    ruc                         VARCHAR(20)  UNIQUE,
    dv                          VARCHAR(1),
    razon_social                VARCHAR(255),
    tipo_contribuyente          INTEGER,
    cod_establecimiento         VARCHAR(3),
    punto_expedicion            VARCHAR(3),
    timbrado                    VARCHAR(8),
    fecha_inicio_timbrado       DATE,
    fecha_vencimiento_timbrado    DATE,
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
    logo_base64                 TEXT
);

-- -----------------------------------------------------------------------------
-- Tabla: sifen_referencia
-- CatÃ¡logos oficiales SIFEN v150: departamentos, ciudades, monedas, etc.
-- RestricciÃ³n UNIQUE: (tipo, codigo, padre_codigo) para soporte de idempotencia
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

-- Nota: Los Ã­ndices parciales de esta tabla son gestionados por
-- DatabaseMigrationConfig.java al arrancar la aplicaciÃ³n, dado que
-- requieren lÃ³gica condicional (DROP constraint viejo + CREATE nuevos)
-- que Spring Boot ScriptUtils no puede ejecutar (no soporta PL/pgSQL).



-- -----------------------------------------------------------------------------
-- Tabla: documentos_electronicos
-- Registro de cada DTE generado y transmitido a SIFEN.
-- -----------------------------------------------------------------------------
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
    -- Campos para nota de remisiÃ³n/traslado
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
    -- Datos de contrataciÃ³n pÃºblica
    modalidad_contratacion      INTEGER,
    entidad_contratante         VARCHAR(255),
    anio_contratacion           INTEGER,
    secuencial_contrato         VARCHAR(20),
    fecha_codigo_contrato       DATE,
    -- Datos del documento asociado (NC/ND)
    tipo_documento_asociado     INTEGER,
    cdc_documento_asociado      VARCHAR(44),
    -- XMLs y respuestas
    xml_generado                TEXT,
    xml_firmado                 TEXT,
    xml_respuesta_sifen         TEXT,
    mensaje_sifen               VARCHAR(500),
    mensaje_usuario             VARCHAR(500),
    fecha_creacion              TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- Tabla: documentos_items
-- LÃ­neas de detalle de cada documento electrÃ³nico.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS documentos_items (
    id              VARCHAR(36)  PRIMARY KEY,
    documento_id    VARCHAR(36)  REFERENCES documentos_electronicos(id),
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

-- -----------------------------------------------------------------------------
-- Tabla: documento_cuotas
-- Cuotas de pago a crÃ©dito para documentos con condiciÃ³n_operacion = 2
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS documento_cuotas (
    id                  VARCHAR(36)  PRIMARY KEY,
    documento_id        VARCHAR(36)  REFERENCES documentos_electronicos(id),
    numero_cuota        INTEGER,
    fecha_vencimiento   DATE,
    monto               NUMERIC(18,2)
);

-- -----------------------------------------------------------------------------
-- Tabla: documento_historial_sifen
-- BitÃ¡cora de interacciones con el WebService (ENVIO, CONSULTA, etc.)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS documento_historial_sifen (
    id                  VARCHAR(36)  PRIMARY KEY,
    documento_id        VARCHAR(36)  NOT NULL REFERENCES documentos_electronicos(id) ON DELETE CASCADE,
    operacion           VARCHAR(50)  NOT NULL,
    codigo_estado       VARCHAR(10),
    mensaje_respuesta   VARCHAR(500),
    xml_respuesta       TEXT,
    fecha_registro      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- Tabla: documentos_papelera
-- Respaldo de documentos eliminados (ej: rechazados que fueron re-generados)
-- -----------------------------------------------------------------------------
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
-- Índices para Paginación y Filtrado Eficiente de DTEs (Server-Side)
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_documento_ambiente_fecha ON documentos_electronicos (ambiente, fecha_creacion DESC);
CREATE INDEX IF NOT EXISTS idx_documento_estado ON documentos_electronicos (estado);
CREATE INDEX IF NOT EXISTS idx_documento_tipo ON documentos_electronicos (tipo_documento);
CREATE INDEX IF NOT EXISTS idx_documento_ruc ON documentos_electronicos (ruc_receptor);

-- Extensión y búsqueda trigram para búsqueda global parcial e instantánea en millones de registros
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_documento_cdc_trgm ON documentos_electronicos USING gin (cdc gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_documento_nro_trgm ON documentos_electronicos USING gin (numero_comprobante gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_documento_razon_trgm ON documentos_electronicos USING gin (receptor_razon_social gin_trgm_ops);

