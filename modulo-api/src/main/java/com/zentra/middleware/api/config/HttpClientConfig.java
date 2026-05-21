package com.zentra.middleware.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Configuración de Spring para proveer la instancia del bean HttpClient de manera dinámica.
 * Permite bypass SSL (trustAllCerts) en entornos locales y de desarrollo, y obliga a
 * una validación SSL estándar y segura en el entorno de producción.
 */
@Configuration
public class HttpClientConfig {
    private static final Logger logger = Logger.getLogger(HttpClientConfig.class.getName());
    private final Environment env;

    public HttpClientConfig(Environment env) {
        this.env = env;
    }

    /**
     * Define el bean HttpClient gestionado por Spring.
     * 
     * @return una instancia configurada de HttpClient.
     */
    @Bean
    public HttpClient httpClient() {
        // Verificar si alguno de los perfiles activos corresponde a producción
        boolean isProduction = Arrays.asList(env.getActiveProfiles()).contains("prod") || 
                              Arrays.asList(env.getActiveProfiles()).contains("production");

        if (isProduction) {
            logger.info("Inicializando HttpClient SEGURO con validación SSL estándar para entorno de producción.");
            return HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
        } else {
            logger.warning("Inicializando HttpClient INSEGURO (bypass de SSL - trustAllCerts) para entorno local/desarrollo.");
            try {
                // Configurar un TrustManager que confía en todos los certificados para entornos de prueba
                javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
                };
                
                javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                return HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .sslContext(sc)
                        .build();
            } catch (Exception e) {
                logger.severe("No se pudo configurar el SSLContext con bypass, se utilizará HttpClient seguro estándar por defecto: " + e.getMessage());
                return HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .build();
            }
        }
    }
}
