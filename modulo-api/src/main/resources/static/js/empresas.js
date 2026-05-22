// --- GestiÃ³n de Empresas ---

async function loadEmpresasGrid() {
    const tbody = document.getElementById('tbodyListaEmpresas');
    if (!tbody) return;
    
    try {
        const response = await fetch('/api/v1/empresas');
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
                let badgeClass = 'aprobado';
                let iconClass = 'fa-check';
                let badgeText = 'Cargado';
                let fVenc = '';

                if (emp.fechaVencimientoCertificado) {
                    const vDate = new Date(emp.fechaVencimientoCertificado);
                    const now = new Date();
                    const diffDays = Math.ceil((vDate - now) / (1000 * 60 * 60 * 24));
                    
                    if (diffDays < 0) {
                        badgeClass = 'rechazado';
                        iconClass = 'fa-times';
                        badgeText = 'Vencido';
                    } else if (diffDays <= 30) {
                        badgeClass = 'pendiente';
                        iconClass = 'fa-exclamation-triangle';
                        badgeText = 'Por Vencer';
                    }
                    fVenc = `<br><small style="font-size: 0.75rem; color: var(--text-secondary);">Vence: ${emp.fechaVencimientoCertificado}</small>`;
                }
                
                certBadge = `<div style="text-align: center;"><span class="badge-status ${badgeClass}"><i class="fas ${iconClass}"></i> ${badgeText}</span>${fVenc}</div>`;
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
                    <button class="btn btn-xs btn-outline" onclick='abrirFormEmpresa(${empJson})' title="Editar Datos">
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
        tbody.innerHTML = '<tr><td colspan="5" class="text-center error">Error de conexiÃ³n con el servidor</td></tr>';
    }
}

const btnRefreshEmpresas = document.getElementById('btnRefreshEmpresas');
if(btnRefreshEmpresas) {
    btnRefreshEmpresas.addEventListener('click', () => {
        loadEmpresasGrid();
        showToast('Lista de empresas actualizada', 'info');
    });
}

