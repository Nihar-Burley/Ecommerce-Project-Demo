package com.company.cart_service.bdd.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

@Getter
@Setter
@Component
public class TestContext {

    private WebTestClient.ResponseSpec response;

    private Long userId = 1L;
    private String role = "USER";

    private Long productId;
    private int quantity;
}