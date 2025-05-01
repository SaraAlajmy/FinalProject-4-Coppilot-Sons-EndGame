package com.example.chat_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {

    @GetMapping("/user/isAuthenticated/{userId}")
    boolean isAuthenticated(@PathVariable("userId") String userId);

    @GetMapping("/user/isBlocked")
    boolean isBlocked(@RequestParam("senderId") String senderId, @RequestParam("receiverId") String receiverId);
}
