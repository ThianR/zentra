// --- Constantes Centralizadas de API ---
const API = {
    empresas:       '/api/v1/empresas',
    emision:        '/api/v1/emision',
    emisionMasivo:  '/api/v1/emision/masivo',
    eventos:        '/api/v1/eventos',
    lotes:          '/api/lotes',
    referencias:    '/api/v1/sifen-referencia'
};

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
                const onclickAttr = item.getAttribute('onclick');
                if (onclickAttr && onclickAttr.includes(`switchView('${viewId}')`)) {
                    item.classList.add('active');
                }
            });
            
            if (viewId === 'emision') resetForm();
            if (viewId === 'dashboard') loadDocumentos();
            if (viewId === 'empresas') loadEmpresasGrid();
            if (viewId === 'eventos') initEventos();
            if (viewId === 'receptor') {
                if(typeof cargarEmpresasEnEvento === 'function') cargarEmpresasEnEvento();
            }
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
        '5': 'Nota de CrÃ©dito', // Corregido: 5 es NC
        '6': 'Nota de DÃ©bito',  // Corregido: 6 es ND
        '7': 'RemisiÃ³n' 
    };
    return labels[val] || 'DTE';
}

class ZentraNotifier {
    constructor(containerId = 'toastContainer') {
        this.container = document.getElementById(containerId);
    }

    success(msg, duration) { this._show(msg, 'success', 'check-circle', duration); }
    error(msg, duration) { this._show(msg, 'error', 'exclamation-circle', duration); }
    warning(msg, duration) { this._show(msg, 'warning', 'exclamation-triangle', duration); }
    info(msg, duration) { this._show(msg, 'info', 'info-circle', duration); }
    
    // Reemplazo para alert() con mayor duraciÃ³n
    alert(msg) { this._show(msg, 'warning', 'bell', 10000); }

    _show(message, type, icon, duration = 4000) {
        if (!this.container) {
            this.container = document.getElementById('toastContainer');
            if (!this.container) return;
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type} slide-up`;
        
        // Soporte para saltos de lÃ­nea
        const formattedMsg = String(message).replace(/\n/g, '<br>');
        toast.innerHTML = `<i class="fas fa-${icon}"></i> <span>${formattedMsg}</span>`;
        
        this.container.appendChild(toast);

        let timeoutId;
        const startTimer = () => {
            timeoutId = setTimeout(() => {
                toast.style.opacity = '0';
                setTimeout(() => toast.remove(), 500);
            }, duration);
        };

        startTimer();

        // Pausar al pasar el mouse
        toast.addEventListener('mouseenter', () => {
            clearTimeout(timeoutId);
            toast.style.opacity = '1';
        });

        // Reanudar al quitar el mouse
        toast.addEventListener('mouseleave', () => {
            startTimer();
        });
        
        // Cerrar al hacer clic
        toast.addEventListener('click', () => {
            clearTimeout(timeoutId);
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 500);
        });
    }
}

// Instancia global
window.Notifier = new ZentraNotifier();

// Retrocompatibilidad
window.showToast = function(message, type = 'info') {
    if (window.Notifier[type]) {
        window.Notifier[type](message);
    } else {
        window.Notifier.info(message);
    }
};

// --- Carga unificada de empresas en selectores ---
// Uso: const lista = await cargarEmpresasEnSelect('miSelectId');
async function cargarEmpresasEnSelect(selectId) {
    const select = document.getElementById(selectId);
    if (!select) return [];
    try {
        const res = await fetch(API.empresas);
        if (!res.ok) throw new Error('Error HTTP ' + res.status);
        const empresas = await res.json();
        select.innerHTML = '<option value="">Seleccione empresa emisora...</option>';
        empresas.forEach(e => {
            const opt = document.createElement('option');
            opt.value = e.id;
            opt.textContent = `${e.ruc}-${e.dv || '0'} | ${e.razonSocial}`;
            select.appendChild(opt);
        });
        return empresas;
    } catch (e) {
        console.error('Error cargando empresas en #' + selectId + ':', e);
        select.innerHTML = '<option value="">Error al cargar empresas</option>';
        return [];
    }
}

// --- Búsqueda Global (Fase A6.2) ---

function initGlobalSearch() {
    const searchInput = document.getElementById('globalSearch');
    if (!searchInput) return;

    searchInput.addEventListener('input', debounce((e) => {
        const query = e.target.value.trim().toLowerCase();
        if (query.length === 0) {
            loadDocumentos();
            return;
        }
        if (query.length < 3) return;
        
        loadDocumentos({ search: query });
    }, 300));

    // Atajo Ctrl+K
    document.addEventListener('keydown', (e) => {
        if (e.ctrlKey && e.key === 'k') {
            e.preventDefault();
            searchInput.focus();
        }
    });
}

function debounce(func, wait) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}
