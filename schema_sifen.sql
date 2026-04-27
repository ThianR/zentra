-- Esquema inicial para Zentra MVP (Plan A)

CREATE TABLE IF NOT EXISTS empresas (
    id VARCHAR(36) PRIMARY KEY,
    ruc VARCHAR(20) NOT NULL UNIQUE,
    dv VARCHAR(1),
    razon_social VARCHAR(255),
    cod_establecimiento VARCHAR(3),
    punto_expedicion VARCHAR(3),
    timbrado VARCHAR(8),
    ruta_certificado VARCHAR(255),
    password_certificado VARCHAR(255),
    id_csc VARCHAR(10) DEFAULT '0001',
    valor_csc VARCHAR(255),
    ambiente INT DEFAULT 2,
    certificado_fisico BYTEA,
    fecha_vencimiento_certificado DATE,
    alias_certificado VARCHAR(100),
    logo_base64 TEXT
);

CREATE TABLE IF NOT EXISTS documentos_electronicos (
    id VARCHAR(36) PRIMARY KEY,
    cdc VARCHAR(44) UNIQUE,
    tipo_documento VARCHAR(2),
    estado VARCHAR(20),
    emisor_id VARCHAR(36) REFERENCES empresas(id),
    numero_comprobante VARCHAR(20),
    xml_generado TEXT,
    xml_firmado TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba (Seeds)

INSERT INTO empresas (id, ruc, dv, razon_social, cod_establecimiento, punto_expedicion, timbrado, id_csc, valor_csc, ambiente)
VALUES ('80014603', '80014603', '4', 'REPUESTOS RG S.A.', '001', '001', '16770994', '0001', '73c9BeeA5AFb8fD17a3fD93a32A07A1a', 1)
ON CONFLICT (ruc) DO NOTHING;

