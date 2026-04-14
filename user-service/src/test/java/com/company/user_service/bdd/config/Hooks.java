package com.company.user_service.bdd.config;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class Hooks {

    @Autowired
    private TestContext context;

    @Before
    public void beforeScenario() {
        context.clear();
    }
}