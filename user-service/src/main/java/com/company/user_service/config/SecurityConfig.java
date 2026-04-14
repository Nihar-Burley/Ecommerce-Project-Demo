package com.company.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(com.company.common.config.BaseSecurityConfig.class)
public class SecurityConfig {
}