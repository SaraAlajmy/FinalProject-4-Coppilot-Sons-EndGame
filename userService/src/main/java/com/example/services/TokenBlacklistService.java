package com.example.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, Map<String, Object>> redisTemplate;

    @Value("${jwt.blacklist.expiration-time}")
    private long blacklistExpirationTime;  // TTL for blacklisted tokens (in seconds)

    public TokenBlacklistService(RedisTemplate<String, Map<String, Object>> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Blacklist a token
    public void blacklist(String token) {
        token = token.startsWith("Bearer ")
                ? token.substring(7)
                : token;
        redisTemplate.opsForValue().set(token, new HashMap<>(), blacklistExpirationTime, TimeUnit.SECONDS);
    }

    // Check if a token is blacklisted
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);  // Check if token is in Redis
    }
}

