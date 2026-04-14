package com.company.product.bdd.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@Scope("cucumber-glue")
public class TestContext {

    // Base URL (dynamic port support)
    private String baseUrl;

    // Last API response
    private ResponseEntity<String> response;

    // Common reusable IDs
    private Long productId;
    private Long userId;
    private Long orderId;

    // Request payload (for debugging / reuse)
    private Object requestBody;

    // Headers (for auth, tokens, etc.)
    private HttpHeaders headers = new HttpHeaders();

    // Generic storage for flexible data sharing
    private Map<String, Object> data = new HashMap<>();
}