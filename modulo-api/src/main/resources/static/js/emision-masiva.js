// --- Gestión de Emisión Masiva (Batch Ingestion) ---

let empresasMasivo = [];

document.addEventListener('DOMContentLoaded', () => {
    // Inicializar listeners cuando la vista está activa
    const viewMasiva = document.getElementById('view-emision-masiva');
    if (viewMasiva) {
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                if (mutation.target.classList.contains('active')) {
                    cargarEmpresasMasivo();
                }
            });
        });
        observer.observe(viewMasiva, { attributes: true, attributeFilter: ['class'] });
    }

    // Drag and Drop
    const dropZone = document.getElementById('masivoDropZone');
    if (dropZone) {
        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.style.background = 'rgba(0, 150, 255, 0.1)';
            dropZone.style.borderColor = 'var(--accent-primary)';
        });
        dropZone.addEventListener('dragleave', (e) => {
            e.preventDefault();
            dropZone.style.background = 'rgba(0,0,0,0.2)';
            dropZone.style.borderColor = 'var(--border-color)';
        });
        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.style.background = 'rgba(0,0,0,0.2)';
            dropZone.style.borderColor = 'var(--border-color)';
            if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
                const file = e.dataTransfer.files[0];
                handleFileSelectInternal(file);
            }
        });
    }
});

async function cargarEmpresasMasivo() {
    // Reutiliza la función centralizada de ui.js
    empresasMasivo = await cargarEmpresasEnSelect('masivoEmpresaId');
}

function handleMasivoFileSelect(event) {
    if (event.target.files && event.target.files.length > 0) {
        handleFileSelectInternal(event.target.files[0]);
    }
}

function handleFileSelectInternal(file) {
    if (!file.name.endsWith('.json')) {
        showToast('El archivo debe ser un JSON válido.', 'warning');
        return;
    }
    document.getElementById('masivoFileName').textContent = file.name;
    document.getElementById('masivoFileName').style.color = 'var(--accent-primary)';
    
    const reader = new FileReader();
    reader.onload = (e) => {
        const content = e.target.result;
        try {
            // Validar parse
            JSON.parse(content);
            document.getElementById('masivoJsonText').value = content;
        } catch (err) {
            showToast('El contenido del archivo no es un JSON válido.', 'error');
            document.getElementById('masivoJsonText').value = '';
        }
    };
    reader.readAsText(file);
}

function limpiarFormMasivo() {
    document.getElementById('masivoEmpresaId').value = '';
    document.getElementById('masivoJsonText').value = '';
    document.getElementById('masivoFile').value = '';
    document.getElementById('masivoFileName').textContent = 'Haga clic o arrastre un archivo .json aquí';
    document.getElementById('masivoFileName').style.color = '';
    document.getElementById('masivoResultPanel').style.display = 'none';
}

