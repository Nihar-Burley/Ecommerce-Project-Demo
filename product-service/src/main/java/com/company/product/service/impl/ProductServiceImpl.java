package com.company.product.service.impl;

import com.company.product.dto.request.ProductRequest;
import com.company.product.dto.response.ProductResponse;
import com.company.product.entity.Product;
import com.company.product.exception.ResourceNotFoundException;
import com.company.product.mapper.ProductMapper;
import com.company.product.repository.ProductRepository;
import com.company.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.company.product.util.Constants.PRODUCT_NOT_FOUND;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public Flux<ProductResponse> getAllProducts() {
        log.info("Fetching all products");

        return repository.findAll()
                .map(ProductMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> getProductById(Long id) {
        log.info("Fetching product with id: {}", id);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException(PRODUCT_NOT_FOUND+ id)))
                .map(ProductMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        log.info("Creating product: {}", request.getName());

        Product product = ProductMapper.toEntity(request);

        return repository.save(product)
                .map(ProductMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException(PRODUCT_NOT_FOUND + id)))
                .flatMap(existing -> {
                    ProductMapper.updateEntity(existing, request);
                    return repository.save(existing);
                })
                .map(ProductMapper::toResponse);
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException(PRODUCT_NOT_FOUND+ id)))
                .flatMap(repository::delete);
    }


    @Override
    public Mono<Void> reduceStock(Long productId, int quantity) {

        log.info("Reducing stock | productId={} quantity={}", productId, quantity);

        return repository.findById(productId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException(PRODUCT_NOT_FOUND + productId)))
                .flatMap(product -> {

                    if (product.getStock() < quantity) {
                        return Mono.error(new IllegalArgumentException("Insufficient stock"));
                    }

                    product.setStock(product.getStock() - quantity);
                    product.setUpdatedAt(LocalDateTime.now());

                    return repository.save(product);
                })
                .then();
    }


    @Override
    public Mono<Void> increaseStock(Long productId, int quantity) {

        log.info("Increasing stock | productId={} quantity={}", productId, quantity);

        return repository.findById(productId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException(PRODUCT_NOT_FOUND + productId)))
                .flatMap(product -> {

                    product.setStock(product.getStock() + quantity);
                    product.setUpdatedAt(LocalDateTime.now());

                    return repository.save(product);
                })
                .then();
    }
}