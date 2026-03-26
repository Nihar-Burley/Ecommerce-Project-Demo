package com.company.cart_service.repository;

import com.company.cart_service.model.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {

    Mono<Cart> findByUserId(String userId);
}