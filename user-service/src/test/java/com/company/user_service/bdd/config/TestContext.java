package com.company.user_service.bdd.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope("cucumber-glue")
public class TestContext {


    private String baseUrl;


    private ResponseEntity<String> response;


    private Long userId;
    private String email;


    private String token;

    public void clear() {
        this.response = null;
        this.userId = null;
        this.email = null;
        this.token = null;
        this.baseUrl = null;
    }
}