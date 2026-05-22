// usuarios.js - Lógica para la gestión de usuarios en el portal de cliente

document.addEventListener('DOMContentLoaded', () => {
    // Si la vista inicial es usuarios, cargar datos
    if (document.getElementById('view-usuarios') && document.getElementById('view-usuarios').classList.contains('active')) {
        loadUsuarios();
    }
});

async function loadUsuarios() {
    const tbody = document.getElementById('tbodyListaUsuarios');
    if (!tbody) return;
    
    tbody.innerHTML = '<tr><td colspan="4" class="text-center"><i class="fas fa-spinner fa-spin"></i> Cargando usuarios...</td></tr>';
    
    try {
        const res = await fetch('/api/v1/usuarios', {
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('token'),
                'X-Empresa-Id': sessionStorage.getItem('activeEmpresa')
            }
        });
        
        if (!res.ok) {
            throw new Error('No se pudo cargar la lista de usuarios');
        }
        
        const usuarios = await res.json();
        
        if (usuarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center">No hay usuarios registrados en esta empresa.</td></tr>';
            return;
        }
        
        tbody.innerHTML = usuarios.map(u => `
            <tr>
                <td>
                    <div style="font-weight: 500;">${u.nombreCompleto || 'Usuario'}</div>
                    <div class="small-text mono" style="color: var(--text-secondary);">${u.username}</div>
                </td>
                <td>${u.email}</td>
                <td><span class="badge badge-${u.rol === 'ADMIN' ? 'primary' : 'secondary'}">${u.rol}</span></td>
                <td>
                    ${u.activo 
                        ? '<span class="badge badge-success">Activo</span>' 
                        : '<span class="badge badge-danger">Inactivo</span>'}
                </td>
            </tr>
        `).join('');
        
    } catch (err) {
        console.error("Error cargando usuarios:", err);
        tbody.innerHTML = '<tr><td colspan="4" class="text-center" style="color: var(--danger-color);"><i class="fas fa-exclamation-triangle"></i> Error al cargar usuarios</td></tr>';
        showToast("Error al cargar la lista de usuarios", "error");
    }
}

window.abrirModalInvitacion = function() {
    const modal = document.getElementById('modalInvitarUsuario');
    if (modal) {
        document.getElementById('formInvitarUsuario').reset();
        modal.style.display = 'flex';
    }
};

window.cerrarModalInvitacion = function() {
    const modal = document.getElementById('modalInvitarUsuario');
    if (modal) {
        modal.style.display = 'none';
    }
};

window.invitarUsuario = async function(event) {
    event.preventDefault();
    
    const email = document.getElementById('invEmail').value;
    const rol = document.getElementById('invRol').value;
    const verSoloSusDtes = document.getElementById('invVerSoloSusDtes').checked;
    const btn = document.getElementById('btnSubmitInvitar');
    
    const oldHtml = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Enviando...';
    btn.disabled = true;
    
    try {
        const res = await fetch('/api/v1/usuarios/invitar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + sessionStorage.getItem('token'),
                'X-Empresa-Id': sessionStorage.getItem('activeEmpresa')
            },
            body: JSON.stringify({ email, rol, verSoloSusDtes })
        });
        
        if (res.ok) {
            showToast('Invitación enviada correctamente al usuario', 'success');
            cerrarModalInvitacion();
        } else {
            const data = await res.json();
            showToast(data.error || 'No se pudo enviar la invitación', 'error');
        }
    } catch (err) {
        console.error("Error al invitar:", err);
        showToast('Error de conexión al enviar invitación', 'error');
    } finally {
        btn.innerHTML = oldHtml;
        btn.disabled = false;
    }
};
