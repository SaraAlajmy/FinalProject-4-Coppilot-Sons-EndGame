package org.example.notificationservice.clients;

import org.example.notificationservice.models.NotificationSettingsDTO;
import org.example.notificationservice.models.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

//TODO: Uncomment and implement the methods and use in NotificationService when user-service is ready

//    @GetMapping("/users/{userId}")
//    UserDTO getUserById(@PathVariable("userId") String userId);
//
//    @GetMapping("/users/{userId}/notification-settings")
//    NotificationSettingsDTO getUserNotificationSettings(@PathVariable("userId") String userId);


}
