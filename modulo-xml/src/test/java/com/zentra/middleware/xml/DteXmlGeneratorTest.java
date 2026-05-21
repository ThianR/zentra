package com.zentra.middleware.xml;

import com.zentra.middleware.core.enums.Ambiente;
import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.util.SifenUtil;
import com.zentra.middleware.core.model.ItemDocumento;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.Cuota;
import com.zentra.middleware.core.model.Transporte;
import com.zentra.middleware.core.model.PagoContado;
// Se omiten dependencias de otros módulos (crypto, sifen) para evitar fallos de compilación cruzada en este módulo.


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DteXmlGenerator.class) // Puede requerir más contextos según cómo esté configurado, por ahora solo pruebo generador, salvo que pueda inyectar los otros
class DteXmlGeneratorTest {

    @Autowired
    DteXmlGenerator generator;

    // @Autowired(required = false)
    // XmlSignerService signerService;

    // @Autowired(required = false)
    // SifenSoapClient soapClient;

    // Estos están en otros módulos, quizás @SpringBootTest no pueda inyectarlos si estamos en modulo-xml,
    // pero intentaré si es que están disponibles en el classpath del test.
    // Si no compila por falta de clases de otros paquetes, no los puedo importar sin cambiar el pom.
    // Lo ideal es primero ver si compila con todos los @Test.
    
    // Si la compilación falla, eliminaré los tests que dependan de otros módulos o los pondré en modulo-api.
    
