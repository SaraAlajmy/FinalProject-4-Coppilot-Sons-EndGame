package com.example.gateway.clients;

import com.example.gateway.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "user-service", configuration = FeignConfig.class,url="http://localhost:8086")
public interface AuthClient {
    @PostMapping("auth/validateToken")
    ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token);
}