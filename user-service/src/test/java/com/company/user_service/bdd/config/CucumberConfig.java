package com.company.user_service.bdd.config;

import com.company.user_service.unit.TestSecurityConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class CucumberConfig {
}