package com.company.product.unit;

import com.company.common.dto.product.request.ProductRequest;
import com.company.common.dto.product.response.ProductResponse;
import com.company.product.entity.Product;
import com.company.product.exception.ResourceNotFoundException;
import com.company.product.repository.ProductRepository;
import com.company.product.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ================= GET ALL =================

    @Test
    void getAllProducts_success() {
        when(repository.findAll())
                .thenReturn(Flux.just(new Product(), new Product()));

        StepVerifier.create(service.getAllProducts())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAllProducts_empty() {
        when(repository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(service.getAllProducts())
                .verifyComplete();
    }

    // ================= GET BY ID =================

    @Test
    void getProductById_success() {
        Product product = new Product();
        product.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        StepVerifier.create(service.getProductById(1L))
                .expectNextMatches(p -> p.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void getProductById_notFound() {
        when(repository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.getProductById(1L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ================= CREATE =================

    @Test
    void createProduct_success() {
        ProductRequest request = new ProductRequest();

        Product saved = new Product();
        saved.setId(10L);

        when(repository.save(any()))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(service.createProduct(request))
                .expectNextMatches(res -> res.getId().equals(10L))
                .verifyComplete();
    }

    // ================= UPDATE =================

    @Test
    void updateProduct_success() {
        Product existing = new Product();
        existing.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(existing));

        when(repository.save(any()))
                .thenReturn(Mono.just(existing));

        StepVerifier.create(service.updateProduct(1L, new ProductRequest()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void updateProduct_notFound() {
        when(repository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.updateProduct(1L, new ProductRequest()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ================= DELETE =================

    @Test
    void deleteProduct_success() {
        Product product = new Product();

        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        when(repository.delete(product))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.deleteProduct(1L))
                .verifyComplete();
    }

    @Test
    void deleteProduct_notFound() {
        when(repository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.deleteProduct(1L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ================= REDUCE STOCK =================

    @Test
    void reduceStock_success() {
        Product product = new Product();
        product.setStock(10);

        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        when(repository.save(any()))
                .thenReturn(Mono.just(product));

        StepVerifier.create(service.reduceStock(1L, 5))
                .verifyComplete();

        verify(repository).save(any());
    }

    @Test
    void reduceStock_insufficient() {
        Product product = new Product();
        product.setStock(2);

        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        StepVerifier.create(service.reduceStock(1L, 5))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void reduceStock_notFound() {
        when(repository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.reduceStock(1L, 5))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ================= INCREASE STOCK =================

    @Test
    void increaseStock_success() {
        Product product = new Product();
        product.setStock(5);

        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        when(repository.save(any()))
                .thenReturn(Mono.just(product));

        StepVerifier.create(service.increaseStock(1L, 5))
                .verifyComplete();

        verify(repository).save(any());
    }

    @Test
    void increaseStock_notFound() {
        when(repository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.increaseStock(1L, 5))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}