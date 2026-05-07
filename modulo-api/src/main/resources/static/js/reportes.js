// --- Zentra Reportes (PDF & Excel) ---

/**
 * Genera un PDF profesional con los datos visibles en la tabla de documentos.
 */
async function exportarPdfDocumentos() {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF('l', 'mm', 'a4'); // Orientación horizontal

    const tbody = document.getElementById('tbodyDocumentos');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    if (rows.length === 0 || rows[0].innerText.includes('No hay') || rows[0].innerText.includes('Cargando')) {
        showToast("No hay datos cargados para exportar", "warning");
        return;
    }

    // Cabecera Estilo Zentra
    doc.setFontSize(22);
    doc.setTextColor(13, 110, 253);
    doc.text("ZENTRA - Reporte Fiscal de Documentos", 14, 20);
    
    doc.setFontSize(10);
    doc.setTextColor(100);
    doc.text(`Fecha de generación: ${new Date().toLocaleString()}`, 14, 28);
    doc.text("Middleware de Facturación Electrónica SIFEN v1.5.0", 14, 33);
    
    doc.setDrawColor(200, 200, 200);
    doc.line(14, 37, 283, 37);

    // Mapeo de datos de la tabla DOM
    const tableData = rows.map(tr => {
        const tds = tr.querySelectorAll('td');
        if (tds.length < 6) return null;
        
        return [
            tds[0].innerText, // Fecha
            tds[1].innerText, // Tipo
            tds[2].innerText, // Comprobante (incluye CDC en title)
            tds[3].innerText, // Receptor
            tds[4].innerText, // Monto Total
            tds[5].innerText.split('\n')[0] // Estado
        ];
    }).filter(row => row !== null);

    // Generación de tabla automática
    doc.autoTable({
        startY: 45,
        head: [['Fecha Emisión', 'Tipo Documento', 'Nro. Comprobante', 'Receptor / RUC', 'Monto Total', 'Estado SIFEN']],
        body: tableData,
        theme: 'grid',
        headStyles: { fillColor: [13, 110, 253], textColor: [255, 255, 255], fontStyle: 'bold' },
        alternateRowStyles: { fillColor: [245, 247, 251] },
        styles: { fontSize: 8, font: 'helvetica', cellPadding: 3 },
        columnStyles: {
            4: { halign: 'right' }, // Alineación a la derecha para montos
            5: { halign: 'center' }
        }
    });

    // Pie de página
    const pageCount = doc.internal.getNumberOfPages();
    for (let i = 1; i <= pageCount; i++) {
        doc.setPage(i);
        doc.setFontSize(8);
        doc.setTextColor(150);
        doc.text(`Página ${i} de ${pageCount} | Generado automáticamente por Zentra SIFEN Middleware`, 14, 200);
    }

    doc.save(`Zentra_Reporte_${new Date().toISOString().split('T')[0]}.pdf`);
}

/**
 * Exporta los datos de la tabla a un archivo Excel formateado (.xlsx)
 */
function exportarExcelDocumentos() {
    const tbody = document.getElementById('tbodyDocumentos');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    if (rows.length === 0 || rows[0].innerText.includes('No hay') || rows[0].innerText.includes('Cargando')) {
        showToast("No hay datos para exportar", "warning");
        return;
    }

    const tableData = rows.map(tr => {
        const tds = tr.querySelectorAll('td');
        if (tds.length < 6) return null;

        // Limpiar monto para que sea numérico en Excel
        const montoLimpio = tds[4].innerText.replace(/\./g, '').replace(',', '.');
        
        return {
            "Fecha": tds[0].innerText,
            "Tipo": tds[1].innerText,
            "Comprobante": tds[2].innerText,
            "CDC": tds[2].title ? tds[2].title.replace('CDC: ', '') : '',
            "Receptor": tds[3].innerText,
            "Monto Total": parseFloat(montoLimpio),
            "Estado": tds[5].innerText.split('\n')[0]
        };
    }).filter(row => row !== null);

    const worksheet = XLSX.utils.json_to_sheet(tableData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Documentos");

    // Definir anchos de columna recomendados
    const wscols = [
        {wch: 18}, // Fecha
        {wch: 15}, // Tipo
        {wch: 20}, // Comprobante
        {wch: 46}, // CDC
        {wch: 35}, // Receptor
        {wch: 15}, // Monto
        {wch: 15}  // Estado
    ];
    worksheet['!cols'] = wscols;

    XLSX.writeFile(workbook, `Zentra_Reporte_${new Date().toISOString().split('T')[0]}.xlsx`);
}
