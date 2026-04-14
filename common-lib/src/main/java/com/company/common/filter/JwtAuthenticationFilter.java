package com.company.common.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;

import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String role = request.getHeaders().getFirst("X-User-Role");
        String userId = request.getHeaders().getFirst("X-User-Id");
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (role != null && userId != null) {

            String finalRole = role.toUpperCase().startsWith("ROLE_")
                    ? role.toUpperCase()
                    : "ROLE_" + role.toUpperCase();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(finalRole))
                    );

            return chain.filter(exchange)
                    .contextWrite(ctx -> {
                        if (authHeader != null) {
                            return ctx.put(HttpHeaders.AUTHORIZATION, authHeader);
                        }
                        return ctx;
                    })
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        return chain.filter(exchange);
    }
}