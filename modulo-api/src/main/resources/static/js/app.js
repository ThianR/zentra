// --- Estado Global ---
window.sifenRefData = {};

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initEnvironment();
    initReferencias(); // Nueva carga dinámica de SIFEN
    initDashboard();
    initNavigation();
    initSidebar();
    initForm();
});

// Alias para compatibilidad con flujos anteriores
function loadDashboard() {
    if (typeof switchView === 'function') switchView('dashboard');
}

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
            if (viewId === 'empresas') loadEmpresasGrid();
        }
    };

    window.toggleSection = function(id) {
        const el = document.getElementById(id);
        const parent = el.parentElement;
        if (parent.classList.contains('active')) {
            parent.classList.remove('active');
        } else {
            parent.classList.add('active');
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
            let statusClass = 'pendiente';
            if (doc.estado === 'APROBADO') statusClass = 'aprobado';
            else if (doc.estado === 'RECHAZADO' || doc.estado === 'ERROR_ENVIO') statusClass = 'rechazado';
            else if (doc.estado === 'EN_PROCESO') statusClass = 'warning';

            // Indicador visual de ticket disponible
            const tieneTicket = doc.numeroTicketLote && doc.numeroTicketLote !== '';
            const badgeTicket = tieneTicket
                ? `<span class="badge-ticket" title="Ticket: ${doc.numeroTicketLote}"><i class="fas fa-ticket-alt"></i></span>`
                : '';

            tr.innerHTML = `
                <td>${formatDate(doc.fechaCreacion)}</td>
                <td><span class="badge-type">${getTipoLabel(doc.tipoDocumento)}</span></td>
                <td title="CDC: ${doc.cdc || ''}"><strong>${doc.numeroComprobante || 'N/A'}</strong></td>
                <td>${doc.receptorRazonSocial || doc.rucReceptor || 'Consumidor Final'}</td>
                <td>${formatCurrency(doc.totalOperacion)}</td>
                <td>
                    <span class="badge-status ${statusClass}">${doc.estado || 'PENDIENTE'}</span>
                    ${badgeTicket}
                </td>
                <td class="acciones-col">
                    <button class="btn btn-xs btn-info" onclick="abrirModalDetalle(${doc.id})" title="Ver detalle y estado SIFEN">
                        <i class="fas fa-info-circle"></i>
                    </button>
                    ${(doc.estado === 'EN_PROCESO' || tieneTicket) ? `
                    <button class="btn btn-xs btn-primary" onclick="consultarLoteSifen('${doc.id}')" title="Consultar Lote en SIFEN">
                        <i class="fas fa-sync-alt"></i>
                    </button>` : ''}
                    <button class="btn btn-xs btn-outline" onclick="descargarKude('${doc.id}')" title="Descargar KuDE (A4)">
                        <i class="fas fa-file-pdf"></i>
                    </button>
                    <button class="btn btn-xs btn-outline" onclick="descargarTicket('${doc.id}')" title="Descargar Ticket">
                        <i class="fas fa-receipt"></i>
                    </button>
                    <button class="btn btn-xs btn-outline" onclick="descargarXml('${doc.id}')" title="Descargar XML Firmado">
                        <i class="fas fa-file-code"></i>
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
    const win = window.open(`/api/v1/emision/kude/${id}?formato=TICKET`, '_blank');
}

window.descargarXml = function(id) {
    const link = document.createElement('a');
    link.href = `/api/v1/emision/xml/${id}`;
    link.download = `dte_${id}.xml`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

window.consultarLoteSifen = async function(id) {
    try {
        showToast('Consultando estado del Lote en SIFEN...', 'info');
        const res = await fetch(`/api/v1/emision/consultar-lote/${id}`);
        const data = await res.json();

        if (res.ok) {
            showToast(`✓ ${data.mensajeUsuario || 'Lote aprobado por SIFEN'}`, 'success');
        } else {
            showToast(`Estado SIFEN: ${data.mensajeUsuario || data.message}`, 'warning');
        }
        loadDocumentos();
    } catch(e) {
        showToast('Error consultando lote: ' + e.message, 'error');
    }
}

// ID del documento actualmente en el modal
let _modalDocId = null;

window.abrirModalDetalle = async function(id) {
    _modalDocId = id;
    const modal = document.getElementById('modalDetalleDte');
    modal.style.display = 'flex';

    // Limpia campos previos
    ['mdEstado','mdCodigo','mdTicket','mdCdc','mdMensajeSifen','mdMensajeUsuario','mdRespuestaXml']
        .forEach(sid => { document.getElementById(sid).textContent = 'Cargando...'; });
    document.getElementById('mdRespuestaRaw').style.display = 'none';

    try {
        const res = await fetch(`/api/v1/emision/documentos/${id}`);
        if (!res.ok) throw new Error('No se pudo obtener el documento');
        const doc = await res.json();

        let estadoClass = 'pendiente';
        if (doc.estado === 'APROBADO') estadoClass = 'aprobado';
        else if (doc.estado === 'RECHAZADO' || doc.estado === 'ERROR_ENVIO') estadoClass = 'rechazado';
        else if (doc.estado === 'EN_PROCESO') estadoClass = 'warning';

        document.getElementById('mdEstado').innerHTML =
            `<span class="badge-status ${estadoClass}">${doc.estado || 'PENDIENTE'}</span>`;
        document.getElementById('mdCodigo').textContent = doc.codigoEstadoSifen || '—';
        document.getElementById('mdTicket').textContent = doc.numeroTicketLote || '(sin ticket)';
        document.getElementById('mdCdc').textContent = doc.cdc || '—';
        document.getElementById('mdMensajeSifen').textContent = doc.mensajeSifen || '—';
        document.getElementById('mdMensajeUsuario').textContent = doc.mensajeUsuario || '—';
        document.getElementById('mdRespuestaXml').textContent = doc.xmlRespuestaSifen || '(sin respuesta)';

        // Mostrar botón de consulta si tiene ticket y no está aprobado/rechazado
        const btnConsultar = document.getElementById('mdBtnConsultar');
        const puedeConsultar = doc.numeroTicketLote &&
            doc.estado !== 'APROBADO' && doc.estado !== 'RECHAZADO';
        btnConsultar.style.display = puedeConsultar ? 'inline-flex' : 'none';
    } catch(e) {
        showToast('Error cargando detalle: ' + e.message, 'error');
    }
}

window.cerrarModalDetalle = function(event) {
    // Si vino del overlay (fondo), cerrar. Si vino del botón, cerrar directamente.
    if (!event || event.target === document.getElementById('modalDetalleDte')) {
        document.getElementById('modalDetalleDte').style.display = 'none';
        _modalDocId = null;
    }
}

window.consultarLoteSifenModal = async function() {
    if (!_modalDocId) return;
    const btnConsultar = document.getElementById('mdBtnConsultar');
    btnConsultar.disabled = true;
    btnConsultar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Consultando...';
    try {
        const res = await fetch(`/api/v1/emision/consultar-lote/${_modalDocId}`);
        const data = await res.json();
        if (res.ok) {
            showToast(`✓ ${data.mensajeUsuario || 'Procesado por SIFEN'}`, 'success');
        } else {
            showToast(`SIFEN: ${data.mensajeUsuario || data.message}`, 'warning');
        }
        // Refrescar el modal con los datos actualizados
        abrirModalDetalle(_modalDocId);
        loadDocumentos();
    } catch(e) {
        showToast('Error: ' + e.message, 'error');
    } finally {
        btnConsultar.disabled = false;
        btnConsultar.innerHTML = '<i class="fas fa-sync-alt"></i> Consultar Estado en SIFEN';
    }
}

window.toggleRawXml = function() {
    const box = document.getElementById('mdRespuestaRaw');
    box.style.display = box.style.display === 'none' ? 'block' : 'none';
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
        const response = await fetch('/api/v1/emision/empresas');
        const empresas = await response.json();
        
        window.zentraEmpresas = empresas;
        
        select.innerHTML = '<option value="">-- Seleccionar Empresa --</option>';
        empresas.forEach(emp => {
            const opt = document.createElement('option');
            opt.value = emp.ruc;
            opt.text = `${emp.razonSocial} (${emp.ruc})`;
            select.appendChild(opt);
        });

        // Si solo hay una, cargarla por defecto
        if (empresas.length === 1) {
            select.value = empresas[0].ruc;
            populateEmisorData(empresas[0].ruc);
        }
    } catch (error) {
        console.error('Error cargando empresas:', error);
        select.innerHTML = '<option value="">Error al cargar</option>';
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
            ambiente: localStorage.getItem('zentra-env') === 'prod' ? 2 : 1,
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
                ruc: rucRec ? rucRec.split('-')[0].replace(/\./g, '') : '',
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
            }
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
                setTimeout(() => alert("El DTE se envió por lote.\nTicket SIFEN: " + res.ticket + "\nPuedes consultar su estado desde el Dashboard."), 500);
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

    // Contribuyente real mock para pruebas de RUC
    document.getElementById('ducReceptor').value = '44444401-7';
    document.getElementById('razonSocialReceptor').value = 'CONTRIBUYENTE DE PRUEBA S.A.';

    if (type === 'factura') {
        addItemRow('SERV01', 'SERVICIO DE SOPORTE TECNICO', 1, 150000, 10);
        addItemRow('PROD02', 'CABLE HDMI 2M', 2, 45000, 10);
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
    const labels = { 
        '1': 'Factura', 
        '4': 'Autofactura',  // Corregido: 4 es Autofactura
        '5': 'Nota de Crédito', // Corregido: 5 es NC
        '6': 'Nota de Débito',  // Corregido: 6 es ND
        '7': 'Remisión' 
    };
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

// --- Gestión de Empresas ---

async function loadEmpresasGrid() {
    const tbody = document.getElementById('tbodyListaEmpresas');
    if (!tbody) return;
    
    try {
        const response = await fetch('/api/v1/emision/empresas');
        const empresas = await response.json();
        
        if (empresas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No hay empresas registradas</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        empresas.forEach(emp => {
            const tr = document.createElement('tr');
            
            // Check si tiene certificado (boolean devuelto por backend)
            const hasCert = emp.hasCertificado === true;
            let certBadge = '';
            if (hasCert) {
                const fVenc = emp.fechaVencimientoCertificado ? `<br><small style="font-size: 0.75rem; color: var(--text-secondary);">Vence: ${emp.fechaVencimientoCertificado}</small>` : '';
                certBadge = `<div style="text-align: center;"><span class="badge-status aprobado"><i class="fas fa-check"></i> Cargado</span>${fVenc}</div>`;
            } else {
                certBadge = `<span class="badge-status rechazado"><i class="fas fa-times"></i> Faltante</span>`;
            }

            const empJson = JSON.stringify(emp).replace(/'/g, "&apos;");
            tr.innerHTML = `
                <td><strong>${emp.ruc}</strong></td>
                <td>${emp.razonSocial}</td>
                <td><span class="badge-type">${emp.ambiente || 'TEST'}</span></td>
                <td>${certBadge}</td>
                <td class="acciones-col">
                    <button class="btn btn-xs btn-outline" onclick='abrirModalEmpresa(${empJson})' title="Editar Datos">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn btn-xs btn-primary" onclick="abrirModalUpload('${emp.id}', ${hasCert})" title="Cargar / Actualizar Certificado">
                        <i class="fas fa-key"></i> ${hasCert ? 'Actualizar' : 'Cargar'}
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Error cargando empresas:', error);
        tbody.innerHTML = '<tr><td colspan="5" class="text-center error">Error de conexión con el servidor</td></tr>';
    }
}

