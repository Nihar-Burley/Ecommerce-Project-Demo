package com.company.product.unit;

import com.company.product.controller.ProductController;
import com.company.product.dto.request.ProductRequest;
import com.company.product.dto.response.ProductResponse;
import com.company.product.exception.GlobalExceptionHandler;
import com.company.product.exception.ResourceNotFoundException;
import com.company.product.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    @Test
    void shouldReturnAllProducts() {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(80000.0)
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productService.getAllProducts())
                .thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .hasSize(1);
    }

    @Test
    void shouldReturnProductById() {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("iPhone 15")
                .build();

        when(productService.getProductById(1L))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() {

        when(productService.getProductById(1L))
                .thenReturn(Mono.error(new ResourceNotFoundException("Product not found")));

        webTestClient.get()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void shouldCreateProduct() {

        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(1000.0);
        request.setStock(5);

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(productService.createProduct(any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test Product");
    }


    @Test
    void shouldFailWhenInvalidInput() {

        ProductRequest request = new ProductRequest(); // empty

        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldUpdateProduct() {

        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setPrice(1200.0);
        request.setStock(5);

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Updated Product")
                .build();

        when(productService.updateProduct(eq(1L), any()))
                .thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Product");
    }


    @Test
    void shouldDeleteProduct() {

        when(productService.deleteProduct(1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}