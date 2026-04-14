package com.company.cart_service.unit;

import com.company.cart_service.service.CartService;
import com.company.common.dto.cart.request.*;
import com.company.common.dto.cart.response.CartResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartService cartService;

    // ================= ADD =================

    @Test
    void addToCart_success() {
        AddToCartRequest request = new AddToCartRequest();
        CartResponse response = new CartResponse();

        when(cartService.addToCart(request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(cartService.addToCart(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void addToCart_error() {
        AddToCartRequest request = new AddToCartRequest();

        when(cartService.addToCart(request))
                .thenReturn(Mono.error(new RuntimeException("Add failed")));

        StepVerifier.create(cartService.addToCart(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void addToCart_empty() {
        AddToCartRequest request = new AddToCartRequest();

        when(cartService.addToCart(request))
                .thenReturn(Mono.empty());

        StepVerifier.create(cartService.addToCart(request))
                .verifyComplete();
    }

    // ================= BULK ADD =================

    @Test
    void addItems_success() {
        CartBulkRequest request = new CartBulkRequest();
        CartResponse response = new CartResponse();

        when(cartService.addItems(request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(cartService.addItems(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void addItems_error() {
        CartBulkRequest request = new CartBulkRequest();

        when(cartService.addItems(request))
                .thenReturn(Mono.error(new RuntimeException("Bulk add failed")));

        StepVerifier.create(cartService.addItems(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ================= UPDATE =================

    @Test
    void updateCart_success() {
        UpdateCartRequest request = new UpdateCartRequest();
        CartResponse response = new CartResponse();

        when(cartService.updateCart(request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(cartService.updateCart(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void updateCart_error() {
        UpdateCartRequest request = new UpdateCartRequest();

        when(cartService.updateCart(request))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        StepVerifier.create(cartService.updateCart(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ================= BULK UPDATE =================

    @Test
    void updateItems_success() {
        CartBulkRequest request = new CartBulkRequest();
        CartResponse response = new CartResponse();

        when(cartService.updateItems(request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(cartService.updateItems(request))
                .expectNext(response)
                .verifyComplete();
    }

    // ================= INCREASE =================

    @Test
    void increaseItems_success() {
        CartBulkRequest request = new CartBulkRequest();
        CartResponse response = new CartResponse();

        when(cartService.increaseItems(request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(cartService.increaseItems(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void increaseItems_error() {
        CartBulkRequest request = new CartBulkRequest();

        when(cartService.increaseItems(request))
                .thenReturn(Mono.error(new RuntimeException("Increase failed")));

        StepVerifier.create(cartService.increaseItems(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ================= DECREASE =================

    @Test
    void decreaseItems_success() {
        CartBulkRequest request = new CartBulkRequest();
        CartResponse response = new CartResponse();

        when(cartService.decreaseItems(request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(cartService.decreaseItems(request))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void decreaseItems_error() {
        CartBulkRequest request = new CartBulkRequest();

        when(cartService.decreaseItems(request))
                .thenReturn(Mono.error(new RuntimeException("Decrease failed")));

        StepVerifier.create(cartService.decreaseItems(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ================= REMOVE =================

    @Test
    void removeItem_success() {
        when(cartService.removeItem(1L, 10L))
                .thenReturn(Mono.empty());

        StepVerifier.create(cartService.removeItem(1L, 10L))
                .verifyComplete();
    }

    @Test
    void removeItem_error() {
        when(cartService.removeItem(1L, 10L))
                .thenReturn(Mono.error(new RuntimeException("Remove failed")));

        StepVerifier.create(cartService.removeItem(1L, 10L))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ================= GET =================

    @Test
    void getCart_success() {
        CartResponse response = new CartResponse();

        when(cartService.getCart(1L))
                .thenReturn(Mono.just(response));

        StepVerifier.create(cartService.getCart(1L))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getCart_empty() {
        when(cartService.getCart(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(cartService.getCart(1L))
                .verifyComplete();
    }

    @Test
    void getCart_error() {
        when(cartService.getCart(1L))
                .thenReturn(Mono.error(new RuntimeException("Fetch failed")));

        StepVerifier.create(cartService.getCart(1L))
                .expectError(RuntimeException.class)
                .verify();
    }
}