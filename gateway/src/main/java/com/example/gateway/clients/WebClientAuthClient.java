package com.example.gateway.clients;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class WebClientAuthClient  {

    private final WebClient webClient;

    public WebClientAuthClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("${user-service.url}")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public Mono<ResponseEntity<Map<String, Object>>> validateToken(String token) {
        return webClient.post()
                .uri("/auth/validateToken")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}