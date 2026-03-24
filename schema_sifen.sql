-- Esquema inicial para Zentra MVP (Plan A)

CREATE TABLE IF NOT EXISTS empresas (
    id VARCHAR(36) PRIMARY KEY,
    ruc VARCHAR(20) NOT NULL UNIQUE,
    dv VARCHAR(1),
    razon_social VARCHAR(255),
    cod_establecimiento VARCHAR(3),
    punto_expedicion VARCHAR(3),
    ruta_certificado VARCHAR(255),
    password_certificado VARCHAR(255)
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

INSERT INTO empresas (id, ruc, dv, razon_social, cod_establecimiento, punto_expedicion)
VALUES ('e1', '80000001', '5', 'Zentra Demo Emisor S.A.', '001', '001')
ON CONFLICT (ruc) DO NOTHING;

INSERT INTO documentos_electronicos (id, cdc, tipo_documento, estado, emisor_id, numero_comprobante)
VALUES ('d1', '018000000150010010000001120240319123456781', '01', 'APROBADO', 'e1', '001-001-0000001')
ON CONFLICT (cdc) DO NOTHING;

INSERT INTO documentos_electronicos (id, cdc, tipo_documento, estado, emisor_id, numero_comprobante)
VALUES ('d2', '018000000150010010000002120240319123456782', '01', 'RECHAZADO', 'e1', '001-001-0000002')
ON CONFLICT (cdc) DO NOTHING;
