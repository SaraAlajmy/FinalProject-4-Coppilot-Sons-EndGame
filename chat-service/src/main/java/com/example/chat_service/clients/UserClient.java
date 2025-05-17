package com.example.chat_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user-service",  url = "${userservice.url}")
public interface UserClient {

    @GetMapping("/user/isAuthenticated/{userId}")
    boolean isAuthenticated(@PathVariable("userId") String userId);

    @GetMapping("/user/areBlocking/{firstUser}/{secondUser}")
    public ResponseEntity<Boolean> areBlocking(@PathVariable UUID firstUser, @PathVariable UUID secondUser);
}

