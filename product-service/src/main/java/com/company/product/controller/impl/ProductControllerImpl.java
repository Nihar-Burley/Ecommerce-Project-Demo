package com.company.product.controller.impl;

import com.company.common.controller.product.ProductController;
import com.company.common.dto.product.request.ProductRequest;
import com.company.common.dto.product.response.ProductResponse;
import com.company.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;

    @Override
    public Flux<ProductResponse> getAllProducts() {
        log.info("API: Fetch all products");
        return productService.getAllProducts();
    }

    @Override
    public Mono<ProductResponse> getProductById(Long id) {
        log.info("API: Fetch product by id: {}", id);
        return productService.getProductById(id);
    }

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        log.info("API: Create product: {}", request.getName());
        return productService.createProduct(request);
    }

    @Override
    public Mono<ProductResponse> updateProduct(Long id, ProductRequest request) {
        log.info("API: Update product with id: {}", id);
        return productService.updateProduct(id, request);
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {
        log.info("API: Delete product with id: {}", id);
        return productService.deleteProduct(id);
    }

    @Override
    public Mono<Void> reduceStock(Long id, int qty) {
        log.info("API: Reduce stock | productId={} qty={}", id, qty);
        return productService.reduceStock(id, qty);
    }

    @Override
    public Mono<Void> increaseStock(Long id, int qty) {
        log.info("API: Increase stock | productId={} qty={}", id, qty);
        return productService.increaseStock(id, qty);
    }
}