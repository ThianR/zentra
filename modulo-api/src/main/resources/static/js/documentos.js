// --- Estado y Variables Globales de Paginación ---

let pagState = {
    currentPage: 0,
    pageSize: 50,
    totalPages: 1,
    totalElements: 0,
    filtros: null
};

// --- Inicialización ---

function initDashboard() {
    // Registrar escuchadores de eventos para los controles de paginación
    setupPaginationEventListeners();
    
    // Realizar la carga inicial
    loadDocumentos();
    
    const btnRefresh = document.getElementById('btnRefresh');
    if (btnRefresh) {
        btnRefresh.addEventListener('click', () => {
            loadDocumentos();
            showToast('Datos actualizados', 'info');
        });
    }
}

// --- Carga de Datos Paginados ---

async function loadDocumentos(filtros) {
    const tbody = document.getElementById('tbodyDocumentos');
    if (!tbody) return;

    // Si se especifican filtros nuevos, reiniciar a la página 0 y actualizar filtros
    if (filtros !== undefined) {
        pagState.filtros = filtros;
        pagState.currentPage = 0;
    }

    // Sincronizar el filtro de búsqueda global con el input real si existe
    const globalSearchInput = document.getElementById('globalSearch');
    if (globalSearchInput) {
        const query = globalSearchInput.value.trim();
        if (query.length >= 3) {
            if (!pagState.filtros) pagState.filtros = {};
            pagState.filtros.search = query;
        } else {
            if (pagState.filtros) {
                delete pagState.filtros.search;
            }
        }
    }

    tbody.innerHTML = '<tr><td colspan="7" class="text-center"><i class="fas fa-spinner fa-spin"></i> Cargando documentos...</td></tr>';
    
    try {
        const savedEnv = sessionStorage.getItem('zentra-env') || 'dev';
        const expectedAmbiente = savedEnv === 'dev' ? 'TEST' : 'PRODUCCION';
        
        // Construir URL con los parámetros de paginación y entorno
        let url = `${API.emision}/documentos?page=${pagState.currentPage}&size=${pagState.pageSize}&ambiente=${expectedAmbiente}`;
        
        // Adjuntar filtros dinámicos si están activos
        if (pagState.filtros) {
            if (pagState.filtros.estado) url += `&estado=${encodeURIComponent(pagState.filtros.estado)}`;
            if (pagState.filtros.tipo) url += `&tipo=${encodeURIComponent(pagState.filtros.tipo)}`;
            if (pagState.filtros.desde) url += `&desde=${encodeURIComponent(pagState.filtros.desde)}`;
            if (pagState.filtros.hasta) url += `&hasta=${encodeURIComponent(pagState.filtros.hasta)}`;
            if (pagState.filtros.search) url += `&search=${encodeURIComponent(pagState.filtros.search)}`;
            if (pagState.filtros.fecha) url += `&desde=${encodeURIComponent(pagState.filtros.fecha)}&hasta=${encodeURIComponent(pagState.filtros.fecha)}`;
            if (pagState.filtros.ruc) url += `&search=${encodeURIComponent(pagState.filtros.ruc)}`;
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error("Error en servidor al obtener documentos paginados");
        
        const data = await response.json();
        
        const docs = data.content || [];
        pagState.totalPages = data.totalPages || 1;
        pagState.totalElements = data.totalElements || 0;
        
        // Renderizar las filas de la página actual
        renderTableRows(docs);
        
        // Actualizar la visualización de los controles de paginación
        updatePaginationUI();
        
        // Cargar y animar estadísticas globales asíncronamente
        loadGlobalStats();

    } catch (error) {
        console.error('Error cargando documentos paginados:', error);
        tbody.innerHTML = '<tr><td colspan="7" class="text-center error"><i class="fas fa-exclamation-triangle"></i> Error de conexión con el servidor</td></tr>';
    }
}

// --- Renderizado de Filas de la Tabla ---

function renderTableRows(docs) {
    const tbody = document.getElementById('tbodyDocumentos');
    if (!tbody) return;

    if (docs.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">No hay documentos que coincidan con los filtros</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    
    // Procesamos directamente el arreglo sin hacer .reverse() porque el servidor ya los entrega ordenados descendente
    docs.forEach(doc => {
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

        // Validar 72 horas para Cancelación
        let pasado72h = false;
        if (doc.fechaCreacion) {
            const fechaEmi = new Date(doc.fechaCreacion);
            const horas = (new Date() - fechaEmi) / (1000 * 60 * 60);
            pasado72h = horas > 72;
        }

        const btnAnular = doc.estado === 'APROBADO' ? 
            (pasado72h ? 
                `<button class="btn btn-xs btn-danger disabled" style="opacity: 0.5; cursor: not-allowed;" title="Plazo Expirado: Han transcurrido más de 72 horas desde la emisión. SIFEN no permite su cancelación.">
                    <i class="fas fa-ban"></i>
                </button>` 
                : 
                `<button class="btn btn-xs btn-danger" onclick="iniciarCancelacionDte('${doc.id}', '${doc.cdc || ''}')" title="Cancelar DTE en SIFEN">
                    <i class="fas fa-ban"></i>
                </button>`
            ) : '';

        tr.innerHTML = `
            <td>${formatDate(doc.fechaCreacion)}</td>
            <td><span class="badge-type">${getTipoLabel(doc.tipoDocumento)}</span></td>
            <td title="CDC: ${doc.cdc || ''}"><strong>${doc.numeroComprobante || 'N/A'}</strong><br>${doc.cdc ? `<a href="${doc.qrUrl || (doc.ambiente === 'PRODUCCION' ? 'https://ekuatia.set.gov.py/consultas/' : 'https://ekuatia.set.gov.py/consultas-test/')}" target="_blank" style="font-size: 0.8em; text-decoration: none; color: #3b82f6;" title="Consultar en SIFEN Ekuatia"><i class="fas fa-external-link-alt" style="font-size: 0.8em;"></i> ${doc.cdc}</a>` : `<span style="font-size: 0.8em; color: #9ca3af;">Sin CDC</span>`}</td>
            <td>${doc.receptorRazonSocial || doc.rucReceptor || 'Consumidor Final'}</td>
            <td>${formatCurrency(doc.totalOperacion)}</td>
            <td>
                <span class="badge-status ${statusClass}">${doc.estado || 'PENDIENTE'}</span>
                ${badgeTicket}
            </td>
            <td class="acciones-col">
                <button class="btn btn-xs btn-info" onclick="abrirModalDetalle('${doc.id}')" title="Ver detalle y estado SIFEN">
                    <i class="fas fa-info-circle"></i>
                </button>
                ${(doc.estado === 'EN_PROCESO' || tieneTicket) ? `
                <button class="btn btn-xs btn-primary" onclick="consultarLoteSifen('${doc.id}')" title="Consultar Lote en SIFEN">
                    <i class="fas fa-sync-alt"></i>
                </button>` : ''}
                ${(doc.estado === 'RECHAZADO' && doc.cdc) ? `
                <button class="btn btn-xs btn-secondary" onclick="consultarCdcSifen('${doc.cdc}')" title="Consultar CDC en SIFEN">
                    <i class="fas fa-search"></i>
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
                ${btnAnular}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// --- Controles de Interfaz de Paginación ---

function updatePaginationUI() {
    const startIdx = pagState.totalElements === 0 ? 0 : (pagState.currentPage * pagState.pageSize) + 1;
    const endIdx = Math.min((pagState.currentPage + 1) * pagState.pageSize, pagState.totalElements);
    
    document.getElementById('pagStart').textContent = startIdx;
    document.getElementById('pagEnd').textContent = endIdx;
    document.getElementById('pagTotalElements').textContent = pagState.totalElements;
    document.getElementById('pagCurrentPage').textContent = pagState.currentPage + 1;
    document.getElementById('pagTotalPages').textContent = pagState.totalPages;
    
    document.getElementById('btnFirstPage').disabled = pagState.currentPage === 0;
    document.getElementById('btnPrevPage').disabled = pagState.currentPage === 0;
    document.getElementById('btnNextPage').disabled = pagState.currentPage >= pagState.totalPages - 1;
    document.getElementById('btnLastPage').disabled = pagState.currentPage >= pagState.totalPages - 1;
}

// --- Vinculación de Eventos de Paginación ---

function setupPaginationEventListeners() {
    if (window.paginationListenersInitialized) return;

    const btnFirst = document.getElementById('btnFirstPage');
    const btnPrev = document.getElementById('btnPrevPage');
    const btnNext = document.getElementById('btnNextPage');
    const btnLast = document.getElementById('btnLastPage');
    const selectSize = document.getElementById('selectPageSize');

    if (btnFirst) {
        btnFirst.addEventListener('click', () => {
            if (pagState.currentPage > 0) {
                pagState.currentPage = 0;
                loadDocumentos();
            }
        });
    }
    
    if (btnPrev) {
        btnPrev.addEventListener('click', () => {
            if (pagState.currentPage > 0) {
                pagState.currentPage--;
                loadDocumentos();
            }
        });
    }
    
    if (btnNext) {
        btnNext.addEventListener('click', () => {
            if (pagState.currentPage < pagState.totalPages - 1) {
                pagState.currentPage++;
                loadDocumentos();
            }
        });
    }
    
    if (btnLast) {
        btnLast.addEventListener('click', () => {
            if (pagState.currentPage < pagState.totalPages - 1) {
                pagState.currentPage = pagState.totalPages - 1;
                loadDocumentos();
            }
        });
    }
    
    if (selectSize) {
        selectSize.addEventListener('change', (e) => {
            pagState.pageSize = parseInt(e.target.value);
            pagState.currentPage = 0;
            loadDocumentos();
        });
    }

    window.paginationListenersInitialized = true;
}

// --- Carga Optimizada de Estadísticas Globales ---

async function loadGlobalStats() {
    try {
        const response = await fetch('/api/v1/estadisticas/resumen-estado');
        if (response.ok) {
            const stats = await response.json();
            
            // Animación suave de los contadores agregados principales
            animateValue("statsAprobados", 0, stats.aprobados || 0, 1000);
            animateValue("statsPendientes", 0, stats.pendientes || 0, 1000);
            animateValue("statsRechazados", 0, stats.rechazados || 0, 1000);
            animateValue("statsEmitidosHoy", 0, stats.emitidosHoy || 0, 1000);
        }
    } catch (e) {
        console.error("Error cargando estadísticas globales:", e);
    }

    // Lotes Huérfanos
    try {
        const res = await fetch(API.lotes);
        if (res.ok) {
            const lotes = await res.json();
            const huerfanosCount = lotes.filter(l => l.estado === 'ERROR').length;
            
            animateValue("statsLotesHuerfanos", 0, huerfanosCount, 1000);
            
            const alertBox = document.getElementById('huerfanosAlert');
            const countBanner = document.getElementById('huerfanosCountBanner');
            
            if (huerfanosCount > 0) {
                if (alertBox) alertBox.style.display = 'block';
                if (countBanner) countBanner.textContent = huerfanosCount;
            } else {
                if (alertBox) alertBox.style.display = 'none';
            }
        }
    } catch (e) {
        console.error("Error obteniendo lotes para métricas:", e);
    }
}

// --- KuDE Handling ---

async function descargarKude(id) {
    try {
        const res = await fetch(`/api/v1/emision/kude/${id}?formato=A4`);
        if (!res.ok) throw new Error('Error al generar el KuDE');
        const blob = await res.blob();
        const url = window.URL.createObjectURL(new Blob([blob], {type: 'application/pdf'}));
        window.open(url, '_blank');
    } catch(e) {
        showToast(e.message, 'error');
    }
}

async function descargarTicket(id) {
    try {
        const res = await fetch(`/api/v1/emision/kude/${id}?formato=TICKET`);
        if (!res.ok) throw new Error('Error al generar el Ticket');
        const blob = await res.blob();
        const url = window.URL.createObjectURL(new Blob([blob], {type: 'application/pdf'}));
        window.open(url, '_blank');
    } catch(e) {
        showToast(e.message, 'error');
    }
}

window.descargarXml = async function(id) {
    try {
        const res = await fetch(`/api/v1/emision/xml/${id}`);
        if (!res.ok) throw new Error('Error al descargar el XML');
        
        const blob = await res.blob();
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `dte_${id}.xml`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    } catch (e) {
        showToast(e.message, 'error');
    }
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
        document.getElementById('mdMensajeSifen').innerHTML = doc.mensajeSifen || '—';
        document.getElementById('mdMensajeUsuario').innerHTML = doc.mensajeUsuario || '—';
        document.getElementById('mdRespuestaXml').textContent = doc.xmlRespuestaSifen || '(sin respuesta)';

        // Mostrar botón de consulta de Lote
        const btnConsultar = document.getElementById('mdBtnConsultar');
        const puedeConsultarLote = doc.numeroTicketLote && doc.estado !== 'APROBADO' && doc.estado !== 'RECHAZADO';
        btnConsultar.style.display = puedeConsultarLote ? 'inline-flex' : 'none';

        // Mostrar botón de consulta CDC si está rechazado y tiene CDC
        const btnConsultarCdc = document.getElementById('mdBtnConsultarCdc');
        const puedeConsultarCdc = doc.estado === 'RECHAZADO' && doc.cdc;
        btnConsultarCdc.style.display = puedeConsultarCdc ? 'inline-flex' : 'none';

        // Cargar historial de transacciones SIFEN
        cargarHistorialSifen(id);
    } catch(e) {
        showToast('Error cargando detalle: ' + e.message, 'error');
    }
}

async function cargarHistorialSifen(documentoId) {
    const tbody = document.getElementById('mdHistorialBody');
    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding:8px; opacity:0.5;">Cargando...</td></tr>';
    // Ocultar el contenedor al recargar
    document.getElementById('mdHistorialContainer').style.display = 'none';
    try {
        const res = await fetch(`/api/v1/emision/documentos/${documentoId}/historial`);
        if (!res.ok) throw new Error('No se pudo obtener el historial');
        const historial = await res.json();

        if (!historial || historial.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding:8px; opacity:0.5;">Sin registros de historial</td></tr>';
            return;
        }

        // Mapa de etiquetas para las operaciones
        const opLabels = {
            'ENVIO': '📤 Envío',
            'CONSULTA_LOTE': '🔄 Consulta Lote',
            'CONSULTA_CDC': '🔍 Consulta CDC'
        };

        tbody.innerHTML = '';
        historial.forEach(h => {
            const tr = document.createElement('tr');
            tr.style.borderBottom = '1px solid rgba(255,255,255,0.05)';

            // Formatear fecha
            let fechaStr = h.fechaRegistro || '';
            if (fechaStr) {
                const d = new Date(fechaStr);
                fechaStr = d.toLocaleDateString('es-PY') + ' ' + d.toLocaleTimeString('es-PY', {hour:'2-digit', minute:'2-digit'});
            }

            const opLabel = opLabels[h.operacion] || h.operacion;
            const codigoClass = h.codigoEstado === '0300' ? 'color: #4ade80;' : (h.codigoEstado ? 'color: #f87171;' : '');

            tr.innerHTML = `
                <td style="padding: 6px 8px; white-space: nowrap;">${fechaStr}</td>
                <td style="padding: 6px 8px;">${opLabel}</td>
                <td style="padding: 6px 8px; font-weight: 600; ${codigoClass}">${h.codigoEstado || '—'}</td>
                <td style="padding: 6px 8px;">${h.mensajeRespuesta || '—'}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch(e) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding:8px; color:#f87171;">Error al cargar historial</td></tr>';
    }
}

window.toggleHistorialSifen = function() {
    const container = document.getElementById('mdHistorialContainer');
    container.style.display = container.style.display === 'none' ? 'block' : 'none';
}

window.cerrarModalDetalle = function(event) {
    // Si vino del overlay (fondo), cerrar. Si vino del botón, cerrar directamente.
    if (!event || event.target === document.getElementById('modalDetalleDte')) {
        document.getElementById('modalDetalleDte').style.display = 'none';
        _modalDocId = null;
    }
}

window.consultarCdcSifen = async function(cdc) {
    try {
        showToast('Consultando estado del CDC en SIFEN...', 'info');
        const res = await fetch(`/api/lotes/consultar-cdc/${cdc}`);
        const data = await res.json();

        if (res.ok) {
            showToast(`✓ ${data.mensajeUsuario || 'Documento aprobado por SIFEN'}`, 'success');
        } else {
            showToast(`Estado SIFEN: ${data.mensajeUsuario || data.message}`, 'warning');
        }
        loadDocumentos();
    } catch(e) {
        showToast('Error: ' + e.message, 'error');
    }
}

window.consultarCdcSifenModal = async function() {
    const cdc = document.getElementById('mdCdc').textContent;
    if (!cdc || cdc === '—') return;
    const btnConsultarCdc = document.getElementById('mdBtnConsultarCdc');
    btnConsultarCdc.disabled = true;
    btnConsultarCdc.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Consultando...';
    try {
        const res = await fetch(`/api/lotes/consultar-cdc/${cdc}`);
        const data = await res.json();
        if (res.ok) {
            showToast(`✓ ${data.mensajeUsuario || 'Documento aprobado por SIFEN'}`, 'success');
        } else {
            showToast(`SIFEN: ${data.mensajeUsuario || data.message}`, 'warning');
        }
        // Refrescar el modal con los datos actualizados
        abrirModalDetalle(_modalDocId);
        loadDocumentos();
    } catch(e) {
        showToast('Error: ' + e.message, 'error');
    } finally {
        btnConsultarCdc.disabled = false;
        btnConsultarCdc.innerHTML = '<i class="fas fa-search"></i> Consultar CDC en SIFEN';
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
        btnConsultar.innerHTML = '<i class="fas fa-sync-alt"></i> Consultar Lote en SIFEN';
    }
}

window.toggleRawXml = function() {
    const box = document.getElementById('mdRespuestaRaw');
    box.style.display = box.style.display === 'none' ? 'block' : 'none';
}

// --- Gestión de Filtros ---

window.aplicarFiltrosGlobales = function() {
    const filtros = {
        desde: document.getElementById('filtroDesde').value,
        hasta: document.getElementById('filtroHasta').value,
        estado: document.getElementById('filtroEstado').value,
        tipo: document.getElementById('filtroTipo').value
    };
    
    loadDocumentos(filtros);
    showToast("Filtros aplicados", "info");
};

window.limpiarFiltrosGlobales = function() {
    document.getElementById('filtroDesde').value = '';
    document.getElementById('filtroHasta').value = '';
    document.getElementById('filtroEstado').value = '';
    document.getElementById('filtroTipo').value = '';
    
    // Limpiar también el input de búsqueda global en la UI
    const globalSearchInput = document.getElementById('globalSearch');
    if (globalSearchInput) globalSearchInput.value = '';
    
    // Limpiar explícitamente el estado de filtros y la página en el estado global
    pagState.filtros = null;
    pagState.currentPage = 0;
    
    loadDocumentos(null);
    showToast("Filtros limpiados", "info");
};

/**
 * Llamada desde los gráficos analíticos
 */
window.aplicarFiltrosManual = function(filtros) {
    // Sincronizar UI de filtros si aplica
    if (filtros.estado) document.getElementById('filtroEstado').value = filtros.estado;
    if (filtros.tipo) document.getElementById('filtroTipo').value = filtros.tipo;
    if (filtros.fecha) {
        // La fecha del gráfico viene como YYYY-MM-DD, el input date también lo usa
        document.getElementById('filtroDesde').value = filtros.fecha;
        document.getElementById('filtroHasta').value = filtros.fecha;
    }
    
    loadDocumentos(filtros);
    showToast("Filtrado por selección de gráfico", "info");
    
    // Scroll suave a la tabla
    document.querySelector('.content-table').scrollIntoView({ behavior: 'smooth' });
};

// --- Últimos Comprobantes Dashboard ---

window.loadUltimosComprobantesPorTipo = async function() {
    try {
        const response = await fetch(API.emision + '/documentos');
        let data = await response.json();
        let docs = data.content || data || [];
        
        // Filtrar por ambiente activo en sesión
        const savedEnv = sessionStorage.getItem('zentra-env') || 'dev';
        const expectedAmbiente = savedEnv === 'dev' ? 'TEST' : 'PRODUCCION';
        if (docs && docs.length > 0) docs = docs.filter(doc => doc.ambiente === expectedAmbiente);
        
        // Ordenar por fecha descendente
        docs.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion));
        
        // Separar por tipo y tomar los primeros 5
        const facturas = docs.filter(d => String(d.tipoDocumento) === '1').slice(0, 5);
        const ncnd = docs.filter(d => String(d.tipoDocumento) === '5' || String(d.tipoDocumento) === '6').slice(0, 5);
        const autofacturas = docs.filter(d => String(d.tipoDocumento) === '4').slice(0, 5);
        const remisiones = docs.filter(d => String(d.tipoDocumento) === '7').slice(0, 5);
        
        renderMiniDocsList('listRecentFacturas', facturas);
        renderMiniDocsList('listRecentNotasCredito', ncnd);
        renderMiniDocsList('listRecentAutofacturas', autofacturas);
        renderMiniDocsList('listRecentRemisiones', remisiones);
        
    } catch (e) {
        console.error("Error al cargar últimos comprobantes:", e);
        showToast("Error al cargar últimos comprobantes", "error");
    }
}

function renderMiniDocsList(containerId, docs) {
    const container = document.getElementById(containerId);
    if (!container) return;
    
    if (docs.length === 0) {
        container.innerHTML = '<div class="text-center" style="opacity: 0.6; padding: 20px;">Sin documentos recientes</div>';
        return;
    }
    
    container.innerHTML = docs.map(doc => {
        let statusColor = '#94a3b8'; // PENDIENTE
        if (doc.estado === 'APROBADO') statusColor = '#2ecc71';
        else if (doc.estado === 'RECHAZADO' || doc.estado === 'ERROR_ENVIO') statusColor = '#e74c3c';
        else if (doc.estado === 'EN_PROCESO') statusColor = '#f39c12';
        
        // Al hacer click, cambiar a vista-lista-dtes y abrir modal de detalle
        return `
            <div class="mini-doc-item" style="cursor:pointer;" onclick="switchView('lista-dtes'); setTimeout(() => abrirModalDetalle('${doc.id}'), 100);" title="Ver detalle de comprobante">
                <div class="mini-doc-info">
                    <span class="mini-doc-nro">${doc.numeroComprobante || 'S/N'}</span>
                    <span class="mini-doc-receptor">${doc.receptorRazonSocial || doc.rucReceptor || 'Consumidor Final'}</span>
                </div>
                <div class="mini-doc-status">
                    <span style="font-size: 0.8rem; font-weight: bold; color: ${statusColor}">${doc.estado || 'PENDIENTE'}</span>
                    <span style="font-size: 0.75rem; color: var(--text-secondary)">${formatDate(doc.fechaCreacion)}</span>
                </div>
            </div>
        `;
    }).join('');
}

