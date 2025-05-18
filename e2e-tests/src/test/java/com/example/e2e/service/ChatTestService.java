package com.example.e2e.service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Test service for interacting with chat service endpoints
 */
public class ChatTestService {
    private final RequestSpecification chatServiceSpec;
    private UserTestService userTestService;

    public ChatTestService(RequestSpecification chatServiceSpec) {
        this.chatServiceSpec = chatServiceSpec;
    }

    /**
     * Constructor with UserTestService for methods that need to create users
     *
     * @param chatServiceSpec RequestSpecification for chat service
     * @param userTestService UserTestService for creating test users
     */
    public ChatTestService(RequestSpecification chatServiceSpec, UserTestService userTestService) {
        this.chatServiceSpec = chatServiceSpec;
        this.userTestService = userTestService;
    }

    /**
     * Creates a new chat between two users or returns an existing one
     *
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Chat details
     */
    public Map<String, Object> createOrGetChat(String userId1, String userId2) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId1", userId1);
        requestBody.put("userId2", userId2);

        return given()
            .spec(chatServiceSpec)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/chats")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(Map.class);
    }

    /**
     * Creates a random chat between two users with specified emails
     * 
     * @param email1 Email for the first user
     * @param email2 Email for the second user
     * @return ChatTestResult containing the chat and user information
     * @throws IllegalStateException if UserTestService is not set
     */
    public ChatTestResult createRandomChat(String email1, String email2) {
        if (userTestService == null) {
            throw new IllegalStateException(
                "UserTestService not initialized. Use constructor with UserTestService parameter.");
        }
    
        // Register two users with the specified emails
        Map<String, Object> user1 = userTestService.registerUserWithEmail(email1);
        Map<String, Object> user2 = userTestService.registerUserWithEmail(email2);
    
        String user1Id = user1.get("id").toString();
        String user2Id = user2.get("id").toString();
    
        // Create a chat between these two users
        Map<String, Object> chat = createOrGetChat(user1Id, user2Id);
    
        // Return result using the record with builder
        return ChatTestResult.builder()
            .chat(chat)
            .user1(user1)
            .user2(user2)
            .build();
    }

    /**
     * Retrieves all chats for a specific user
     *
     * @param userId User ID
     * @return List of chats
     */
    public List<Map<String, Object>> getChatsForUser(String userId) {
        return given()
            .spec(chatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .get("/chats/user/" + userId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(List.class);
    }

    /**
     * Get the latest messages from a chat after a specific message ID
     *
     * @param chatId        Chat ID
     * @param lastMessageId Last message ID seen by client
     * @return List of new messages
     */
    public List<Map<String, Object>> getLatestMessages(String chatId, String lastMessageId) {
        return given()
            .spec(chatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .get("/chats/" + chatId + "/messages/" + lastMessageId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(List.class);
    }

    /**
     * Record to hold the result of creating a random chat with users
     * Provides a structured and type-safe way to return chat and user information
     */
    @Builder
    public record ChatTestResult(
        Map<String, Object> chat,
        Map<String, Object> user1,
        Map<String, Object> user2
    ) {}
}
