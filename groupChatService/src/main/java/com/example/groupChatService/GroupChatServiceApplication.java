package com.example.groupChatService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication(
	scanBasePackages = {
		"com.example.groupChatService",
		"org.example.shared"
	}
)
@EnableMongoAuditing
@EnableFeignClients
public class GroupChatServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(GroupChatServiceApplication.class, args);
	}

}
