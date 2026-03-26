package com.company.product.controller;

import com.company.product.dto.request.ProductRequest;
import com.company.product.dto.response.ProductResponse;
import com.company.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product API", description = "Operations related to products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products", description = "Fetch all available products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping
    public Flux<ProductResponse> getAllProducts() {
        log.info("API: Fetch all products");
        return productService.getAllProducts();
    }

    @Operation(summary = "Get product by ID", description = "Fetch product details by ID")
    @ApiResponse(responseCode = "200", description = "Product found",
            content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    public Mono<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("API: Fetch product by id: {}", id);
        return productService.getProductById(id);
    }

    @Operation(summary = "Create product", description = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        log.info("API: Create product: {}", request.getName());
        return productService.createProduct(request);
    }

    @Operation(summary = "Update product", description = "Update an existing product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}")
    public Mono<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        log.info("API: Update product with id: {}", id);
        return productService.updateProduct(id, request);
    }

    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        log.info("API: Delete product with id: {}", id);
        return productService.deleteProduct(id);
    }

    @Operation(summary = "Reduce product stock",
            description = "Reduce stock of a product by given quantity")
    @ApiResponse(responseCode = "204", description = "Stock reduced successfully")
    @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}/reduce/{qty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> reduceStock(
            @PathVariable Long id,
            @PathVariable @Min(1) int qty) {

        log.info("API: Reduce stock | productId={} qty={}", id, qty);

        return productService.reduceStock(id, qty);
    }

    @Operation(summary = "Increase product stock",
            description = "Increase stock of a product by given quantity")
    @ApiResponse(responseCode = "204", description = "Stock increased successfully")
    @ApiResponse(responseCode = "400", description = "Invalid quantity")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}/increase/{qty}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> increaseStock(
            @PathVariable Long id,
            @PathVariable @Min(1) int qty) {

        log.info("API: Increase stock | productId={} qty={}", id, qty);

        return productService.increaseStock(id, qty);
    }



}