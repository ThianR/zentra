package com.zentra.middleware.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.zentra.middleware")
@EnableJpaRepositories(basePackages = "com.zentra.middleware.core.repository")
@EntityScan(basePackages = "com.zentra.middleware.core.model")
public class ZentraApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZentraApiApplication.class, args);
    }
}
