package com.zentra.middleware.api.controller;

import com.zentra.middleware.core.model.*;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.xml.DteXmlGenerator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class TestScenarioController {

    private final DteXmlGenerator xmlGenerator;
    private final DocumentoElectronicoRepository repository;
    private final com.zentra.middleware.core.repository.EmpresaRepository empresaRepository;

    public TestScenarioController(DteXmlGenerator xmlGenerator, DocumentoElectronicoRepository repository, com.zentra.middleware.core.repository.EmpresaRepository empresaRepository) {
        this.xmlGenerator = xmlGenerator;
        this.repository = repository;
        this.empresaRepository = empresaRepository;
    }

    @PostMapping("/factura-contado")
    public DocumentoElectronico testFacturaContado() {
        DocumentoElectronico dte = createBaseLocal("1", "001-001-0000101");
        dte.setCondicionOperacion(1); // Contado
        dte.setTotalOperacion(110000.0);
        
        dte.getItems().add(createItem(dte, "P001", "Producto IVA 10", 1, 100000.0, 10.0));
        
        xmlGenerator.generarXml(dte);
        return repository.save(dte);
    }

    @PostMapping("/factura-credito")
    public DocumentoElectronico testFacturaCredito() {
        DocumentoElectronico dte = createBaseLocal("1", "001-001-0000102");
        dte.setCondicionOperacion(2); // Crédito
        dte.setTotalOperacion(220000.0);
        dte.getItems().add(createItem(dte, "P002", "Producto Crédito", 2, 100000.0, 10.0));
        
        Cuota c1 = new Cuota();
        c1.setDocumento(dte);
        c1.setNumeroCuota(1);
        c1.setMonto(110000.0);
        c1.setFechaVencimiento(java.time.LocalDate.now().plusDays(30));
        dte.getCuotas().add(c1);
        
        Cuota c2 = new Cuota();
        c2.setDocumento(dte);
        c2.setNumeroCuota(2);
        c2.setMonto(110000.0);
        c2.setFechaVencimiento(java.time.LocalDate.now().plusDays(60));
        dte.getCuotas().add(c2);
        
        xmlGenerator.generarXml(dte);
        return repository.save(dte);
    }

    @PostMapping("/nota-credito")
    public DocumentoElectronico testNotaCredito() {
        DocumentoElectronico dte = createBaseLocal("4", "001-001-0000103");
        dte.setCdcDocumentoAsociado("01234567890123456789012345678901234567890123");
        dte.setTipoDocumentoAsociado(1); // Electrónico
        dte.setMotivoEmision("1"); // Devolución
        dte.setTotalOperacion(55000.0);
        dte.getItems().add(createItem(dte, "P001", "Devolución Parcial", 1, 50000.0, 10.0));
        
        xmlGenerator.generarXml(dte);
        return repository.save(dte);
    }

    @PostMapping("/nota-remision")
    public DocumentoElectronico testNotaRemision() {
        DocumentoElectronico dte = createBaseLocal("7", "001-001-0000104");
        dte.setTotalOperacion(0.0);
        dte.getItems().add(createItem(dte, "P001", "Traslado Maquinaria", 1, 0.0, 0.0));
        
        Transporte t = new Transporte();
        t.setNombreTransportista("LOGISTICA VELOZ S.A.");
        t.setRucTransportista("80001122-3");
        t.setNombreChofer("MARIO KART");
        t.setNumeroDocumentoChofer("1234567");
        t.setMotivoTraslado(1); // Venta
        t.setKmsRecorrido(150);
        t.setPrecioFlete(250000.0);
        dte.setTransporte(t);
        
        xmlGenerator.generarXml(dte);
        return repository.save(dte);
    }

    @PostMapping("/factura-licitacion")
    public DocumentoElectronico testFacturaLicitacion() {
        DocumentoElectronico dte = createBaseLocal("1", "001-001-0000105");
        dte.setTotalOperacion(10000000.0);
        dte.getItems().add(createItem(dte, "S001", "Software Gobierno", 1, 10000000.0, 0.0));
        
        ComprasPublicas cp = new ComprasPublicas();
        cp.setModalidadContratacion("LCO");
        cp.setEntidadContratante(123);
        cp.setAnioContratacion(2024);
        cp.setSecuencialContrato(456);
        cp.setFechaCodigoContrato("2024-03-20");
        dte.setComprasPublicas(cp);
        
        xmlGenerator.generarXml(dte);
        return repository.save(dte);
    }

    private DocumentoElectronico createBaseLocal(String tipo, String numero) {
        DocumentoElectronico dte = new DocumentoElectronico();
        dte.setTipoDocumento(tipo);
        dte.setNumeroComprobante(numero);
        dte.setRucEmisor("80000001-5");
        dte.setRucReceptor("4445556-7");

        Empresa emisor = empresaRepository.findByRuc("80000001").orElseGet(() -> {
            Empresa e = new Empresa();
            e.setRuc("80000001");
            e.setDv("5");
            e.setRazonSocial("EMPRESA TEST MOCK S.A.");
            e.setCodEstablecimiento("001");
            e.setPuntoExpedicion("001");
            e.setNumeroCasa("123");
            e.setCodDepartamento(1);
            e.setDepartamento("CENTRAL");
            e.setCodCiudad(1);
            e.setCiudad("ASUNCION");
            return empresaRepository.save(e);
        });
        
        dte.setEmisor(emisor);
        return dte;
    }

    private ItemDocumento createItem(DocumentoElectronico dte, String cod, String des, Integer cant, Double precio, Double tasa) {
        ItemDocumento item = new ItemDocumento();
        item.setDocumento(dte);
        item.setCodigo(cod);
        item.setDescripcion(des);
        item.setCantidad(cant);
        item.setPrecioUnitario(precio);
        item.setTasaIva(tasa);
        item.setMontoDescuento(0.0);
        
        double total = cant * precio;
        item.setMontoTotalItem(total);
        
        if (tasa == 10.0) item.setMontoIvaItem(total / 11);
        else if (tasa == 5.0) item.setMontoIvaItem(total / 21);
        else item.setMontoIvaItem(0.0);
        
        return item;
    }
}
