// --- Estado Global ---
window.sifenRefData = {};

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initEnvironment();
    initReferencias(); // Nueva carga dinámica de SIFEN
    initDashboard();
    initDashboardAnalytics();
    initNavigation();
    initGlobalSearch();
    initSidebar();
    initForm();
});

// Alias para compatibilidad con flujos anteriores
function loadDashboard() {
    if (typeof switchView === 'function') switchView('dashboard');
}