window.abrirFormEmpresa = async function(empresa = null) {
    document.getElementById('formEmpresa').reset();
    currentWizardStep = 1;
    updateWizardUI();
    
    // Cargar selectores dinámicos y esperar
    await initReferencias();

    // Vincular selectores dependientes para el modal
    const deptoSelect = document.getElementById('empresaCodDepto');
    if (deptoSelect && !deptoSelect.onchange) {
        deptoSelect.onchange = (e) => loadCiudadesModal(e.target.value);
    }
    
    // Se eliminÃ³ el onchange que seteaba la descripciÃ³n manualmente

    if (empresa && empresa.id) {
        document.getElementById('modalEmpresaTitle').innerHTML = '<i class="fas fa-edit"></i> Editar Empresa';
        document.getElementById('empresaId').value = empresa.id;
        document.getElementById('empresaRuc').value = empresa.ruc || '';
        document.getElementById('empresaDv').value = empresa.dv || '';
        document.getElementById('empresaRazonSocial').value = empresa.razonSocial || '';
        document.getElementById('empresaTimbrado').value = empresa.timbrado || '';
        document.getElementById('empresaFechaInicioTimbrado').value = empresa.fechaInicioTimbrado || '';
        document.getElementById('empresaFechaVencimientoTimbrado').value = empresa.fechaVencimientoTimbrado || '';
        
        // Asignar actividad (el select ya cargÃ³ por await loadDynamicRefs)
        document.getElementById('empresaCodActividadEconomica').value = empresa.codActividadEconomica || '';
        
        document.getElementById('empresaDireccion').value = empresa.direccion || '';
        document.getElementById('empresaTelefono').value = empresa.telefono || '';
        document.getElementById('empresaEmail').value = empresa.email || '';
        document.getElementById('empresaCodEstablecimiento').value = empresa.codEstablecimiento || '001';
        document.getElementById('empresaPuntoExpedicion').value = empresa.puntoExpedicion || '001';
        
        document.getElementById('empresaAmbiente').value = empresa.ambiente || 'TEST';
        document.getElementById('empresaTipoContribuyente').value = empresa.tipoContribuyente || '2';
        document.getElementById('empresaIdCsc').value = empresa.idCsc || '0001';
        document.getElementById('empresaValorCsc').value = empresa.valorCsc || '';
        
        // Configuraciones de Lotes y Logo
        document.getElementById('empresaFrecuenciaLote').value = empresa.frecuenciaLoteMinutos || 15;
        document.getElementById('empresaFrecuenciaTicket').value = empresa.frecuenciaConsultaTicketMinutos || 5;
        document.getElementById('empresaLogoBase64').value = empresa.logoBase64 || '';
        
        if (empresa.logoBase64) {
            document.getElementById('previewLogoImg').src = empresa.logoBase64;
            document.getElementById('previewLogoImg').style.display = 'block';
            document.getElementById('previewLogoText').style.display = 'none';
            document.getElementById('btnRemoveLogo').style.display = 'inline-block';
        } else {
            quitarLogo();
        }
        
        if (empresa.codDepartamento) {
            document.getElementById('empresaCodDepto').value = empresa.codDepartamento;
            await loadCiudadesModal(empresa.codDepartamento);
            if (empresa.codCiudad) {
                document.getElementById('empresaCodCiudad').value = empresa.codCiudad;
            }
        }
        
        // SMTP
        document.getElementById('empresaSmtpHost').value = empresa.smtpHost || '';
        document.getElementById('empresaSmtpPort').value = empresa.smtpPort || '';
        document.getElementById('empresaSmtpUsername').value = empresa.smtpUsername || '';
        document.getElementById('empresaSmtpPassword').value = ''; // Por seguridad no devolvemos la contraseña
        document.getElementById('empresaSmtpUseTls').checked = empresa.smtpUseTls !== false; // Por defecto true
        
    } else {
        document.getElementById('modalEmpresaTitle').innerHTML = '<i class="fas fa-building"></i> Nueva Empresa';
        document.getElementById('empresaId').value = '';
    }
    
    switchView('empresa-form');
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

// Listener para actualizar descripciÃ³n de actividad automÃ¡ticamente
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

window.cerrarFormEmpresa = function() {
    switchView('empresas');
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
    // Validar solo el paso actual si estamos en un paso
    const currentStepDiv = document.querySelector(`.wizard-step[data-step="${currentWizardStep}"]`);
    if (currentStepDiv) {
        const inputs = currentStepDiv.querySelectorAll("input, select, textarea");
        for (let input of inputs) {
            if (!input.checkValidity()) {
                input.reportValidity();
                return;
            }
        }
    }

    const empresaId = document.getElementById('empresaId').value;
    const ruc = document.getElementById('empresaRuc').value;
    const razonSocial = document.getElementById('empresaRazonSocial').value;
    const dv = document.getElementById('empresaDv').value;

    if (!ruc || !razonSocial || !dv) {
        return showToast('Los campos RUC, DV y RazÃ³n Social son requeridos', 'warning');
    }

    const codActEcon = document.getElementById('empresaCodActividadEconomica').value;
    const actDescObj = window.sifenRefData['ACTIVIDAD_ECONOMICA']?.find(a => a.codigo === codActEcon);
    const actDesc = actDescObj ? actDescObj.descripcion : '';

    const payload = {
        ruc: ruc,
        dv: dv,
        razonSocial: razonSocial,
        timbrado: document.getElementById('empresaTimbrado').value,
        fechaInicioTimbrado: document.getElementById('empresaFechaInicioTimbrado').value || null,
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
        tipoContribuyente: parseInt(document.getElementById('empresaTipoContribuyente').value),
        idCsc: document.getElementById('empresaIdCsc').value,
        valorCsc: document.getElementById('empresaValorCsc').value,
        frecuenciaLoteMinutos: parseInt(document.getElementById('empresaFrecuenciaLote').value) || 15,
        frecuenciaConsultaTicketMinutos: parseInt(document.getElementById('empresaFrecuenciaTicket').value) || 5,
        logoBase64: document.getElementById('empresaLogoBase64').value,
        smtpHost: document.getElementById('empresaSmtpHost').value,
        smtpPort: parseInt(document.getElementById('empresaSmtpPort').value) || null,
        smtpUsername: document.getElementById('empresaSmtpUsername').value,
        smtpPasswordPlain: document.getElementById('empresaSmtpPassword').value,
        smtpUseTls: document.getElementById('empresaSmtpUseTls').checked
    };

    const isEdit = !!empresaId;
    const url = isEdit ? `/api/v1/empresas/${empresaId}` : '/api/v1/empresas';
    const method = isEdit ? 'PUT' : 'POST';

    const btn = document.getElementById('btnWizardFinish');
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
            showToast(`Empresa ${isEdit ? 'actualizada' : 'registrada'} correctamente`, 'success');
            cerrarFormEmpresa();
            loadEmpresasGrid();
            // Si hay un selector de emisor en la vista principal, refrescarlo
            if (typeof loadEmpresasEmisoras === 'function') loadEmpresasEmisoras();
        } else {
            const errorMsg = await res.text();
            showToast(errorMsg || 'Error al procesar la solicitud', 'error');
        }
    } catch (e) {
        showToast('Error de conexiÃ³n con el servidor', 'error');
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
        return showToast('Por favor ingrese la contraseÃ±a del certificado', 'error');
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
            showToast('Certificado guardado correctamente', 'success');
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

// --- Manejo del Logo de la Empresa ---
document.addEventListener('DOMContentLoaded', () => {
    const logoInput = document.getElementById('empresaLogoFile');
    if (logoInput) {
        logoInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (!file) return;
            
            // Validar tamaÃ±o (mÃ¡ximo 500KB)
            if (file.size > 500 * 1024) {
                showToast('El logo no debe superar los 500KB', 'warning');
                this.value = '';
                return;
            }
            
            const reader = new FileReader();
            reader.onload = function(evt) {
                const base64Str = evt.target.result;
                document.getElementById('empresaLogoBase64').value = base64Str;
                
                // Mostrar preview
                const img = document.getElementById('previewLogoImg');
                img.src = base64Str;
                img.style.display = 'block';
                document.getElementById('previewLogoText').style.display = 'none';
                document.getElementById('btnRemoveLogo').style.display = 'inline-block';
            };
            reader.readAsDataURL(file);
        });
    }
});

