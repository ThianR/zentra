package com.zentra.middleware.api.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Gestiona la inicialización completa de la base de datos en el orden correcto:
 * 1. Migra el constraint de sifen_referencia (DROP viejo, CREATE parciales)
 * 2. Carga los datos de referencia (catálogos SIFEN v150) desde data.sql
 *
 * Se usa @PostConstruct en un @Configuration para garantizar que se ejecute
 * después de que Hibernate inicializa las tablas pero antes de cualquier petición.
 * El data.sql de Spring Init está deshabilitado (ver application.yml).
 */
@Configuration
public class DatabaseMigrationConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    private final JdbcTemplate jdbc;
    private final DataSource dataSource;

    public DatabaseMigrationConfig(JdbcTemplate jdbc, DataSource dataSource) {
        this.jdbc = jdbc;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void inicializar() {
        migrarConstraintSifenReferencia();
        migrarColumnasEmpresa();
        cargarDatosReferencia();
        forzarAmbienteEmpresa();
    }

    private void forzarAmbienteEmpresa() {
        logger.info("[DB Init] Forzando ambiente de empresas a TEST (1) según cambio de Enum SIFEN...");
        try {
            jdbc.execute("UPDATE empresas SET ambiente = 1 WHERE ruc IN ('80144342', '80014603')");
        } catch (Exception e) {
            logger.warn("[DB Init] Error actualizando ambiente de empresas: " + e.getMessage());
        }
    }

    /**
     * Asegura que la tabla empresas tenga las columnas necesarias para
     * timbrado y actividades económicas.
     */
    private void migrarColumnasEmpresa() {
        // Columna: fecha_vencimiento_timbrado
        boolean colVencTimb = Boolean.TRUE.equals(jdbc.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_name='empresas' AND column_name='fecha_vencimiento_timbrado')",
            Boolean.class
        ));
        if (!colVencTimb) {
            logger.info("[DB Init] Agregando columna fecha_vencimiento_timbrado a empresas...");
            jdbc.execute("ALTER TABLE empresas ADD COLUMN fecha_vencimiento_timbrado DATE");
        }

        // Columna: cod_actividad_economica
        boolean colCodAct = Boolean.TRUE.equals(jdbc.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_name='empresas' AND column_name='cod_actividad_economica')",
            Boolean.class
        ));
        if (!colCodAct) {
            logger.info("[DB Init] Agregando columna cod_actividad_economica a empresas...");
            jdbc.execute("ALTER TABLE empresas ADD COLUMN cod_actividad_economica VARCHAR(20)");
        }

        // Migración de OID a BYTEA para certificado_fisico
        String tipoCert = jdbc.queryForObject(
            "SELECT data_type FROM information_schema.columns WHERE table_name='empresas' AND column_name='certificado_fisico'",
            String.class
        );
        if ("oid".equalsIgnoreCase(tipoCert)) {
            logger.info("[DB Init] Migrando columna certificado_fisico de OID a BYTEA...");
            try {
                jdbc.execute("ALTER TABLE empresas ALTER COLUMN certificado_fisico TYPE bytea USING lo_get(certificado_fisico)");
            } catch (Exception e) {
                logger.warn("[DB Init] Error migrando certificado_fisico con lo_get, vaciando columna por seguridad: " + e.getMessage());
                jdbc.execute("ALTER TABLE empresas DROP COLUMN certificado_fisico");
                jdbc.execute("ALTER TABLE empresas ADD COLUMN certificado_fisico BYTEA");
            }
        }
    }

    /**
     * Migración de constraints: reemplaza el UNIQUE estándar (incompatible con NULL)
     * por dos índices parciales que manejan correctamente padre_codigo = NULL.
     */
    private void migrarConstraintSifenReferencia() {
        // Eliminar constraint estándar si aún existe
        boolean constraintExiste = Boolean.TRUE.equals(jdbc.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM pg_constraint WHERE conname = 'uk_sifen_ref_tipo_cod')",
            Boolean.class
        ));

        if (constraintExiste) {
            logger.info("[DB Init] Eliminando constraint uk_sifen_ref_tipo_cod...");
            jdbc.execute("ALTER TABLE sifen_referencia DROP CONSTRAINT uk_sifen_ref_tipo_cod");
        }

        // Índice parcial: registros SIN padre (departamentos, monedas, tipos, etc.)
        boolean sinPadre = Boolean.TRUE.equals(jdbc.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM pg_indexes WHERE indexname = 'uk_sifen_ref_sin_padre')",
            Boolean.class
        ));
        if (!sinPadre) {
            logger.info("[DB Init] Creando índice uk_sifen_ref_sin_padre...");
            jdbc.execute(
                "CREATE UNIQUE INDEX uk_sifen_ref_sin_padre " +
                "ON sifen_referencia (tipo, codigo) WHERE padre_codigo IS NULL"
            );
        }

        // Índice parcial: registros CON padre (distritos, ciudades)
        boolean conPadre = Boolean.TRUE.equals(jdbc.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM pg_indexes WHERE indexname = 'uk_sifen_ref_con_padre')",
            Boolean.class
        ));
        if (!conPadre) {
            logger.info("[DB Init] Creando índice uk_sifen_ref_con_padre...");
            jdbc.execute(
                "CREATE UNIQUE INDEX uk_sifen_ref_con_padre " +
                "ON sifen_referencia (tipo, codigo, padre_codigo) WHERE padre_codigo IS NOT NULL"
            );
        }

        logger.info("[DB Init] Índices de sifen_referencia verificados.");
    }

    /**
     * Carga los datos de referencia SIFEN v150 desde data.sql.
     * Usa ResourceDatabasePopulator que soporta scripts multi-sentencia.
     * El script es idempotente: usa ON CONFLICT DO NOTHING.
     */
    private void cargarDatosReferencia() {
        logger.info("[DB Init] Verificando/Cargando catálogos SIFEN v150 desde data.sql...");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("data.sql"));
        populator.addScript(new ClassPathResource("data_sifen_diccionario.sql"));
        populator.setSeparator(";");
        populator.setIgnoreFailedDrops(true);
        populator.setContinueOnError(false);
        if (dataSource != null) {
            org.springframework.jdbc.datasource.init.DatabasePopulatorUtils.execute(populator, dataSource);
        }
        
        Long totalRes = jdbc.queryForObject("SELECT COUNT(*) FROM sifen_referencia", Long.class);
        long total = totalRes != null ? totalRes : 0L;
        logger.info("[DB Init] Sincronización finalizada: {} registros en sifen_referencia.", total);
    }
}
