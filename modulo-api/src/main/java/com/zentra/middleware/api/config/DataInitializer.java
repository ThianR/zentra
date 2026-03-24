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
                
                Empresa demo = new Empresa();
                demo.setRuc("80000001");
                demo.setDv("5");
                demo.setRazonSocial("Zentra Demo Emisor S.A.");
                demo.setCodEstablecimiento("001");
                demo.setPuntoExpedicion("001");
                empresaRepo.save(demo);

                DocumentoElectronico dte = new DocumentoElectronico();
                dte.setEmisor(demo);
                dte.setNumeroComprobante("001-001-0000001");
                dte.setTipoDocumento("1");
                dte.setCdc("018000000150010010000001120240319123456781");
                dte.setEstado(EstadoDte.APROBADO);
                dteRepo.save(dte);

                System.out.println("Inicialización completada.");
            }
        };
    }
}
