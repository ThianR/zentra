package com.zentra.middleware.api.config;

import com.zentra.middleware.api.interceptor.SubscriptionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SubscriptionInterceptor subscriptionInterceptor;

    public WebConfig(SubscriptionInterceptor subscriptionInterceptor) {
        this.subscriptionInterceptor = subscriptionInterceptor;
    }

    @Override
    @SuppressWarnings("null")
    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
        registry.addInterceptor(subscriptionInterceptor)
                .addPathPatterns("/api/v1/emision/**");
    }
}
