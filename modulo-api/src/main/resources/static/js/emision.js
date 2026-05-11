// --- Form Logic ---

function initForm() {
    const form = document.getElementById('formDte');
    const tipoDteSelect = document.getElementById('tipoDocumento');
    const btnLookup = document.getElementById('btnLookupRuc');
    const btnAddItem = document.getElementById('btnAddItem');

    tipoDteSelect.onchange = () => {
        toggleSections(tipoDteSelect.value);
    };

    btnLookup.onclick = lookupRuc;
    document.getElementById('ducReceptor').onblur = (e) => {
        if (e.target.value.length >= 6) lookupRuc();
    };

    btnAddItem.onclick = () => addItemRow();

    document.querySelectorAll('.btn-dev').forEach(btn => {
        btn.onclick = (e) => {
            e.preventDefault();
            loadMockData(btn.dataset.mock);
        };
    });

    form.onsubmit = async (e) => {
        e.preventDefault();
        await submitDte();
    };

    // Vincular filtrado de ciudades
    const deptoSelect = document.getElementById('emisorCodDepto');
    if (deptoSelect) {
        deptoSelect.onchange = (e) => loadCiudades(e.target.value);
    }

    // Vincular selector de empresas
    const empresaSelect = document.getElementById('selectEmpresaEmisora');
    if (empresaSelect) {
        loadEmpresasEmisoras();
        empresaSelect.onchange = (e) => populateEmisorData(e.target.value);
    }
}

async function loadEmpresasEmisoras() {
    const select = document.getElementById('selectEmpresaEmisora');
    if (!select) return;
    
    try {
        const response = await fetch('/api/v1/empresas');
        const empresas = await response.json();
        
        window.zentraEmpresas = empresas;
        
        select.innerHTML = '<option value="">-- Seleccionar Empresa --</option>';
        empresas.forEach(emp => {
            const opt = document.createElement('option');
            opt.value = emp.ruc;
            opt.text = `${emp.razonSocial} (${emp.ruc})`;
            select.appendChild(opt);
        });

        // Intentar preseleccionar la empresa activa del sistema
        preseleccionarEmpresa(empresas);
    } catch (error) {
        console.error('Error cargando empresas:', error);
        select.innerHTML = '<option value="">Error al cargar</option>';
    }
}

function preseleccionarEmpresa(empresasObj) {
    const select = document.getElementById('selectEmpresaEmisora');
    if (!select) return;
    
    // Si no pasamos empresas, usamos la caché global
    const empresas = empresasObj || window.zentraEmpresas || [];
    if (empresas.length === 0) return;

    const empresaActivaStr = localStorage.getItem('empresa_activa');
    let empresaSeleccionada = false;
    
    if (empresaActivaStr) {
        try {
            const empActiva = JSON.parse(empresaActivaStr);
            const empresaEncontrada = empresas.find(e => e.ruc === empActiva.ruc || e.id === empActiva.id);
            if (empresaEncontrada) {
                select.value = empresaEncontrada.ruc;
                populateEmisorData(empresaEncontrada.ruc);
                empresaSeleccionada = true;
            }
        } catch (e) {
            console.warn('Error al leer empresa_activa de localStorage', e);
        }
    }

    // Si no hay empresa activa seleccionada y solo hay una, cargarla por defecto
    if (!empresaSeleccionada && empresas.length === 1) {
        select.value = empresas[0].ruc;
        populateEmisorData(empresas[0].ruc);
    }
}

