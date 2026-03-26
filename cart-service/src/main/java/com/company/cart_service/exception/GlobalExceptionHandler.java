package com.company.cart_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Product Not Found
    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleProductNotFound(ProductNotFoundException ex) {

        log.error("ProductNotFoundException: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    //Cart Not Found
    @ExceptionHandler(CartNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCartNotFound(CartNotFoundException ex) {

        log.error("CartNotFoundException: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), "CART_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    //Insufficient Stock
    @ExceptionHandler(InsufficientStockException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleStock(InsufficientStockException ex) {

        log.error("InsufficientStockException: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), "INSUFFICIENT_STOCK", HttpStatus.BAD_REQUEST);
    }

    //Bad Request
    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(BadRequestException ex) {

        log.error("BadRequestException: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }

    //Validation Errors
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException ex) {

        String errorMessage = ex.getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("Validation error");

        log.error("ValidationException: {}", errorMessage);

        return buildResponse(errorMessage, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    //Generic Exception
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {

        log.error("Unhandled Exception: {}", ex.getMessage(), ex);

        return buildResponse("Something went wrong",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ================= COMMON BUILDER =================

    private Mono<ResponseEntity<ErrorResponse>> buildResponse(String message,
                                                              String code,
                                                              HttpStatus status) {

        ErrorResponse error = ErrorResponse.builder()
                .message(message)
                .errorCode(code)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(new ResponseEntity<>(error, status));
    }
}