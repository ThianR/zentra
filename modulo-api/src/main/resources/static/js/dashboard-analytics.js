// --- Zentra Dashboard Analytics ---

let charts = {};

function initDashboardAnalytics() {
    loadCharts();
    
    // Listener para el selector de días
    const selectDias = document.getElementById('chartRangoDias');
    if (selectDias) {
        selectDias.addEventListener('change', (e) => {
            loadChartEmisiones(e.target.value);
        });
    }
}

async function loadCharts() {
    await Promise.all([
        loadChartEmisiones(30),
        loadChartEstados(),
        loadChartTopReceptores(),
        loadChartMensual()
    ]);
}

async function loadChartEmisiones(dias) {
    try {
        const res = await fetch(`/api/v1/estadisticas/resumen-diario?dias=${dias}`);
        const data = await res.json();
        
        // Procesar datos para Chart.js
        const labels = [...new Set(data.map(item => item.fecha))].sort();
        const datasets = [
            {
                label: 'Aprobados',
                data: labels.map(f => {
                    const found = data.find(item => item.fecha === f && item.estado === 'APROBADO');
                    return found ? found.cantidad : 0;
                }),
                backgroundColor: 'rgba(25, 135, 84, 0.5)',
                borderColor: '#198754',
                borderWidth: 1
            },
            {
                label: 'Rechazados',
                data: labels.map(f => {
                    const found = data.find(item => item.fecha === f && (item.estado.includes('ERROR') || item.estado === 'RECHAZADO'));
                    return found ? found.cantidad : 0;
                }),
                backgroundColor: 'rgba(220, 53, 69, 0.5)',
                borderColor: '#dc3545',
                borderWidth: 1
            }
        ];

        renderChart('chartEmisionesDiarias', 'bar', labels, datasets, {
            onClick: (e, elements) => {
                if (elements.length > 0) {
                    const idx = elements[0].index;
                    const datasetIdx = elements[0].datasetIndex;
                    const fecha = labels[idx];
                    const labelEstado = datasets[datasetIdx].label;
                    const estado = labelEstado === 'Aprobados' ? 'APROBADO' : 'RECHAZADO';
                    filtrarTablaDesdeGrafico({ fecha, estado });
                }
            }
        });
    } catch (e) {
        console.error("Error al cargar gráfico de emisiones:", e);
    }
}

async function loadChartEstados() {
    try {
        const res = await fetch('/api/v1/estadisticas/resumen-estado');
        const data = await res.json();
        
        // Actualizar Gauge de Salud y Alertas (Fase A6)
        updateHealthGauge(data);

        const labels = ['Aprobados', 'Rechazados', 'Pendientes', 'En Proceso', 'Anulados'];
        const datasets = [{
            data: [data.aprobados, data.rechazados, data.pendientes, data.enProceso, data.anulados],
            backgroundColor: [
                '#198754', // Aprobados
                '#dc3545', // Rechazados
                '#ffc107', // Pendientes
                '#0d6efd', // En Proceso
                '#6c757d'  // Anulados
            ],
            borderWidth: 0
        }];

        renderChart('chartEstados', 'doughnut', labels, datasets, {
            plugins: {
                legend: { position: 'right' }
            },
            onClick: (e, elements) => {
                if (elements.length > 0) {
                    const idx = elements[0].index;
                    const estadoMap = {
                        'Aprobados': 'APROBADO',
                        'Rechazados': 'RECHAZADO',
                        'Pendientes': 'PENDIENTE',
                        'En Proceso': 'EN_PROCESO',
                        'Anulados': 'ANULADO'
                    };
                    const estado = estadoMap[labels[idx]];
                    filtrarTablaDesdeGrafico({ estado });
                }
            }
        });
    } catch (e) {
        console.error("Error al cargar gráfico de estados:", e);
    }
}

async function loadChartTopReceptores() {
    try {
        const res = await fetch('/api/v1/estadisticas/top-receptores?limit=10');
        const data = await res.json();
        
        const labels = data.map(item => item.razonSocial || item.ruc);
        const datasets = [{
            label: 'Monto Total',
            data: data.map(item => item.montoTotal),
            backgroundColor: 'rgba(13, 110, 253, 0.6)',
            borderColor: '#0d6efd',
            borderWidth: 1
        }];

        renderChart('chartTopReceptores', 'bar', labels, datasets, {
            indexAxis: 'y',
            onClick: (e, elements) => {
                if (elements.length > 0) {
                    const idx = elements[0].index;
                    const ruc = data[idx].ruc;
                    filtrarTablaDesdeGrafico({ ruc });
                }
            }
        });
    } catch (e) {
        console.error("Error al cargar gráfico de receptores:", e);
    }
}

async function loadChartMensual() {
    try {
        const res = await fetch('/api/v1/estadisticas/facturacion-mensual?meses=6');
        const data = await res.json();
        
        // Invertir para que vaya de antiguo a nuevo
        data.reverse();
        
        const labels = data.map(item => `${item.mes}/${item.anio}`);
        const datasets = [
            {
                label: 'Facturación Total',
                data: data.map(item => item.montoTotal),
                borderColor: '#0d6efd',
                backgroundColor: 'rgba(13, 110, 253, 0.1)',
                fill: true,
                tension: 0.4
            },
            {
                label: 'Monto IVA',
                data: data.map(item => item.montoIva),
                borderColor: '#198754',
                backgroundColor: 'transparent',
                borderDash: [5, 5]
            }
        ];

        renderChart('chartFacturacionMensual', 'line', labels, datasets);
    } catch (e) {
        console.error("Error al cargar gráfico mensual:", e);
    }
}

