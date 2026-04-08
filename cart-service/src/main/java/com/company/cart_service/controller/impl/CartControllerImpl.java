package com.company.cart_service.controller.impl;

import com.company.cart_service.controller.CartController;
import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.request.UpdateCartRequest;
import com.company.cart_service.dto.request.CartBulkRequest;
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

    // ================= ADD =================
    @Override
    public Mono<CartResponse> addToCart(Long userId, String role, AddToCartRequest request) {

        log.info("API: Add to cart | userId={} role={}", userId, role);

        validateUser(role);
        request.setUserId(userId);
        return cartService.addToCart(request);
    }

    // ================= BULK ADD =================
    @Override
    public Mono<CartResponse> addItems(Long userId, String role, CartBulkRequest request) {

        log.info("API: Bulk Add | userId={}", userId);

        validateUser(role);
        request.setUserId(userId);
        return cartService.addItems(request);
    }

    // ================= UPDATE =================
    @Override
    public Mono<CartResponse> updateCart(Long userId, String role, UpdateCartRequest request) {

        log.info("API: Update cart | userId={} role={}", userId, role);

        validateUser(role);
        request.setUserId(userId);
        return cartService.updateCart(request);
    }

    // ================= BULK UPDATE =================
    @Override
    public Mono<CartResponse> updateItems(Long userId, String role, CartBulkRequest request) {

        log.info("API: Bulk Update | userId={}", userId);

        validateUser(role);
        request.setUserId(userId);
        return cartService.updateItems(request);
    }

    // ================= INCREASE =================
    @Override
    public Mono<CartResponse> increaseItems(Long userId, String role, CartBulkRequest request) {

        log.info("API: Increase items | userId={}", userId);

        validateUser(role);
        request.setUserId(userId);
        return cartService.increaseItems(request);
    }

    // ================= DECREASE =================
    @Override
    public Mono<CartResponse> decreaseItems(Long userId, String role, CartBulkRequest request) {

        log.info("API: Decrease items | userId={}", userId);

        validateUser(role);
        request.setUserId(userId);
        return cartService.decreaseItems(request);
    }

    // ================= REMOVE =================
    @Override
    public Mono<Void> removeItem(Long userId, String role, Long productId) {

        log.info("API: Remove item | userId={} productId={}", userId, productId);

        validateUser(role);
        return cartService.removeItem(userId, productId);
    }

    // ================= GET =================
    @Override
    public Mono<CartResponse> getCart(Long userId, String role) {

        log.info("API: Get cart | userId={} role={}", userId, role);

        validateUser(role);
        return cartService.getCart(userId);
    }

    // ================= VALIDATION =================
    private void validateUser(String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new CustomException("Access Denied", "FORBIDDEN", 403);
        }
    }
}