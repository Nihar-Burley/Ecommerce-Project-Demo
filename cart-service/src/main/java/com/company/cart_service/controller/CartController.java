package com.company.cart_service.controller;

import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.request.CartBulkRequest;
import com.company.cart_service.dto.request.UpdateCartRequest;
import com.company.cart_service.dto.response.CartResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequestMapping("/api/v1/cart")
@Tag(name = "Cart API", description = "Operations related to Cart")
public interface CartController {

    @Operation(summary = "Add item to cart", description = "Add a product to user's cart")
    @ApiResponse(responseCode = "200", description = "Item added to cart successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping("/add")
    Mono<CartResponse> addToCart(
            @Parameter(description = "User ID", required = true)
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "User Role", required = true)
            @RequestHeader("X-User-Role") String role,

            @Valid @RequestBody AddToCartRequest request);

    // ================= BULK ADD =================

    @Operation(summary = "Add multiple items", description = "Add multiple products to cart")
    @ApiResponse(responseCode = "200", description = "Items added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping("/bulk/add")
    Mono<CartResponse> addItems(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CartBulkRequest request);


    @Operation(summary = "Update cart item quantity", description = "Update quantity of a product in cart")
    @ApiResponse(responseCode = "200", description = "Cart updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Cart or product not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PutMapping("/update")
    Mono<CartResponse> updateCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateCartRequest request);

    // ================= BULK UPDATE =================

    @Operation(summary = "Update multiple items", description = "Update multiple cart items")
    @PutMapping("/bulk/update")
    Mono<CartResponse> updateItems(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CartBulkRequest request);

    // ================= INCREASE =================

    @Operation(summary = "Increase quantity", description = "Increase quantity of cart items")
    @PutMapping("/increase")
    Mono<CartResponse> increaseItems(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CartBulkRequest request);

    // ================= DECREASE =================

    @Operation(summary = "Decrease quantity", description = "Decrease quantity or remove items")
    @PutMapping("/decrease")
    Mono<CartResponse> decreaseItems(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CartBulkRequest request);

    // ================= REMOVE =================

    @Operation(summary = "Remove item", description = "Remove product from cart")
    @ApiResponse(responseCode = "204", description = "Item removed successfully")
    @ApiResponse(responseCode = "404", description = "Cart or product not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> removeItem(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam Long productId);

    // ================= GET =================

    @Operation(summary = "Get cart", description = "Fetch cart details")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
            content = @Content(schema = @Schema(implementation = CartResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cart not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping
    Mono<CartResponse> getCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role);
}