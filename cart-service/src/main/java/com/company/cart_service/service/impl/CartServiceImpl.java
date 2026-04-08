package com.company.cart_service.service.impl;

import com.company.cart_service.client.ProductClient;
import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.request.CartBulkRequest;
import com.company.cart_service.dto.request.UpdateCartRequest;
import com.company.cart_service.dto.response.CartResponse;
import com.company.cart_service.dto.response.ProductResponse;
import com.company.cart_service.exception.CartNotFoundException;
import com.company.cart_service.exception.InsufficientStockException;
import com.company.cart_service.exception.ProductNotFoundException;
import com.company.cart_service.mapper.CartMapper;
import com.company.cart_service.model.Cart;
import com.company.cart_service.model.CartItem;
import com.company.cart_service.repository.CartItemRepository;
import com.company.cart_service.repository.CartRepository;
import com.company.cart_service.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    // ================= ADD ITEM =================
    @Override
    public Mono<CartResponse> addToCart(AddToCartRequest request) {

        log.info("START addToCart | userId={} productId={} quantity={}",
                request.getUserId(), request.getProductId(), request.getQuantity());

        return productClient.getProduct(request.getProductId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException()))

                .flatMap(product -> validateAndReduceStock(product, request.getQuantity()))

                .flatMap(product ->
                        cartRepository.findByUserId(request.getUserId())
                                .switchIfEmpty(createCart(request.getUserId()))
                                .flatMap(cart -> upsertCartItem(cart, product, request.getQuantity()))
                )

                .flatMap(this::buildCartResponse);
    }

    // ================= UPDATE =================
    @Override
    public Mono<CartResponse> updateCart(UpdateCartRequest request) {

        return cartRepository.findByUserId(request.getUserId())
                .switchIfEmpty(Mono.error(new CartNotFoundException()))

                .flatMap(cart ->
                        cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))

                                .flatMap(item ->
                                        adjustStock(item, request.getQuantity())
                                                .then(updateOrRemoveItem(cart, item, request.getQuantity()))
                                )
                )
                .flatMap(this::buildCartResponse);
    }

    // ================= REMOVE =================
    @Override
    public Mono<Void> removeItem(Long userId, Long productId) {

        return cartRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new CartNotFoundException()))

                .flatMap(cart ->
                        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))

                                .flatMap(item ->
                                        productClient.increaseStock(productId, item.getQuantity())
                                                .then(cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId))
                                                .then(checkAndDeleteCartIfEmpty(cart))
                                )
                );
    }

    // ================= GET =================
    @Override
    public Mono<CartResponse> getCart(Long userId) {

        return cartRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new CartNotFoundException()))
                .flatMap(this::buildCartResponse);
    }

    // ================= BULK ADD =================
    @Override
    public Mono<CartResponse> addItems(CartBulkRequest request) {

        return cartRepository.findByUserId(request.getUserId())
                .switchIfEmpty(createCart(request.getUserId()))

                .flatMap(cart ->
                        Flux.fromIterable(request.getItems())
                                .flatMap(item ->
                                        productClient.getProduct(item.getProductId())
                                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                                                .flatMap(product -> validateAndReduceStock(product, item.getQuantity()))
                                                .flatMap(product -> upsertCartItem(cart, product, item.getQuantity()))
                                )
                                .then(Mono.just(cart))
                )
                .flatMap(this::buildCartResponse);
    }

    // ================= BULK UPDATE =================
    @Override
    public Mono<CartResponse> updateItems(CartBulkRequest request) {

        return cartRepository.findByUserId(request.getUserId())
                .switchIfEmpty(Mono.error(new CartNotFoundException()))

                .flatMap(cart ->
                        Flux.fromIterable(request.getItems())
                                .flatMap(item ->
                                        cartItemRepository.findByCartIdAndProductId(cart.getId(), item.getProductId())
                                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                                                .flatMap(existing ->
                                                        adjustStock(existing, item.getQuantity())
                                                                .then(updateOrRemoveItem(cart, existing, item.getQuantity()))
                                                )
                                )
                                .then(Mono.just(cart))
                )
                .flatMap(this::buildCartResponse);
    }

    // ================= INCREASE =================
    @Override
    public Mono<CartResponse> increaseItems(CartBulkRequest request) {

        return cartRepository.findByUserId(request.getUserId())
                .switchIfEmpty(Mono.error(new CartNotFoundException()))

                .flatMap(cart ->
                        Flux.fromIterable(request.getItems())
                                .flatMap(item ->
                                        cartItemRepository.findByCartIdAndProductId(cart.getId(), item.getProductId())
                                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                                                .flatMap(existing -> {

                                                    int newQty = existing.getQuantity() + item.getQuantity();

                                                    return adjustStock(existing, newQty)
                                                            .then(updateOrRemoveItem(cart, existing, newQty));
                                                })
                                )
                                .then(Mono.just(cart))
                )
                .flatMap(this::buildCartResponse);
    }

    // ================= DECREASE =================
    @Override
    public Mono<CartResponse> decreaseItems(CartBulkRequest request) {

        return cartRepository.findByUserId(request.getUserId())
                .switchIfEmpty(Mono.error(new CartNotFoundException()))

                .flatMap(cart ->
                        Flux.fromIterable(request.getItems())
                                .flatMap(item ->
                                        cartItemRepository.findByCartIdAndProductId(cart.getId(), item.getProductId())
                                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                                                .flatMap(existing -> {

                                                    int newQty = existing.getQuantity() - item.getQuantity();
                                                    int finalQty = Math.max(newQty, 0);

                                                    return adjustStock(existing, finalQty)
                                                            .then(updateOrRemoveItem(cart, existing, finalQty));
                                                })
                                )
                                .then(Mono.just(cart))
                )
                .flatMap(this::buildCartResponse);
    }

    // ================= HELPERS =================

    private Mono<ProductResponse> validateAndReduceStock(ProductResponse product, int quantity) {

        if (product.getStock() < quantity) {
            return Mono.error(new InsufficientStockException());
        }

        return productClient.reduceStock(product.getId(), quantity)
                .thenReturn(product);
    }

    private Mono<Cart> createCart(Long userId) {
        return cartRepository.save(Cart.builder().userId(userId).build());
    }

    private Mono<Cart> upsertCartItem(Cart cart, ProductResponse product, int quantity) {

        return cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .flatMap(item -> {
                    item.setQuantity(item.getQuantity() + quantity);
                    return cartItemRepository.save(item);
                })
                .switchIfEmpty(
                        cartItemRepository.save(
                                CartItem.builder()
                                        .cartId(cart.getId())
                                        .productId(product.getId())
                                        .productName(product.getName())
                                        .price(product.getPrice())
                                        .quantity(quantity)
                                        .build()
                        )
                )
                .thenReturn(cart);
    }

    private Mono<Void> adjustStock(CartItem item, int newQty) {

        int diff = newQty - item.getQuantity();

        if (diff > 0) {
            return productClient.reduceStock(item.getProductId(), diff);
        } else if (diff < 0) {
            return productClient.increaseStock(item.getProductId(), Math.abs(diff));
        }

        return Mono.empty();
    }

    private Mono<Cart> updateOrRemoveItem(Cart cart, CartItem item, int quantity) {

        if (quantity == 0) {

            return cartItemRepository.deleteByCartIdAndProductId(cart.getId(), item.getProductId())
                    .then(checkAndDeleteCartIfEmpty(cart))
                    .thenReturn(cart);
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item).thenReturn(cart);
    }

    private Mono<Void> checkAndDeleteCartIfEmpty(Cart cart) {

        return cartItemRepository.findByCartId(cart.getId()).count()
                .flatMap(count -> {
                    if (count == 0) {
                        log.info("Deleting empty cart | cartId={}", cart.getId());
                        return cartRepository.deleteById(cart.getId());
                    }
                    return Mono.empty();
                });
    }

    private Mono<CartResponse> buildCartResponse(Cart cart) {

        return cartItemRepository.findByCartId(cart.getId())
                .map(CartMapper::toCartItemResponse)
                .collectList()
                .map(items -> CartMapper.toCartResponse(cart, items));
    }
}