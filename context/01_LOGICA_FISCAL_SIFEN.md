# Logica de Dominio SIFEN - Paraguay

## 1. Documentos Tributarios Electronicos (DTE)
- FE: Factura Electronica (Tipo 1).
- FEx: Factura Electronica de Exportacion (Tipo 2).
- AF: Autofactura Electronica (Tipo 3).
- NRE: Nota de Remision Electronica (Tipo 7).
- NCE: Nota de Credito Electronica (Tipo 4).
- NDE: Nota de Debito Electronica (Tipo 5).

## 2. Identificadores Criticos
- CDC: Codigo de Control de 44 digitos. Algoritmo de generacion estricto.
- Digito Verificador: Modulo 11.

## 3. Ciclo de Vida del DTE (Estados Internos)
- CREADO, FIRMADO, ENVIADO, APROBADO, RECHAZADO, OBSERVADO, ANULADO.

## 4. Reglas de Validacion Prioritarias
- Validacion de RUC y DV.
- Validacion de Schema XSD oficial.
- Validacion de obligatoriedad de campos por tipo de operacion.
