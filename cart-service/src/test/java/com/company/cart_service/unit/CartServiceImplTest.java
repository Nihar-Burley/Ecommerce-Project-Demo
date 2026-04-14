package com.company.cart_service.unit;

import com.company.cart_service.client.ProductClient;
import com.company.common.dto.cart.request.AddToCartRequest;
import com.company.common.dto.cart.request.UpdateCartRequest;
import com.company.common.dto.cart.response.ProductResponse;
import com.company.cart_service.exception.CartNotFoundException;
import com.company.cart_service.exception.InsufficientStockException;
import com.company.cart_service.exception.ProductNotFoundException;
import com.company.cart_service.model.Cart;
import com.company.cart_service.model.CartItem;
import com.company.cart_service.repository.CartItemRepository;
import com.company.cart_service.repository.CartRepository;
import com.company.cart_service.service.impl.CartServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartServiceImpl service;

    private AddToCartRequest addRequest;
    private ProductResponse product;
    private Cart cart;

    @BeforeEach
    void setup() {
        addRequest = new AddToCartRequest();
        addRequest.setUserId(1L);
        addRequest.setProductId(1L);
        addRequest.setQuantity(2);

        product = new ProductResponse();
        product.setId(1L);
        product.setStock(10);
        product.setName("iPhone");

        cart = Cart.builder().id(1L).userId(1L).build();
    }


    @Test
    void shouldAddToCartSuccessfully() {

        when(productClient.getProduct(1L)).thenReturn(Mono.just(product));
        when(productClient.reduceStock(anyLong(), anyInt())).thenReturn(Mono.empty());

        when(cartRepository.findByUserId(1L)).thenReturn(Mono.just(cart));
        when(cartRepository.save(any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Mono.empty());
        when(cartItemRepository.save(any()))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(cartItemRepository.findByCartId(1L))
                .thenReturn(Flux.empty());

        StepVerifier.create(service.addToCart(addRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenProductNotFound() {

        when(productClient.getProduct(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.addToCart(addRequest))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void shouldThrowWhenStockInsufficient() {

        product.setStock(1);
        when(productClient.getProduct(1L)).thenReturn(Mono.just(product));

        StepVerifier.create(service.addToCart(addRequest))
                .expectError(InsufficientStockException.class)
                .verify();
    }

    @Test
    void shouldThrowWhenCartNotFound() {

        UpdateCartRequest request = new UpdateCartRequest();
        request.setUserId(1L);
        request.setProductId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.updateCart(request))
                .expectError(CartNotFoundException.class)
                .verify();
    }

    @Test
    void shouldThrowWhenRemovingNonExistingItem() {

        when(cartRepository.findByUserId(1L)).thenReturn(Mono.just(cart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.removeItem(1L, 1L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void shouldRemoveItemSuccessfully() {

        CartItem item = CartItem.builder()
                .cartId(1L)
                .productId(1L)
                .quantity(2)
                .build();

        when(cartRepository.findByUserId(1L)).thenReturn(Mono.just(cart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(Mono.just(item));

        when(productClient.increaseStock(1L, 2)).thenReturn(Mono.empty());
        when(cartItemRepository.deleteByCartIdAndProductId(1L, 1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.removeItem(1L, 1L))
                .verifyComplete();
    }
}