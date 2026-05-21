// ============================================================
// eventos.js — Módulo de Eventos SIFEN (Cancelación e Inutilización)
// Fase E6 — Interfaz de Usuario
// ============================================================

// Empresa seleccionada en el selector global de la vista
let _eventoEmpresaId = null;
let _cancelacionDocId = null;  // ID interno del DTE a cancelar
let _cancelacionCdc   = null;  // CDC del DTE a cancelar

// Mapeo de códigos de tipo de documento SIFEN a nombres legibles
const TIPOS_DOCUMENTO_SIFEN = {
    1: 'Factura Electrónica',
    2: 'Factura Electrónica de Exportación',
    3: 'Factura Electrónica de Importación',
    4: 'Autofactura Electrónica',
    5: 'Nota de Crédito Electrónica',
    6: 'Nota de Débito Electrónica',
    7: 'Nota de Remisión Electrónica'
};

/**
 * Retorna el nombre legible del tipo de documento SIFEN.
 * Si no existe en el mapeo, retorna "Tipo [código]".
 */
function nombreTipoDocSifen(codigo) {
    if (codigo == null) return '—';
    return TIPOS_DOCUMENTO_SIFEN[codigo] || `Tipo ${codigo}`;
}

// ============================================================
// Inicialización de la vista de Eventos
// ============================================================

function initEventos() {
    cargarEmpresasEnEvento();
    cargarHistorialEventos();

    const btnRefresh = document.getElementById('btnRefreshEventos');
    if (btnRefresh) {
        btnRefresh.addEventListener('click', () => {
            cargarHistorialEventos();
            showToast('Historial actualizado', 'info');
        });
    }
}

// ============================================================
// Carga de empresas para el selector de la vista de eventos
// ============================================================

async function cargarEmpresasEnEvento() {
    // Cargar los 3 selectores usando la función centralizada de ui.js
    const selectIds = ['eventoEmpresaSelect', 'inutEmpresaSelect', 'receptorEmpresaId'];
    
    // Se usa la primera carga para obtener la lista y reutilizarla
    const empresas = await cargarEmpresasEnSelect(selectIds[0]);
    
    // Llenar los demás selectores con la misma lista (evita 3 fetches)
    for (let i = 1; i < selectIds.length; i++) {
        const sel = document.getElementById(selectIds[i]);
        if (!sel) continue;
        sel.innerHTML = '<option value="">Seleccione empresa...</option>';
        empresas.forEach(e => {
            const opt = document.createElement('option');
            opt.value = e.id;
            opt.textContent = `${e.ruc}-${e.dv || '0'} | ${e.razonSocial}`;
            // Datos extra para inutilización (pre-cargar timbrado, etc.)
            if (sel.id === 'inutEmpresaSelect') {
                opt.dataset.timbrado        = e.timbrado        || '';
                opt.dataset.establecimiento = e.codEstablecimiento || '001';
                opt.dataset.puntoExpedicion = e.puntoExpedicion  || '001';
            }
            sel.appendChild(opt);
        });
    }

    // Seleccionar automáticamente la empresa actual y bloquear los selectores
    const empresaData = localStorage.getItem('empresa_activa');
    if (empresaData) {
        try {
            const empresa = JSON.parse(empresaData);
            selectIds.forEach(id => {
                const sel = document.getElementById(id);
                if (sel) {
                    sel.value = empresa.id;
                    sel.disabled = true;
                }
            });
            // Rellenar los datos de inutilización basados en la empresa seleccionada
            onInutEmpresaChange();
        } catch (e) {
            console.error("Error al pre-seleccionar empresa activa en eventos:", e);
        }
    }
}

// ============================================================
// Autocompletado de datos de inutilización desde la empresa
// ============================================================

function onInutEmpresaChange() {
    const sel = document.getElementById('inutEmpresaSelect');
    const opt = sel.selectedOptions[0];
    if (!opt || !opt.value) return;

    const timbrado        = opt.dataset.timbrado        || '';
    const establecimiento = opt.dataset.establecimiento || '001';
    const puntoExpedicion = opt.dataset.puntoExpedicion || '001';

    const fTimbrado = document.getElementById('inutTimbrado');
    const fEstab    = document.getElementById('inutEstablecimiento');
    const fPunto    = document.getElementById('inutPuntoExpedicion');
    if (fTimbrado && !fTimbrado.value) fTimbrado.value = timbrado;
    if (fEstab    && !fEstab.value)    fEstab.value    = establecimiento;
    if (fPunto    && !fPunto.value)    fPunto.value    = puntoExpedicion;
}

