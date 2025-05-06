package com.example.gateway.filter;

import com.example.gateway.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AuthenticationFilter implements GlobalFilter {

    @Autowired
    @Lazy
    private AuthService authService;
    private static final Set<String> WHITELIST = Set.of(
            "/auth/login",
            "/auth/register",
            "/auth/validateToken",
            "/auth/refresh",
            "/auth/forgotPassword",
            "/auth/resetPassword"

    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip authentication if path is whitelisted
        if (WHITELIST.contains(path)) {
            return chain.filter(exchange);
        }
        String token = extractToken(exchange);
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return authService.validateToken(token)
                .flatMap(response -> {
                    Map<String, Object> body = response.getBody();
                    if (body == null || !body.containsKey("userId")) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(builder -> builder
                                    .header("userId", body.get("userId").toString())
                                    .header("username", body.get("username").toString()))
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(e -> {
                    System.out.print(e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    private String extractToken(ServerWebExchange exchange) {
        List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
        if (!authHeaders.isEmpty() && authHeaders.get(0).startsWith("Bearer ")) {
            return authHeaders.get(0).substring(7);
        }
        return null;
    }
}
