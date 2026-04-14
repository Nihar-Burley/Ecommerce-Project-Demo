package com.company.gateway.filter;

import com.company.gateway.config.JwtUtil;
import com.company.gateway.exception.CustomException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // 🔓 Public endpoints (NO JWT required)
        if (path.contains("/login") || path.contains("/register")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new CustomException("Missing Authorization Header", "AUTH_HEADER_MISSING", 401));
        }

        String token = authHeader.substring(7);

        Claims claims = jwtUtil.validateAndGetClaims(token);

        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        Long userId = claims.get("userId", Long.class);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Email", email)
                .header("X-User-Role", role)
                .header("X-User-Id", String.valueOf(userId))
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}