function descargarPlantillaMasiva() {
    const plantilla = [
        {
            "tipoDocumento": "1",
            "establecimiento": "001",
            "punto": "001",
            "numero": "1",
            "fechaEmision": new Date().toISOString(),
            "receptor": {
                "tipoContribuyente": "2",
                "ruc": "80000001",
                "razonSocial": "EMPRESA DE PRUEBA SIFEN"
            },
            "items": [
                {
                    "codigo": "P001",
                    "descripcion": "Articulo de prueba",
                    "cantidad": 1,
                    "precioUnitario": 150000,
                    "tasaIva": 10
                }
            ]
        }
    ];
    
    const blob = new Blob([JSON.stringify(plantilla, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'plantilla_masiva.json';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}

async function procesarEmisionMasiva() {
    const btn = document.getElementById('btnProcesarMasivo');
    const empresaId = document.getElementById('masivoEmpresaId').value;
    const jsonText = document.getElementById('masivoJsonText').value;

    if (!empresaId) {
        showToast('Debe seleccionar una empresa emisora', 'warning');
        return;
    }

    if (!jsonText || jsonText.trim() === '') {
        showToast('El contenido JSON está vacío. Cargue un archivo o péguelo en el campo.', 'warning');
        return;
    }

    let payloadList;
    try {
        payloadList = JSON.parse(jsonText);
        if (!Array.isArray(payloadList)) {
            showToast('El JSON debe ser un Array (lista de documentos) [...]', 'error');
            return;
        }
    } catch (err) {
        showToast('El JSON ingresado tiene errores de sintaxis', 'error');
        return;
    }

    if (payloadList.length === 0) {
        showToast('El array JSON no contiene ningún documento', 'warning');
        return;
    }

    // Buscar el ruc de la empresa
    const emp = empresasMasivo.find(e => e.id == empresaId);
    if (!emp) return;

    // Inyectar el emisor a cada documento
    payloadList.forEach((doc) => {
        if (!doc.emisor) {
            doc.emisor = {};
        }
        // Fuerza el ruc a usar el seleccionado en la UI
        doc.emisor.ruc = emp.ruc;
    });

    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
    btn.disabled = true;
    
    try {
        const res = await fetch(API.emisionMasivo, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payloadList)
        });
        
        if (!res.ok) {
            const errorBody = await res.json();
            throw new Error(errorBody.message || 'Error en el servidor o timeout');
        }
        
        const respuesta = await res.json();
        
        // Renderizar resultados
        // Renderizar resultados
        mostrarResultadosMasivos(respuesta, payloadList);
        
        // Limpiar el textArea solo si hubo al menos un exito
        if (respuesta.procesadosExitosamente > 0) {
            showToast(`Se encolaron ${respuesta.procesadosExitosamente} documentos exitosamente`, 'success');
            if (respuesta.conErrores === 0) {
                document.getElementById('masivoJsonText').value = '';
                document.getElementById('masivoFile').value = '';
                document.getElementById('masivoFileName').textContent = 'Haga clic o arrastre un archivo .json aquí';
                document.getElementById('masivoFileName').style.color = '';
            }
        } else {
            showToast('Ningún documento pudo ser procesado con éxito', 'warning');
        }
        
    } catch (e) {
        console.error(e);
        showToast('Error al procesar emisión masiva: ' + e.message, 'error');
    } finally {
        btn.innerHTML = '<i class="fas fa-cogs"></i> Procesar Documentos';
        btn.disabled = false;
    }
}

function mostrarResultadosMasivos(res, payloadList) {
    document.getElementById('masivoResultPanel').style.display = 'block';

    document.getElementById('resMasivoTotal').textContent = res.totalRecibidos || 0;
    document.getElementById('resMasivoExito').textContent = res.procesadosExitosamente || 0;
    document.getElementById('resMasivoError').textContent = res.conErrores || 0;

    const tbody = document.getElementById('tbodyMasivoResult');
    tbody.innerHTML = '';

    if (res.resultados && res.resultados.length > 0) {
        res.resultados.forEach(item => {
            const tr = document.createElement('tr');
            
            let statusClass = 'pendiente';
            if (item.estado === 'FIRMADO' || item.estado === 'APROBADO' || item.estado === 'ENVIADO') statusClass = 'aprobado';
            else if (item.estado && item.estado.includes('ERROR')) statusClass = 'rechazado';
            
            let errorText = '';
            if (item.estado === 'ERROR_VALIDACION' || item.estado === 'ERROR_INTERNO') {
                const msg = item.error || 'Error en validación de datos';
                const detalles = item.detalles ? ` (${item.detalles.join(', ')})` : '';
                errorText = `<span class="text-error small-text">${msg}${detalles}</span>`;
            } else {
                errorText = `<span class="mono small-text" style="color:var(--accent-primary);">${item.cdc || '—'}</span>`;
            }
            
            // Tratamos de obtener el Nro Comprobante del payload original
            let nroComprobante = item.idInterno || '—';
            if (!item.idInterno && item.indice !== undefined && payloadList[item.indice]) {
                const p = payloadList[item.indice];
                if (p.establecimiento && p.punto && p.numero) {
                    nroComprobante = `${p.establecimiento}-${p.punto}-${p.numero}`;
                } else if (p.numero) {
                    nroComprobante = p.numero;
                }
            }
            
            tr.innerHTML = `
                <td>${(item.indice !== undefined ? item.indice + 1 : '-')}</td>
                <td>${nroComprobante}</td>
                <td><span class="badge-status ${statusClass}">${item.estado || 'DESCONOCIDO'}</span></td>
                <td>${errorText}</td>
            `;
            tbody.appendChild(tr);
        });
    } else {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">No hay detalles de resultados.</td></tr>';
    }
    
    // Scroll al resultado
    document.getElementById('masivoResultPanel').scrollIntoView({ behavior: 'smooth', block: 'start' });
}
