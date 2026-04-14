package com.company.user_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Custom Exceptions
    @ExceptionHandler(CustomException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCustomException(CustomException ex) {

        log.error("CustomException: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .status(ex.getStatus())
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(ResponseEntity.status(ex.getStatus()).body(error));
    }

    // Validation Errors (@Valid)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException ex) {

        String errorMessage = ex.getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Validation error: {}", errorMessage);

        ErrorResponse error = ErrorResponse.builder()
                .message(errorMessage)
                .errorCode("VALIDATION_ERROR")
                .status(400)
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccessDenied(AccessDeniedException ex) {

        log.error("Access denied: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message("Access Denied")
                .errorCode("ACCESS_DENIED")
                .status(403)
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(error));
    }

    // Generic Exception
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .message("Internal Server Error")
                .errorCode("INTERNAL_ERROR")
                .status(500)
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(ResponseEntity.status(500).body(error));
    }
}