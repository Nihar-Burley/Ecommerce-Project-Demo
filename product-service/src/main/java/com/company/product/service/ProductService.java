package com.company.product.service;

import com.company.product.dto.request.ProductRequest;
import com.company.product.dto.response.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<ProductResponse> getAllProducts();

    Mono<ProductResponse> getProductById(Long id);

    Mono<ProductResponse> createProduct(ProductRequest request);

    Mono<ProductResponse> updateProduct(Long id, ProductRequest request);

    Mono<Void> deleteProduct(Long id);

    Mono<Void> reduceStock(Long productId, int quantity);

    Mono<Void> increaseStock(Long productId, int quantity);
}
