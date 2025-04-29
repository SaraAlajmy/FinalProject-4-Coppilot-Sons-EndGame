package com.example.gateway;

import com.example.gateway.clients.AuthClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
public class LService {
    AuthClient authClient;
    public LService(AuthClient authClient) {
        this.authClient = authClient;
    }
    public Mono<ResponseEntity<Map<String, Object>>> validateToken(String token) {
        return Mono.fromCallable(() -> authClient.validateToken(token))
                .subscribeOn(Schedulers.boundedElastic()); // Offload blocking
    }
}
