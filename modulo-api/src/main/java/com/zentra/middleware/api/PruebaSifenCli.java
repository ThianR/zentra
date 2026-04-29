package com.zentra.middleware.api;

import com.zentra.middleware.core.enums.Ambiente;
import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.ItemDocumento;
import com.zentra.middleware.crypto.service.XmlSignerService;
import com.zentra.middleware.sifen.SifenSoapClient;
import com.zentra.middleware.xml.DteXmlGenerator;

import java.time.LocalDateTime;
import java.util.List;

public class PruebaSifenCli {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA SIFEN CLI ===");
        try {
            // 1. Instanciar los componentes en modo manual (sin Spring Data/DB)
            DteXmlGenerator generator = new DteXmlGenerator();
            XmlSignerService signer = new XmlSignerService();
            SifenSoapClient soapClient = new SifenSoapClient();

            // 2. Construir DTE de prueba básico
            DocumentoElectronico dte = new DocumentoElectronico();
            
            Empresa emisor = new Empresa();
            emisor.setRuc("80014603");
            emisor.setDv("4");
            emisor.setRazonSocial("EMPRESA MOCK");
            emisor.setCodEstablecimiento("001");
            emisor.setPuntoExpedicion("001");
            // Nota: Aquí se debería apuntar al P12 real que se tenga
            emisor.setRutaCertificado("d:/Personales/SISTEMAS/SIFEN/zentra/context/certificado_para_facturacion.pfx");
            emisor.setPasswordCertificado("77145137"); 
            emisor.setIdCsc("0001");
            emisor.setValorCsc("73c9BeeA5AFb8fD17a3fD93a32A07A1a");

            dte.setEmisor(emisor);
            dte.setTipoDocumento("1");
            dte.setAmbiente(Ambiente.PRODUCCION);
            dte.setTipoTransaccion(1);
            dte.setTotalOperacion(83000.0);
            dte.setFechaCreacion(LocalDateTime.now());
            dte.setNumeroComprobante("001-001-0000001");
            dte.setRucReceptor("1234567");
            dte.setReceptorRazonSocial("CLIENTE DE PRUEBA");

            ItemDocumento item = new ItemDocumento();
            item.setDescripcion("PRODUCTO DE PRUEBA");
            item.setCantidad(1);
            item.setPrecioUnitario(83000.0);
            item.setTasaIva(10.0);
            item.setMontoIvaItem(7545.0);
            item.setMontoTotalItem(83000.0);
            dte.setItems(List.of(item));

            // 3. Ejecutar pipeline
            System.out.println("[1] Generando XML...");
            String xml = generator.generarXml(dte);
            System.out.println("XML Generado Exitosamente (Longitud: " + xml.length() + ")");

            System.out.println("[2] Firmando XML...");
            signer.firmarXml(dte);
            System.out.println("XML Firmado Exitosamente");

            System.out.println("[3] Enviando a SIFEN (" + dte.getAmbiente() + ")...");
            boolean exito = soapClient.enviarDteSincrono(dte);
            
            System.out.println("=== RESULTADO SIFEN ===");
            System.out.println("Éxito del envío: " + exito);
            System.out.println("Código Estado: " + dte.getCodigoEstadoSifen());
            System.out.println("Mensaje SIFEN: " + dte.getMensajeSifen());
            
        } catch (Exception e) {
            System.err.println("Error crítico en la prueba:");
            e.printStackTrace();
        }
    }
}
