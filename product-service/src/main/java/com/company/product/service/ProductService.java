package com.company.product.service;

import com.company.common.dto.product.request.ProductRequest;
import com.company.common.dto.product.response.ProductResponse;
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