const btnRefreshEmpresas = document.getElementById('btnRefreshEmpresas');
if(btnRefreshEmpresas) {
    btnRefreshEmpresas.addEventListener('click', () => {
        loadEmpresasGrid();
        showToast('Lista de empresas actualizada', 'info');
    });
}

window.abrirModalEmpresa = async function(empresa = null) {
    document.getElementById('formEmpresa').reset();
    
    // Cargar selectores dinámicos y esperar
    await initReferencias();

    // Vincular selectores dependientes para el modal
    const deptoSelect = document.getElementById('empresaCodDepto');
    if (deptoSelect && !deptoSelect.onchange) {
        deptoSelect.onchange = (e) => loadCiudadesModal(e.target.value);
    }
    
    // Se eliminó el onchange que seteaba la descripción manualmente

    if (empresa && empresa.id) {
        document.getElementById('modalEmpresaTitle').innerHTML = '<i class="fas fa-edit"></i> Editar Empresa';
        document.getElementById('empresaId').value = empresa.id;
        document.getElementById('empresaRuc').value = empresa.ruc || '';
        document.getElementById('empresaDv').value = empresa.dv || '';
        document.getElementById('empresaRazonSocial').value = empresa.razonSocial || '';
        document.getElementById('empresaTimbrado').value = empresa.timbrado || '';
        document.getElementById('empresaFechaVencimientoTimbrado').value = empresa.fechaVencimientoTimbrado || '';
        
        // Asignar actividad (el select ya cargó por await loadDynamicRefs)
        document.getElementById('empresaCodActividadEconomica').value = empresa.codActividadEconomica || '';
        
        document.getElementById('empresaDireccion').value = empresa.direccion || '';
        document.getElementById('empresaTelefono').value = empresa.telefono || '';
        document.getElementById('empresaEmail').value = empresa.email || '';
        document.getElementById('empresaCodEstablecimiento').value = empresa.codEstablecimiento || '001';
        document.getElementById('empresaPuntoExpedicion').value = empresa.puntoExpedicion || '001';
        
        document.getElementById('empresaAmbiente').value = empresa.ambiente || 'TEST';
        document.getElementById('empresaTipoContribuyente').value = empresa.tipoContribuyente || '2';
        
        if (empresa.codDepartamento) {
            document.getElementById('empresaCodDepto').value = empresa.codDepartamento;
            await loadCiudadesModal(empresa.codDepartamento);
            if (empresa.codCiudad) {
                document.getElementById('empresaCodCiudad').value = empresa.codCiudad;
            }
        }
    } else {
        document.getElementById('modalEmpresaTitle').innerHTML = '<i class="fas fa-building"></i> Nueva Empresa';
        document.getElementById('empresaId').value = '';
    }
    
    document.getElementById('modalFormEmpresa').style.display = 'flex';
};

