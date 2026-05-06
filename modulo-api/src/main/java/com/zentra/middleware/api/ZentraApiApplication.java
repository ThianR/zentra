package com.zentra.middleware.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.zentra.middleware")
@EnableJpaRepositories(basePackages = "com.zentra.middleware.core.repository")
@EntityScan(basePackages = "com.zentra.middleware.core.model")
@EnableScheduling
public class ZentraApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZentraApiApplication.class, args);
    }
}
