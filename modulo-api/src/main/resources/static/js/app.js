/**
 * Zentra - Dashboard Logic (Vanilla JS)
 */

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initEnvironment(); // New
    initDashboard();
    initNavigation();
    initSidebar();
    initForm();
});

// --- Environment Management ---
function initEnvironment() {
    const envSwitch = document.getElementById('envSwitch');
    const devTools = document.getElementById('devToolsSection');
    const savedEnv = localStorage.getItem('zentra-env') || 'dev';
    
    const setEnv = (env) => {
        if (env === 'prod') {
            envSwitch.checked = true;
            devTools.classList.add('hidden');
        } else {
            envSwitch.checked = false;
            devTools.classList.remove('hidden');
        }
        localStorage.setItem('zentra-env', env);
    };

    setEnv(savedEnv);

    envSwitch.addEventListener('change', () => {
        const newEnv = envSwitch.checked ? 'prod' : 'dev';
        setEnv(newEnv);
        showToast(`Cambiado a ambiente: ${newEnv.toUpperCase()}`, 'info');
    });
}
function initSidebar() {
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');
    
    if (menuToggle && sidebar && overlay) {
        const toggleAll = () => {
            sidebar.classList.toggle('active');
            overlay.classList.toggle('active');
        };
        
        menuToggle.addEventListener('click', toggleAll);
        overlay.addEventListener('click', toggleAll);
    }
}

// --- Theme Management ---

function initTheme() {
    const themeToggle = document.getElementById('btnThemeToggle');
    const savedTheme = localStorage.getItem('zentra-theme') || 'dark';
    
    setTheme(savedTheme);

    themeToggle.addEventListener('click', () => {
        const currentTheme = document.body.classList.contains('light-theme') ? 'light' : 'dark';
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        setTheme(newTheme);
    });
}

function setTheme(theme) {
    const icon = document.querySelector('#btnThemeToggle i');
    if (theme === 'light') {
        document.body.classList.add('light-theme');
        icon.className = 'fas fa-sun';
    } else {
        document.body.classList.remove('light-theme');
        icon.className = 'fas fa-moon';
    }
    localStorage.setItem('zentra-theme', theme);
}

// --- Navigation ---

function initNavigation() {
    // Current view state management
    window.switchView = function(viewId) {
        document.querySelectorAll('.content-view').forEach(view => {
            view.classList.remove('active');
        });
        
        const activeView = document.getElementById(`view-${viewId}`);
        if (activeView) {
            activeView.classList.add('active');
            
            // Auto close sidebar on mobile
            const sidebar = document.getElementById('sidebar');
            const overlay = document.getElementById('sidebarOverlay');
            if (sidebar) sidebar.classList.remove('active');
            if (overlay) overlay.classList.remove('active');

            // Sidebar link update
            document.querySelectorAll('.nav-item').forEach(item => {
                item.classList.remove('active');
                if (item.innerText.toLowerCase().includes(viewId)) {
                    item.classList.add('active');
                }
            });
            
            if (viewId === 'emision') resetForm();
            if (viewId === 'dashboard') loadDocumentos();
        }
    };
}

// --- Initialization ---

function initDashboard() {
    loadDocumentos();
    
    document.getElementById('btnRefresh').addEventListener('click', () => {
        loadDocumentos();
        showToast('Datos actualizados', 'info');
    });
}

// --- Data Loading ---

