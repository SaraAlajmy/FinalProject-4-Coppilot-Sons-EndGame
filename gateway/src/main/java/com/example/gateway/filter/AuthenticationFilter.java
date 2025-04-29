//package com.example.gateway.filter;
//
//import com.example.gateway.clients.AuthClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class AuthenticationFilter implements GlobalFilter {
//
//    @Autowired
//    @Lazy
//    private AuthClient authClient;
//
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String token = extractToken(exchange);
//
//        if (token == null) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        try {
//            ResponseEntity<Map<String, Object>> response = authClient.validateToken(token);
//            // Optionally store claims in headers
//            exchange.getRequest().mutate()
//                    .header("userId", response.getBody().get("userId").toString())
//                    .header("username", response.getBody().get("username").toString())
//                    .build();
//        } catch (Exception e) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        return chain.filter(exchange);
//    }
//
//    private String extractToken(ServerWebExchange exchange) {
//        List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
//        if (!authHeaders.isEmpty() && authHeaders.get(0).startsWith("Bearer ")) {
//            return authHeaders.get(0).substring(7);
//        }
//        return null;
//    }
//}
