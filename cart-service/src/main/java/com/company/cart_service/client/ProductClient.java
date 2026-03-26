package com.company.cart_service.client;

import com.company.cart_service.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

    private final WebClient productWebClient;

    // ================= GET PRODUCT =================
    public Mono<ProductResponse> getProduct(Long productId) {

        log.info("Calling Product Service to fetch product | productId={}", productId);

        return productWebClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .doOnNext(p -> log.debug("Product received: {}", p))
                .doOnError(err -> log.error("Error fetching product | {}", err.getMessage()));
    }

    // ================= REDUCE STOCK =================
    public Mono<Void> reduceStock(Long productId, int quantity) {

        log.info("Calling Product Service to reduce stock | productId={} quantity={}",
                productId, quantity);

        return productWebClient.put()
                .uri("/products/{id}/reduce/{qty}", productId, quantity)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Stock reduced successfully"))
                .doOnError(err -> log.error("Error reducing stock | {}", err.getMessage()));
    }

    // ================= INCREASE STOCK =================
    public Mono<Void> increaseStock(Long productId, int quantity) {

        log.info("Calling Product Service to increase stock | productId={} quantity={}",
                productId, quantity);

        return productWebClient.put()
                .uri("/products/{id}/increase/{qty}", productId, quantity)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.debug("Stock increased successfully"))
                .doOnError(err -> log.error("Error increasing stock | {}", err.getMessage()));
    }
}