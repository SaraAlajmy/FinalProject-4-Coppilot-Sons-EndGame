package com.example.e2e.service;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

/**
 * Service class for user-related test operations.
 * Centralizes common test operations like user creation and deletion.
 */
public class UserTestService {

    private static final Logger logger = LoggerFactory.getLogger(UserTestService.class);
    private final RequestSpecification userServiceSpec;
    private final List<UUID> createdUserIds = new ArrayList<>();
    private Map<UUID, String> userTokens = new HashMap<>();

    public UserTestService(RequestSpecification userServiceSpec) {
        this.userServiceSpec = userServiceSpec;
    }

    /**
     * Registers a new test user with random data
     *
     * @return Map containing the created user data including ID
     */
    public Map<String, Object> registerUser() {
        Map<String, Object> userData = generateRandomUserData();
        return registerUser(userData);
    }

    public Map<String, Object> registerUserWithEmail(String email) {
        Map<String, Object> userData = generateRandomUserData();
        userData.put("email", email);
        return registerUser(userData);
    }

    /**
     * Registers a user with the provided data
     *
     * @param userData The user data to register
     * @return Map containing the created user data including ID
     */
    public Map<String, Object> registerUser(Map<String, Object> userData) {
        Response response = given()
            .spec(userServiceSpec)
            .contentType(ContentType.JSON)
            .body(userData)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .extract()
            .response();

        Map<String, Object> responseData = response.jsonPath().getMap("$");
        UUID userId = UUID.fromString(responseData.get("id").toString());
        createdUserIds.add(userId);

        logger.info("Registered test user with ID: {}", userId);
        return responseData;
    }

    /**
     * Generates random user data to prevent test conflicts
     *
     * @return Map with random user data
     */
    public Map<String, Object> generateRandomUserData() {
        long timestamp = System.currentTimeMillis();
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "testuser" + timestamp);
        userData.put("email", "test" + timestamp + "@example.com");
        userData.put("password", "Password123!");
        userData.put("phoneNumber", "+1" + (1000000000L + (long)(Math.random() * 9000000000L)));
        return userData;
    }

    /**
     * Login a user and retrieve authentication tokens
     *
     * @param username The username to login with
     * @param password The password to login with
     * @return Map containing access and refresh tokens
     */
    public Map<String, String> loginUser(String username, String password) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("identifier", username);
        loginData.put("password", password);

        Response response = given()
            .spec(userServiceSpec)
            .contentType(ContentType.JSON)
            .body(loginData)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .response();

        Map<String, String> tokens = response.jsonPath().getMap("$");
        return tokens;
    }

    /**
     * Get all users from the system
     *
     * @return List of user data maps
     */
    public List<Map<String, Object>> getAllUsers() {
        return given()
            .spec(userServiceSpec)
            .contentType(ContentType.JSON)
        .when()
            .get("/user/getAll")
        .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getList("$");
    }

    /**
     * Get a specific user by ID
     *
     * @param userId The ID of the user to retrieve
     * @return Map containing user data
     */
    public Map<String, Object> getUserById(UUID userId) {
        return given()
            .spec(userServiceSpec)
            .contentType(ContentType.JSON)
        .when()
            .get("/user/" + userId)
        .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getMap("$");
    }

    /**
     * Delete a specific user by ID
     *
     * @param userId The ID of the user to delete
     * @param token Authentication token (can be null for test environments)
     * @return True if deletion was successful
     */
    public boolean deleteUser(UUID userId, String token) {
        try {
            RequestSpecification request = given()
                .spec(userServiceSpec)
                .contentType(ContentType.JSON);

            // Add token if provided
            if (token != null && !token.isEmpty()) {
                request.header("Authorization", "Bearer " + token);
            }

            request.when()
                .delete("/user/" + userId)
            .then()
                .statusCode(204);

            createdUserIds.remove(userId);
            logger.info("Deleted test user with ID: {}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete test user with ID: {}", userId, e);
            return false;
        }
    }

    /**
     * Delete all users created through this service
     */
    public void deleteAllCreatedUsers() {
        // Check if the service has a convenience method for clean-up
        try {
            given()
                .spec(userServiceSpec)
                .contentType(ContentType.JSON)
            .when()
                .delete("/user/deleteAll")
            .then()
                .statusCode(200);

            logger.info("Bulk deleted all test users");
            createdUserIds.clear();
        } catch (Exception e) {
            // Fall back to deleting one by one
            logger.info("Bulk delete failed, deleting users one by one");
            List<UUID> userIdsCopy = new ArrayList<>(createdUserIds);
            for (UUID userId : userIdsCopy) {
                deleteUser(userId, userTokens.get(userId));
            }
        }
    }

    /**
     * Update an existing user
     *
     * @param userId The ID of the user to update
     * @param updateData The data to update
     * @return The updated user data
     */
    public Map<String, Object> updateUser(UUID userId, Map<String, Object> updateData) {
        return given()
            .spec(userServiceSpec)
            .contentType(ContentType.JSON)
            .body(updateData)
        .when()
            .put("/user/" + userId)
        .then()
            .statusCode(200)
            .extract()
            .body()
            .jsonPath()
            .getMap("$");
    }

    /**
     * Check if a user is blocked by another user
     *
     * @param blockerId The ID of the user who might be blocking
     * @param blockedId The ID of the user who might be blocked
     * @return True if blocked, false otherwise
     */
    public boolean isUserBlocked(UUID blockerId, UUID blockedId) {
        String response = given()
            .spec(userServiceSpec)
            .contentType(ContentType.JSON)
        .when()
            .get("/user/isBlocked/{blockerId}/{blockedId}", blockerId, blockedId)
        .then()
            .statusCode(200)
            .extract()
            .asString();

        return Boolean.parseBoolean(response);
    }

    /**
     * Get list of users created during tests
     *
     * @return List of created user IDs
     */
    public List<UUID> getCreatedUserIds() {
        return new ArrayList<>(createdUserIds);
    }

    public void cleanup() {
        // Delete all created users
        deleteAllCreatedUsers();

        // Clear the list of created user IDs
        createdUserIds.clear();
    }
}
