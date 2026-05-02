-- =============================================================================
-- ZENTRA SIFEN Middleware - Datos de Referencia Idempotentes
-- CatÃ¡logos oficiales SIFEN v150 (SET/DNIT Paraguay)
--
-- Todos los INSERT usan ON CONFLICT DO NOTHING para garantizar idempotencia:
-- el script puede ejecutarse en cada arranque sin duplicar datos.
--
-- La tabla sifen_referencia tiene UNIQUE (tipo, codigo, padre_codigo).
-- Para registros sin padre, padre_codigo es NULL -> se usa el truco de
-- insertar via ON CONFLICT (tipo, codigo) WHERE padre_codigo IS NULL
-- o bien vÃ­a la restricciÃ³n UNIQUE que ya incluye el NULL como parte del Ã­ndice.
-- En PostgreSQL, dos NULLs en un UNIQUE index NO colisionan por defecto,
-- por eso usamos un Ã­ndice parcial + DO NOTHING con condiciÃ³n explÃ­cita.
-- =============================================================================

-- =============================================================================
-- SECCIÃ“N 1: DEPARTAMENTOS (Tabla 3 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('DEPARTAMENTO', '1',  'CAPITAL',              NULL, 1,  true),
    ('DEPARTAMENTO', '2',  'CONCEPCION',            NULL, 2,  true),
    ('DEPARTAMENTO', '3',  'SAN PEDRO',             NULL, 3,  true),
    ('DEPARTAMENTO', '4',  'CORDILLERA',            NULL, 4,  true),
    ('DEPARTAMENTO', '5',  'GUAIRA',                NULL, 5,  true),
    ('DEPARTAMENTO', '6',  'CAAGUAZU',              NULL, 6,  true),
    ('DEPARTAMENTO', '7',  'CAAZAPA',               NULL, 7,  true),
    ('DEPARTAMENTO', '8',  'ITAPUA',                NULL, 8,  true),
    ('DEPARTAMENTO', '9',  'MISIONES',              NULL, 9,  true),
    ('DEPARTAMENTO', '10', 'PARAGUARI',             NULL, 10, true),
    ('DEPARTAMENTO', '11', 'ALTO PARANA',           NULL, 11, true),
    ('DEPARTAMENTO', '12', 'CENTRAL',               NULL, 12, true),
    ('DEPARTAMENTO', '13', 'NEEMBUCU',              NULL, 13, true),
    ('DEPARTAMENTO', '14', 'AMAMBAY',               NULL, 14, true),
    ('DEPARTAMENTO', '15', 'CANINDEYU',             NULL, 15, true),
    ('DEPARTAMENTO', '16', 'PRESIDENTE HAYES',      NULL, 16, true),
    ('DEPARTAMENTO', '17', 'ALTO PARAGUAY',         NULL, 17, true),
    ('DEPARTAMENTO', '18', 'BOQUERON',              NULL, 18, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 2: DISTRITOS (Tabla 5 - SIFEN v150) - Distrito por departamento
-- Solo los principales; padre_codigo = cÃ³digo del DEPARTAMENTO
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    -- Capital (dep=1)
    ('DISTRITO', '1',   'ASUNCION (DISTRITO)',           '1',  1, true),
    -- Concepcion (dep=2)
    ('DISTRITO', '2',   'CONCEPCION',                    '2',  1, true),
    ('DISTRITO', '3',   'BELEN',                         '2',  2, true),
    -- San Pedro (dep=3)
    ('DISTRITO', '15',  'SAN PEDRO DEL YCUAMANDYYU',     '3',  1, true),
    -- Cordillera (dep=4)
    ('DISTRITO', '35',  'CAACUPE',                       '4',  1, true),
    -- Guaira (dep=5)
    ('DISTRITO', '51',  'VILLARRICA',                    '5',  1, true),
    -- Caaguazu (dep=6)
    ('DISTRITO', '66',  'CORONEL OVIEDO',                '6',  1, true),
    -- Caazapa (dep=7)
    ('DISTRITO', '84',  'CAAZAPA',                       '7',  1, true),
    -- Itapua (dep=8)
    ('DISTRITO', '93',  'ENCARNACION',                   '8',  1, true),
    -- Misiones (dep=9)
    ('DISTRITO', '111', 'SAN JUAN BAUTISTA',             '9',  1, true),
    -- Paraguari (dep=10)
    ('DISTRITO', '121', 'PARAGUARI',                     '10', 1, true),
    -- Alto Parana (dep=11)
    ('DISTRITO', '134', 'CIUDAD DEL ESTE',               '11', 1, true),
    -- Central (dep=12)
    ('DISTRITO', '143', 'AREGUÃ',                        '12', 1, true),
    ('DISTRITO', '144', 'CAPIATÃ',                       '12', 2, true),
    ('DISTRITO', '145', 'FERNANDO DE LA MORA',           '12', 3, true),
    ('DISTRITO', '146', 'GUARAMBARÃ‰',                    '12', 4, true),
    ('DISTRITO', '147', 'ITA',                           '12', 5, true),
    ('DISTRITO', '148', 'ITAUGUÃ',                       '12', 6, true),
    ('DISTRITO', '149', 'LAMBARÃ‰',                       '12', 7, true),
    ('DISTRITO', '150', 'LIMPIO',                        '12', 8, true),
    ('DISTRITO', '151', 'LUQUE',                         '12', 9, true),
    ('DISTRITO', '152', 'MARIANO ROQUE ALONSO',          '12', 10, true),
    ('DISTRITO', '153', 'Ã‘EMBY',                         '12', 11, true),
    ('DISTRITO', '154', 'NUEVA ITALIA',                  '12', 12, true),
    ('DISTRITO', '155', 'SAN ANTONIO',                   '12', 13, true),
    ('DISTRITO', '156', 'SAN LORENZO',                   '12', 14, true),
    ('DISTRITO', '157', 'VILLA ELISA',                   '12', 15, true),
    ('DISTRITO', '158', 'VILLETA',                       '12', 16, true),
    ('DISTRITO', '159', 'J. AUGUSTO SALDIVAR',           '12', 17, true),
    -- Neembucu (dep=13)
    ('DISTRITO', '163', 'PILAR',                         '13', 1, true),
    -- Amambay (dep=14)
    ('DISTRITO', '174', 'PEDRO JUAN CABALLERO',          '14', 1, true),
    -- Canindeyu (dep=15)
    ('DISTRITO', '181', 'SALTO DEL GUAIRA',              '15', 1, true),
    -- Presidente Hayes (dep=16)
    ('DISTRITO', '192', 'VILLA HAYES',                   '16', 1, true),
    -- Alto Paraguay (dep=17)
    ('DISTRITO', '200', 'FUERTE OLIMPO',                 '17', 1, true),
    -- Boqueron (dep=18)
    ('DISTRITO', '204', 'FILADELFIA',                    '18', 1, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 3: CIUDADES (Tabla 6 - SIFEN v150) - padre_codigo = cÃ³digo DISTRITO
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    -- Asuncion (dist=1)
    ('CIUDAD', '1',   'ASUNCION',                  '1',   1, true),
    -- Concepcion (dist=2)
    ('CIUDAD', '2',   'CONCEPCION',                '2',   1, true),
    -- San Pedro del Ycuamandyyu (dist=15)
    ('CIUDAD', '15',  'SAN PEDRO DEL YCUAMANDYYU', '15',  1, true),
    -- Caacupe (dist=35)
    ('CIUDAD', '35',  'CAACUPE',                   '35',  1, true),
    -- Villarrica (dist=51)
    ('CIUDAD', '51',  'VILLARRICA',                '51',  1, true),
    -- Coronel Oviedo (dist=66)
    ('CIUDAD', '66',  'CORONEL OVIEDO',            '66',  1, true),
    -- Caazapa (dist=84)
    ('CIUDAD', '84',  'CAAZAPA',                   '84',  1, true),
    -- Encarnacion (dist=93)
    ('CIUDAD', '93',  'ENCARNACION',               '93',  1, true),
    -- San Juan Bautista (dist=111)
    ('CIUDAD', '111', 'SAN JUAN BAUTISTA',         '111', 1, true),
    -- Paraguari (dist=121)
    ('CIUDAD', '121', 'PARAGUARI',                 '121', 1, true),
    -- Ciudad del Este (dist=134)
    ('CIUDAD', '134', 'CIUDAD DEL ESTE',           '134', 1, true),
    -- AreguÃ¡ (dist=143)
    ('CIUDAD', '143', 'AREGUA',                    '143', 1, true),
    -- CapiatÃ¡ (dist=144)
    ('CIUDAD', '144', 'CAPIATA',                   '144', 1, true),
    -- Fernando de la Mora (dist=145)
    ('CIUDAD', '145', 'FERNANDO DE LA MORA',       '145', 1, true),
    -- ItauguÃ¡ (dist=148)
    ('CIUDAD', '148', 'ITAUGUA',                   '148', 1, true),
    -- LambarÃ© (dist=149)
    ('CIUDAD', '149', 'LAMBARE',                   '149', 1, true),
    -- Limpio (dist=150)
    ('CIUDAD', '150', 'LIMPIO',                    '150', 1, true),
    -- Luque (dist=151)
    ('CIUDAD', '151', 'LUQUE',                     '151', 1, true),
    -- Mariano Roque Alonso (dist=152)
    ('CIUDAD', '152', 'MARIANO ROQUE ALONSO',      '152', 1, true),
    -- Ã‘emby (dist=153)
    ('CIUDAD', '153', 'NEMBY',                     '153', 1, true),
    -- San Antonio (dist=155)
    ('CIUDAD', '155', 'SAN ANTONIO',               '155', 1, true),
    -- San Lorenzo (dist=156)
    ('CIUDAD', '156', 'SAN LORENZO',               '156', 1, true),
    -- Villeta (dist=158)
    ('CIUDAD', '158', 'VILLETA',                   '158', 1, true),
    -- Pilar (dist=163)
    ('CIUDAD', '163', 'PILAR',                     '163', 1, true),
    -- Pedro Juan Caballero (dist=174)
    ('CIUDAD', '174', 'PEDRO JUAN CABALLERO',      '174', 1, true),
    -- Filadelfia (dist=204)
    ('CIUDAD', '204', 'FILADELFIA',                '204', 1, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 4: TIPOS DE DOCUMENTO ELECTRÃ“NICO (Tabla 1 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('TIPO_DOCUMENTO', '1', 'Factura electronica',                    NULL, 1, true),
    ('TIPO_DOCUMENTO', '2', 'Factura electronica de exportacion',     NULL, 2, true),
    ('TIPO_DOCUMENTO', '3', 'Factura electronica de importacion',     NULL, 3, true),
    ('TIPO_DOCUMENTO', '4', 'Autofactura electronica',                NULL, 4, true),
    ('TIPO_DOCUMENTO', '5', 'Nota de credito electronica',            NULL, 5, true),
    ('TIPO_DOCUMENTO', '6', 'Nota de debito electronica',             NULL, 6, true),
    ('TIPO_DOCUMENTO', '7', 'Nota de remision electronica',           NULL, 7, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 5: TIPOS DE EMISIÃ“N (Tabla 4 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('TIPO_EMISION', '1', 'Normal',                       NULL, 1, true),
    ('TIPO_EMISION', '2', 'Contingencia',                 NULL, 2, true),
    ('TIPO_EMISION', '3', 'Contingencia offline web',     NULL, 3, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 6: TIPOS DE TRANSACCIÃ“N (Tabla 11 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('TIPO_TRANSACCION', '1', 'Venta de mercaderia',                     NULL, 1,  true),
    ('TIPO_TRANSACCION', '2', 'Prestacion de servicios',                  NULL, 2,  true),
    ('TIPO_TRANSACCION', '3', 'Mixto (Bienes y servicios)',               NULL, 3,  true),
    ('TIPO_TRANSACCION', '4', 'Venta de activo fijo',                     NULL, 4,  true),
    ('TIPO_TRANSACCION', '5', 'Venta de divisas',                         NULL, 5,  true),
    ('TIPO_TRANSACCION', '6', 'Credito fiscal',                           NULL, 6,  true),
    ('TIPO_TRANSACCION', '7', 'Enajenacion de inmueble',                  NULL, 7,  true),
    ('TIPO_TRANSACCION', '8', 'Prestacion de servicios de espectaculos',  NULL, 8,  true),
    ('TIPO_TRANSACCION', '9', 'Provision de alimentos y bebidas',         NULL, 9,  true),
    ('TIPO_TRANSACCION', '10','Anticipos',                                NULL, 10, true),
    ('TIPO_TRANSACCION', '11','Fletamento maritimo o aereo',              NULL, 11, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 7: TIPO DE IMPUESTO (Tabla 12 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('TIPO_IMPUESTO', '1', 'IVA',                           NULL, 1, true),
    ('TIPO_IMPUESTO', '2', 'ISC',                           NULL, 2, true),
    ('TIPO_IMPUESTO', '3', 'Renta',                         NULL, 3, true),
    ('TIPO_IMPUESTO', '4', 'Ninguno',                       NULL, 4, true),
    ('TIPO_IMPUESTO', '5', 'IVA - Renta',                   NULL, 5, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 8: MONEDAS (Tabla 15 - SIFEN v150)
-- valor_aux = clasificacion (NACIONAL / EXTRANJERA)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, valor_aux, descripcion_aux, orden, activo)
VALUES
    ('MONEDA', 'PYG', 'Guarani',              NULL, 'NACIONAL',    'Moneda local',         1,  true),
    ('MONEDA', 'USD', 'Dolar Americano',      NULL, 'EXTRANJERA',  'Dolar USA',            2,  true),
    ('MONEDA', 'BRL', 'Real BrasileÃ±o',       NULL, 'EXTRANJERA',  'Real Brasil',          3,  true),
    ('MONEDA', 'ARS', 'Peso Argentino',       NULL, 'EXTRANJERA',  'Peso Argentina',       4,  true),
    ('MONEDA', 'EUR', 'Euro',                 NULL, 'EXTRANJERA',  'Euros',                5,  true),
    ('MONEDA', 'UYU', 'Peso Uruguayo',        NULL, 'EXTRANJERA',  'Peso Uruguay',         6,  true),
    ('MONEDA', 'BOB', 'Boliviano',            NULL, 'EXTRANJERA',  'Bolivianos Bolivia',   7,  true),
    ('MONEDA', 'CLP', 'Peso Chileno',         NULL, 'EXTRANJERA',  'Peso Chile',           8,  true),
    ('MONEDA', 'GBP', 'Libra Esterlina',      NULL, 'EXTRANJERA',  'Libra Reino Unido',    9,  true),
    ('MONEDA', 'CHF', 'Franco Suizo',         NULL, 'EXTRANJERA',  'Franco Suizo',         10, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 9: CONDICIÃ“N DE OPERACIÃ“N (Tabla 17 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('CONDICION_OPERACION', '1', 'Contado',  NULL, 1, true),
    ('CONDICION_OPERACION', '2', 'Credito',  NULL, 2, true),
    ('CONDICION_OPERACION', '3', 'Anticipo', NULL, 3, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 10: TIPO DE PAGO (Tabla 18 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('TIPO_PAGO', '1',  'Efectivo',                                  NULL, 1,  true),
    ('TIPO_PAGO', '2',  'Cheque',                                    NULL, 2,  true),
    ('TIPO_PAGO', '3',  'Tarjeta de credito',                        NULL, 3,  true),
    ('TIPO_PAGO', '4',  'Tarjeta de debito',                         NULL, 4,  true),
    ('TIPO_PAGO', '5',  'Transferencia',                             NULL, 5,  true),
    ('TIPO_PAGO', '6',  'Giro',                                      NULL, 6,  true),
    ('TIPO_PAGO', '7',  'Billetera electronica',                     NULL, 7,  true),
    ('TIPO_PAGO', '8',  'Tarjeta empresarial',                       NULL, 8,  true),
    ('TIPO_PAGO', '9',  'Vale',                                      NULL, 9,  true),
    ('TIPO_PAGO', '10', 'Otro',                                      NULL, 10, true),
    ('TIPO_PAGO', '11', 'Criptomoneda',                              NULL, 11, true),
    ('TIPO_PAGO', '12', 'Pago electronico',                          NULL, 12, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 11: TIPO DE CONTRIBUYENTE (Tabla 2 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('TIPO_CONTRIBUYENTE', '1', 'Persona Fisica',   NULL, 1, true),
    ('TIPO_CONTRIBUYENTE', '2', 'Persona Juridica', NULL, 2, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 12: NATURALEZA DEL RECEPTOR (Tabla 7 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('NATURALEZA_RECEPTOR', '1', 'Contribuyente',      NULL, 1, true),
    ('NATURALEZA_RECEPTOR', '2', 'No contribuyente',   NULL, 2, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 13: TIPO DE OPERACIÃ“N (Tabla 9 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('TIPO_OPERACION', '1', 'B2B',  NULL, 1, true),
    ('TIPO_OPERACION', '2', 'B2C',  NULL, 2, true),
    ('TIPO_OPERACION', '3', 'B2G',  NULL, 3, true),
    ('TIPO_OPERACION', '4', 'B2F',  NULL, 4, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 14: INDICADOR DE PRESENCIA (Tabla 10 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('INDICADOR_PRESENCIA', '1', 'Operacion presencial',           NULL, 1, true),
    ('INDICADOR_PRESENCIA', '2', 'Operacion electronica',          NULL, 2, true),
    ('INDICADOR_PRESENCIA', '3', 'Operacion televenta',            NULL, 3, true),
    ('INDICADOR_PRESENCIA', '4', 'Operacion otro medio',           NULL, 4, true),
    ('INDICADOR_PRESENCIA', '5', 'Operacion via correo',           NULL, 5, true),
    ('INDICADOR_PRESENCIA', '6', 'Operacion via vendedor',         NULL, 6, true),
    ('INDICADOR_PRESENCIA', '7', 'Operacion via deposito',         NULL, 7, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 15: AFECTACIÃ“N IVA (Tabla 19 - SIFEN v150)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('AFECTACION_IVA', '1', 'Gravado IVA',     NULL, 1, true),
    ('AFECTACION_IVA', '2', 'Exonerado (E)',   NULL, 2, true),
    ('AFECTACION_IVA', '3', 'Exento (Ex)',     NULL, 3, true),
    ('AFECTACION_IVA', '4', 'Gravado parcial', NULL, 4, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 16: UNIDADES DE MEDIDA (Tabla 20 - SIFEN v150) - cÃ³digos principales
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('UNIDAD_MEDIDA', '77',  'UNI',   NULL, 1,  true),
    ('UNIDAD_MEDIDA', '83',  'KGM',   NULL, 2,  true),
    ('UNIDAD_MEDIDA', 'LTR', 'LTR',   NULL, 3,  true),
    ('UNIDAD_MEDIDA', 'MTR', 'MTR',   NULL, 4,  true),
    ('UNIDAD_MEDIDA', 'MTK', 'MTK',   NULL, 5,  true),
    ('UNIDAD_MEDIDA', 'MTQ', 'MTQ',   NULL, 6,  true),
    ('UNIDAD_MEDIDA', 'HUR', 'HUR',   NULL, 7,  true),
    ('UNIDAD_MEDIDA', 'DAY', 'DAY',   NULL, 8,  true),
    ('UNIDAD_MEDIDA', 'MON', 'MON',   NULL, 9,  true),
    ('UNIDAD_MEDIDA', 'ANN', 'ANN',   NULL, 10, true),
    ('UNIDAD_MEDIDA', 'TNE', 'TNE',   NULL, 11, true),
    ('UNIDAD_MEDIDA', 'GRM', 'GRM',   NULL, 12, true),
    ('UNIDAD_MEDIDA', 'CMT', 'CMT',   NULL, 13, true),
    ('UNIDAD_MEDIDA', 'MMT', 'MMT',   NULL, 14, true),
    ('UNIDAD_MEDIDA', 'SET', 'SET',   NULL, 15, true),
    ('UNIDAD_MEDIDA', 'PR',  'PAR',   NULL, 16, true),
    ('UNIDAD_MEDIDA', 'BX',  'CAJA',  NULL, 17, true),
    ('UNIDAD_MEDIDA', 'BG',  'BOLSA', NULL, 18, true)
ON CONFLICT DO NOTHING;

-- =============================================================================
-- SECCIÃ“N 17: PAÃSES (solo los principales para receptor)
-- =============================================================================
INSERT INTO sifen_referencia (tipo, codigo, descripcion, padre_codigo, orden, activo)
VALUES
    ('PAIS', 'PRY', 'Paraguay',           NULL, 1,  true),
    ('PAIS', 'ARG', 'Argentina',          NULL, 2,  true),
    ('PAIS', 'BRA', 'Brasil',             NULL, 3,  true),
    ('PAIS', 'BOL', 'Bolivia',            NULL, 4,  true),
    ('PAIS', 'CHL', 'Chile',              NULL, 5,  true),
    ('PAIS', 'URY', 'Uruguay',            NULL, 6,  true),
    ('PAIS', 'PER', 'Peru',               NULL, 7,  true),
    ('PAIS', 'USA', 'Estados Unidos',     NULL, 8,  true),
    ('PAIS', 'ESP', 'EspaÃ±a',             NULL, 9,  true),
    ('PAIS', 'DEU', 'Alemania',           NULL, 10, true),
    ('PAIS', 'ITA', 'Italia',             NULL, 11, true),
    ('PAIS', 'CHN', 'China',              NULL, 12, true),
    ('PAIS', 'JPN', 'Japon',              NULL, 13, true),
    ('PAIS', 'GBR', 'Reino Unido',        NULL, 14, true),
    ('PAIS', 'FRA', 'Francia',            NULL, 15, true),
    ('PAIS', 'MEX', 'Mexico',             NULL, 16, true),
    ('PAIS', 'VEN', 'Venezuela',          NULL, 17, true),
    ('PAIS', 'COL', 'Colombia',           NULL, 18, true)
ON CONFLICT DO NOTHING;

-- -----------------------------------------------------------------------------
-- Actividades Económicas (SET Marangatu / SIFEN)
-- -----------------------------------------------------------------------------
INSERT INTO sifen_referencia (tipo, codigo, descripcion, orden, activo) VALUES
('ACTIVIDAD_ECONOMICA', '45101', 'VENTA DE VEHICULOS AUTOMOTORES NUEVOS', 1, true),
('ACTIVIDAD_ECONOMICA', '45301', 'COMERCIO DE PARTES, PIEZAS Y ACCESORIOS NUEVOS PARA VEHICULOS AUTOMOTORES', 2, true),
('ACTIVIDAD_ECONOMICA', '46499', 'COMERCIO AL POR MAYOR DE OTROS ARTICULOS DE USO DOMESTICO N.C.P.', 3, true),
('ACTIVIDAD_ECONOMICA', '47111', 'COMERCIO AL POR MENOR EN SUPERMERCADOS', 4, true),
('ACTIVIDAD_ECONOMICA', '47112', 'COMERCIO AL POR MENOR EN MINIMERCADOS', 5, true),
('ACTIVIDAD_ECONOMICA', '47521', 'COMERCIO AL POR MENOR DE ARTICULOS DE FERRETERIA, PINTURAS Y PRODUCTOS DE VIDRIO', 6, true),
('ACTIVIDAD_ECONOMICA', '47711', 'COMERCIO AL POR MENOR DE PRENDAS DE VESTIR', 7, true),
('ACTIVIDAD_ECONOMICA', '56101', 'SERVICIOS DE RESTAURANTES Y PARRILLADAS', 8, true),
('ACTIVIDAD_ECONOMICA', '62010', 'ACTIVIDADES DE PROGRAMACION INFORMATICA', 9, true),
('ACTIVIDAD_ECONOMICA', '62020', 'ACTIVIDADES DE CONSULTORIA DE INFORMATICA Y GESTION DE INSTALACIONES INFORMATICAS', 10, true),
('ACTIVIDAD_ECONOMICA', '69101', 'ACTIVIDADES JURIDICAS', 11, true),
('ACTIVIDAD_ECONOMICA', '69201', 'ACTIVIDADES DE CONTABILIDAD, TENEDURIA DE LIBROS Y AUDITORIA; ASESORAMIENTO FISCAL', 12, true),
('ACTIVIDAD_ECONOMICA', '70201', 'ACTIVIDADES DE CONSULTORIA DE GESTION', 13, true),
('ACTIVIDAD_ECONOMICA', '86201', 'ACTIVIDADES DE MEDICOS Y ODONTOLOGOS', 14, true),
('ACTIVIDAD_ECONOMICA', '86202', 'SERVICIOS DE CLINICAS Y HOSPITALES', 15, true)
ON CONFLICT DO NOTHING;


-- =============================================================================
-- NOTA: Los datos de la tabla "empresas" NO se incluyen aquÃ­.
-- La empresa emisora se registra exclusivamente mediante la interfaz web (API).
-- Esto evita hardcodear datos sensibles (certificados, contraseÃ±as, timbrados)
-- en archivos SQL versionados.
-- =============================================================================
