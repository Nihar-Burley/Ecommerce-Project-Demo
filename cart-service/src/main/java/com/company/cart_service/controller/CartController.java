package com.company.cart_service.controller;

import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.request.UpdateCartRequest;
import com.company.cart_service.dto.response.CartResponse;
import com.company.cart_service.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart API", description = "Operations related to Cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add item to cart", description = "Add a product to user's cart")
    @ApiResponse(responseCode = "200", description = "Item added to cart successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @PostMapping("/add")
    public Mono<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        log.info("Received request to add item to cart for user {}", request.getUserId());
        return cartService.addToCart(request);
    }


    @Operation(summary = "Update cart item quantity", description = "Update quantity of a product in cart")
    @ApiResponse(responseCode = "200", description = "Cart updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Cart or product not found")
    @PutMapping("/update")
    public Mono<CartResponse> updateCart(
            @Valid @RequestBody UpdateCartRequest request) {

        log.info("Received request to update cart for user {}", request.getUserId());
        return cartService.updateCart(request);
    }

    @Operation(summary = "Remove item from cart", description = "Remove a specific product from user's cart")
    @ApiResponse(responseCode = "204", description = "Item removed successfully")
    @ApiResponse(responseCode = "404", description = "Cart or product not found")
    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeItem(
            @RequestParam String userId,
            @RequestParam Long productId) {

        log.info("Received request to remove item {} from user {}", productId, userId);
        return cartService.removeItem(userId, productId);
    }


    @Operation(summary = "Get cart by userId", description = "Fetch cart details for a specific user")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
            content = @Content(schema = @Schema(implementation = CartResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cart not found")
    @GetMapping("/{userId}")
    public Mono<CartResponse> getCart(@PathVariable String userId) {

        log.info("Fetching cart for user {}", userId);
        return cartService.getCart(userId);
    }
}