window.autoCalcularDv = function() {
    const ruc = document.getElementById('empresaRuc').value.replace(/[^0-9]/g, '');
    if (!ruc) return;
    
    let total = 0;
    let factor = 2;
    for (let i = ruc.length - 1; i >= 0; i--) {
        total += parseInt(ruc[i]) * factor;
        factor++;
    }
    let resto = total % 11;
    let dv = resto > 1 ? 11 - resto : 0;
    document.getElementById('empresaDv').value = dv;
    showToast(`DV Calculado: ${dv}`, 'info');
};

// Listener para actualizar descripción de actividad automáticamente
document.addEventListener('DOMContentLoaded', () => {
    const selectAct = document.getElementById('empresaCodActividadEconomica');
    if (selectAct) {
        selectAct.addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            if (selectedOption && selectedOption.value) {
                // El texto suele ser "COD - DESCRIPCION"
                const parts = selectedOption.text.split('-');
                const desc = parts.length > 1 ? parts.slice(1).join('-').trim() : selectedOption.text;
                document.getElementById('empresaActividadEconomica').value = desc;
            }
        });
    }
});

async function loadCiudadesModal(codDepto) {
    const ciudadSelect = document.getElementById('empresaCodCiudad');
    if (!ciudadSelect) return;
    
    ciudadSelect.innerHTML = '<option value="">Cargando...</option>';
    try {
        const response = await fetch(`/api/v1/referencia/ciudad/${codDepto}`);
        const data = await response.json();
        ciudadSelect.innerHTML = '';
        data.forEach(ref => {
            const opt = document.createElement('option');
            opt.value = ref.codigo;
            opt.text = `${ref.descripcion} (${ref.codigo})`;
            ciudadSelect.appendChild(opt);
        });
    } catch (e) { console.error(e); }
}