// ============================================================
// Modal de Cancelación (iniciado desde documentos.js / tabla)
// ============================================================

window.abrirModalCancelacion = async function(docId, cdc) {
    _cancelacionDocId = docId;
    _cancelacionCdc   = cdc;

    // Pre-cargar empresas en el selector de cancelación
    await cargarEmpresasEnEvento();

    // Seleccionar automáticamente la empresa actual y bloquear el selector
    const selectEmpresa = document.getElementById('eventoEmpresaSelect');
    const empresaData = localStorage.getItem('empresa_activa');
    if (empresaData && selectEmpresa) {
        try {
            const empresa = JSON.parse(empresaData);
            selectEmpresa.value = empresa.id;
            selectEmpresa.disabled = true;
        } catch(e) {}
    }

    // Limpiar y mostrar el modal
    const motivoEl = document.getElementById('canMotivo');
    if (motivoEl) motivoEl.value = '';

    const cdcEl = document.getElementById('canCdcDisplay');
    if (cdcEl) cdcEl.textContent = cdc || '—';

    // Resetear estado del botón
    const btn = document.getElementById('btnConfirmarCancelacion');
    if (btn) {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-ban"></i> Confirmar Cancelación';
    }

    document.getElementById('modalCancelacion').style.display = 'flex';
};

window.cerrarModalCancelacion = function() {
    document.getElementById('modalCancelacion').style.display = 'none';
    _cancelacionDocId = null;
    _cancelacionCdc   = null;
};

window.confirmarCancelacion = async function() {
    const empresaId = document.getElementById('eventoEmpresaSelect')?.value;
    const motivo    = document.getElementById('canMotivo')?.value?.trim();

    if (!empresaId) {
        showToast('Seleccione la empresa emisora del DTE.', 'warning');
        return;
    }
    if (!motivo || motivo.length < 5) {
        showToast('El motivo debe tener al menos 5 caracteres.', 'warning');
        return;
    }
    if (!_cancelacionCdc || _cancelacionCdc.length !== 44) {
        showToast('CDC inválido. No se puede procesar la cancelación.', 'error');
        return;
    }

    const btn = document.getElementById('btnConfirmarCancelacion');
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';

    try {
        const res = await fetch('/api/v1/eventos/cancelacion', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cdc:       _cancelacionCdc,
                motivo:    motivo,
                empresaId: empresaId
            })
        });

        const data = await res.json();

        if (res.ok && data.aprobado) {
            showToast('✓ Cancelación aprobada por SIFEN.', 'success');
            cerrarModalCancelacion();
            loadDocumentos();       // Refrescar tabla de documentos
            cargarHistorialEventos();
        } else if (res.ok && !data.aprobado) {
            showToast(`SIFEN rechazó la cancelación (${data.codigoSifen}): ${data.mensajeSifen}`, 'warning');
            cerrarModalCancelacion();
            cargarHistorialEventos();
        } else {
            showToast(`Error: ${data.message || 'Error desconocido'}`, 'error');
        }
    } catch (e) {
        showToast('Error de red al procesar cancelación: ' + e.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-ban"></i> Confirmar Cancelación';
    }
};

// ============================================================
// Inutilización de numeración
// ============================================================

