package com.zentra.middleware.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Forzar UTF-8 como prioridad en el conversor de Strings
        java.nio.charset.Charset utf8 = java.util.Objects.requireNonNull(StandardCharsets.UTF_8);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(utf8));
        return restTemplate;
    }
}
