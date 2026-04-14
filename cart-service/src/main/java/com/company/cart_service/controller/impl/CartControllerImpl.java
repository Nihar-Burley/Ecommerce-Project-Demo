package com.company.cart_service.controller.impl;

import com.company.common.controller.cart.CartController;
import com.company.common.dto.cart.request.*;
import com.company.common.dto.cart.response.CartResponse;
import com.company.common.config.SecurityUtils;
import com.company.cart_service.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CartControllerImpl implements CartController {

    private final CartService cartService;

    // ================= ADD =================
    @Override
    public Mono<CartResponse> addToCart(Authentication authentication, AddToCartRequest request) {

        Long userId = SecurityUtils.getUserId(authentication);

        log.info("API: Add to cart | userId={}", userId);

        request.setUserId(userId);
        return cartService.addToCart(request);
    }

    // ================= BULK ADD =================
    @Override
    public Mono<CartResponse> addItems(Authentication authentication, CartBulkRequest request) {

        Long userId = SecurityUtils.getUserId(authentication);

        log.info("API: Bulk Add | userId={}", userId);

        request.setUserId(userId);
        return cartService.addItems(request);
    }

    // ================= UPDATE =================
    @Override
    public Mono<CartResponse> updateCart(Authentication authentication, UpdateCartRequest request) {

        Long userId = SecurityUtils.getUserId(authentication);

        log.info("API: Update cart | userId={}", userId);

        request.setUserId(userId);
        return cartService.updateCart(request);
    }

    // ================= BULK UPDATE =================
    @Override
    public Mono<CartResponse> updateItems(Authentication authentication, CartBulkRequest request) {

        Long userId = SecurityUtils.getUserId(authentication);

        request.setUserId(userId);
        return cartService.updateItems(request);
    }

    // ================= INCREASE =================
    @Override
    public Mono<CartResponse> increaseItems(Authentication authentication, CartBulkRequest request) {

        Long userId = SecurityUtils.getUserId(authentication);

        request.setUserId(userId);
        return cartService.increaseItems(request);
    }

    // ================= DECREASE =================
    @Override
    public Mono<CartResponse> decreaseItems(Authentication authentication, CartBulkRequest request) {

        Long userId = SecurityUtils.getUserId(authentication);

        request.setUserId(userId);
        return cartService.decreaseItems(request);
    }

    // ================= REMOVE =================
    @Override
    public Mono<Void> removeItem(Authentication authentication, Long productId) {

        Long userId = SecurityUtils.getUserId(authentication);

        return cartService.removeItem(userId, productId);
    }

    // ================= GET =================
    @Override
    public Mono<CartResponse> getCart(Authentication authentication) {

        Long userId = SecurityUtils.getUserId(authentication);

        return cartService.getCart(userId);
    }
}