function abrirFormInutilizacion() {
    const section = document.getElementById('sectionInutilizacion');
    if (section) {
        section.style.display = section.style.display === 'none' ? 'block' : 'none';
        section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

window.submitInutilizacion = async function(event) {
    event.preventDefault();

    const empresaId       = document.getElementById('inutEmpresaSelect')?.value;
    const timbrado        = document.getElementById('inutTimbrado')?.value?.trim();
    const tipoDocumento   = parseInt(document.getElementById('inutTipoDoc')?.value);
    const establecimiento = document.getElementById('inutEstablecimiento')?.value?.trim();
    const puntoExpedicion = document.getElementById('inutPuntoExpedicion')?.value?.trim();
    const rangoDesde      = parseInt(document.getElementById('inutRangoDesde')?.value);
    const rangoHasta      = parseInt(document.getElementById('inutRangoHasta')?.value);
    const motivo          = document.getElementById('inutMotivo')?.value?.trim();

    // Validaciones de campo
    if (!empresaId)                         { showToast('Seleccione la empresa emisora.', 'warning'); return; }
    if (!timbrado || !/^\d{8}$/.test(timbrado)) { showToast('El timbrado debe tener 8 dígitos numéricos.', 'warning'); return; }
    if (!establecimiento)                   { showToast('El campo Establecimiento es obligatorio.', 'warning'); return; }
    if (!puntoExpedicion)                   { showToast('El campo Punto de Expedición es obligatorio.', 'warning'); return; }
    if (!rangoDesde || !rangoHasta)         { showToast('Ingrese el rango de numeración.', 'warning'); return; }
    if (rangoDesde > rangoHasta)            { showToast('El rango Desde no puede ser mayor que Hasta.', 'warning'); return; }
    if (!motivo || motivo.length < 5)       { showToast('El motivo debe tener al menos 5 caracteres.', 'warning'); return; }

    const btn = document.getElementById('btnSubmitInutilizacion');
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';

    try {
        const res = await fetch('/api/v1/eventos/inutilizacion', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                empresaId, timbrado, tipoDocumento,
                establecimiento, puntoExpedicion,
                rangoDesde, rangoHasta, motivo
            })
        });

        const data = await res.json();

        if (res.ok && data.aprobado) {
            showToast('✓ Inutilización aprobada por SIFEN.', 'success');
            document.getElementById('formInutilizacion').reset();
            // Restaurar selección de empresa activa después del reset
            const empresaData = localStorage.getItem('empresa_activa');
            if (empresaData) {
                try {
                    const empresa = JSON.parse(empresaData);
                    const sel = document.getElementById('inutEmpresaSelect');
                    if (sel) {
                        sel.value = empresa.id;
                        sel.disabled = true;
                    }
                } catch(e) {}
            }
            onInutEmpresaChange();
            document.getElementById('sectionInutilizacion').style.display = 'none';
            cargarHistorialEventos();
        } else if (res.ok && !data.aprobado) {
            showToast(`SIFEN rechazó la inutilización (${data.codigoSifen}): ${data.mensajeSifen}`, 'warning');
            cargarHistorialEventos();
        } else {
            showToast(`Error: ${data.message || 'Error al procesar'}`, 'error');
        }
    } catch (e) {
        showToast('Error de red en inutilización: ' + e.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-ban"></i> Enviar Inutilización a SIFEN';
    }
};

// ============================================================
// Historial de eventos
// ============================================================