    @Test
    void xmlGeneradoDebeSerValidoContraXsd() throws Exception {
        // Arrange
        DocumentoElectronico dte = crearDteDePrueba();

        // Act
        String xml = generator.generarXml(dte);
        String xmlConFirma = xml.replace("</rDE>", 
            "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" +
            "<SignedInfo>" +
            "<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" +
            "<SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>" +
            "<Reference URI=\"#\" >" +
            "<Transforms>" +
            "<Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>" +
            "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" +
            "</Transforms>" +
            "<DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>" +
            "<DigestValue>eDNVdUJrUk5wS2F4T0dGSEt4dz0=</DigestValue>" +
            "</Reference>" +
            "</SignedInfo>" +
            "<SignatureValue>dGVzdA==</SignatureValue>" +
            "<KeyInfo><X509Data><X509Certificate>dGVzdA==</X509Certificate></X509Data></KeyInfo>" +
            "</Signature>" +
            "<gCamFuFD xmlns=\"http://ekuatia.set.gov.py/sifen/xsd\">" +
            "<dCarQR>https://ekuatia.set.gov.py/consultas-test/qr?nVersion=150&amp;Id=01800146034001001373824622026051915319628821&amp;dFeEmiDE=323032362d30352d31395432323a32343a3138&amp;dNumIDRec=5166165</dCarQR>" +
            "</gCamFuFD></rDE>");

        // Assert — validar contra XSD oficial
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("src/main/resources/xsd/siRecepDE_v150.xsd"));
        Validator validator = schema.newValidator();
        assertDoesNotThrow(() ->
            validator.validate(new StreamSource(new StringReader(xmlConFirma))),
            "El XML generado no es válido contra el XSD v150"
        );
    }

    @Test
    void xmlNoDebeContenerDTotalGs() {
        DocumentoElectronico dte = crearDteDePrueba();
        String xml = generator.generarXml(dte);
        assertFalse(xml.contains("dTotalGs"),
            "El XML no debe contener el campo dTotalGs");
    }

    @Test
    void xmlDebeContenerITipTra() {
        DocumentoElectronico dte = crearDteDePrueba();
        String xml = generator.generarXml(dte);
        assertTrue(xml.contains("<iTipTra>"),
            "El XML debe contener iTipTra en gOpeCom");
        assertTrue(xml.contains("<dDesTipTra>"),
            "El XML debe contener dDesTipTra en gOpeCom");
    }

    @Test
    void xmlNoDebeContenerDecimalesEnMontos() {
        DocumentoElectronico dte = crearDteDePrueba();
        String xml = generator.generarXml(dte);
        // Ningún valor monetario debe tener punto decimal
        assertFalse(xml.matches("(?s).*<(dTotGralOpe|dSub10|dIVA10|dBasGravIVA)>[0-9]+\\.[0-9]+<.*"),
            "Los montos en PYG no deben contener decimales");
    }

    @Test
    void xmlDebeEstarEnUtf8SinCaracteresCorruptos() {
        DocumentoElectronico dte = crearDteDePrueba();
        String xml = generator.generarXml(dte);
        // Verificar caracteres especiales correctos
        assertTrue(xml.contains("Guarani"),
            "La moneda Guarani debe estar en el XML");
        assertFalse(xml.contains("├"),
            "No deben existir caracteres corruptos de encoding");
    }

    @Test
    void cdcDebeCalcularseCorrectamente() {
        // Ejemplo oficial Manual v150 pág. 57
        String cdc = SifenUtil.generarCdc("01","80014603","4","001","001",
            "0000001","1","20230101","1","000000001");
        assertEquals(44, cdc.length(), "El CDC debe tener 44 caracteres");
        assertEquals("0", cdc.substring(43), "El DV del CDC debe ser 0");
    }

    /*
    @Test
    void xmlFirmadoDebeSerVerificable() throws Exception { ... } // Requería XmlSignerService

    @Test
    @EnabledIfEnvironmentVariable(named = "SIFEN_TEST_ENABLED", matches = "true")
    void envioAmbienteTestDebeRetornar0300() { ... } // Requería SifenSoapClient
    */

    @Test
    void xmlDebeContenerMontosDeCuotaYFleteSinDecimales() {
        DocumentoElectronico dte = crearDteDePrueba();
        dte.setCondicionOperacion(2); // Crédito
        
        Cuota cuota = new Cuota();
        cuota.setMonto(12345.67);
        cuota.setFechaVencimiento(java.time.LocalDate.now().plusDays(30));
        dte.setCuotas(List.of(cuota));
        
        Transporte trans = new Transporte();
        trans.setPrecioFlete(9876.54);
        trans.setMotivoTraslado(1);
        trans.setResponsableEmision(1);
        trans.setKmsRecorrido(50);
        trans.setFechaInicioTraslado(java.time.LocalDate.now().toString());
        trans.setLocalSalidaDireccion("Dir Salida");
        trans.setLocalSalidaNumeroCasa(123);
        trans.setLocalSalidaCodigoDepartamento(1);
        trans.setLocalSalidaDescripcionDepartamento("CAPITAL");
        trans.setLocalSalidaCodigoCiudad(1);
        trans.setLocalSalidaDescripcionCiudad("ASUNCION (DISTRITO)");
        
        trans.setLocalEntregaDireccion("Dir Entrega");
        trans.setLocalEntregaNumeroCasa(456);
        trans.setLocalEntregaCodigoDepartamento(1);
        trans.setLocalEntregaDescripcionDepartamento("CAPITAL");
        trans.setLocalEntregaCodigoCiudad(1);
        trans.setLocalEntregaDescripcionCiudad("ASUNCION (DISTRITO)");
        
        dte.setTransporte(trans);
        dte.setTipoDocumento("7"); // Remisión
        
        String xml = generator.generarXml(dte);
        
        assertTrue(xml.contains("<cPreFle>9877</cPreFle>"), "El flete debe redondearse a entero");
        
        dte.setTipoDocumento("1"); // Factura
        String xmlFactura = generator.generarXml(dte);
        assertTrue(xmlFactura.contains("<dMonCuota>12346</dMonCuota>"), "El monto de cuota debe redondearse a entero");
    }

    @Test
    void xmlDebeContenerCamposDeTarjetaCuandoEsPagoTarjeta() {
        DocumentoElectronico dte = crearDteDePrueba();
        dte.setCondicionOperacion(1); // Contado
        
        PagoContado pagoTarjeta = new PagoContado();
        pagoTarjeta.setDocumento(dte);
        pagoTarjeta.setTipoPago(3); // Tarjeta de crédito
        pagoTarjeta.setMonto(83000.0);
        dte.setPagos(List.of(pagoTarjeta));
        
        String xml = generator.generarXml(dte);
        
        assertTrue(xml.contains("<gPagTarCD>"), "El XML debe contener el grupo de tarjeta");
        assertTrue(xml.contains("<iDenTarj>99</iDenTarj>"), "El XML debe contener la denominación de tarjeta 99");
        assertTrue(xml.contains("<dDesDenTarj>OTRO</dDesDenTarj>"), "El XML debe describir la denominación como OTRO");
        assertTrue(xml.contains("<iForProPa>1</iForProPa>"), "El XML debe contener la forma de procesamiento 1");
    }

    @Test
    void xmlDebeContenerCamposDeChequeCuandoEsPagoCheque() {
        DocumentoElectronico dte = crearDteDePrueba();
        dte.setCondicionOperacion(1); // Contado
        
        PagoContado pagoCheque = new PagoContado();
        pagoCheque.setDocumento(dte);
        pagoCheque.setTipoPago(2); // Cheque
        pagoCheque.setMonto(83000.0);
        dte.setPagos(List.of(pagoCheque));
        
        String xml = generator.generarXml(dte);
        
        assertTrue(xml.contains("<gPagCheq>"), "El XML debe contener el grupo de cheque");
        assertTrue(xml.contains("<dNumCheq>00000000</dNumCheq>"), "El XML debe contener el número de cheque por defecto");
        assertTrue(xml.contains("<dBcoEmi>OTRO</dBcoEmi>"), "El XML debe contener el banco emisor OTRO");
    }

    @Test
    void ambienteInvalidoDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> Ambiente.fromCodigo(99),
            "Un código de ambiente inválido debe lanzar excepción");
    }

    @Test
    void documentoNuevoDebeDefaulTESTAmbiente() {
        DocumentoElectronico dte = new DocumentoElectronico();
        assertEquals(Ambiente.TEST, dte.getAmbiente(),
            "Un documento nuevo debe defaultear a ambiente TEST");
    }

    private DocumentoElectronico crearDteDePrueba() {
        Empresa emisor = new Empresa();
        emisor.setRuc("80014603");
        emisor.setDv("4");
        emisor.setRazonSocial("EMPRESA MOCK");
        emisor.setCodEstablecimiento("001");
        emisor.setPuntoExpedicion("001");

        DocumentoElectronico dte = new DocumentoElectronico();
        dte.setEmisor(emisor);
        dte.setTipoDocumento("1");
        dte.setAmbiente(Ambiente.TEST);
        dte.setTipoTransaccion(1);
        dte.setTotalOperacion(83000.0);
        dte.setFechaCreacion(LocalDateTime.now());
        dte.setNumeroComprobante("001-001-0000001");
        dte.setRucReceptor("1234567");
        dte.setReceptorRazonSocial("CLIENTE DE PRUEBA");

        ItemDocumento item = new ItemDocumento();
        item.setCodigo("PRD-001");
        item.setDescripcion("PRODUCTO DE PRUEBA");
        item.setUnidadMedida("UNI");
        item.setCantidad(1);
        item.setPrecioUnitario(83000.0);
        item.setTasaIva(10.0);
        item.setMontoIvaItem(7545.0);
        item.setMontoTotalItem(83000.0);
        dte.setItems(List.of(item));

        return dte;
    }
}
