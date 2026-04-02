package com.company.cart_service.unit;

import com.company.cart_service.controller.CartController;
import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.response.CartResponse;
import com.company.cart_service.exception.CustomException;
import com.company.cart_service.service.CartService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(CartController.class)
class CartControllerImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CartService cartService;

    @Test
    void shouldAddToCart() {

        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        CartResponse response = new CartResponse();
        response.setUserId(1L);

        when(cartService.addToCart(any())).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/cart/add")
                .header("X-User-Id", "1")
                .header("X-User-Role", "USER")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(1);
    }


    @Test
    void shouldReturnForbiddenWhenInvalidRole() {

        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        webTestClient.post()
                .uri("/api/v1/cart/add")
                .header("X-User-Id", "1")
                .header("X-User-Role", "GUEST")
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldHandleServiceException() {

        AddToCartRequest request = new AddToCartRequest();

        when(cartService.addToCart(any()))
                .thenReturn(Mono.error(new CustomException("Error", "ERR", 400)));

        webTestClient.post()
                .uri("/api/v1/cart/add")
                .header("X-User-Id", "1")
                .header("X-User-Role", "USER")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnCart() {

        CartResponse response = new CartResponse();
        response.setUserId(1L);

        when(cartService.getCart(1L))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/cart")
                .header("X-User-Id", "1")
                .header("X-User-Role", "USER")
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    void shouldDeleteItem() {

        when(cartService.removeItem(1L, 1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/cart/remove")
                        .queryParam("productId", 1L)
                        .build())
                .header("X-User-Id", "1")
                .header("X-User-Role", "USER")
                .exchange()
                .expectStatus().isNoContent();
    }
}