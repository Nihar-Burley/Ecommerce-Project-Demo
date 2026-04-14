package com.company.cart_service.client;

import com.company.common.dto.product.response.ProductResponse;
import com.company.cart_service.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

    private final WebClient.Builder webClientBuilder;

    private static final String PRODUCT_SERVICE_BASE_URL = "http://api-gateway";

    // ================= GET PRODUCT =================
    public Mono<ProductResponse> getProduct(Long productId) {

        log.info("Calling Product Service | GET product | productId={}", productId);

        return getAuthHeader()
                .flatMap(authHeader ->
                        webClientBuilder.build()
                                .get()
                                .uri(PRODUCT_SERVICE_BASE_URL + "/api/v1/products/{id}", productId)
                                .header(HttpHeaders.AUTHORIZATION, authHeader) // 🔥 FIX
                                .retrieve()
                                .onStatus(HttpStatus::is4xxClientError, response -> {
                                    log.error("Client error while fetching product: {}", response.statusCode());
                                    return Mono.error(new CustomException("Product not found", "PRODUCT_NOT_FOUND", 404));
                                })
                                .onStatus(HttpStatus::is5xxServerError, response -> {
                                    log.error("Server error from Product Service: {}", response.statusCode());
                                    return Mono.error(new RuntimeException("Product service unavailable"));
                                })
                                .bodyToMono(ProductResponse.class)
                                .doOnNext(p -> log.info("Product fetched successfully | productId={}", p.getId()))
                                .doOnError(err -> log.error("Error fetching product | {}", err.getMessage()))
                );
    }

    // ================= REDUCE STOCK =================
    public Mono<Void> reduceStock(Long productId, int quantity) {

        log.info("Calling Product Service | REDUCE stock | productId={} quantity={}", productId, quantity);

        return getAuthHeader()
                .flatMap(authHeader ->
                        webClientBuilder.build()
                                .put()
                                .uri(PRODUCT_SERVICE_BASE_URL + "/api/v1/products/{id}/reduce/{qty}", productId, quantity)
                                .header(HttpHeaders.AUTHORIZATION, authHeader) // 🔥 FIX
                                .retrieve()
                                .onStatus(HttpStatus::isError, response -> {
                                    log.error("Error reducing stock: {}", response.statusCode());
                                    return Mono.error(new RuntimeException("Failed to reduce stock"));
                                })
                                .bodyToMono(Void.class)
                                .doOnSuccess(v -> log.info("Stock reduced successfully | productId={}", productId))
                                .doOnError(err -> log.error("Error reducing stock | {}", err.getMessage()))
                );
    }

    // ================= INCREASE STOCK =================
    public Mono<Void> increaseStock(Long productId, int quantity) {

        log.info("Calling Product Service | INCREASE stock | productId={} quantity={}", productId, quantity);

        return getAuthHeader()
                .flatMap(authHeader ->
                        webClientBuilder.build()
                                .put()
                                .uri(PRODUCT_SERVICE_BASE_URL + "/api/v1/products/{id}/increase/{qty}", productId, quantity)
                                .header(HttpHeaders.AUTHORIZATION, authHeader) // 🔥 FIX
                                .retrieve()
                                .onStatus(HttpStatus::isError, response -> {
                                    log.error("Error increasing stock: {}", response.statusCode());
                                    return Mono.error(new RuntimeException("Failed to increase stock"));
                                })
                                .bodyToMono(Void.class)
                                .doOnSuccess(v -> log.info("Stock increased successfully | productId={}", productId))
                                .doOnError(err -> log.error("Error increasing stock | {}", err.getMessage()))
                );
    }

    // ================= HELPER =================
    private Mono<String> getAuthHeader() {
        return Mono.deferContextual(contextView -> {
            if (!contextView.hasKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new RuntimeException("Missing Authorization Header"));
            }
            return Mono.just(contextView.get(HttpHeaders.AUTHORIZATION));
        });
    }
}