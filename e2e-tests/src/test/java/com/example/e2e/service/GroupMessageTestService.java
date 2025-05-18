package com.example.e2e.service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Test service for interacting with group message endpoints
 */
public class GroupMessageTestService {
    private final RequestSpecification groupChatServiceSpec;

    public GroupMessageTestService(RequestSpecification groupChatServiceSpec) {
        this.groupChatServiceSpec = groupChatServiceSpec;
    }

    /**
     * Get all group messages
     *
     * @return List of all group messages
     */
    public List<Map<String, Object>> getAllGroupMessages() {
        return given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .get("/groupMessage/allMessages")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(List.class);
    }

    /**
     * Get all unarchived messages for a specific group
     *
     * @param groupId Group ID
     * @return List of group messages
     */
    public List<Map<String, Object>> getGroupMessages(String groupId) {
        return given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .get("/groupMessage/" + groupId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(List.class);
    }

    /**
     * Send a new message to a group chat
     *
     * @param groupId    Group ID
     * @param content    Message content
     * @return Created message
     */
    public Map<String, Object> sendGroupMessage(
        String groupId,
        String content
    ) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", content);

        return given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/groupMessage/send/" + groupId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(Map.class);
    }

    /**
     * Add a new group message (alternative to send)
     *
     * @param groupMessage Full group message object
     * @return Created message
     */
    public Map<String, Object> addGroupMessage(Map<String, Object> groupMessage) {
        return given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .body(groupMessage)
            .when()
            .post("/groupMessage/")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(Map.class);
    }

    /**
     * Edit a group message
     *
     * @param messageId  Message ID to edit
     * @param newContent New message content
     * @return Updated message
     */
    public Map<String, Object> editGroupMessage(String messageId, String newContent) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("content", newContent);

        return given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .put("/groupMessage/" + messageId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(Map.class);
    }

    /**
     * Delete a group message
     *
     * @param messageId Message ID to delete
     */
    public void deleteGroupMessage(String messageId) {
        given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .delete("/groupMessage/" + messageId)
            .then()
            .statusCode(200);
    }

    /**
     * Archive a group message
     *
     * @param messageId Message ID to archive
     */
    public void archiveGroupMessage(String messageId) {
        given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .put("/groupMessage/archive/" + messageId)
            .then()
            .statusCode(200);
    }

    /**
     * Unarchive a group message
     *
     * @param messageId Message ID to unarchive
     */
    public void unarchiveGroupMessage(String messageId) {
        given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .put("/groupMessage/unarchive/" + messageId)
            .then()
            .statusCode(200);
    }

    /**
     * Get all archived messages for a group
     *
     * @param groupId Group ID
     * @return List of archived messages
     */
    public List<Map<String, Object>> getArchivedGroupMessages(String groupId) {
        return given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .get("/groupMessage/archived/" + groupId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(List.class);
    }

    /**
     * Filter group messages by sender ID
     *
     * @param groupId  Group ID
     * @param senderId Sender ID to filter by
     * @return List of filtered messages
     */
    public List<Map<String, Object>> filterGroupMessagesBySenderId(
        String groupId,
        String senderId
    ) {
        return given()
            .spec(groupChatServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .get("/groupMessage/filter/" + groupId + "/" + senderId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(List.class);
    }
}
