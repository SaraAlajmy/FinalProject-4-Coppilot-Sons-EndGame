package com.example.gateway.services;

import com.example.gateway.clients.AuthClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
public class AuthService {

    AuthClient authClient;
    public AuthService(AuthClient authClient) {
        this.authClient = authClient;
    }

   // @Cacheable(value = "token", key = "#token")
    public Mono<ResponseEntity<Map<String, Object>>> validateToken(String token) {
        return Mono.fromCallable(() -> authClient.validateToken(token))
                .subscribeOn(Schedulers.boundedElastic()); // Offload blocking
    }
}