function populateEmisorData(ruc) {
    if (!ruc || !window.zentraEmpresas) return;
    
    const emp = window.zentraEmpresas.find(e => e.ruc === ruc);
    if (!emp) return;
    
    document.getElementById('emisorRuc').value = emp.ruc || '';
    document.getElementById('emisorDv').value = emp.dv || '';
    document.getElementById('emisorRazonSocial').value = emp.razonSocial || '';
    document.getElementById('emisorActividad').value = emp.actividadEconomica || '';
    document.getElementById('emisorDireccion').value = emp.direccion || '';
    document.getElementById('emisorTelefono').value = emp.telefono || '';
    document.getElementById('emisorEmail').value = emp.email || '';
    
    if (emp.codDepartamento) {
        document.getElementById('emisorCodDepto').value = emp.codDepartamento;
        loadCiudades(emp.codDepartamento).then(() => {
            if (emp.codCiudad) document.getElementById('emisorCodCiudad').value = emp.codCiudad;
        });
    }

    if (emp.codEstablecimiento) document.getElementById('codEstablecimiento').value = emp.codEstablecimiento;
    if (emp.puntoExpedicion) document.getElementById('puntoExpedicion').value = emp.puntoExpedicion;
    
    // Datos de Timbrado y Seguridad (Fase A6+)
    if (emp.timbrado) document.getElementById('timbrado').value = emp.timbrado;
    if (emp.fechaInicioTimbrado) document.getElementById('fechaInicioTimbrado').value = emp.fechaInicioTimbrado;
    if (emp.fechaVencimientoTimbrado) document.getElementById('fechaVencimientoTimbrado').value = emp.fechaVencimientoTimbrado;
    if (emp.ambiente) {
        // Mapear "TEST" -> 1, "PROD" -> 2 si es necesario, o usar el valor directo
        const ambValue = (emp.ambiente === 'PROD' || emp.ambiente === 'PRODUCCION') ? '2' : '1';
        document.getElementById('ambienteSifen').value = ambValue;
    }
    if (emp.tipoContribuyente) document.getElementById('emisorTipoContribuyente').value = emp.tipoContribuyente;
    if (emp.idCsc) document.getElementById('idCsc').value = emp.idCsc;
    if (emp.valorCsc) document.getElementById('valorCsc').value = emp.valorCsc;
    
    showToast(`Emisor: ${emp.razonSocial} cargado`, 'info');
}

// --- Tablas de Referencia Dinámicas ---

async function initReferencias() {
    console.log("Cargando tablas de referencia SIFEN...");
    const dynamicSelects = document.querySelectorAll('.dynamic-ref');
    
    for (const select of dynamicSelects) {
        const tipo = select.dataset.ref;
        if (!tipo) continue;
        
        try {
            const response = await fetch(`/api/v1/referencia/${tipo}`);
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            const data = await response.json();
            
            // Limpiar y cargar
            const currentValue = select.value;
            select.innerHTML = '';
            
            if (data.length === 0) {
                select.innerHTML = '<option value="">Sin datos</option>';
                continue;
            }

            data.forEach(ref => {
                const opt = document.createElement('option');
                opt.value = ref.codigo;
                opt.text = `${ref.descripcion} (${ref.codigo})`;
                // Si tiene valor auxiliar (abreviatura, %), mostrarlo
                if (ref.valorAux) {
                    opt.text = `${ref.descripcion} [${ref.valorAux}]`;
                }
                select.appendChild(opt);
            });

            // Guardar en caché global
            window.sifenRefData[tipo] = data;

            // Restaurar valor anterior si existía
            if (currentValue) select.value = currentValue;
            
            // Si es departamento, disparar carga de ciudades
            if (tipo === 'DEPARTAMENTO' && data.length > 0) {
                loadCiudades(select.value);
            }
        } catch (error) {
            console.error(`Error cargando referencia ${tipo}:`, error);
            select.innerHTML = `<option value="">Error al cargar</option>`;
        }
    }
}

async function loadCiudades(codDepto) {
    const ciudadSelect = document.getElementById('emisorCodCiudad');
    if (!ciudadSelect) return;
    
    ciudadSelect.innerHTML = '<option value="">Cargando ciudades...</option>';
    
    try {
        const response = await fetch(`/api/v1/referencia/ciudad/${codDepto}`);
        const data = await response.json();
        
        ciudadSelect.innerHTML = '';
        if (data.length === 0) {
            ciudadSelect.innerHTML = '<option value="">No hay ciudades para este Depto.</option>';
            return;
        }

        data.forEach(ref => {
            const opt = document.createElement('option');
            opt.value = ref.codigo;
            opt.text = `${ref.descripcion} (${ref.codigo})`;
            ciudadSelect.appendChild(opt);
        });
    } catch (error) {
        console.error('Error cargando ciudades:', error);
        ciudadSelect.innerHTML = '<option value="">Error ciudades</option>';
    }
}

