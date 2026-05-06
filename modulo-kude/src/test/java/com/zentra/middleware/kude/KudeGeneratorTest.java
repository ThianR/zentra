package com.zentra.middleware.kude;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.model.ItemDocumento;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class KudeGeneratorTest {

    @Test
    public void testGenerarKudeAnulado() throws Exception {
        KudeGenerator generator = new KudeGenerator();
        
        DocumentoElectronico dte = new DocumentoElectronico();
        dte.setCdc("01234567890123456789012345678901234567890123");
        dte.setNumeroComprobante("001-001-0000001");
        dte.setRazonSocialEmisor("EMPRESA TEST S.A.");
        dte.setRucEmisor("80000001-0");
        dte.setEstado(EstadoDte.ANULADO); // <--- ESTADO ANULADO
        dte.setTotalOperacion(150000.0);
        dte.setReceptorRazonSocial("CLIENTE DE PRUEBA");
        dte.setRucReceptor("4444444-1");
        
        // Totales requeridos por KudeGenerator
        dte.setTotalExenta(0.0);
        dte.setTotalGravada5(0.0);
        dte.setTotalGravada10(150000.0);
        dte.setTotalIva5(0.0);
        dte.setTotalIva10(13636.0);
        dte.setTotalIva(13636.0);

        // Agregar un item
        ItemDocumento item = new ItemDocumento();
        item.setDescripcion("PRODUCTO DE PRUEBA ANULADO");
        item.setCantidad(1);
        item.setPrecioUnitario(150000.0);
        item.setMontoTotalItem(150000.0);
        item.setTasaIva(10.0);
        List<ItemDocumento> items = new ArrayList<>();
        items.add(item);
        dte.setItems(items);

        // Generar PDF
        byte[] pdf = generator.generarKudePdf(dte, "A4");

        // Guardar en directorio temp/
        File tempDir = new File("temp");
        if (!tempDir.exists()) tempDir.mkdirs();
        
        File pdfFile = new File(tempDir, "TEST_KUDE_ANULADO.pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(pdf);
        }
        
        System.out.println("PDF generado exitosamente con marca de agua en: " + pdfFile.getAbsolutePath());
        
        // También probar formato TICKET
        byte[] ticket = generator.generarKudePdf(dte, "TICKET");
        File ticketFile = new File(tempDir, "TEST_TICKET_ANULADO.pdf");
        try (FileOutputStream fos = new FileOutputStream(ticketFile)) {
            fos.write(ticket);
        }
        System.out.println("TICKET generado exitosamente con marca de agua en: " + ticketFile.getAbsolutePath());
    }
}
