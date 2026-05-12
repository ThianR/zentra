// --- Gestión de Lotes Asíncronos ---

document.addEventListener('DOMContentLoaded', () => {
    // Escuchar cuando se cambie a la vista de lotes para cargar los datos
    const viewLotes = document.getElementById('view-lotes');
    if (viewLotes) {
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                if (mutation.target.classList.contains('active')) {
                    cargarLotes();
                }
            });
        });
        observer.observe(viewLotes, { attributes: true, attributeFilter: ['class'] });
    }
});

async function cargarLotes() {
    const tbody = document.getElementById('tbodyLotes');
    if (!tbody) return;
    
    tbody.innerHTML = '<tr><td colspan="7" class="text-center">Cargando lotes... <i class="fas fa-spinner fa-spin"></i></td></tr>';
    
    try {
        const res = await fetch(API.lotes);
        if (!res.ok) throw new Error('Error en la respuesta del servidor');
        const lotes = await res.json();

        if (!lotes || lotes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">No hay lotes asíncronos registrados</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        
        // Ordenar del más reciente al más antiguo
        lotes.sort((a, b) => {
            const dateA = a.fechaEnvio ? new Date(a.fechaEnvio) : new Date(0);
            const dateB = b.fechaEnvio ? new Date(b.fechaEnvio) : new Date(0);
            return dateB - dateA;
        });

        lotes.forEach(lote => {
            // Fila principal
            const tr = document.createElement('tr');
            tr.style.cursor = 'pointer';
            tr.onclick = () => toggleLoteDetails(lote.id);
            tr.title = "Clic para ver los documentos del lote";
            
            let statusClass = 'pendiente';
            if (lote.estado === 'PROCESADO') statusClass = 'aprobado';
            else if (lote.estado === 'ERROR') statusClass = 'rechazado';
            else if (lote.estado === 'ENVIADO') statusClass = 'warning';

            let timeoutBadge = '';
            if (lote.estado === 'ERROR') {
                timeoutBadge = ' <i class="fas fa-clock text-error" title="Lote con Timeout de 24h. Consultar DTEs individualmente."></i>';
            }

            tr.innerHTML = `
                <td class="mono small-text"><i class="fas fa-chevron-right chevron-lote" id="icon-${lote.id}"></i> ${lote.id.substring(0,8)}...</td>
                <td>${lote.empresa ? lote.empresa.ruc : 'N/A'}</td>
                <td><span class="badge-status ${statusClass}">${lote.estado}</span>${timeoutBadge}</td>
                <td>${lote.fechaEnvio ? formatDate(lote.fechaEnvio) : '<span style="color:#666">En cola...</span>'}</td>
                <td class="mono" style="color:var(--accent-primary)">${lote.numeroTicket || '—'}</td>
                <td>${lote.fechaUltimaConsulta ? formatDate(lote.fechaUltimaConsulta) : '—'}</td>
                <td><span class="badge-ticket">${lote.intentosConsulta || 0}</span></td>
            `;
            tbody.appendChild(tr);

            // Fila de detalles (oculta por defecto)
            const trDetails = document.createElement('tr');
            trDetails.id = `details-${lote.id}`;
            trDetails.style.display = 'none';
            trDetails.className = 'lote-details-row';
            
            let dtesHtml = '<div class="lote-dtes-container">';
            if (lote.documentos && lote.documentos.length > 0) {
                dtesHtml += `<table class="table-dtes-inner">
                                <thead>
                                    <tr>
                                        <th>Comprobante</th>
                                        <th>CDC</th>
                                        <th>Estado DTE</th>
                                        <th>Código SIFEN</th>
                                        <th>Acción</th>
                                    </tr>
                                </thead>
                                <tbody>`;
                lote.documentos.forEach(dte => {
                    let dteStatusClass = 'pendiente';
                    if (dte.estado === 'APROBADO') dteStatusClass = 'aprobado';
                    else if (dte.estado.includes('ERROR')) dteStatusClass = 'rechazado';
                    
                    let btnConsultar = '';
                    if (dte.estado === 'ERROR_ENVIO' || lote.estado === 'ERROR') {
                        btnConsultar = `<button class="btn btn-xs btn-primary" onclick="consultarDteCdc(event, '${dte.cdc}', '${dte.id}')" title="Consultar estado oficial en SIFEN por CDC">
                                            <i class="fas fa-search"></i> Consultar CDC
                                        </button>`;
                    }
                    
                    dtesHtml += `<tr>
                                    <td>${dte.numeroComprobante || '—'}</td>
                                    <td class="mono small-text">${dte.cdc || '—'}</td>
                                    <td><span class="badge-status ${dteStatusClass}" id="badge-${dte.id}">${dte.estado}</span></td>
                                    <td id="sifen-${dte.id}">${dte.codigoEstadoSifen || '—'}</td>
                                    <td>${btnConsultar}</td>
                                 </tr>`;
                });
                dtesHtml += `</tbody></table>`;
            } else {
                dtesHtml += '<p class="text-center" style="padding:10px;">No hay documentos en este lote.</p>';
            }
            dtesHtml += '</div>';

            trDetails.innerHTML = `<td colspan="7" style="padding:0; background:rgba(0,0,0,0.2);">${dtesHtml}</td>`;
            tbody.appendChild(trDetails);
        });
    } catch (e) {
        console.error('Error cargando lotes:', e);
        tbody.innerHTML = `<tr><td colspan="7" class="text-center text-error">Error al cargar lotes: ${e.message}</td></tr>`;
        showToast('Error de conexión al cargar lotes', 'error');
    }
}

function toggleLoteDetails(loteId) {
    const detailsRow = document.getElementById(`details-${loteId}`);
    const icon = document.getElementById(`icon-${loteId}`);
    if (!detailsRow) return;

    if (detailsRow.style.display === 'none') {
        detailsRow.style.display = 'table-row';
        if(icon) icon.className = 'fas fa-chevron-down chevron-lote';
    } else {
        detailsRow.style.display = 'none';
        if(icon) icon.className = 'fas fa-chevron-right chevron-lote';
    }
}

async function consultarDteCdc(event, cdc, dteId) {
    event.stopPropagation(); // Evitar que colapse el lote
    if (!cdc || cdc === 'null') {
        showToast('El DTE no tiene un CDC válido para consultar', 'warning');
        return;
    }

    const btn = event.currentTarget;
    const originalHtml = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    btn.disabled = true;

    try {
        const res = await fetch(`/api/lotes/consultar-cdc/${cdc}`);
        const data = await res.json();
        
        if (res.ok) {
            showToast('DTE Aprobado en SIFEN', 'success');
        } else {
            showToast(`Rechazado por SIFEN: ${data.mensajeUsuario || 'Verifique los datos'}`, 'error');
        }
        
        // Actualizar UI dinámica
        const badge = document.getElementById(`badge-${dteId}`);
        if (badge) {
            badge.textContent = data.estado;
            badge.className = `badge-status ${data.estado === 'APROBADO' ? 'aprobado' : (data.estado.includes('ERROR') ? 'rechazado' : 'warning')}`;
        }
        
        const sifen = document.getElementById(`sifen-${dteId}`);
        if (sifen) {
            sifen.textContent = data.codigoSifen || '—';
        }
        
        btn.innerHTML = '<i class="fas fa-check"></i> Listo';
    } catch (e) {
        console.error(e);
        showToast('Error de red al consultar CDC', 'error');
        btn.innerHTML = originalHtml;
        btn.disabled = false;
    }
}

async function ejecutarConsultaManualCdc() {
    const input = document.getElementById('manualCdcInput');
    const cdc = input.value.trim();
    const btn = document.getElementById('btnConsultarManual');
    const resultBox = document.getElementById('manualCdcResult');
    const statusBadge = document.getElementById('manualCdcStatusBadge');
    const sifenCode = document.getElementById('manualCdcSifenCode');
    const message = document.getElementById('manualCdcMessage');

    if (!cdc || cdc.length !== 44) {
        showToast('Ingrese un CDC válido de 44 caracteres numéricos.', 'warning');
        input.focus();
        return;
    }

    const originalBtnText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    btn.disabled = true;
    
    resultBox.style.display = 'block';
    statusBadge.className = 'badge-status pendiente';
    statusBadge.textContent = 'CONSULTANDO...';
    sifenCode.textContent = '—';
    message.innerHTML = 'Conectando con el gateway SIFEN...';
    message.style.color = 'var(--text-color)';

    try {
        const res = await fetch(`/api/lotes/consultar-cdc/${cdc}`);
        
        if (res.status === 404) {
            statusBadge.className = 'badge-status rechazado';
            statusBadge.textContent = 'NO ENCONTRADO';
            message.innerHTML = 'El CDC no existe en la base de datos local de Zentra. No se puede consultar.';
            message.style.color = 'var(--accent-danger)';
            return;
        }

        const data = await res.json();
        
        if (res.ok) {
            statusBadge.className = 'badge-status aprobado';
            statusBadge.textContent = data.estado || 'APROBADO';
            sifenCode.textContent = data.codigoSifen || '0300';
            message.innerHTML = `<i class="fas fa-check-circle" style="color:var(--accent-success)"></i> ${data.mensajeSifen || 'Documento Aprobado en SIFEN'}`;
            showToast('Consulta finalizada con éxito', 'success');
            // Refrescar tabla si es que está en la grilla
            cargarLotes();
        } else {
            statusBadge.className = 'badge-status rechazado';
            statusBadge.textContent = data.estado || 'RECHAZADO';
            sifenCode.textContent = data.codigoSifen || '—';
            message.innerHTML = `<i class="fas fa-exclamation-triangle" style="color:var(--accent-danger)"></i> ${data.mensajeUsuario || data.mensajeSifen || 'Error al consultar'}`;
        }
    } catch (e) {
        console.error(e);
        statusBadge.className = 'badge-status warning';
        statusBadge.textContent = 'ERROR RED';
        message.innerHTML = 'Hubo un error de conexión con el servidor Zentra o el timeout expiró.';
        message.style.color = 'var(--accent-danger)';
    } finally {
        btn.innerHTML = originalBtnText;
        btn.disabled = false;
    }
}
