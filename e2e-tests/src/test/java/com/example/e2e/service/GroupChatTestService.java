package com.example.e2e.service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Test service for interacting with group chat endpoints
 */
public class GroupChatTestService {
    private final RequestSpecification groupChatServiceSpec;
    private UserTestService userTestService;

    public GroupChatTestService(RequestSpecification groupChatServiceSpec) {
        this.groupChatServiceSpec = groupChatServiceSpec;
    }

    /**
     * Constructor with UserTestService for methods that need to create users
     *
     * @param groupChatServiceSpec RequestSpecification for group chat service
     * @param userTestService      UserTestService for creating test users
     */
    public GroupChatTestService(
        RequestSpecification groupChatServiceSpec,
        UserTestService userTestService
    ) {
        this.groupChatServiceSpec = groupChatServiceSpec;
        this.userTestService = userTestService;
    }

    /**
     * Get all group chats
     *
     * @return List of all group chats
     */
    public List<Map<String, Object>> getAllGroupChats() {
        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .when()
                      .get("/groupChat/allGroupChat")
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(List.class);
    }

    /**
     * Get a group chat by ID
     *
     * @param groupChatId Group chat ID
     * @return Group chat details
     */
    public Map<String, Object> getGroupChatById(String groupChatId) {
        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .when()
                      .get("/groupChat/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Delete a group chat
     *
     * @param groupChatId Group chat ID
     * @param userId      User ID of the requester (must be admin)
     * @return Result message
     */
    public String deleteGroupChat(String groupChatId, String userId) {
        return given().spec(groupChatServiceSpec)
                      .header("userId", userId)
                      .contentType(ContentType.JSON)
                      .when()
                      .delete("/groupChat/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .asString();
    }

    /**
     * Create a new group chat
     *
     * @param name        Group chat name
     * @param description Group chat description
     * @param memberIds   List of member IDs to include
     * @return Created group chat
     */
    public Map<String, Object> createGroupChat(
        String name,
        String description,
        List<String> memberIds
    ) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("description", description);
        requestBody.put("members", memberIds);

        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .body(requestBody)
                      .when()
                      .post("/groupChat/addGroupChat")
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Update a group chat
     *
     * @param groupChatId Group chat ID
     * @param name        New name (optional)
     * @param description New description (optional)
     * @return Updated group chat
     */
    public Map<String, Object> updateGroupChat(
        String groupChatId,
        String name,
        String description
    ) {
        Map<String, Object> requestBody = new HashMap<>();
        if (name != null) {
            requestBody.put("name", name);
        }
        if (description != null) {
            requestBody.put("description", description);
        }

        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .body(requestBody)
                      .when()
                      .put("/groupChat/update/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Enable admin-only messages in a group chat
     *
     * @param groupChatId Group chat ID
     * @return Updated group chat
     */
    public Map<String, Object> activateAdminOnlyMessages(String groupChatId) {
        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .when()
                      .put("/groupChat/activateAdminOnlyMessages/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Disable admin-only messages in a group chat
     *
     * @param groupChatId Group chat ID
     * @return Updated group chat
     */
    public Map<String, Object> deactivateAdminOnlyMessages(String groupChatId) {
        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .when()
                      .put("/groupChat/unactivateAdminOnlyMessages/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Add a member to a group chat
     *
     * @param groupChatId Group chat ID
     * @param newMemberId User ID of the new member
     * @return Updated group chat
     */
    public Map<String, Object> addMember(
        String groupChatId,
        String newMemberId
    ) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("memberId", newMemberId);

        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .body(requestBody)
                      .when()
                      .put("/groupChat/addMember/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Remove a member from a group chat
     *
     * @param groupChatId      Group chat ID
     * @param memberIdToRemove User ID of the member to remove
     * @return Updated group chat
     */
    public Map<String, Object> removeMember(
        String groupChatId,
        String memberIdToRemove
    ) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("memberId", memberIdToRemove);

        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .body(requestBody)
                      .when()
                      .put("/groupChat/removeMember/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Make a member an admin of a group chat
     *
     * @param groupChatId Group chat ID
     * @param newAdminId  User ID to make admin
     * @return Updated group chat
     */
    public Map<String, Object> makeAdmin(
        String groupChatId,
        String newAdminId
    ) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("memberId", newAdminId);

        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .body(requestBody)
                      .when()
                      .put("/groupChat/makeAdmin/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Remove admin status from a member
     *
     * @param groupChatId     Group chat ID
     * @param adminToRemoveId User ID to remove admin status from
     * @return Updated group chat
     */
    public Map<String, Object> removeAdmin(
        String groupChatId,
        String adminToRemoveId
    ) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("memberId", adminToRemoveId);

        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .body(requestBody)
                      .when()
                      .put("/groupChat/removeAdmin/" + groupChatId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(Map.class);
    }

    /**
     * Get all group chats for a specific member
     *
     * @param memberId Member ID
     * @return List of group chats the member belongs to
     */
    public List<Map<String, Object>> getGroupChatsByMemberId(String memberId) {
        return given().spec(groupChatServiceSpec)
                      .contentType(ContentType.JSON)
                      .when()
                      .get("/groupChat/getGroupChatByMemberId/" + memberId)
                      .then()
                      .statusCode(200)
                      .extract()
                      .body()
                      .as(List.class);
    }

    /**
     * Creates a random group chat with specific creator and member emails
     *
     * @param memberEmails List of emails for the member users
     * @return GroupChatTestResult containing the group chat and all user information
     * @throws IllegalStateException if UserTestService is not set
     */
    public GroupChatTestResult createRandomGroupChatWithUsers(
        List<String> memberEmails
    ) {
        if (userTestService == null) {
            throw new IllegalStateException(
                "UserTestService not initialized. Use constructor with UserTestService parameter.");
        }

        // Create members with the specified emails
        List<Map<String, Object>> members = new ArrayList<>();
        List<String> memberIds = new ArrayList<>();

        for (String email : memberEmails) {
            Map<String, Object> member = userTestService.registerUserWithEmail(email);
            members.add(member);
            memberIds.add(member.get("id").toString());
        }

        // Generate random group name and description
        String randomGroupName = "Test Group " + System.currentTimeMillis();
        String randomDescription =
            "Auto-generated test group with " + memberEmails.size() + " members";

        // Create the group chat
        Map<String, Object> groupChat =
            createGroupChat(randomGroupName, randomDescription, memberIds);

        // Return result using the record
        return GroupChatTestResult.builder()
                                  .groupChat(groupChat)
                                  .members(members)
                                  .build();
    }

    /**
     * Record to hold the result of creating a random group chat with users
     * Provides a structured and type-safe way to return multiple values
     */
    @Builder
    public record GroupChatTestResult(
        Map<String, Object> groupChat,
        List<Map<String, Object>> members
    ) {
    }
}
