package com.company.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {


    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        ErrorResponse error;

        if (ex instanceof CustomException) {

            CustomException customEx = (CustomException) ex;

            log.error("CustomException: {}", customEx.getMessage());

            error = ErrorResponse.builder()
                    .message(customEx.getMessage())
                    .errorCode(customEx.getErrorCode())
                    .status(customEx.getStatus())
                    .timestamp(LocalDateTime.now())
                    .build();

            exchange.getResponse().setStatusCode(HttpStatus.valueOf(customEx.getStatus()));

        } else {

            log.error("Unexpected error: {}", ex.getMessage(), ex);

            error = ErrorResponse.builder()
                    .message("Internal Server Error")
                    .errorCode("INTERNAL_ERROR")
                    .status(500)
                    .timestamp(LocalDateTime.now())
                    .build();

            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(error);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(Mono.just(buffer));

        } catch (Exception e) {
            log.error("Error writing response", e);
            return Mono.error(e);
        }
    }
}