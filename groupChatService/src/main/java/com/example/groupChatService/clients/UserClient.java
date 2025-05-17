package com.example.groupChatService.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {
    @GetMapping("/user/bulk-get-ids-by-usernames")
    Map<String, String> getUsersIdsByUsernames(@RequestParam List<String> usernames);
}
