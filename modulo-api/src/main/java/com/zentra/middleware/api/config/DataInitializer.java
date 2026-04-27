package com.zentra.middleware.api.config;

import com.zentra.middleware.core.model.DocumentoElectronico;
import com.zentra.middleware.core.model.Empresa;
import com.zentra.middleware.core.model.EstadoDte;
import com.zentra.middleware.core.repository.DocumentoElectronicoRepository;
import com.zentra.middleware.core.repository.EmpresaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;

/**
 * Inicializador de datos de prueba para PostgreSQL.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            EmpresaRepository empresaRepo,
            DocumentoElectronicoRepository dteRepo) {
        return args -> {
            if (empresaRepo.count() == 0) {
                System.out.println("Creando datos de prueba para Zentra...");
                
                Empresa rg = new Empresa();
                rg.setId("80014603");
                rg.setRuc("80014603");
                rg.setDv("4");
                rg.setRazonSocial("RG S.A");
                rg.setCodEstablecimiento("001");
                rg.setPuntoExpedicion("001");
                rg.setDireccion("(ASUNCION) AV. EUSEBIO AYALA 4840 C/GUIDO BOGGIANI");
                rg.setNumeroCasa("4840");
                rg.setCodDepartamento(1);
                rg.setDepartamento("CAPITAL");
                rg.setCodDistrito(1);
                rg.setDistrito("ASUNCION (DISTRITO)");
                rg.setCodCiudad(1);
                rg.setCiudad("ASUNCION (DISTRITO)");
                rg.setTelefono("000000000511296");
                rg.setEmail("fact.electronicargsa@gmail.com");
                rg.setActividadEconomica("COMERCIO DE PARTES, PIEZAS Y ACCESORIOS NUEVOS PARA VEHICULOS AUTOMOTORES");
                rg.setTipoContribuyente(2);
                rg.setRutaCertificado("d:/Personales/SISTEMAS/SIFEN/zentra/context/certificado_para_facturacion.pfx");
                rg.setPasswordCertificado("77145137");
                rg.setAmbiente(com.zentra.middleware.core.enums.Ambiente.TEST);
                rg.setIdCsc("0001");
                rg.setValorCsc("73c9BeeA5AFb8fD17a3fD93a32A07A1a");
                empresaRepo.save(rg);

/* 
                DocumentoElectronico dte = new DocumentoElectronico();
                dte.setEmisor(rg);
                dte.setNumeroComprobante("001-001-0000001");
                dte.setTipoDocumento("1");
                dte.setCdc("01800146034001001167709942024031900000011");
                dte.setEstado(EstadoDte.APROBADO);
                dteRepo.save(dte);
*/

                System.out.println("Inicialización completada.");
            }
        };
    }
}
