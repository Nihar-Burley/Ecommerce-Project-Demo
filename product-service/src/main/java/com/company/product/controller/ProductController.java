package com.company.product.controller;

import com.company.product.dto.request.ProductRequest;
import com.company.product.dto.response.ProductResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "Operations related to products")
public interface ProductController {

    @Operation(summary = "Get all products", description = "Fetch all available products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping
    Flux<ProductResponse> getAllProducts();

    @Operation(summary = "Get product by ID", description = "Fetch product details by ID")
    @ApiResponse(responseCode = "200", description = "Product found",
            content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    Mono<ProductResponse> getProductById(@PathVariable Long id);

    @Operation(summary = "Create product", description = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request);

    @Operation(summary = "Update product", description = "Update an existing product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}")
    Mono<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request);

    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> deleteProduct(@PathVariable Long id);

    @Operation(summary = "Reduce product stock",
            description = "Reduce stock of a product by given quantity")
    @ApiResponse(responseCode = "204", description = "Stock reduced successfully")
    @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}/reduce/{qty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> reduceStock(
            @PathVariable Long id,
            @PathVariable @Min(1) int qty);

    @Operation(summary = "Increase product stock",
            description = "Increase stock of a product by given quantity")
    @ApiResponse(responseCode = "204", description = "Stock increased successfully")
    @ApiResponse(responseCode = "400", description = "Invalid quantity")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}/increase/{qty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> increaseStock(
            @PathVariable Long id,
            @PathVariable @Min(1) int qty);
}