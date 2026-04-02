package com.company.product.unit;

import com.company.product.dto.request.ProductRequest;
import com.company.product.entity.Product;
import com.company.product.exception.ResourceNotFoundException;
import com.company.product.repository.ProductRepository;
import com.company.product.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;
    private ProductRequest request;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(80000.0)
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        request = new ProductRequest();
        request.setName("iPhone 15");
        request.setDescription("Apple smartphone");
        request.setPrice(80000.0);
        request.setStock(10);
    }

    @Test
    void shouldReturnAllProducts() {
        when(repository.findAll()).thenReturn(Flux.just(product));

        StepVerifier.create(service.getAllProducts())
                .expectNextMatches(res -> res.getName().equals("iPhone 15"))
                .verifyComplete();

        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyWhenNoProducts() {
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(service.getAllProducts())
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void shouldReturnProductById() {
        when(repository.findById(1L)).thenReturn(Mono.just(product));

        StepVerifier.create(service.getProductById(1L))
                .expectNextMatches(res -> res.getId().equals(1L))
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.getProductById(1L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository).findById(1L);
    }

    @Test
    void shouldCreateProduct() {
        when(repository.save(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(service.createProduct(request))
                .expectNextMatches(res -> res.getName().equals("iPhone 15"))
                .verifyComplete();

        verify(repository).save(any(Product.class));
    }

    @Test
    void shouldUpdateProduct() {
        when(repository.findById(1L)).thenReturn(Mono.just(product));
        when(repository.save(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(service.updateProduct(1L, request))
                .expectNextMatches(res -> res.getName().equals("iPhone 15"))
                .verifyComplete();

        verify(repository).findById(1L);
        verify(repository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.updateProduct(1L, request))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository).findById(1L);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteProduct() {
        when(repository.findById(1L)).thenReturn(Mono.just(product));
        when(repository.delete(product)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteProduct(1L))
                .verifyComplete();

        verify(repository).delete(product);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProduct() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteProduct(1L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repository).findById(1L);
    }
}