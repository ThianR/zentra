// --- Initialization ---

function initDashboard() {
    loadDocumentos();
    
    document.getElementById('btnRefresh').addEventListener('click', () => {
        loadDocumentos();
        showToast('Datos actualizados', 'info');
    });
}

// --- Data Loading ---

async function loadDocumentos(filtros = null) {
    const tbody = document.getElementById('tbodyDocumentos');
    try {
        const response = await fetch(API.emision + '/documentos');
        let docs = await response.json();
        const savedEnv = sessionStorage.getItem('zentra-env') || 'dev';
        const expectedAmbiente = savedEnv === 'dev' ? 'TEST' : 'PRODUCCION';
        if (docs && docs.length > 0) docs = docs.filter(doc => doc.ambiente === expectedAmbiente);
        
        // Aplicar filtros en memoria si existen
        if (filtros) {
            docs = docs.filter(doc => {
                let cumple = true;
                
                // Filtro por fecha exacta (del gráfico)
                if (filtros.fecha) {
                    const fDoc = doc.fechaCreacion ? doc.fechaCreacion.split('T')[0] : '';
                    if (fDoc !== filtros.fecha) cumple = false;
                }
                
                // Filtro por estado
                if (filtros.estado && doc.estado !== filtros.estado) cumple = false;
                
                // Filtro por RUC (del gráfico top receptores)
                if (filtros.ruc && doc.rucReceptor !== filtros.ruc) cumple = false;
                
                // Filtro por Tipo
                if (filtros.tipo && String(doc.tipoDocumento) !== String(filtros.tipo)) cumple = false;
                
                // Rango de fechas (de la barra de filtros)
                if (filtros.desde) {
                    const fDoc = doc.fechaCreacion ? doc.fechaCreacion.split('T')[0] : '';
                    if (fDoc < filtros.desde) cumple = false;
                }
                if (filtros.hasta) {
                    const fDoc = doc.fechaCreacion ? doc.fechaCreacion.split('T')[0] : '';
                    if (fDoc > filtros.hasta) cumple = false;
                }
                
                // Búsqueda Global (Fase A6.2)
                if (filtros.search) {
                    const s = filtros.search.toLowerCase();
                    const cdc = (doc.cdc || '').toLowerCase();
                    const ruc = (doc.rucReceptor || '').toLowerCase();
                    const nro = (doc.numeroComprobante || '').toLowerCase();
                    const razon = (doc.receptorRazonSocial || '').toLowerCase();
                    
                    if (!cdc.includes(s) && !ruc.includes(s) && !nro.includes(s) && !razon.includes(s)) {
                        cumple = false;
                    }
                }
                
                return cumple;
            });
        }

        updateStats(docs);

        if (docs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">No hay documentos que coincidan con los filtros</td></tr>';
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
    } catch (error) {
        console.error('Error cargando documentos:', error);
        tbody.innerHTML = '<tr><td colspan="7" class="text-center error">Error de conexión con el servidor</td></tr>';
    }
}

async function updateStats(docs) {
    const total = docs.length;
    const aprobados = docs.filter(d => d.estado === 'APROBADO').length;
    const rechazados = docs.filter(d => d.estado && d.estado.includes('ERROR')).length;
    const pendientes = total - aprobados - rechazados;

    // Calcular Emitidos Hoy
    const hoy = new Date();
    const emitidosHoy = docs.filter(d => {
        if (!d.fechaCreacion) return false;
        const dFecha = new Date(d.fechaCreacion);
        return dFecha.getDate() === hoy.getDate() && 
               dFecha.getMonth() === hoy.getMonth() && 
               dFecha.getFullYear() === hoy.getFullYear();
    }).length;

    // Animación de números
    animateValue("statsAprobados", 0, aprobados, 1000);
    animateValue("statsPendientes", 0, pendientes, 1000);
    animateValue("statsRechazados", 0, rechazados, 1000);
    animateValue("statsEmitidosHoy", 0, emitidosHoy, 1000);

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
                alertBox.style.display = 'block';
                countBanner.textContent = huerfanosCount;
            } else {
                alertBox.style.display = 'none';
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
    
    loadDocumentos();
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
        let docs = await response.json();
        
        // Filtrar por ambiente activo en sesi�n
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

