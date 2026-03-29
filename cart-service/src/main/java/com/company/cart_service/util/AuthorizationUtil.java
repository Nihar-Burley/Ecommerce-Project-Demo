package com.company.cart_service.util;

import com.company.cart_service.exception.CustomException;

public class AuthorizationUtil {

    public static void checkUserOrAdmin(String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new CustomException("Access Denied", "FORBIDDEN", 403);
        }
    }

    public static void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new CustomException("Access Denied", "FORBIDDEN", 403);
        }
    }
}