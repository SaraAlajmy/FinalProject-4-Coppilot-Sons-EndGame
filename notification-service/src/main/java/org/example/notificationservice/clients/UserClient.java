package org.example.notificationservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {
    @GetMapping("/user/getUserEmail/{userId}")
    ResponseEntity<String> getUserEmailById(@PathVariable("userId") String userId);
}
