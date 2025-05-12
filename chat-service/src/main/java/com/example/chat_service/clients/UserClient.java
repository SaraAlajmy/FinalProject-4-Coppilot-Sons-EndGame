package com.example.chat_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user-service",  url = "${user-service.url}")
public interface UserClient {

    @GetMapping("/user/isAuthenticated/{userId}")
    boolean isAuthenticated(@PathVariable("userId") String userId);

    @GetMapping("/user/isBlocked/{blockerId}/{blockedId}")
    public boolean isBlocked(@PathVariable UUID blockerId, @PathVariable UUID blockedId);

}

