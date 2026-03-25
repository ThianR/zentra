-- SIFEN References v150
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('DEPARTAMENTO', '1', 'CAPITAL', 1, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('DEPARTAMENTO', '2', 'CONCEPCION', 1, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('DEPARTAMENTO', '12', 'CENTRAL', 1, true);

INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo) VALUES ('CIUDAD', '1', 'ASUNCION', '1', 1, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo) VALUES ('CIUDAD', '1', 'AREGUA', '12', 1, true);

INSERT INTO sifen_referencia (tipo, codigo, descripcion, valor_aux, descripcion_aux, orden, activo) VALUES ('MONEDA', 'PYG', 'Guarani', 'NACIONAL', 'Moneda local', 1, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, valor_aux, descripcion_aux, orden, activo) VALUES ('MONEDA', 'USD', 'Dolar', 'EXTRANJERA', 'Dolar USA', 2, true);

INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_DOCUMENTO', '1', 'Factura electronica', 1, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_DOCUMENTO', '2', 'Factura electronica de exportacion', 2, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_DOCUMENTO', '3', 'Factura electronica de importacion', 3, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_DOCUMENTO', '4', 'Autofactura electronica', 4, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_DOCUMENTO', '5', 'Nota de credito electronica', 5, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_DOCUMENTO', '6', 'Nota de debito electronica', 6, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_DOCUMENTO', '7', 'Nota de remision electronica', 7, true);

INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('CONDICION_OPERACION', '1', 'Contado', 1, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('CONDICION_OPERACION', '2', 'Credito', 2, true);

INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_CONTRIBUYENTE', '1', 'Persona Fisica', 1, true);
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES ('TIPO_CONTRIBUYENTE', '2', 'Persona Juridica', 2, true);

INSERT INTO empresas (id, ruc, razon_social, dv, cod_establecimiento, punto_expedicion, direccion, numero_casa, cod_departamento, departamento, cod_distrito, distrito, cod_ciudad, ciudad, telefono, email, actividad_economica, tipo_contribuyente) VALUES ('80000001', '80000001', 'ZENTRA MIDDLEWARE S.A.', '7', '001', '001', 'Avda Mariscal Lopez 1234', '1234', 1, 'CAPITAL', 1, 'ASUNCION', 1, 'ASUNCION', '021000000', 'emisor@zentra.com.py', 'SERVICIOS TECNOLOGICOS', 2);
