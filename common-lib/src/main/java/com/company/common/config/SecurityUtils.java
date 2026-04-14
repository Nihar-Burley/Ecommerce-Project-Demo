package com.company.common.config;

import org.springframework.security.core.Authentication;

public class SecurityUtils {

    public static Long getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User not authenticated");
        }

        return Long.valueOf(authentication.getPrincipal().toString());
    }
}