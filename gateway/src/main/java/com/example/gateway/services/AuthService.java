package com.example.gateway.services;

import com.example.gateway.clients.AuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthClient authClient;
    private final RedisTemplate<String, Map<String, Object>> redisTemplate;
    private TokenBlacklistService tokenBlacklistService;

    public AuthService(AuthClient authClient, RedisTemplate<String, Map<String, Object>> redisTemplate, TokenBlacklistService tokenBlacklistService) {
        this.authClient = authClient;
        this.redisTemplate = redisTemplate;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public Mono<ResponseEntity<Map<String, Object>>> validateToken(String token) {


        if (isTokenBlacklisted(token)) {
            log.warn("🚫 Token is blacklisted: {}", token);
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized", "message", "Token is logged out")));
        }

        String redisKey = "token::" + token;

        try {
            Map<String, Object> cached = redisTemplate.opsForValue().get(redisKey);
            if (cached != null) {
                log.info("✅ Cache HIT for token: {}", token);
                return Mono.just(ResponseEntity.ok(cached));
            } else {
                log.info("❌ Cache MISS for token: {}", token);
            }
        } catch (Exception e) {
            log.error("⚠️ Redis GET failed for key '{}': {}", redisKey, e.getMessage(), e);
        }

        return Mono.fromCallable(() -> {
            log.info("🔐 Calling auth service to validate token: {}", token);
            ResponseEntity<Map<String, Object>> response = authClient.validateToken(token);
            log.info("🔍 Token response body: {}", response.getBody());
            Long exp = extractExpiry(response.getBody());
            if (exp != null) {
                long ttl = exp - Instant.now().getEpochSecond();
                if (ttl > 0) {
                    try {
                        redisTemplate.opsForValue().set(redisKey, response.getBody(), Duration.ofSeconds(ttl));
                        log.info("📝 Cached token with TTL {} seconds (exp={}): {}", ttl, exp, redisKey);
                    } catch (Exception e) {
                        log.error("⚠️ Redis SET failed for key '{}': {}", redisKey, e.getMessage(), e);
                    }
                } else {
                    log.warn("⏳ Token already expired or TTL too short (exp={}, now={})", exp, Instant.now().getEpochSecond());
                }
            } else {
                log.warn("⚠️ Could not extract 'exp' from token response body");
            }

            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Long extractExpiry(Map<String, Object> body) {
        if (body != null && body.containsKey("exp")) {
            try {
                return ((Number) body.get("exp")).longValue();
            } catch (Exception e) {
                log.error("❗ Failed to parse 'exp' from token body: {}", e.getMessage(), e);
            }
        }
        return null;
    }

    private boolean isTokenBlacklisted(String token) {
        return tokenBlacklistService.isBlacklisted(token);
    }
}
