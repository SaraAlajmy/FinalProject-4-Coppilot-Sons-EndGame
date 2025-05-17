package com.example.e2e.service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Test service for interacting with message-related endpoints
 */
public class MessageTestService {
    private final RequestSpecification messagingServiceSpec;

    public MessageTestService(RequestSpecification messagingServiceSpec) {
        this.messagingServiceSpec = messagingServiceSpec;
    }

    /**
     * Sends a direct message from one user to another, which should trigger a notification
     * @param senderUserId The ID of the user sending the message
     * @param senderUsername The username of the sender
     * @param recipientUserId The ID of the user receiving the message
     * @param messageText The content of the message
     * @return Map containing the response details
     */
    public Map<String, Object> sendDirectMessage(String senderUserId, String senderUsername, String recipientUserId, String messageText) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", senderUserId);
        messageData.put("receiverId", recipientUserId);
        messageData.put("content", messageText);
        
        return given()
                .spec(messagingServiceSpec)
                .header("userName", senderUsername)
                .header("userId", senderUserId)
                .contentType(ContentType.JSON)
                .body(messageData)
                .when()
                .post("/api/messages/send")
                .then()
                .statusCode(201)
                .extract()
                .body()
                .as(Map.class);
    }
    
    /**
     * Delete a message
     * @param messageId Message ID to delete
     * @param userId User ID of the message owner
     */
    public void deleteMessage(String messageId, String userId) {
        given()
                .spec(messagingServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/messages/" + messageId)
                .then()
                .statusCode(200);
    }
    
    /**
     * Mark a message as favorite
     * @param messageId Message ID 
     * @param userId User ID marking the message as favorite
     */
    public void markAsFavorite(String messageId, String userId) {
        given()
                .spec(messagingServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/messages/" + messageId + "/favorite")
                .then()
                .statusCode(200);
    }
    
    /**
     * Unmark a message as favorite
     * @param messageId Message ID 
     * @param userId User ID unmarking the message as favorite
     */
    public void unmarkAsFavorite(String messageId, String userId) {
        given()
                .spec(messagingServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/messages/" + messageId + "/favorite")
                .then()
                .statusCode(200);
    }
    
    /**
     * Get all messages for a specific chat
     * @param chatId Chat ID
     * @param userId User ID requesting the messages
     * @return List of messages
     */
    public List<Map<String, Object>> getMessagesByChat(String chatId, String userId) {
        return given()
                .spec(messagingServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/messages/by-chat/" + chatId)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(List.class);
    }
    
    /**
     * Get all favorite messages for a user
     * @param userId User ID 
     * @return List of favorite messages
     */
    public List<Map<String, Object>> getFavoriteMessages(String userId) {
        return given()
                .spec(messagingServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/messages/favorites")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(List.class);
    }
    
    /**
     * Filter messages by date range
     * @param userId User ID
     * @param startDate Start date for the filter
     * @param endDate End date for the filter
     * @return List of filtered messages
     */
    public List<Map<String, Object>> filterByDate(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        
        return given()
                .spec(messagingServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .queryParam("startDate", startDateStr)
                .queryParam("endDate", endDateStr)
                .when()
                .get("/api/messages/filter")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(List.class);
    }
    
    /**
     * Search messages by keyword
     * @param userId User ID
     * @param keyword Search keyword
     * @return List of matching messages
     */
    public List<Map<String, Object>> searchMessages(String userId, String keyword) {
        return given()
                .spec(messagingServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .queryParam("keyword", keyword)
                .when()
                .get("/api/messages/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(List.class);
    }
}
