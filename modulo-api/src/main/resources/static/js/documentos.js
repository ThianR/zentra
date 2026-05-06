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
        const response = await fetch(API.emision + '/documentos');
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
                <td title="CDC: ${doc.cdc || ''}"><strong>${doc.numeroComprobante || 'N/A'}</strong></td>
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