async function loadDocumentos() {
    const tbody = document.getElementById('tbodyDocumentos');
    try {
        const response = await fetch('/api/v1/emision/documentos');
        const docs = await response.json();
        
        updateStats(docs);

        if (docs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">No hay documentos emitidos</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        docs.reverse().forEach(doc => {
            const tr = document.createElement('tr');
            const statusClass = doc.estado === 'APROBADO' ? 'aprobado' : 'rechazado';
            
            tr.innerHTML = `
                <td>${formatDate(doc.fechaCreacion)}</td>
                <td><span class="badge-type">${getTipoLabel(doc.tipoDocumento)}</span></td>
                <td title="${doc.cdc}">${doc.numeroComprobante || 'N/A'}</td>
                <td>${doc.receptorRazonSocial || doc.rucReceptor || 'Consumidor Final'}</td>
                <td>${formatCurrency(doc.totalOperacion)}</td>
                <td><span class="badge-status ${statusClass}">${doc.estado || 'PENDIENTE'}</span></td>
                <td>
                    <button class="btn btn-xs btn-outline" onclick="descargarKude('${doc.id}')" title="Descargar KuDE (A4)">
                        <i class="fas fa-file-pdf"></i>
                    </button>
                    <button class="btn btn-xs btn-outline" onclick="descargarTicket('${doc.id}')" title="Descargar Ticket">
                        <i class="fas fa-receipt"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Error cargando documentos:', error);
        tbody.innerHTML = '<tr><td colspan="7" class="text-center error">Error de conexión con el servidor</td></tr>';
    }
}

function updateStats(docs) {
    const total = docs.length;
    const aprobados = docs.filter(d => d.estado === 'APROBADO').length;
    const rechazados = docs.filter(d => d.estado === 'RECHAZADO').length;
    const pendientes = total - aprobados - rechazados;

    document.getElementById('statsTotal').innerText = total;
    document.getElementById('statsAprobados').innerText = aprobados;
    document.getElementById('statsRechazados').innerText = rechazados;
    document.getElementById('statsPendientes').innerText = pendientes;
}

// --- KuDE Handling ---

async function descargarKude(id) {
    window.open(`/api/v1/emision/kude/${id}`, '_blank');
}

async function descargarTicket(id) {
    window.open(`/api/v1/emision/kude/${id}?formato=TICKET`, '_blank');
}

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
}

function toggleSections(type) {
    const sectionFactura = document.getElementById('sectionFactura');
    const sectionAsociado = document.getElementById('sectionAsociado');
    const sectionTransporte = document.getElementById('sectionTransporte');

    sectionAsociado.classList.add('hidden');
    sectionTransporte.classList.add('hidden');
    sectionFactura.classList.remove('hidden');

    if (type === '4' || type === '5') {
        sectionAsociado.classList.remove('hidden');
    } else if (type === '7') {
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
    const rucWithDv = document.getElementById('ducReceptor').value;
    if (!rucWithDv) return;

    const btnLookup = document.getElementById('btnLookupRuc');
    const rucInput = document.getElementById('ducReceptor'); // Use ducReceptor as per existing code
    
    const ruc = rucInput.value.split('-')[0].trim();
    if (!ruc) return showToast('Ingrese un RUC válido', 'error');
    
    btnLookup.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    
    try {
        const response = await fetch(`https://turuc.com.py/api/contribuyente/${ruc}`);
        if (response.ok) {
            const json = await response.json();
            // Handle both flat and nested (data.data) responses
            const data = json.data ? json.data : json;
            document.getElementById('razonSocialReceptor').value = data.razonSocial || data.razon_social || '';
            showToast('Datos cargados desde TuRuc.com.py', 'success');
        } else {
            throw new Error(`Status: ${response.status}`);
        }
    } catch (error) {
        console.error('RUC Lookup error:', error);
        // Fallback for demo when API fails (like CORS or offline)
        if (ruc === '44444401') {
             document.getElementById('razonSocialReceptor').value = 'CONTRIBUYENTE REAL S.A.';
        } else {
             document.getElementById('razonSocialReceptor').value = `CLIENTE MOCK (${ruc})`;
        }
        showToast('RUC cargado (Modo Simulado / Offline)', 'warning');
    } finally {
        btnLookup.innerHTML = '<i class="fas fa-search"></i>';
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
            <select class="form-control item-tasa" onchange="calculateTotals()">
                <option value="10" ${tasa === 10 ? 'selected' : ''}>10%</option>
                <option value="5" ${tasa === 5 ? 'selected' : ''}>5%</option>
                <option value="0" ${tasa === 0 ? 'selected' : ''}>Exenta</option>
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
    } else if (tipoDoc === '4' || tipoDoc === '5') {
        if (document.getElementById('cdcAsociado').value.length !== 44) {
            return showToast('CDC asociado inválido (debe tener 44 dígitos)', 'error');
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
            ambiente: localStorage.getItem('zentra-env') === 'prod' ? 2 : 1,
            formatoKuDE: document.getElementById('kudeFormat') ? document.getElementById('kudeFormat').value : 'A4',
            receptor: {
                ruc: rucRec,
                razonSocial: razonRec,
                direccion: direccionRec,
                telefono: telefonoRec,
                email: emailRec,
                tipoReceptor: rucRec.includes('-') ? 1 : 2
            },
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
                motivoTraslado: document.getElementById('motivoTraslado').value
            }
        };

        const response = await fetch('/api/v1/emision/generar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const res = await response.json();
            showToast(`DTE Emitido con éxito. CDC: ${res.cdc}`, 'success');
            switchView('dashboard');
            loadDashboard();
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
                showToast(`Error: ${err.message || 'Fallo en la emisión'}`, 'error');
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
    document.getElementById('timbrado').value = '12345678';
    
    // Contribuyente real mock para pruebas de RUC
    document.getElementById('ducReceptor').value = '44444401-7';
    document.getElementById('razonSocialReceptor').value = 'CONTRIBUYENTE DE PRUEBA S.A.';

    if (type === 'factura') {
        addItemRow('SERV01', 'SERVICIO DE SOPORTE TECNICO', 1, 150000, 10);
        addItemRow('PROD02', 'CABLE HDMI 2M', 2, 45000, 10);
    } else if (type === 'nc') {
        document.getElementById('tipoDocumento').value = '4';
        toggleSections('4');
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
    toggleSections('1');
    calculateTotals();
}

// --- Helpers ---

function formatDate(isoString) {
    if (!isoString) return '-';
    const date = new Date(isoString);
    return date.toLocaleString('es-PY', { day:'2-digit', month:'2-digit', year:'numeric', hour:'2-digit', minute:'2-digit' });
}

function formatCurrency(val) {
    return new Intl.NumberFormat('es-PY', { style: 'decimal' }).format(val);
}

function getTipoLabel(val) {
    const labels = { '1': 'Factura', '4': 'Nota Crédito', '5': 'Nota Débito', '7': 'Remisión' };
    return labels[val] || 'DTE';
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type} slide-up`;
    
    let icon = 'info-circle';
    if (type === 'success') icon = 'check-circle';
    if (type === 'error') icon = 'exclamation-circle';
    if (type === 'warning') icon = 'exclamation-triangle';

    toast.innerHTML = `<i class="fas fa-${icon}"></i> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 500);
    }, 4000);
}
