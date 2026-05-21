package com.zentra.middleware.core.service;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.ItemDocumento;
import com.zentra.middleware.core.model.Cuota;
import com.zentra.middleware.core.model.PagoContado;
import com.zentra.middleware.core.model.Transporte;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servicio de validación de Documentos Tributarios Electrónicos (DTE).
 *
 * Aplica todas las reglas definidas en los esquemas XSD DE_v150.xsd y
 * DE_Types_v150.xsd de SIFEN Paraguay, más validaciones de consistencia cruzada.
 *
 * Se debe invocar ANTES del generador XML para evitar errores en la firma
 * o rechazos del WebService de la SET.
 */
@Service
public class DteValidatorService {

    // Patrón de email según DE_Types_v150.xsd
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "([0-9a-zA-Z]([0-9a-zA-Z\\.\\-_])*@([0-9a-zA-Z][0-9a-zA-Z\\-_]*\\.)+[a-zA-Z]{2,9})"
    );

    // Tipos de DTE válidos según tiTiDE: 1|4|5|6|7|9|10
    private static final List<String> TIPOS_DTE_VALIDOS = List.of("1", "4", "5", "6", "7", "9", "10");

    /**
     * Resultado de la validación con lista acumulada de errores.
     */
    public static class ResultadoValidacion {
        private final List<String> errores = new ArrayList<>();

        public void agregar(String error) {
            errores.add(error);
        }

        public boolean esValido() {
            return errores.isEmpty();
        }

        public List<String> getErrores() {
            return errores;
        }

        public String getMensajeResumen() {
            return String.join("; ", errores);
        }
    }

    /**
     * Punto de entrada principal: valida el DTE completo.
     * Lanza IllegalArgumentException con el primer error crítico
     * o acumula todos los errores y los devuelve como resultado.
     *
     * @param dte el documento a validar
     * @return ResultadoValidacion con la lista de errores encontrados
     */
    public ResultadoValidacion validar(DocumentoElectronico dte) {
        ResultadoValidacion resultado = new ResultadoValidacion();

        validarEncabezado(dte, resultado);
        validarEmisor(dte, resultado);
        validarReceptor(dte, resultado);
        validarItems(dte, resultado);
        validarCondicionPago(dte, resultado);
        validarPorTipoDte(dte, resultado);
        validarTotalesConsistencia(dte, resultado);

        return resultado;
    }

    // =========================================================================
    // 1. ENCABEZADO / TIMBRADO
    // =========================================================================

    private void validarEncabezado(DocumentoElectronico dte, ResultadoValidacion r) {
        // Tipo de DTE
        String tipo = dte.getTipoDocumento();
        if (isBlank(tipo)) {
            r.agregar("ENCABEZADO: El tipo de documento (tipoDocumento) es obligatorio.");
        } else if (!TIPOS_DTE_VALIDOS.contains(tipo)) {
            r.agregar("ENCABEZADO: Tipo de documento inválido '" + tipo
                    + "'. Valores permitidos: 1(FE), 4(Autofactura), 5(NC), 6(ND), 7(Remisión), 9(Boleta), 10(Boleta Resimple).");
        }

        // Timbrado: 8 dígitos, no todos ceros
        String timbrado = dte.getTimbrado();
        if (isBlank(timbrado)) {
            r.agregar("TIMBRADO: El número de timbrado es obligatorio.");
        } else if (!timbrado.matches("[0-9]{8}") || timbrado.matches("0+")) {
            r.agregar("TIMBRADO: El timbrado debe tener exactamente 8 dígitos numéricos y no puede ser todo ceros. Recibido: '" + timbrado + "'.");
        }

        // Número de comprobante: xxx-xxx-xxxxxxx
        String nro = dte.getNumeroComprobante();
        if (isBlank(nro)) {
            r.agregar("COMPROBANTE: El número de comprobante es obligatorio.");
        } else {
            String[] partes = nro.split("-");
            if (partes.length != 3) {
                r.agregar("COMPROBANTE: Formato inválido '" + nro + "'. Se esperan 3 segmentos Estab-PtoExp-Numero.");
            } else {
                if (!partes[0].matches("[0-9]{3}")) r.agregar("COMPROBANTE: Establecimiento debe ser exactamente 3 dígitos. Recibido: '" + partes[0] + "'.");
                if (!partes[1].matches("[0-9]{3}")) r.agregar("COMPROBANTE: Punto de expedición debe ser exactamente 3 dígitos. Recibido: '" + partes[1] + "'.");
                if (!partes[2].matches("[0-9]{7}") || partes[2].matches("0+")) r.agregar("COMPROBANTE: Número debe ser exactamente 7 dígitos y no puede ser '0000000'. Recibido: '" + partes[2] + "'.");
            }
        }
    }

    // =========================================================================
    // 2. EMISOR
    // =========================================================================

    private void validarEmisor(DocumentoElectronico dte, ResultadoValidacion r) {
        if (dte.getEmisor() == null) {
            r.agregar("EMISOR: No hay empresa emisora configurada en el sistema.");
            return;
        }

        var emisor = dte.getEmisor();

        // RUC: máx 8 dígitos numéricos
        if (isBlank(emisor.getRuc())) {
            r.agregar("EMISOR: El RUC del emisor es obligatorio.");
        } else if (!emisor.getRuc().matches("[0-9\\-]+")) {
            r.agregar("EMISOR: El RUC del emisor debe ser numérico (puede incluir guion). Recibido: '" + emisor.getRuc() + "'.");
        }

        // DV: 1 carácter
        if (isBlank(emisor.getDv())) {
            r.agregar("EMISOR: El dígito verificador del RUC (dv) es obligatorio.");
        }

        // Tipo Contribuyente: 1 (Física) o 2 (Jurídica)
        if (emisor.getTipoContribuyente() == null) {
            r.agregar("EMISOR: El tipo de contribuyente (tipoContribuyente) es obligatorio.");
        } else if (emisor.getTipoContribuyente() != 1 && emisor.getTipoContribuyente() != 2) {
            r.agregar("EMISOR: Tipo de contribuyente inválido. Debe ser 1=Física o 2=Jurídica.");
        }

        // Razón Social
        if (isBlank(emisor.getRazonSocial()) || emisor.getRazonSocial().length() < 4) {
            r.agregar("EMISOR: La razón social debe tener al menos 4 caracteres.");
        }

        // Dirección
        if (isBlank(emisor.getDireccion())) {
            r.agregar("EMISOR: La dirección del emisor es obligatoria.");
        }

        // Teléfono: al menos 6 caracteres
        if (isBlank(emisor.getTelefono()) || emisor.getTelefono().length() < 6) {
            r.agregar("EMISOR: El teléfono debe tener al menos 6 caracteres.");
        }

        // Email
        if (isBlank(emisor.getEmail())) {
            r.agregar("EMISOR: El correo electrónico del emisor es obligatorio.");
        } else if (!EMAIL_PATTERN.matcher(emisor.getEmail()).matches()) {
            r.agregar("EMISOR: El correo '" + emisor.getEmail() + "' no tiene formato válido.");
        }

        // Departamento
        if (emisor.getCodDepartamento() == null) {
            r.agregar("EMISOR: El código de departamento (codDepartamento) es obligatorio.");
        }

        // Ciudad
        if (emisor.getCodCiudad() == null) {
            r.agregar("EMISOR: El código de ciudad (codCiudad) es obligatorio.");
        }

        // Actividad Económica
        if (isBlank(emisor.getActividadEconomica())) {
            r.agregar("EMISOR: La actividad económica es obligatoria (gActEco).");
        }
    }

    // =========================================================================
    // 3. RECEPTOR
    // =========================================================================

    private void validarReceptor(DocumentoElectronico dte, ResultadoValidacion r) {
        // Nombre/Razón Social es siempre obligatorio
        if (isBlank(dte.getReceptorRazonSocial()) || dte.getReceptorRazonSocial().length() < 4) {
            r.agregar("RECEPTOR: La razón social del receptor es obligatoria y debe tener al menos 4 caracteres.");
        }

        // RUC del receptor
        String rucRecepr = dte.getRucReceptor();
        if (!isBlank(rucRecepr) && !rucRecepr.equalsIgnoreCase("Varios")) {
            if (!rucRecepr.matches("[0-9\\-]+")) {
                r.agregar("RECEPTOR: El RUC del receptor debe ser numérico (puede incluir guion). Recibido: '" + rucRecepr + "'.");
            }
        }

        // Email del receptor (si presente, validar formato)
        String emailRec = dte.getReceptorEmail();
        if (!isBlank(emailRec) && !EMAIL_PATTERN.matcher(emailRec).matches()) {
            r.agregar("RECEPTOR: El correo '" + emailRec + "' no tiene formato válido.");
        }
    }

    // =========================================================================
    // 4. ITEMS — dCodInt OBLIGATORIO según XSD (sin minOccurs="0")
    // =========================================================================

    private void validarItems(DocumentoElectronico dte, ResultadoValidacion r) {
        List<ItemDocumento> items = dte.getItems();

        if (items == null || items.isEmpty()) {
            r.agregar("ITEMS: El documento debe tener al menos 1 ítem.");
            return;
        }

        if (items.size() > 999) {
            r.agregar("ITEMS: Se permite un máximo de 999 ítems. Recibidos: " + items.size() + ".");
        }

        int idx = 1;
        for (ItemDocumento item : items) {
            String prefijo = "ITEM " + idx + ": ";

            // dCodInt — OBLIGATORIO en el XSD (sin minOccurs="0")
            if (isBlank(item.getCodigo()) || item.getCodigo().equalsIgnoreCase("S/C")) {
                r.agregar(prefijo + "El código interno del producto (dCodInt) es obligatorio según el XSD SIFEN. No puede estar vacío ni usar 'S/C'.");
            } else if (item.getCodigo().length() > 20) {
                r.agregar(prefijo + "El código del producto supera los 20 caracteres permitidos.");
            }

            // dDesProSer — OBLIGATORIO
            if (isBlank(item.getDescripcion())) {
                r.agregar(prefijo + "La descripción del producto/servicio (dDesProSer) es obligatoria.");
            } else if (item.getDescripcion().length() > 2000) {
                r.agregar(prefijo + "La descripción supera los 2000 caracteres permitidos.");
            }

            // Cantidad
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                r.agregar(prefijo + "La cantidad debe ser mayor a 0.");
            }

            // Precio unitario
            if (item.getPrecioUnitario() == null || item.getPrecioUnitario() < 0) {
                r.agregar(prefijo + "El precio unitario no puede ser negativo.");
            }

            // Tasa IVA: solo 0, 5 o 10
            Double tasa = item.getTasaIva();
            if (tasa == null || (tasa != 0 && tasa != 5 && tasa != 10)) {
                r.agregar(prefijo + "La tasa IVA debe ser 0 (exento), 5 o 10. Recibida: " + tasa + ".");
            }

            // Subtotal calculado
            if (item.getCantidad() != null && item.getPrecioUnitario() != null) {
                double esperado = Math.round(item.getCantidad() * item.getPrecioUnitario() * 100.0) / 100.0;
                double montoTotal = item.getMontoTotalItem() != null ? item.getMontoTotalItem() : 0;
                if (Math.abs(montoTotal - esperado) > 0.01) {
                    r.agregar(prefijo + "El monto total del ítem (" + montoTotal
                            + ") no coincide con Cantidad × PrecioUnit (" + esperado + ").");
                }
            }

            idx++;
        }
    }

    // =========================================================================
    // 5. CONDICIÓN DE PAGO
    // =========================================================================

    private void validarCondicionPago(DocumentoElectronico dte, ResultadoValidacion r) {
        String tipo = dte.getTipoDocumento();

        // Notas de Crédito (5), Débito (6) y Remisión (7) no requieren informar
        // condición de operación según las reglas técnicas de SIFEN (Error 1501).
        if ("5".equals(tipo) || "6".equals(tipo) || "7".equals(tipo)) {
            return;
        }

        Integer cond = dte.getCondicionOperacion();
        if (cond == null || (cond != 1 && cond != 2)) {
            r.agregar("CONDICION: La condición de operación debe ser 1=Contado o 2=Crédito.");
        }

        // Validar cuotas cuando es crédito
        if (cond != null && cond == 2) {
            List<Cuota> cuotas = dte.getCuotas();
            if (cuotas == null || cuotas.isEmpty()) {
                r.agregar("CONDICION: Con condición Crédito (2), se debe incluir al menos una cuota.");
            } else {
                double sumaCuotas = cuotas.stream()
                        .mapToDouble(c -> c.getMonto() != null ? c.getMonto() : 0)
                        .sum();
                double totalOp = dte.getTotalOperacion() != null ? dte.getTotalOperacion() : 0;
                if (Math.abs(sumaCuotas - totalOp) > 1.0) {
                    r.agregar(String.format(
                            "CONDICION: La suma de las cuotas (%.2f) debe ser igual al Total de la Operación (%.2f).",
                            sumaCuotas, totalOp));
                }
            }
        }

        // Validar detalles de tarjeta/cheque cuando safeSecure está desactivado (condición contado)
        if (cond != null && cond == 1 && dte.getPagos() != null) {
            int numeroPago = 1;
            for (PagoContado pago : dte.getPagos()) {
                int tipoPago = pago.getTipoPago() != null ? pago.getTipoPago() : 1;
                boolean esTarjeta = tipoPago == 3 || tipoPago == 4;
                boolean esCheque  = tipoPago == 2;
                boolean safe = pago.getSafeSecure() == null || pago.getSafeSecure();
                String prefijoPago = "PAGO " + numeroPago + ": ";

                if (!safe && esTarjeta) {
                    // Denominación de tarjeta obligatoria
                    if (pago.getTarjetaDenominacion() == null) {
                        r.agregar(prefijoPago + "La denominación de la tarjeta es obligatoria "
                                + "(1=Visa, 2=Mastercard, 3=Amex, 4=Maestro, 5=Panal, 6=Caball, 99=Otro).");
                    } else if (pago.getTarjetaDenominacion() == 99) {
                        // Si es 'Otro', la descripción es obligatoria
                        if (isBlank(pago.getTarjetaDescripcion()) || pago.getTarjetaDescripcion().length() < 4) {
                            r.agregar(prefijoPago + "Al seleccionar Denominación 'Otro' (99), "
                                    + "la descripción de la tarjeta es obligatoria (mínimo 4 caracteres).");
                        }
                    }
                    // Forma de procesamiento obligatoria (1=POS, 2=Pago Electrónico)
                    if (pago.getTarjetaFormaProcesamiento() == null) {
                        r.agregar(prefijoPago + "La forma de procesamiento de la tarjeta es obligatoria "
                                + "(1=POS, 2=Pago Electrónico).");
                    } else if (pago.getTarjetaFormaProcesamiento() != 1 && pago.getTarjetaFormaProcesamiento() != 2) {
                        r.agregar(prefijoPago + "La forma de procesamiento de la tarjeta debe ser 1=POS o 2=Pago Electrónico.");
                    }
                }

                if (!safe && esCheque) {
                    // Número de cheque: mínimo 8 caracteres
                    if (isBlank(pago.getChequeNumero()) || pago.getChequeNumero().length() < 8) {
                        r.agregar(prefijoPago + "El número de cheque es obligatorio y debe tener al menos 8 caracteres.");
                    }
                    // Banco emisor: mínimo 4 caracteres
                    if (isBlank(pago.getChequeBanco()) || pago.getChequeBanco().length() < 4) {
                        r.agregar(prefijoPago + "El banco emisor del cheque es obligatorio (mínimo 4 caracteres).");
                    }
                }
                numeroPago++;
            }
        }
    }

    // =========================================================================
    // 6. VALIDACIONES POR TIPO DE DTE
    // =========================================================================

    private void validarPorTipoDte(DocumentoElectronico dte, ResultadoValidacion r) {
        String tipo = dte.getTipoDocumento();
        if (isBlank(tipo)) return;

        switch (tipo) {
            case "1": // Factura Electrónica
                validarFactura(dte, r);
                break;
            case "5": // Nota de Crédito
            case "6": // Nota de Débito
                validarNotaCreditoDebito(dte, r);
                break;
            case "7": // Nota de Remisión
                validarRemision(dte, r);
                break;
            default:
                break;
        }
    }

    private void validarFactura(DocumentoElectronico dte, ResultadoValidacion r) {
        // Indicador de Presencia: 1–6 o 9
        Integer indPres = dte.getIndicadorPresencia();
        if (indPres == null) {
            r.agregar("FACTURA: El indicador de presencia (iIndPres) es obligatorio. Valores: 1=Presencial, 2=Electrónico, 3=Telemarketing, 4=Domicilio, 5=Bancaria, 6=Cíclica, 9=Otro.");
        } else if (!(indPres >= 1 && indPres <= 6) && indPres != 9) {
            r.agregar("FACTURA: Indicador de presencia inválido: " + indPres + ". Valores válidos: 1-6 o 9.");
        }
    }

    private void validarNotaCreditoDebito(DocumentoElectronico dte, ResultadoValidacion r) {
        String tipo = "5".equals(dte.getTipoDocumento()) ? "Nota de Crédito" : "Nota de Débito";

        // Motivo de emisión obligatorio (1 al 8)
        if (isBlank(dte.getMotivoEmision())) {
            r.agregar(tipo + ": El motivo de emisión es obligatorio. Valores válidos: "
                    + "1=Devolución y Ajuste, 2=Devolución, 3=Descuento, 4=Bonificación, "
                    + "5=Crédito incobrable, 6=Recupero de costo, 7=Recupero de gasto, 8=Ajuste de precio.");
        } else {
            try {
                int motId = Integer.parseInt(dte.getMotivoEmision().trim());
                if (motId < 1 || motId > 8) {
                    r.agregar(tipo + ": El motivo de emisión debe ser un número entre 1 y 8. Recibido: " + motId + ".");
                }
            } catch (NumberFormatException e) {
                r.agregar(tipo + ": El motivo de emisión debe ser un número entero. Recibido: '" + dte.getMotivoEmision() + "'.");
            }
        }

        // CDC del documento asociado: exactamente 44 dígitos numéricos
        String cdcAsoc = dte.getCdcDocumentoAsociado();
        if (isBlank(cdcAsoc)) {
            r.agregar(tipo + ": El CDC del comprobante asociado es obligatorio. Ingrese el código de 44 dígitos del documento original.");
        } else if (cdcAsoc.length() != 44 || !cdcAsoc.matches("[0-9]{44}")) {
            r.agregar(tipo + ": El CDC del comprobante asociado debe tener exactamente 44 dígitos numéricos. "
                    + "Longitud recibida: " + cdcAsoc.length() + " dígito(s).");
        }
    }

    private void validarRemision(DocumentoElectronico dte, ResultadoValidacion r) {
        // Motivo de traslado obligatorio: valores 1-14 o 99
        if (isBlank(dte.getMotivoEmision())) {
            r.agregar("NOTA DE REMISIÓN: El motivo de traslado es obligatorio. "
                    + "Valores válidos: 1=Traslado por ventas, 2=Consignación, 3=Exportación, "
                    + "4=Traslado por compra, 5=Importación, 6=Devolución, 7=Entre locales, "
                    + "8=Transformación, 9=Reparación, 10=Emisor móvil, 11=Exhibición, "
                    + "12=Ferias, 13=Encomienda, 14=Decomiso, 99=Otro.");
        } else {
            try {
                int motId = Integer.parseInt(dte.getMotivoEmision().trim());
                boolean esValido = (motId >= 1 && motId <= 14) || motId == 99;
                if (!esValido) {
                    r.agregar("NOTA DE REMISIÓN: El motivo de traslado '" + motId + "' no es válido. "
                            + "Use un valor entre 1 y 14, o 99 para 'Otro'.");
                }
            } catch (NumberFormatException e) {
                r.agregar("NOTA DE REMISIÓN: El motivo de traslado debe ser un número entero. "
                        + "Recibido: '" + dte.getMotivoEmision() + "'.");
            }
        }

        // Transporte obligatorio
        Transporte t = dte.getTransporte();
        if (t == null) {
            r.agregar("NOTA DE REMISIÓN: Los datos de transporte son obligatorios. "
                    + "Debe completar al menos los datos del chofer y el vehículo.");
            return;
        }

        // Responsable de emisión: 1-5
        Integer respEmi = t.getResponsableEmision();
        if (respEmi != null && (respEmi < 1 || respEmi > 5)) {
            r.agregar("NOTA DE REMISIÓN: El responsable de emisión no es válido. "
                    + "Valores permitidos: 1=Emisor del Comprobante, 2=Receptor del Comprobante, "
                    + "3=Empresa Transportista, 4=Despachante de Aduanas, 5=Agente de Transporte.");
        }

        // Nombre del chofer: al menos 4 caracteres
        if (isBlank(t.getNombreChofer()) || t.getNombreChofer().trim().length() < 4) {
            r.agregar("NOTA DE REMISIÓN: El nombre del chofer es obligatorio y debe tener al menos 4 caracteres.");
        }

        // Documento del chofer
        if (isBlank(t.getNumeroDocumentoChofer())) {
            r.agregar("NOTA DE REMISIÓN: El número de documento del chofer es obligatorio.");
        }

        // Matrícula del vehículo
        if (isBlank(t.getMatriculaVehiculo())) {
            r.agregar("NOTA DE REMISIÓN: La matrícula del vehículo es obligatoria.");
        }

        // Validación de ubicación del receptor para Remisión
        if (dte.getReceptorCodigoDepartamento() == null) {
            r.agregar("NOTA DE REMISIÓN: El código de departamento del receptor es obligatorio.");
        }
        if (dte.getReceptorCodigoCiudad() == null) {
            r.agregar("NOTA DE REMISIÓN: El código de ciudad del receptor es obligatorio.");
        }
    }

    // =========================================================================
    // 7. VALIDACIONES CRUZADAS DE TOTALES (Consistencia financiera)
    // =========================================================================

    private void validarTotalesConsistencia(DocumentoElectronico dte, ResultadoValidacion r) {
        List<ItemDocumento> items = dte.getItems();
        if (items == null || items.isEmpty()) return;

        // Suma real desde los ítems
        double sumaItems = items.stream()
                .mapToDouble(i -> i.getMontoTotalItem() != null ? i.getMontoTotalItem() : 0)
                .sum();
        sumaItems = Math.round(sumaItems * 100.0) / 100.0;

        double totalOpe = dte.getTotalOperacion() != null ? dte.getTotalOperacion() : 0;
        totalOpe = Math.round(totalOpe * 100.0) / 100.0;

        if (Math.abs(sumaItems - totalOpe) > 1.0) {
            r.agregar(String.format(
                    "TOTALES: La suma de los montos de ítems (%.2f) no coincide con el totalOperacion registrado (%.2f).",
                    sumaItems, totalOpe));
        }

        // Validar IVA calculado por ítem
        for (ItemDocumento item : items) {
            if (item.getTasaIva() == null || item.getTasaIva() == 0) continue;
            double subtotal = item.getMontoTotalItem() != null ? item.getMontoTotalItem() : 0;
            double ivaEsperado;
            if (item.getTasaIva() == 10) {
                // IVA incluido: subtotal / 11
                ivaEsperado = Math.round((subtotal / 11.0) * 100.0) / 100.0;
            } else { // 5%
                ivaEsperado = Math.round((subtotal / 21.0) * 100.0) / 100.0;
            }
            double ivaCalculado = item.getMontoIvaItem() != null ? Math.round(item.getMontoIvaItem() * 100.0) / 100.0 : 0;
            if (Math.abs(ivaCalculado - ivaEsperado) > 1.0) {
                r.agregar(String.format(
                        "TOTALES: El IVA del ítem '%s' debería ser %.2f (tasa %.0f%%) pero se recibió %.2f.",
                        item.getDescripcion(), ivaEsperado, item.getTasaIva(), ivaCalculado));
            }
        }

        // IVA total consistente
        double ivaTotal10 = dte.getTotalIva10() != null ? dte.getTotalIva10() : 0;
        double ivaTotal5 = dte.getTotalIva5() != null ? dte.getTotalIva5() : 0;
        double ivaTotalSuma = Math.round((ivaTotal10 + ivaTotal5) * 100.0) / 100.0;
        double ivaDeclarado = dte.getTotalIva() != null ? Math.round(dte.getTotalIva() * 100.0) / 100.0 : 0;
        if (Math.abs(ivaTotalSuma - ivaDeclarado) > 1.0) {
            r.agregar(String.format(
                    "TOTALES: El totalIva declarado (%.2f) no coincide con Iva10 + Iva5 (%.2f).",
                    ivaDeclarado, ivaTotalSuma));
        }
    }

    // =========================================================================
    // UTILIDAD
    // =========================================================================

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