window.quitarLogo = function() {
    document.getElementById('empresaLogoFile').value = '';
    document.getElementById('empresaLogoBase64').value = '';
    document.getElementById('previewLogoImg').src = '';
    document.getElementById('previewLogoImg').style.display = 'none';
    document.getElementById('previewLogoText').style.display = 'block';
    document.getElementById('btnRemoveLogo').style.display = 'none';
};

// --- Navegación del Wizard ---
let currentWizardStep = 1;
const totalWizardSteps = 3;

function updateWizardUI() {
    // Actualizar los indicadores
    document.querySelectorAll(".wizard-step-indicator").forEach(el => {
        const step = parseInt(el.getAttribute("data-step-indicator"));
        if (step === currentWizardStep) el.classList.add("active");
        else el.classList.remove("active");
    });

    // Actualizar las vistas
    document.querySelectorAll(".wizard-step").forEach(el => {
        const step = parseInt(el.getAttribute("data-step"));
        if (step === currentWizardStep) el.style.display = "block";
        else el.style.display = "none";
    });

    // Actualizar botones
    const btnPrev = document.getElementById("btnWizardPrev");
    const btnNext = document.getElementById("btnWizardNext");
    const btnFinish = document.getElementById("btnWizardFinish");

    if (currentWizardStep === 1) {
        btnPrev.style.visibility = "hidden";
    } else {
        btnPrev.style.visibility = "visible";
    }

    if (currentWizardStep === totalWizardSteps) {
        btnNext.style.display = "none";
        btnFinish.style.display = "inline-block";
    } else {
        btnNext.style.display = "inline-block";
        btnFinish.style.display = "none";
    }
}

window.nextWizardStep = function() {
    const currentStepDiv = document.querySelector(`.wizard-step[data-step="${currentWizardStep}"]`);
    const inputs = currentStepDiv.querySelectorAll("input, select, textarea");
    let isValid = true;
    
    for (let input of inputs) {
        if (!input.checkValidity()) {
            input.reportValidity();
            isValid = false;
            break;
        }
    }

    if (isValid && currentWizardStep < totalWizardSteps) {
        currentWizardStep++;
        updateWizardUI();
    }
};

window.prevWizardStep = function() {
    if (currentWizardStep > 1) {
        currentWizardStep--;
        updateWizardUI();
    }
};

window.probarConexionSmtp = async function() {
    const host = document.getElementById("empresaSmtpHost").value;
    const port = parseInt(document.getElementById("empresaSmtpPort").value);
    const username = document.getElementById("empresaSmtpUsername").value;
    const password = document.getElementById("empresaSmtpPassword").value;
    const useTls = document.getElementById("empresaSmtpUseTls").checked;

    if (!host || !port || !username || !password) {
        return showToast("Complete Host, Puerto, Usuario y Contraseña para probar", "warning");
    }

    const btn = document.getElementById("btnTestSmtp");
    const originalText = btn.innerHTML;
    btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Probando...`;
    btn.disabled = true;

    try {
        const res = await fetch("/api/v1/empresas/test-smtp", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ host, port, username, password, useTls })
        });

        const data = await res.json();
        if (res.ok && data.success) {
            showToast("Conexión SMTP exitosa", "success");
        } else {
            showToast(data.error || "Fallo en conexión", "error");
        }
    } catch (e) {
        showToast("Error al probar conexión SMTP", "error");
    } finally {
        btn.innerHTML = originalText;
        btn.disabled = false;
    }
};

