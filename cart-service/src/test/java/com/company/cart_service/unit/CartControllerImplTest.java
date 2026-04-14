package com.company.cart_service.unit;

import com.company.cart_service.controller.impl.CartControllerImpl;
import com.company.cart_service.service.CartService;
import com.company.common.config.SecurityUtils;
import com.company.common.dto.cart.request.*;
import com.company.common.dto.cart.response.CartResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerImplTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartControllerImpl cartController;

    private Authentication authentication;

    @BeforeEach
    void setup() {
        authentication = new UsernamePasswordAuthenticationToken("user", null);
    }

    // ================= ADD =================

    @Test
    void addToCart_success() {
        AddToCartRequest request = new AddToCartRequest();
        CartResponse response = new CartResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.addToCart(any()))
                    .thenReturn(Mono.just(response));

            StepVerifier.create(cartController.addToCart(authentication, request))
                    .expectNext(response)
                    .verifyComplete();

            verify(cartService).addToCart(any());
        }
    }

    @Test
    void addToCart_error() {
        AddToCartRequest request = new AddToCartRequest();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.addToCart(any()))
                    .thenReturn(Mono.error(new RuntimeException("Add failed")));

            StepVerifier.create(cartController.addToCart(authentication, request))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

    // ================= BULK ADD =================

    @Test
    void addItems_success() {
        CartBulkRequest request = new CartBulkRequest();
        CartResponse response = new CartResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.addItems(any()))
                    .thenReturn(Mono.just(response));

            StepVerifier.create(cartController.addItems(authentication, request))
                    .expectNext(response)
                    .verifyComplete();
        }
    }

    @Test
    void addItems_emptyCart() {
        CartBulkRequest request = new CartBulkRequest();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.addItems(any()))
                    .thenReturn(Mono.empty());

            StepVerifier.create(cartController.addItems(authentication, request))
                    .verifyComplete();
        }
    }

    // ================= UPDATE =================

    @Test
    void updateCart_success() {
        UpdateCartRequest request = new UpdateCartRequest();
        CartResponse response = new CartResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.updateCart(any()))
                    .thenReturn(Mono.just(response));

            StepVerifier.create(cartController.updateCart(authentication, request))
                    .expectNext(response)
                    .verifyComplete();
        }
    }

    @Test
    void updateCart_error() {
        UpdateCartRequest request = new UpdateCartRequest();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.updateCart(any()))
                    .thenReturn(Mono.error(new RuntimeException("Update failed")));

            StepVerifier.create(cartController.updateCart(authentication, request))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

    // ================= INCREASE =================

    @Test
    void increaseItems_success() {
        CartBulkRequest request = new CartBulkRequest();
        CartResponse response = new CartResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.increaseItems(any()))
                    .thenReturn(Mono.just(response));

            StepVerifier.create(cartController.increaseItems(authentication, request))
                    .expectNext(response)
                    .verifyComplete();
        }
    }

    // ================= DECREASE =================

    @Test
    void decreaseItems_success() {
        CartBulkRequest request = new CartBulkRequest();
        CartResponse response = new CartResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.decreaseItems(any()))
                    .thenReturn(Mono.just(response));

            StepVerifier.create(cartController.decreaseItems(authentication, request))
                    .expectNext(response)
                    .verifyComplete();
        }
    }

    // ================= REMOVE =================

    @Test
    void removeItem_success() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.removeItem(1L, 10L))
                    .thenReturn(Mono.empty());

            StepVerifier.create(cartController.removeItem(authentication, 10L))
                    .verifyComplete();
        }
    }

    @Test
    void removeItem_error() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.removeItem(1L, 10L))
                    .thenReturn(Mono.error(new RuntimeException("Remove failed")));

            StepVerifier.create(cartController.removeItem(authentication, 10L))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

    // ================= GET =================

    @Test
    void getCart_success() {
        CartResponse response = new CartResponse();

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.getCart(1L))
                    .thenReturn(Mono.just(response));

            StepVerifier.create(cartController.getCart(authentication))
                    .expectNext(response)
                    .verifyComplete();
        }
    }

    @Test
    void getCart_empty() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.getCart(1L))
                    .thenReturn(Mono.empty());

            StepVerifier.create(cartController.getCart(authentication))
                    .verifyComplete();
        }
    }

    @Test
    void getCart_error() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.getUserId(authentication)).thenReturn(1L);

            when(cartService.getCart(1L))
                    .thenReturn(Mono.error(new RuntimeException("Fetch failed")));

            StepVerifier.create(cartController.getCart(authentication))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }
}