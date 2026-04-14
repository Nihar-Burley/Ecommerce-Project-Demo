package com.company.product.unit;

import com.company.common.dto.product.request.ProductRequest;
import com.company.common.dto.product.response.ProductResponse;
import com.company.product.controller.impl.ProductControllerImpl;
import com.company.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = ProductControllerImpl.class)
@Import(TestSecurityConfig.class) // disable security
class ProductControllerImplTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private ProductService service;

    // ================= HELPER =================

    private ProductRequest validRequest() {
        ProductRequest req = new ProductRequest();
        req.setName("iPhone");
        req.setDescription("Apple mobile");
        req.setPrice(1000.0);
        req.setStock(10);
        return req;
    }

    private ProductResponse response() {
        ProductResponse res = new ProductResponse();
        res.setId(1L);
        res.setName("iPhone");
        return res;
    }

    // ================= GET ALL =================

    @Test
    void getAllProducts_success() {
        when(service.getAllProducts())
                .thenReturn(Flux.just(response()));

        client.get()
                .uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class).hasSize(1);
    }

    // ================= GET BY ID =================

    @Test
    void getProductById_success() {
        when(service.getProductById(1L))
                .thenReturn(Mono.just(response()));

        client.get()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getProductById_notFound() {
        when(service.getProductById(1L))
                .thenReturn(Mono.error(new RuntimeException()));

        client.get()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().is5xxServerError(); // no handler → 500
    }

    // ================= CREATE =================

    @Test
    void createProduct_success() {
        when(service.createProduct(any()))
                .thenReturn(Mono.just(response()));

        client.post()
                .uri("/api/v1/products")
                .bodyValue(validRequest()) // ✅ IMPORTANT
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void createProduct_validationFail() {
        client.post()
                .uri("/api/v1/products")
                .bodyValue(new ProductRequest()) // ❌ invalid
                .exchange()
                .expectStatus().isBadRequest();
    }

    // ================= UPDATE =================

    @Test
    void updateProduct_success() {
        when(service.updateProduct(eq(1L), any()))
                .thenReturn(Mono.just(response()));

        client.put()
                .uri("/api/v1/products/1")
                .bodyValue(validRequest()) // ✅ IMPORTANT
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateProduct_notFound() {
        when(service.updateProduct(eq(1L), any()))
                .thenReturn(Mono.error(new RuntimeException()));

        client.put()
                .uri("/api/v1/products/1")
                .bodyValue(validRequest()) // ✅ IMPORTANT
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ================= DELETE =================

    @Test
    void deleteProduct_success() {
        when(service.deleteProduct(1L))
                .thenReturn(Mono.empty());

        client.delete()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteProduct_notFound() {
        when(service.deleteProduct(1L))
                .thenReturn(Mono.error(new RuntimeException()));

        client.delete()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ================= STOCK =================

    @Test
    void reduceStock_success() {
        when(service.reduceStock(1L, 5))
                .thenReturn(Mono.empty());

        client.put()
                .uri("/api/v1/products/1/reduce/5")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void reduceStock_insufficient() {
        when(service.reduceStock(1L, 5))
                .thenReturn(Mono.error(new IllegalArgumentException()));

        client.put()
                .uri("/api/v1/products/1/reduce/5")
                .exchange()
                .expectStatus().isBadRequest(); // ✅ FIX
    }

    @Test
    void increaseStock_success() {
        when(service.increaseStock(1L, 5))
                .thenReturn(Mono.empty());

        client.put()
                .uri("/api/v1/products/1/increase/5")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void increaseStock_notFound() {
        when(service.increaseStock(1L, 5))
                .thenReturn(Mono.error(new RuntimeException()));

        client.put()
                .uri("/api/v1/products/1/increase/5")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}