package com.company.common.controller.cart;

import com.company.common.dto.cart.request.AddToCartRequest;
import com.company.common.dto.cart.request.CartBulkRequest;
import com.company.common.dto.cart.request.UpdateCartRequest;
import com.company.common.dto.cart.response.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    Mono<CartResponse> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request);

    // ================= BULK ADD =================
    @Operation(summary = "Add multiple items", description = "Add multiple products to cart")
    @ApiResponse(responseCode = "200", description = "Items added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping("/bulk/add")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    Mono<CartResponse> addItems(
            Authentication authentication,
            @Valid @RequestBody CartBulkRequest request);


    @Operation(summary = "Update cart item quantity", description = "Update quantity of a product in cart")
    @ApiResponse(responseCode = "200", description = "Cart updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Cart or product not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    Mono<CartResponse> updateCart(
            Authentication authentication,
            @Valid @RequestBody UpdateCartRequest request);

    // ================= BULK UPDATE =================
    @Operation(summary = "Update multiple items", description = "Update multiple cart items")
    @PutMapping("/bulk/update")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    Mono<CartResponse> updateItems(
            Authentication authentication,
            @Valid @RequestBody CartBulkRequest request);

    // ================= INCREASE =================
    @Operation(summary = "Increase quantity", description = "Increase quantity of cart items")
    @PutMapping("/increase")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    Mono<CartResponse> increaseItems(
            Authentication authentication,
            @Valid @RequestBody CartBulkRequest request);


    // ================= DECREASE =================

    @Operation(summary = "Decrease quantity", description = "Decrease quantity or remove items")
    @PutMapping("/decrease")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    Mono<CartResponse> decreaseItems(
            Authentication authentication,
            @Valid @RequestBody CartBulkRequest request);

    // ================= REMOVE =================

    @Operation(summary = "Remove item", description = "Remove product from cart")
    @ApiResponse(responseCode = "204", description = "Item removed successfully")
    @ApiResponse(responseCode = "404", description = "Cart or product not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> removeItem(
            Authentication authentication,
            @RequestParam Long productId);

    // ================= GET =================

    @Operation(summary = "Get cart", description = "Fetch cart details")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
            content = @Content(schema = @Schema(implementation = CartResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cart not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    Mono<CartResponse> getCart(Authentication authentication);
}