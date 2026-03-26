package com.company.cart_service.repository;

import com.company.cart_service.model.CartItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {

    Flux<CartItem> findByCartId(Long cartId);

    Mono<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    Mono<Void> deleteByCartIdAndProductId(Long cartId, Long productId);

    Mono<Void> deleteByCartId(Long cartId);
}