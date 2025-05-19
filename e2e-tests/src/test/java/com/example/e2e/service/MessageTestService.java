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
     * @param recipientUserId The ID of the user receiving the message
     * @param messageText The content of the message
     * @return Map containing the response details
     */
    public Map<String, Object> sendDirectMessage(String recipientUserId, String messageText) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("receiverId", recipientUserId);
        messageData.put("content", messageText);
        
        return given()
                .spec(messagingServiceSpec)
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
     */
    public void deleteMessage(String messageId) {
        given()
                .spec(messagingServiceSpec)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/messages/" + messageId)
                .then()
                .statusCode(200);
    }
    
    /**
     * Mark a message as favorite
     * @param messageId Message ID
     */
    public void markAsFavorite(String messageId) {
        given()
                .spec(messagingServiceSpec)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/messages/" + messageId + "/favorite")
                .then()
                .statusCode(200);
    }
    
    /**
     * Unmark a message as favorite
     * @param messageId Message ID
     */
    public void unmarkAsFavorite(String messageId) {
        given()
                .spec(messagingServiceSpec)
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/messages/" + messageId + "/favorite")
                .then()
                .statusCode(200);
    }
    
    /**
     * Get all messages for a specific chat
     * @param chatId Chat ID
     * @return List of messages
     */
    public List<Map<String, Object>> getMessagesByChat(String chatId) {
        return given()
                .spec(messagingServiceSpec)
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
     * @return List of favorite messages
     */
    public List<Map<String, Object>> getFavoriteMessages() {
        return given()
                .spec(messagingServiceSpec)
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
     * @param startDate Start date for the filter
     * @param endDate End date for the filter
     * @return List of filtered messages
     */
    public List<Map<String, Object>> filterByDate(LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        
        return given()
                .spec(messagingServiceSpec)
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
     * @param keyword Search keyword
     * @return List of matching messages
     */
    public List<Map<String, Object>> searchMessages(String keyword) {
        return given()
                .spec(messagingServiceSpec)
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