function toggleSections(type) {
    const sectionFactura = document.getElementById('sectionFactura');
    const sectionAsociado = document.getElementById('sectionAsociado');
    const sectionTransporte = document.getElementById('sectionTransporte');
    const groupNatVen = document.getElementById('groupNatVen');

    sectionAsociado.classList.add('hidden');
    sectionTransporte.classList.add('hidden');
    sectionFactura.classList.remove('hidden');
    if (groupNatVen) groupNatVen.classList.add('hidden');

    if (type === '4') { // Autofactura
        if (groupNatVen) groupNatVen.classList.remove('hidden');
    } else if (type === '5' || type === '6') { // NC / ND
        sectionAsociado.classList.remove('hidden');
        const motivoSelect = document.getElementById('motivoEmision');
        if (type === '5') {
            motivoSelect.innerHTML = `
                <option value="1">1 - Devolución y ajuste de precios</option>
                <option value="2">2 - Devolución</option>
                <option value="3">3 - Descuento</option>
                <option value="4">4 - Bonificación</option>
                <option value="5">5 - Crédito incobrable</option>
            `;
        } else {
            motivoSelect.innerHTML = `
                <option value="1">1 - Intereses</option>
                <option value="2">2 - Gastos de papelería</option>
                <option value="3">3 - Gastos administrativos</option>
                <option value="4">4 - Otros</option>
            `;
        }
    } else if (type === '7') { // Remisión
        sectionTransporte.classList.remove('hidden');
        sectionFactura.classList.add('hidden');
    }
    toggleCuotas(); // Initial check
}

function toggleCuotas() {
    const sectionCuotas = document.getElementById('sectionCuotas');
    const condicion = document.getElementById('condicionOperacion');
    const isCredito = condicion && condicion.value === '2';
    
    if (isCredito) {
        sectionCuotas.classList.remove('hidden');
        if (document.getElementById('tbodyCuotas').children.length === 0) {
            addCuotaRow(); // Add first cuota
        }
    } else {
        sectionCuotas.classList.add('hidden');
    }
}

function addCuotaRow(nro = null, fecha = null, monto = 0) {
    const tbody = document.getElementById('tbodyCuotas');
    const rowCount = nro || (tbody.children.length + 1);
    const row = document.createElement('tr');
    
    // Default vencimiento: Today + 30 days
    if (!fecha) {
        const date = new Date();
        date.setDate(date.getDate() + (30 * rowCount));
        fecha = date.toISOString().split('T')[0];
    }
    
    row.innerHTML = `
        <td><input type="number" class="form-control text-center cuota-nro" value="${rowCount}"></td>
        <td><input type="date" class="form-control cuota-fecha" value="${fecha}"></td>
        <td><input type="text" class="form-control text-right cuota-monto" value="${formatCurrency(monto)}" 
            onkeyup="this.value = formatCurrency(this.value.replace(/\\./g, ''))" onclick="this.select()"></td>
        <td class="text-center">
            <button type="button" class="btn-icon text-danger" onclick="this.closest('tr').remove()"><i class="fas fa-trash"></i></button>
        </td>
    `;
    tbody.appendChild(row);
}

function generateAutoCuotas() {
    const cant = parseInt(document.getElementById('cantCuotasGen').value) || 1;
    const totalRaw = document.getElementById('totalGral').innerText.replace(/\./g, '');
    const total = parseFloat(totalRaw) || 0;
    
    if (total <= 0) {
        return showToast('El total de la factura debe ser mayor a 0', 'warning');
    }
    
    const montoCuota = Math.floor(total / cant);
    const residuo = total % cant;
    
    const tbody = document.getElementById('tbodyCuotas');
    tbody.innerHTML = '';
    
    for (let i = 1; i <= cant; i++) {
        let monto = montoCuota;
        // Asignar el residuo a la última cuota para cuadrar el total exacto
        if (i === cant) monto += residuo;
        
        const date = new Date();
        date.setDate(date.getDate() + (30 * i));
        const fecha = date.toISOString().split('T')[0];
        
        addCuotaRow(i, fecha, monto);
    }
    
    showToast(`${cant} cuotas generadas automáticamente`, 'success');
}

