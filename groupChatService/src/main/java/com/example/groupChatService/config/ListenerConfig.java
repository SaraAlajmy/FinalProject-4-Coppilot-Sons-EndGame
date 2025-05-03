package com.example.groupChatService.config;

import com.example.groupChatService.services.GroupMessageService;
import com.example.groupChatService.services.NotificationListener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {

    @Bean
    public ApplicationRunner bindListeners(GroupMessageService messageService,
                                           NotificationListener notificationListener) {
        return args -> {
            messageService.addListener(notificationListener);
            System.out.println("✅ NotificationListener registered successfully");
        };
    }
}