function renderChart(id, type, labels, datasets, options = {}) {
    const ctx = document.getElementById(id);
    if (!ctx) return;

    if (charts[id]) {
        charts[id].destroy();
    }

    const isDark = document.body.classList.contains('dark-theme');
    const textColor = isDark ? '#f8fafc' : '#334155';
    const gridColor = isDark ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)';

    const baseOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                labels: { color: textColor, font: { family: 'Inter', size: 11 } }
            },
            tooltip: {
                backgroundColor: isDark ? '#1e293b' : '#fff',
                titleColor: isDark ? '#fff' : '#000',
                bodyColor: isDark ? '#fff' : '#000',
                borderColor: 'rgba(255,255,255,0.1)',
                borderWidth: 1
            }
        },
        scales: type !== 'doughnut' ? {
            x: {
                grid: { color: gridColor },
                ticks: { color: textColor, font: { size: 10 } }
            },
            y: {
                grid: { color: gridColor },
                ticks: { color: textColor, font: { size: 10 } }
            }
        } : {}
    };

    charts[id] = new Chart(ctx, {
        type: type,
        data: { labels, datasets },
        options: { ...baseOptions, ...options }
    });
}

function filtrarTablaDesdeGrafico(filtros) {
    console.log("Filtrando tabla por:", filtros);
    if (typeof aplicarFiltrosManual === 'function') {
        aplicarFiltrosManual(filtros);
        if (typeof switchView === 'function') {
            switchView('lista-dtes');
        }
    } else {
        showToast("Filtro aplicado: " + Object.values(filtros).join(', '), "info");
    }
}

function switchDashboardTab(tabId) {
    document.querySelectorAll('.dashboard-tab-content').forEach(el => {
        el.style.display = 'none';
        el.classList.remove('active');
    });
    document.querySelectorAll('.dashboard-tabs .tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    const activeTab = document.getElementById(tabId);
    if (activeTab) {
        activeTab.style.display = 'block';
        activeTab.classList.add('active');
    }
    
    if (tabId === 'tab-analiticas') {
        document.getElementById('btnTabAnaliticas').classList.add('active');
    } else if (tabId === 'tab-ultimos-dtes') {
        document.getElementById('btnTabUltimos').classList.add('active');
        if (typeof loadUltimosComprobantesPorTipo === 'function') {
            loadUltimosComprobantesPorTipo();
        }
    }
}

// --- A6.1 Salud Fiscal & A6.4 Alertas ---

function updateHealthGauge(stats) {
    const total = stats.aprobados + stats.rechazados + stats.pendientes + stats.enProceso + stats.anulados;
    if (total === 0) return;
    
    const percent = Math.round((stats.aprobados / total) * 100);
    const fill = document.getElementById('healthFill');
    const value = document.getElementById('healthValue');
    const status = document.getElementById('healthStatus');
    
    if (!fill) return;

    // Cálculo del stroke-dashoffset para el círculo SVG (circunferencia = 2 * PI * r)
    // Con r = 34, la circunferencia es de 213.63px
    const circumference = 2 * Math.PI * 34;
    const offset = circumference - (percent / 100) * circumference;
    
    // Aplicar la animación y el porcentaje de texto
    fill.style.strokeDashoffset = offset;
    value.innerText = `${percent}%`;
    
    // Determinar estado de salud y aplicar variables de color semánticas CSS
    if (percent >= 95) {
        status.innerText = "Excelente — Sin observaciones";
        fill.style.stroke = "var(--accent-success, #198754)";
    } else if (percent >= 80) {
        status.innerText = "Atención — Revisar rechazos";
        fill.style.stroke = "var(--accent-warning, #ffc107)";
    } else {
        status.innerText = "Crítico — Acción requerida";
        fill.style.stroke = "var(--accent-danger, #dc3545)";
    }

    generateSmartAlerts(stats, percent);
}

function generateSmartAlerts(stats, healthPercent) {
    const list = document.getElementById('alertsList');
    if (!list) return;
    
    const alerts = [];
    
    if (healthPercent < 90) {
        alerts.push({ type: 'warning', text: `Tasa de aprobación baja (${healthPercent}%). Revisa documentos rechazados.` });
    }
    
    if (stats.rechazados > 0) {
        alerts.push({ type: 'error', text: `Se detectaron ${stats.rechazados} documentos con errores SIFEN.` });
    }
    
    // Alerta estática para demo de expiración
    alerts.push({ type: 'info', text: "El certificado digital vence en 45 días (15 de Junio)." });
    
    if (stats.pendientes > 10) {
        alerts.push({ type: 'warning', text: `Hay ${stats.pendientes} documentos pendientes de envío.` });
    }

    list.innerHTML = alerts.map(a => `
        <div class="alert-item ${a.type}">
            <i class="fas ${a.type === 'error' ? 'fa-times-circle' : a.type === 'warning' ? 'fa-exclamation-triangle' : 'fa-info-circle'}"></i>
            <span>${a.text}</span>
        </div>
    `).join('');
    
    if (alerts.length === 0) {
        list.innerHTML = '<div class="alert-item empty">Todo se ve en orden hoy.</div>';
    }
}
