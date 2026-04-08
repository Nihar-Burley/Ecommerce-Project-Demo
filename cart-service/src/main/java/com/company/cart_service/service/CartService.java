package com.company.cart_service.service;


import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.request.CartBulkRequest;
import com.company.cart_service.dto.request.UpdateCartRequest;
import com.company.cart_service.dto.response.CartResponse;
import reactor.core.publisher.Mono;

public interface CartService {

    Mono<CartResponse> addToCart(AddToCartRequest request);

    Mono<CartResponse> updateCart(UpdateCartRequest request);

    Mono<Void> removeItem(Long userId, Long productId);

    Mono<CartResponse> getCart(Long userId);

    Mono<CartResponse> addItems(CartBulkRequest request);

    Mono<CartResponse> updateItems(CartBulkRequest request);

    Mono<CartResponse> increaseItems(CartBulkRequest request);

    Mono<CartResponse> decreaseItems(CartBulkRequest request);
}