window.cerrarModalEmpresa = function(event) {
    if (!event || event.target === document.getElementById('modalFormEmpresa')) {
        document.getElementById('modalFormEmpresa').style.display = 'none';
    }
};

window.buscarRucEmpresa = async function() {
    const inputRuc = document.getElementById('empresaRuc');
    let ruc = inputRuc.value.trim();
    if (!ruc) return showToast('Ingrese un RUC para buscar', 'warning');
    
    const icon = document.querySelector('button[onclick="buscarRucEmpresa()"] i');
    icon.className = 'fas fa-spinner fa-spin';
    
    try {
        const res = await fetch(`/api/v1/emision/consultar-ruc?ruc=${encodeURIComponent(ruc)}`);
        if(res.ok) {
            const data = await res.json();
            document.getElementById('empresaRazonSocial').value = data.razonSocial;
            inputRuc.value = data.ruc;
            document.getElementById('empresaDv').value = data.dv;
            showToast('Datos recuperados correctamente', 'success');
        } else {
            showToast('No se encontraron datos para el RUC indicado', 'error');
        }
    } catch(e) {
        showToast('Error conectando con el servicio de RUC', 'error');
    } finally {
        icon.className = 'fas fa-search';
    }
};

window.submitFormEmpresa = async function() {
    const form = document.getElementById('formEmpresa');
    if (form && !form.reportValidity()) {
        return; // Detiene si hay campos requeridos vacíos (como la fecha de vencimiento)
    }

    const empresaId = document.getElementById('empresaId').value;
    const ruc = document.getElementById('empresaRuc').value;
    const razonSocial = document.getElementById('empresaRazonSocial').value;
    const dv = document.getElementById('empresaDv').value;

    if (!ruc || !razonSocial || !dv) {
        return showToast('Los campos RUC, DV y Razón Social son requeridos', 'warning');
    }

    const codActEcon = document.getElementById('empresaCodActividadEconomica').value;
    const actDescObj = window.sifenRefData['ACTIVIDAD_ECONOMICA']?.find(a => a.codigo === codActEcon);
    const actDesc = actDescObj ? actDescObj.descripcion : '';

    const payload = {
        ruc: ruc,
        dv: dv,
        razonSocial: razonSocial,
        timbrado: document.getElementById('empresaTimbrado').value,
        fechaVencimientoTimbrado: document.getElementById('empresaFechaVencimientoTimbrado').value || null,
        codActividadEconomica: codActEcon,
        actividadEconomica: actDesc,
        direccion: document.getElementById('empresaDireccion').value,
        telefono: document.getElementById('empresaTelefono').value,
        email: document.getElementById('empresaEmail').value,
        codDepartamento: parseInt(document.getElementById('empresaCodDepto').value) || 1,
        codCiudad: parseInt(document.getElementById('empresaCodCiudad').value) || 1,
        codEstablecimiento: document.getElementById('empresaCodEstablecimiento').value,
        puntoExpedicion: document.getElementById('empresaPuntoExpedicion').value,
        ambiente: document.getElementById('empresaAmbiente').value,
        tipoContribuyente: parseInt(document.getElementById('empresaTipoContribuyente').value)
    };

    const isEdit = !!empresaId;
    const url = isEdit ? `/api/v1/empresas/${empresaId}` : '/api/v1/empresas';
    const method = isEdit ? 'PUT' : 'POST';

    const btn = document.getElementById('btnSaveEmpresa');
    const originalHtml = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';
    btn.disabled = true;

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            showToast(`✓ Empresa ${isEdit ? 'actualizada' : 'registrada'} correctamente`, 'success');
            cerrarModalEmpresa();
            loadEmpresasGrid();
            // Si hay un selector de emisor en la vista principal, refrescarlo
            if (typeof loadEmpresasEmisoras === 'function') loadEmpresasEmisoras();
        } else {
            const errorMsg = await res.text();
            showToast(errorMsg || 'Error al procesar la solicitud', 'error');
        }
    } catch (e) {
        showToast('Error de conexión con el servidor', 'error');
    } finally {
        btn.innerHTML = originalHtml;
        btn.disabled = false;
    }
};