async function lookupRuc() {
    const rucInput = document.getElementById('ducReceptor');
    if (!rucInput) return;

    let ruc = rucInput.value.trim();
    if (!ruc) return;

    // Si tiene guión, lo limpiamos para la consulta base si fuera necesario, 
    // pero el backend ya maneja la limpieza.
    
    const btnLookup = document.getElementById('btnLookupRuc');
    const originalHtml = btnLookup.innerHTML;
    
    btnLookup.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    btnLookup.disabled = true;
    
    try {
        const response = await fetch(`/api/v1/emision/consultar-ruc?ruc=${encodeURIComponent(ruc)}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        
        const data = await response.json();
        
        if (data && data.razonSocial) {
            document.getElementById('razonSocialReceptor').value = data.razonSocial;
            // Si el backend devolvió el DV, podríamos intentar ponerlo si tuviéramos un campo separado,
            // pero ducReceptor suele incluir el DV en este flujo.
            showToast('Datos del receptor recuperados', 'success');
        } else {
            showToast('No se encontraron datos para este RUC', 'warning');
        }
    } catch (error) {
        console.error('RUC Lookup error:', error);
        showToast('Error al consultar RUC en el servidor', 'error');
    } finally {
        btnLookup.innerHTML = originalHtml;
        btnLookup.disabled = false;
    }
}

function addItemRow(codigo = '', descripcion = '', cantidad = 1, precio = 0, tasa = 10) {
    const tbody = document.getElementById('tbodyItems');
    const tr = document.createElement('tr');
    const id = Date.now() + Math.random();
    
    tr.id = `item-${id.toString().replace('.', '')}`;
    tr.innerHTML = `
        <td><input type="text" class="form-control item-codigo" value="${codigo}" placeholder="001"></td>
        <td><input type="text" class="form-control item-desc" value="${descripcion}" placeholder="Nombre del producto"></td>
        <td><input type="number" class="form-control item-cant" value="${cantidad}" onchange="calculateTotals()"></td>
        <td><input type="number" class="form-control item-precio" value="${precio}" onchange="calculateTotals()"></td>
        <td>
            <select class="form-select item-tasa" onchange="calculateTotals()">
                ${(window.sifenRefData['TIPO_IMPUESTO'] || []).map(ref => `
                    <option value="${ref.valorAux || 0}" ${parseFloat(ref.valorAux) === tasa ? 'selected' : ''}>
                        ${ref.descripcion}
                    </option>
                `).join('') || `
                    <option value="10" ${tasa === 10 ? 'selected' : ''}>10%</option>
                    <option value="5" ${tasa === 5 ? 'selected' : ''}>5%</option>
                    <option value="0" ${tasa === 0 ? 'selected' : ''}>Exenta</option>
                `}
            </select>
        </td>
        <td class="item-subtotal text-right">${formatCurrency(cantidad * precio)}</td>
        <td><button type="button" class="btn-icon danger" onclick="removeItemRow('${tr.id}')"><i class="fas fa-trash"></i></button></td>
    `;
    tbody.appendChild(tr);
    calculateTotals();
}

window.removeItemRow = function(id) {
    document.getElementById(id).remove();
    calculateTotals();
};

window.calculateTotals = function() {
    let exenta = 0, iva5Val = 0, iva10Val = 0, gravada5 = 0, gravada10 = 0, total = 0;
    
    document.querySelectorAll('#tbodyItems tr').forEach(tr => {
        const cant = parseFloat(tr.querySelector('.item-cant').value) || 0;
        const precio = parseFloat(tr.querySelector('.item-precio').value) || 0;
        const tasa = parseFloat(tr.querySelector('.item-tasa').value);
        const subtotal = cant * precio;
        
        tr.querySelector('.item-subtotal').innerText = formatCurrency(subtotal);
        
        if (tasa === 10) {
            let liquidacion = subtotal / 11;
            iva10Val += liquidacion;
            gravada10 += (subtotal - liquidacion);
        } else if (tasa === 5) {
            let liquidacion = subtotal / 21;
            iva5Val += liquidacion;
            gravada5 += (subtotal - liquidacion);
        } else {
            exenta += subtotal;
        }
        
        total += subtotal;
    });

    document.getElementById('totalExenta').innerText = formatCurrency(exenta);
    document.getElementById('totalGravada5').innerText = formatCurrency(gravada5);
    document.getElementById('totalIva5').innerText = formatCurrency(iva5Val);
    document.getElementById('totalGravada10').innerText = formatCurrency(gravada10);
    document.getElementById('totalIva10').innerText = formatCurrency(iva10Val);
    document.getElementById('totalGral').innerText = formatCurrency(total);
};

async function submitDte() {
    const btn = document.getElementById('btnSubmitDte');
    const oldHtml = btn.innerHTML;
    
    // 0. Limpiar errores previos
    const contenedor = document.getElementById('validationErrors');
    if (contenedor) contenedor.innerHTML = '';
    
    // 1. Recopilar datos generales
    const tipoDoc = document.getElementById('tipoDocumento').value;
    const estab = document.getElementById('codEstablecimiento').value;
    const pexp = document.getElementById('puntoExpedicion').value;
    const nro = document.getElementById('numeroComprobante').value;
    const timbrado = document.getElementById('timbrado').value;
    const rucRec = document.getElementById('ducReceptor').value;
    const razonRec = document.getElementById('razonSocialReceptor').value;
    const direccionRec = document.getElementById('direccionReceptor').value;
    const telefonoRec = document.getElementById('telefonoReceptor').value;
    const emailRec = document.getElementById('emailReceptor').value;
    
    // 2. Validaciones Comunes
    if (!estab || estab.length !== 3) return showToast('Establecimiento debe tener 3 dígitos', 'error');
    if (!pexp || pexp.length !== 3) return showToast('Punto de Expedición debe tener 3 dígitos', 'error');
    if (!nro || nro.length !== 7) return showToast('Número de Comprobante debe tener 7 dígitos', 'error');
    if (!timbrado || timbrado.length !== 8) return showToast('Timbrado debe tener 8 dígitos', 'error');
    if (!rucRec) return showToast('Debe ingresar el RUC o CI del receptor', 'error');
    if (!razonRec) return showToast('Debe ingresar la Razón Social del receptor', 'error');

    // 3. Recopilar Ítems
    const items = [];
    document.querySelectorAll('#tbodyItems tr').forEach(tr => {
        items.push({
            codigo: tr.querySelector('.item-codigo').value,
            descripcion: tr.querySelector('.item-desc').value,
            cantidad: parseFloat(tr.querySelector('.item-cant').value) || 0,
            precioUnitario: parseFloat(tr.querySelector('.item-precio').value) || 0,
            tasaIva: parseFloat(tr.querySelector('.item-tasa').value)
        });
    });

    if (items.length === 0) return showToast('Debe agregar al menos un ítem', 'error');

    // 4. Validaciones de Ítems
    for (const item of items) {
        // dCodInt es obligatorio según XSD SIFEN v150
        if (!item.codigo || item.codigo.trim() === '') {
            return showToast(`El código interno (dCodInt) del ítem "${item.descripcion || 'sin nombre'}" es obligatorio según SIFEN.`, 'error');
        }
        if (item.codigo.length > 20) {
            return showToast(`El código del ítem "${item.descripcion}" supera los 20 caracteres permitidos.`, 'error');
        }
        if (!item.descripcion || item.descripcion.trim() === '') {
            return showToast(`La descripción del ítem con código "${item.codigo}" no puede estar vacía.`, 'error');
        }
        if (item.cantidad <= 0) return showToast(`La cantidad del ítem "${item.descripcion}" debe ser mayor a 0`, 'error');
        if (item.precioUnitario < 0) return showToast(`El precio del ítem "${item.descripcion}" no puede ser negativo`, 'error');
        if (item.precioUnitario <= 0 && tipoDoc !== '7') {
            return showToast(`El precio del ítem "${item.descripcion}" debe ser mayor a 0 para este tipo de DTE`, 'error');
        }
    }

    const totalGralLabel = document.getElementById('totalGral').innerText.replace(/\./g, '');
    const totalGral = parseFloat(totalGralLabel) || 0;

    // 5. Validaciones Específicas
    if (tipoDoc === '1') {
        const condicion = document.getElementById('condicionOperacion').value;
        if (condicion === '2') {
            const rowsCuotas = Array.from(document.getElementById('tbodyCuotas').children);
            if (rowsCuotas.length === 0) return showToast('Ventas a crédito requieren plan de pagos', 'error');
            
            let totalCuotasVal = 0;
            rowsCuotas.forEach(row => {
                totalCuotasVal += parseFloat(row.querySelector('.cuota-monto').value.replace(/\./g, '').replace(',', '.')) || 0;
            });
            
            if (Math.abs(totalCuotasVal - totalGral) > 1) {
                return showToast(`El plan de pagos (${formatCurrency(totalCuotasVal)}) no coincide con el total (${formatCurrency(totalGral)})`, 'warning');
            }
        }
    } else if (tipoDoc === '5' || tipoDoc === '6') {
        const cdc = document.getElementById('cdcAsociado').value.trim();
        if (cdc.length !== 44) {
            return showToast('CDC asociado inválido (debe tener exactamente 44 dígitos)', 'error');
        }
        if (!/^\d+$/.test(cdc)) {
            return showToast('El CDC solo puede contener números', 'error');
        }
    } else if (tipoDoc === '7') {
        if (!document.getElementById('nombreChofer').value || !document.getElementById('patente').value) {
            return showToast('Faltan datos de transporte (Chofer/Patente)', 'error');
        }
    }

    // 6. Preparar envío
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
    
    try {
        const payload = {
            tipoDocumento: tipoDoc,
            establecimiento: estab,
            puntoExpedicion: pexp,
            numero: nro,
            timbrado: timbrado,
            ambiente: parseInt(document.getElementById('ambienteSifen').value) || 1,
            formatoKuDE: document.getElementById('kudeFormat') ? document.getElementById('kudeFormat').value : 'A4',
            iTiOpe: parseInt(document.getElementById('iTiOpe').value) || 1,
            iIndPres: parseInt(document.getElementById('iIndPres').value) || 1,
            emisor: {
                ruc: document.getElementById('emisorRuc').value,
                dv: document.getElementById('emisorDv').value,
                tipoContribuyente: parseInt(document.getElementById('emisorTipoContribuyente').value),
                razonSocial: document.getElementById('emisorRazonSocial').value,
                actividadEconomica: document.getElementById('emisorActividad').value,
                direccion: document.getElementById('emisorDireccion').value,
                telefono: document.getElementById('emisorTelefono').value,
                email: document.getElementById('emisorEmail').value,
                codDepartamento: parseInt(document.getElementById('emisorCodDepto').value),
                codCiudad: parseInt(document.getElementById('emisorCodCiudad').value)
            },
            receptor: {
                ruc: rucRec ? rucRec.replace(/\./g, '') : '',
                razonSocial: razonRec,
                direccion: direccionRec,
                telefono: telefonoRec,
                email: emailRec,
                tipoReceptor: rucRec.includes('-') ? 1 : 2,
                cPaisReceptor: document.getElementById('cPaisReceptor').value || 'PRY'
            },
            naturalezaVendedor: tipoDoc === '4' ? parseInt(document.getElementById('naturalezaVendedor').value) : null,
            condicionOperacion: document.getElementById('condicionOperacion') ? document.getElementById('condicionOperacion').value : '1',
            cuotas: Array.from(document.getElementById('tbodyCuotas').children).map(row => ({
                numero: row.querySelector('.cuota-nro').value,
                vencimiento: row.querySelector('.cuota-fecha').value,
                monto: parseFloat(row.querySelector('.cuota-monto').value.replace(/\./g, '').replace(',', '.')) || 0
            })),
            items: items,
            cdcDocumentoAsociado: document.getElementById('cdcAsociado').value,
            motivoEmision: document.getElementById('motivoEmision').value,
            transporte: {
                nombreChofer: document.getElementById('nombreChofer').value,
                numeroDocumentoChofer: document.getElementById('docChofer').value,
                matriculaVehiculo: document.getElementById('patente').value,
                motivoTraslado: document.getElementById('motivoTraslado').value,
                kmsRecorrido: parseInt(document.getElementById('kmsRecorrido').value) || 10,
                descVehiculo: document.getElementById('descVehiculo').value
            },
            ambiente: (sessionStorage.getItem('zentra-env') || 'dev') === 'dev' ? 'TEST' : 'PRODUCCION'
        };
        
        const selectorEnvio = document.getElementById('tipoEnvio');
        if (selectorEnvio) {
            payload.tipoEnvio = selectorEnvio.value;
        }

        const response = await fetch('/api/v1/emision/generar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const res = await response.json();
            if (res.ticket) {
                showToast(`Lote en proceso SIFEN. Ticket: ${res.ticket}`, 'info');
                // Alerta prolongada para Lote
                setTimeout(() => Notifier.alert("El DTE se envió por lote.\nTicket SIFEN: " + res.ticket + "\nPuedes consultar su estado desde el Dashboard."), 500);
            } else {
                showToast(`DTE Emitido con éxito. CDC: ${res.cdc}`, 'success');
            }
            switchView('dashboard');
            loadDocumentos();
        } else {
            const err = await response.json();
            // Si el backend devuelve una lista de errores de validación SIFEN (HTTP 422)
            if (err.errores && Array.isArray(err.errores) && err.errores.length > 0) {
                const contenedor = document.getElementById('validationErrors');
                if (contenedor) {
                    contenedor.innerHTML = `
                        <div class="validation-error-box">
                            <strong><i class="fas fa-exclamation-triangle"></i> Errores de validación SIFEN:</strong>
                            <ul>${err.errores.map(e => `<li>${e}</li>`).join('')}</ul>
                        </div>`;
                    contenedor.scrollIntoView({ behavior: 'smooth' });
                } else {
                    showToast(`Validación: ${err.errores[0]}`, 'error');
                }
                showToast(`${err.errores.length} error(es) de validación. Ver detalles arriba.`, 'error');
            } else {
                if (err.mensajeUsuario) {
                    showToast(err.mensajeUsuario, 'error');
                } else if (err.codigoSifen) {
                    showToast(`SIFEN Rechazo [${err.codigoSifen}]: ${err.message}`, 'error');
                } else {
                    showToast(`Error: ${err.message || 'Fallo en la emisión'}`, 'error');
                }
            }
        }
    } catch (error) {
        console.error('Submit error:', error);
        showToast('Error de conexión con el servidor', 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = oldHtml;
    }
}

// --- Mock Data Engine ---

function loadMockData(type) {
    resetForm();
    const numero = Math.floor(Math.random() * 9000000 + 1000000).toString();
    document.getElementById('numeroComprobante').value = numero;
    document.getElementById('codEstablecimiento').value = '001';
    document.getElementById('puntoExpedicion').value = '001';
    document.getElementById('timbrado').value = '16770994';
    
    // Datos del Emisor (Auto-completar para evitar errores de validación)
    document.getElementById('emisorRuc').value = '80014603';
    document.getElementById('emisorDv').value = '4';
    document.getElementById('emisorTipoContribuyente').value = '2';
    document.getElementById('emisorRazonSocial').value = "REPUESTOS RG S.A.";
    document.getElementById('emisorActividad').value = 'Autopartes, servicios de mantenimiento, repuestos';
    document.getElementById('emisorDireccion').value = 'AVDA. FERNANDO DE LA MORA 1234, ASUNCION';
    document.getElementById('emisorTelefono').value = '021000000';
    document.getElementById('emisorEmail').value = 'facturacion@repuestosrg.com.py';
    document.getElementById('emisorCodDepto').value = '1';
    document.getElementById('emisorCodCiudad').value = '1';

    // Receptor Innominado (Documento 0) para aprobación en test
    document.getElementById('ducReceptor').value = '0';
    document.getElementById('razonSocialReceptor').value = 'Sin Nombre';
    if (document.getElementById('iTiOpe')) document.getElementById('iTiOpe').value = '2'; // B2C

    if (type === 'factura') {
        // Factura mixta: 10%, 5%, Exenta, 10% — prueba de validación SIFEN completa
        document.getElementById('condicionOperacion').value = '1'; // Contado
        toggleCuotas();
        addItemRow('1', 'PP1', 10, 1000, 10);
        addItemRow('2', 'PP1', 20, 2000, 5);
        addItemRow('3', 'PP2', 30, 3000, 0);
        addItemRow('4', 'PP3', 40, 4000, 10);
    } else if (type === 'nc') {
        document.getElementById('tipoDocumento').value = '5';
        toggleSections('5');
        addItemRow('AJUSTE', 'AJUSTE DE PRECIO POR DEVOLUCION', 1, 250000, 10);
    } else if (type === 'remision') {
        document.getElementById('tipoDocumento').value = '7';
        toggleSections('7');
        document.getElementById('nombreChofer').value = 'MARIO KART';
        document.getElementById('docChofer').value = '1234567';
        document.getElementById('patente').value = 'ABC-123';
        addItemRow('MAQ-01', 'TRACTOR AGRICOLA CAT', 1, 0, 0);
    }
    
    showToast(`Datos de ${type.toUpperCase()} cargados`, 'info');
}

function resetForm() {
    document.getElementById('formDte').reset();
    document.getElementById('tbodyItems').innerHTML = '';
    const contenedor = document.getElementById('validationErrors');
    if (contenedor) contenedor.innerHTML = '';
    toggleSections('1');
    calculateTotals();
    
    // Restaurar la empresa preseleccionada después de reiniciar el formulario
    if (typeof preseleccionarEmpresa === 'function') {
        setTimeout(preseleccionarEmpresa, 50); // Pequeño retraso para que el reset native termine
    }
}

