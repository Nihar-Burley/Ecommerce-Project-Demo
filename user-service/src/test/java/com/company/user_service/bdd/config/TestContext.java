package com.company.user_service.bdd.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

@Getter
@Setter
@Component
public class TestContext {

    private WebTestClient.ResponseSpec response;

    private String email;
    private String password;
    private Long userId;
}