window.abrirModalUpload = function(empresaId, hasCert = false) {
    document.getElementById('uploadCertEmpresaId').value = empresaId;
    document.getElementById('formUploadCert').reset();
    
    const infoBox = document.getElementById('certLoadedInfo');
    if (infoBox) {
        infoBox.style.display = hasCert ? 'block' : 'none';
    }
    
    document.getElementById('modalUploadCert').style.display = 'flex';
};

window.cerrarModalUpload = function(event) {
    if (!event || event.target === document.getElementById('modalUploadCert')) {
        document.getElementById('modalUploadCert').style.display = 'none';
    }
};

window.submitUploadCert = async function() {
    const empresaId = document.getElementById('uploadCertEmpresaId').value;
    const fileInput = document.getElementById('certFile');
    const passwordInput = document.getElementById('certPassword');
    
    if (!fileInput.files[0]) {
        return showToast('Por favor seleccione el archivo .p12 o .pfx', 'error');
    }
    if (!passwordInput.value) {
        return showToast('Por favor ingrese la contraseña del certificado', 'error');
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    formData.append('password', passwordInput.value);

    const btn = document.querySelector('#modalUploadCert .btn-primary');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Subiendo...';
    btn.disabled = true;

    try {
        const response = await fetch(`/api/v1/empresas/${empresaId}/certificado`, {
            method: 'POST',
            body: formData
        });

        const text = await response.text();
        if (response.ok) {
            showToast('✓ Certificado guardado correctamente', 'success');
            cerrarModalUpload();
            loadEmpresasGrid();
        } else {
            showToast('Error: ' + text, 'error');
        }
    } catch (error) {
        showToast('Error de red al subir el certificado', 'error');
    } finally {
        btn.innerHTML = originalText;
        btn.disabled = false;
    }
};
