/**
 * auth.js
 * Interceptor de autenticación para inyectar JWT en todas las peticiones fetch.
 * También gestiona el estado de sesión y redirección al login.
 */

// Interceptar window.fetch original
const originalFetch = window.fetch;

window.fetch = async function () {
    let [resource, config] = arguments;
    
    // Obtener la URL de la petición
    let url = typeof resource === 'string' ? resource : (resource instanceof Request ? resource.url : '');
    
    // Si la URL es de login o recursos estáticos, dejar pasar sin interceptar
    // (seleccionar-empresa SI requiere token ya que se llama con token temporal o de sesión)
    if (url.includes('/api/v1/auth/login')) {
        return originalFetch(resource, config);
    }
    
    const token = localStorage.getItem('jwt_token');
    
    // Si no hay token, redirigir a login
    if (!token && !window.location.pathname.includes('login.html')) {
        window.location.href = '/login.html';
        return Promise.reject('No autenticado');
    }
    
    // Configurar cabeceras
    if (!config) {
        config = {};
    }
    if (!config.headers) {
        config.headers = {};
    }
    
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    try {
        const response = await originalFetch(resource, config);
        
        // Si el token expiró o es inválido (401 Unauthorized)
        if (response.status === 401) {
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('empresa_activa');
            if (!window.location.pathname.includes('login.html')) {
                window.location.href = '/login.html';
            }
        }
        
        return response;
    } catch (error) {
        throw error;
    }
};

function logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('empresa_activa');
    window.location.href = '/login.html';
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

function applyRoleRestrictions() {
    const token = localStorage.getItem('jwt_token');
    if (!token) return;
    
    const payload = parseJwt(token);
    if (!payload) return;
    
    let isOperador = false;
    if (payload.roles && payload.roles.includes('ROLE_OPERADOR')) isOperador = true;
    if (payload.authorities && payload.authorities.includes('ROLE_OPERADOR')) isOperador = true;
    if (payload.role === 'OPERADOR') isOperador = true;
    if (payload.rol === 'OPERADOR') isOperador = true;
    if (payload.rol === 'ROLE_OPERADOR') isOperador = true;

    // Obtener y actualizar avatar
    const avatarImg = document.getElementById('userProfileAvatar');
    const displayNombre = payload.nombreCompleto || payload.sub || 'Usuario';
    
    if (avatarImg) {
        avatarImg.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(displayNombre)}&background=0D8ABC&color=fff`;
    }

    if (isOperador) {
        const navEmpresas = document.getElementById('nav-empresas');
        const navUsuarios = document.getElementById('nav-usuarios');
        const navConfig = document.getElementById('nav-config');
        const navLotes = document.getElementById('nav-lotes');
        
        if(navEmpresas) navEmpresas.style.display = 'none';
        if(navUsuarios) navUsuarios.style.display = 'none';
        if(navConfig) navConfig.style.display = 'none';
        
        // Hide name and role of super admin in sidebar footer to show correct info
        const userRoleElement = document.querySelector('.user-role');
        const userNameElement = document.querySelector('.user-name');
        if(userRoleElement) userRoleElement.textContent = 'Operador';
        if(userNameElement) {
            userNameElement.textContent = displayNombre;
            userNameElement.title = displayNombre;
        }
    } else {
        const userRoleElement = document.querySelector('.user-role');
        const userNameElement = document.querySelector('.user-name');
        if(userRoleElement) userRoleElement.textContent = 'Administrador';
        if(userNameElement) {
            userNameElement.textContent = displayNombre;
            userNameElement.title = displayNombre;
        }
    }
}

// Inicializar UI de usuario (ej: Header)
document.addEventListener('DOMContentLoaded', () => {
    const empresaInfo = document.getElementById('active-empresa-info');
    const empresaData = localStorage.getItem('empresa_activa');
    const btnChange = document.getElementById('btnChangeEmpresa');
    
    if (empresaInfo && empresaData) {
        try {
            const emp = JSON.parse(empresaData);
            empresaInfo.textContent = `${emp.razonSocial} (${emp.ruc}-${emp.dv})`;
        } catch(e) {
            empresaInfo.textContent = "Error en sesión";
        }
    }

    if (btnChange) {
        btnChange.addEventListener('click', abrirModalCambio);
    }
    
    applyRoleRestrictions();
});

async function abrirModalCambio() {
    const modal = document.getElementById('modalCambioEmpresa');
    const container = document.getElementById('listaEmpresasSelector');
    if (!modal || !container) return;

    modal.style.display = 'flex';
    container.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></i> Cargando empresas...</div>';

    try {
        const response = await fetch('/api/v1/empresas');
        const empresas = await response.json();

        if (empresas.length === 0) {
            container.innerHTML = '<p class="text-center">No hay empresas vinculadas a este usuario.</p>';
            return;
        }

        container.innerHTML = '';
        empresas.forEach(emp => {
            const card = document.createElement('div');
            card.className = 'company-card-mini';
            card.innerHTML = `
                <div class="company-logo-mini">
                    ${emp.logoBase64 ? `<img src="${emp.logoBase64}">` : `<i class="fas fa-building"></i>`}
                </div>
                <div class="company-details-mini">
                    <strong>${emp.razonSocial}</strong>
                    <small>${emp.ruc}-${emp.dv}</small>
                </div>
                <button class="btn btn-xs btn-primary" onclick="seleccionarEmpresaSession('${emp.id}')">
                    Seleccionar
                </button>
            `;
            container.appendChild(card);
        });
    } catch (error) {
        container.innerHTML = '<p class="text-center text-error">Error al cargar empresas.</p>';
    }
}

function cerrarModalCambio() {
    const modal = document.getElementById('modalCambioEmpresa');
    if (modal) modal.style.display = 'none';
}

async function seleccionarEmpresaSession(empresaId) {
    const setDefault = document.getElementById('chk-switch-default')?.checked || false;
    try {
        const response = await fetch('/api/v1/auth/seleccionar-empresa', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ empresaId, setDefault: setDefault.toString() })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('empresa_activa', JSON.stringify(data.empresa));
            
            showToast('Empresa cambiada correctamente', 'success');
            
            // Recargar la página para limpiar estados previos y aplicar nuevo contexto
            setTimeout(() => window.location.reload(), 800);
        } else {
            const err = await response.json().catch(() => ({ error: 'Error desconocido' }));
            showToast(err.error || 'Error al seleccionar la empresa', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
}
