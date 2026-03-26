package com.company.cart_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${product.service.base-url}")
    private String productBaseUrl;

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl(productBaseUrl)
                .build();
    }
}