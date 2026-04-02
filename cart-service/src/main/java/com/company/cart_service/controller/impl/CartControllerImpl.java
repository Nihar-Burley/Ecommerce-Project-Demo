package com.company.cart_service.controller.impl;

import com.company.cart_service.controller.CartController;
import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.request.UpdateCartRequest;
import com.company.cart_service.dto.response.CartResponse;
import com.company.cart_service.exception.CustomException;
import com.company.cart_service.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CartControllerImpl implements CartController {

    private final CartService cartService;

    @Override
    public Mono<CartResponse> addToCart(Long userId, String role, AddToCartRequest request) {

        log.info("API: Add to cart | userId={} role={}", userId, role);

        validateUser(role);
        request.setUserId(userId);
        return cartService.addToCart(request);
    }

    @Override
    public Mono<CartResponse> updateCart(Long userId, String role, UpdateCartRequest request) {

        log.info("API: Update cart | userId={} role={}", userId, role);

        validateUser(role);
        request.setUserId(userId);
        return cartService.updateCart(request);
    }

    @Override
    public Mono<Void> removeItem(Long userId, String role, Long productId) {

        log.info("API: Remove item | userId={} productId={}", userId, productId);

        validateUser(role);
        return cartService.removeItem(userId, productId);
    }

    @Override
    public Mono<CartResponse> getCart(Long userId, String role) {

        log.info("API: Get cart | userId={} role={}", userId, role);

        validateUser(role);
        return cartService.getCart(userId);
    }

    private void validateUser(String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new CustomException("Access Denied", "FORBIDDEN", 403);
        }
    }
}