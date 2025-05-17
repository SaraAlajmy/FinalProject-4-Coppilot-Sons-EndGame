package com.example.gateway.clients;

import com.example.gateway.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;


@Component
public class WebClientAuthClient  {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final WebClient webClient;

    @Autowired
    public WebClientAuthClient(WebClient.Builder webClientBuilder, @Value("${userservice.url}") String baseUrl) {
        log.info("WebClientAuthClient initialized with userServiceUrl: {}", baseUrl);
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public Mono<ResponseEntity<Map<String, Object>>> validateToken(String token) {
        log.info("WebClientAuthClient initialized with userServiceUrl: {}", token);
        return webClient.post()
                .uri("/auth/validateToken")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}