async function cargarHistorialEventos() {
    const tbody = document.getElementById('tbodyEventos');
    if (!tbody) return;

    tbody.innerHTML = '<tr><td colspan="7" class="text-center">Cargando eventos...</td></tr>';

    try {
        const res = await fetch('/api/v1/eventos');
        if (!res.ok) throw new Error('Error cargando historial');
        const eventos = await res.json();

        if (eventos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center" style="color:var(--text-muted)">No hay eventos registrados</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        eventos.forEach(ev => {
            const tr = document.createElement('tr');
            const estadoClass = ev.estado === 'APROBADO' ? 'aprobado'
                              : ev.estado === 'RECHAZADO' ? 'rechazado'
                              : 'pendiente';

            const tipoLabel = ev.tipoEvento === 'CANCELACION'    ? '<span class="badge-type cancelacion">Cancelación</span>'
                            : ev.tipoEvento === 'INUTILIZACION'  ? '<span class="badge-type inutilizacion">Inutilización</span>'
                            : ev.tipoEvento;

            let refLabel = ev.cdcRelacionado || '—';
            if (ev.tipoEvento === 'INUTILIZACION') {
                const rangoDesdeStr = String(ev.rangoDesde || 0).padStart(7, '0');
                const rangoHastaStr = String(ev.rangoHasta || 0).padStart(7, '0');
                const tipoDoc = nombreTipoDocSifen(ev.tipoDocumentoInutilizar);
                refLabel = `${tipoDoc} | Timb: ${ev.timbrado || '—'} | ${ev.establecimiento || '—'}-${ev.puntoExpedicion || '—'}-[${rangoDesdeStr}..${rangoHastaStr}]`;
            }

            tr.innerHTML = `
                <td>${formatDate(ev.fechaCreacion)}</td>
                <td>${tipoLabel}</td>
                <td class="mono" style="font-size:0.75em; word-break:break-all; max-width:320px;" title="${refLabel}">${refLabel}</td>
                <td><span class="badge-status ${estadoClass}">${ev.estado}</span></td>
                <td class="mono">${ev.codigoSifen || '—'}</td>
                <td title="${ev.mensajeUsuario || ''}">${truncar(ev.mensajeUsuario, 45)}</td>
                <td>
                    <button class="btn btn-xs btn-info" onclick="abrirDetalleEvento('${ev.eventoId}')" title="Ver detalle">
                        <i class="fas fa-eye"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center error">Error: ${e.message}</td></tr>`;
    }
}

// ============================================================
// Modal detalle de un evento
// ============================================================

window.abrirDetalleEvento = async function(eventoId) {
    try {
        const res = await fetch(`/api/v1/eventos/${eventoId}`);
        if (!res.ok) throw new Error('No se pudo cargar el evento.');
        const ev = await res.json();

        const estadoClass = ev.estado === 'APROBADO' ? 'aprobado'
                          : ev.estado === 'RECHAZADO' ? 'rechazado'
                          : 'pendiente';

        document.getElementById('evDetEstado').innerHTML =
            `<span class="badge-status ${estadoClass}">${ev.estado}</span>`;
        document.getElementById('evDetTipo').textContent    = ev.tipoEvento    || '—';
        document.getElementById('evDetCodigo').textContent  = ev.codigoSifen   || '—';
        document.getElementById('evDetCdc').textContent     = ev.cdcRelacionado || '—';
        document.getElementById('evDetMensaje').innerHTML = ev.mensajeSifen  || '—';
        document.getElementById('evDetUsuario').innerHTML = ev.mensajeUsuario || '—';
        document.getElementById('evDetFecha').textContent   = ev.fechaCreacion  || '—';
        document.getElementById('evDetRespuesta').textContent = ev.xmlRespuestaSifen || '(sin respuesta)';
        document.getElementById('evDetRespuestaBox').style.display = 'none';

        // Manejar visualización condicional para inutilización
        const timbradoCol = document.getElementById('evDetInutTimbradoCol');
        const estPuntoCol = document.getElementById('evDetInutEstPuntoCol');
        const tipoDocCol  = document.getElementById('evDetInutTipoDocCol');
        const rangoCol    = document.getElementById('evDetInutRangoCol');
        const cdcCol      = document.getElementById('evDetCdcCol');

        if (ev.tipoEvento === 'INUTILIZACION') {
            document.getElementById('evDetInutTimbrado').textContent = ev.timbrado || '—';
            document.getElementById('evDetInutEstPunto').textContent = `${ev.establecimiento || '—'}-${ev.puntoExpedicion || '—'}`;
            document.getElementById('evDetInutTipoDoc').textContent  = nombreTipoDocSifen(ev.tipoDocumentoInutilizar);
            const rangoDesdeStr = String(ev.rangoDesde || 0).padStart(7, '0');
            const rangoHastaStr = String(ev.rangoHasta || 0).padStart(7, '0');
            document.getElementById('evDetInutRango').textContent    = `${rangoDesdeStr} a ${rangoHastaStr}`;

            if (timbradoCol) timbradoCol.style.display = 'block';
            if (estPuntoCol) estPuntoCol.style.display = 'block';
            if (tipoDocCol)  tipoDocCol.style.display  = 'block';
            if (rangoCol)    rangoCol.style.display    = 'block';
            if (cdcCol)      cdcCol.style.display      = 'none';
        } else {
            if (timbradoCol) timbradoCol.style.display = 'none';
            if (estPuntoCol) estPuntoCol.style.display = 'none';
            if (tipoDocCol)  tipoDocCol.style.display  = 'none';
            if (rangoCol)    rangoCol.style.display    = 'none';
            if (cdcCol)      cdcCol.style.display      = 'block';
        }

        // Guardar para ver XML
        document.getElementById('evDetRespuestaBox').dataset.eventoId = eventoId;

        document.getElementById('modalDetalleEvento').style.display = 'flex';
    } catch (e) {
        showToast('Error cargando detalle del evento: ' + e.message, 'error');
    }
};

window.cerrarDetalleEvento = function() {
    document.getElementById('modalDetalleEvento').style.display = 'none';
};

window.toggleRespuestaEvento = async function() {
    const box = document.getElementById('evDetRespuestaBox');
    box.style.display = box.style.display === 'none' ? 'block' : 'none';
};

// ============================================================
// Botón de cancelación integrado en documentos.js (inyectado)
// ============================================================

// Esta función es invocada desde la tabla de documentos (documentos.js)
// cuando el usuario hace clic en el botón "Anular" de un DTE aprobado.
window.iniciarCancelacionDte = async function(docId, cdc) {
    if (!cdc) return showToast('El DTE no tiene CDC, no se puede cancelar.', 'error');
    
    // Verificar si ya fue cancelado previamente
    try {
        showToast('Verificando estado del DTE...', 'info');
        const res = await fetch(`/api/v1/eventos/estado/${cdc}`);
        if (res.ok) {
            const data = await res.json();
            if (data.cancelado) {
                return Notifier.alert(`El documento con CDC ${cdc.substring(0,10)}... ya se encuentra CANCELADO en los registros de SIFEN.\nNo es necesario enviarlo de nuevo.`);
            }
        }
    } catch (e) {
        console.warn("No se pudo verificar el estado previo de cancelación:", e);
    }

    switchView('eventos');
    // Pequeño delay para asegurar que la vista esté visible antes del modal
    setTimeout(() => abrirModalCancelacion(docId, cdc), 300);
};

// ============================================================
// Eventos de Receptor
// ============================================================

window.toggleMotivoReceptor = function() {
    const tipo = parseInt(document.getElementById('receptorTipoEvento')?.value);
    const reqMotivo = document.getElementById('reqMotivoReceptor');
    const divMotivo = document.getElementById('divMotivoReceptor');
    
    // Alerta de 72 horas para Disconformidad (4) o Desconocimiento (5)
    let warningMsg = document.getElementById('warning72h');
    if (!warningMsg) {
        warningMsg = document.createElement('div');
        warningMsg.id = 'warning72h';
        warningMsg.className = 'text-warning small-text mb-2';
        warningMsg.innerHTML = '<i class="fas fa-exclamation-triangle"></i> <strong>Atención:</strong> Según reglas de SIFEN, los rechazos (Disconformidad) deben realizarse dentro del plazo reglamentario (ej. 72 horas) desde la emisión del DTE.';
        divMotivo.parentNode.insertBefore(warningMsg, divMotivo);
    }
    
    if (tipo === 4 || tipo === 5) {
        reqMotivo.style.display = 'inline';
        document.getElementById('receptorMotivo').required = true;
        warningMsg.style.display = 'block';
    } else {
        reqMotivo.style.display = 'none';
        document.getElementById('receptorMotivo').required = false;
        warningMsg.style.display = 'none';
    }
};

window.limpiarFormReceptor = function() {
    document.getElementById('receptorCdc').value = '';
    document.getElementById('receptorTipoEvento').value = '';
    document.getElementById('receptorMotivo').value = '';
    toggleMotivoReceptor();
};

window.enviarEventoReceptor = async function() {
    const empresaId  = document.getElementById('receptorEmpresaId')?.value;
    const cdc        = document.getElementById('receptorCdc')?.value?.trim();
    const tipoEvento = parseInt(document.getElementById('receptorTipoEvento')?.value);
    const motivo     = document.getElementById('receptorMotivo')?.value?.trim();

    if (!empresaId) { showToast('Seleccione la empresa receptora.', 'warning'); return; }
    if (!cdc || cdc.length !== 44) { showToast('El CDC debe tener exactamente 44 dígitos.', 'warning'); return; }
    if (!tipoEvento) { showToast('Seleccione el tipo de evento a generar.', 'warning'); return; }
    if ((tipoEvento === 4 || tipoEvento === 5) && (!motivo || motivo.length < 5)) {
        showToast('El motivo es obligatorio y debe tener al menos 5 caracteres para este evento.', 'warning'); return;
    }

    const btn = document.getElementById('btnEnviarEventoReceptor');
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';

    try {
        const res = await fetch('/api/v1/eventos/receptor', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cdc, tipoEvento, motivo, empresaId
            })
        });

        const data = await res.json();

        if (res.ok && data.aprobado) {
            showToast('✓ Evento de receptor aprobado por SIFEN.', 'success');
            limpiarFormReceptor();
            cargarHistorialEventos();
        } else if (res.ok && !data.aprobado) {
            showToast(`SIFEN rechazó el evento (${data.codigoSifen}): ${data.mensajeSifen}`, 'warning');
            cargarHistorialEventos();
        } else {
            showToast(`Error: ${data.message || 'Error al procesar el evento'}`, 'error');
        }
    } catch (e) {
        showToast('Error de red al procesar el evento de receptor: ' + e.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-paper-plane"></i> Enviar Evento a SIFEN';
    }
};

// ============================================================
// Utilidades
// ============================================================

function truncar(str, max) {
    if (!str) return '—';
    return str.length > max ? str.substring(0, max) + '…' : str;
}
