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

public class UserTestService {
    public static final String PASSWORD = "password";

    private static final Logger logger = LoggerFactory.getLogger(UserTestService.class);
    private final RequestSpecification authorizedSpec;
    private final RequestSpecification unauthorizedSpec;
    private final List<UUID> createdUserIds = new ArrayList<>();
    private Map<UUID, String> userTokens = new HashMap<>();

    public UserTestService(RequestSpecification authorizedSpec, RequestSpecification unauthorizedSpec) {
        this.authorizedSpec = authorizedSpec;
        this.unauthorizedSpec = unauthorizedSpec;
    }

    public Map<String, Object> registerUser() {
        Map<String, Object> userData = generateRandomUserData();
        return registerUser(userData);
    }

    public Map<String, Object> registerUserWithEmail(String email) {
        Map<String, Object> userData = generateRandomUserData();
        userData.put("email", email);
        return registerUser(userData);
    }

    public Map<String, Object> registerUser(Map<String, Object> userData) {
        Response response = given()
            .spec(unauthorizedSpec)
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

    public Map<String, Object> generateRandomUserData() {
        long timestamp = System.currentTimeMillis();
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "testuser" + timestamp);
        userData.put("email", "test" + timestamp + "@example.com");
        userData.put("password", PASSWORD);
        userData.put("phoneNumber", "+1" + (1000000000L + (long)(Math.random() * 9000000000L)));
        return userData;
    }


    public String loginUser(String username) {
        return loginUser(username, PASSWORD);
    }

    public String loginUser(String username, String password) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("identifier", username);
        loginData.put("password", password);
        loginData.put("type", "username");

        Response response = given()
            .spec(unauthorizedSpec)
            .contentType(ContentType.JSON)
            .body(loginData)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .response();

        Map<String, String> tokens = response.jsonPath().getMap("$");
        var accessToken = tokens.get("accessToken");
        return accessToken;
    }

    public List<Map<String, Object>> getAllUsers() {
        return given()
            .spec(authorizedSpec)
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

    public Map<String, Object> getUserById(UUID userId) {
        return given()
            .spec(authorizedSpec)
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

    public boolean deleteUser(UUID userId, String token) {
        try {
            RequestSpecification request = given()
                .spec(authorizedSpec)
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

    public void deleteAllCreatedUsers() {
        // Check if the service has a convenience method for clean-up
        try {
            given()
                .spec(authorizedSpec)
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

    public Map<String, Object> updateUser(UUID userId, Map<String, Object> updateData) {
        return given()
            .spec(authorizedSpec)
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

    public boolean isUserBlocked(UUID blockerId, UUID blockedId) {
        String response = given()
            .spec(authorizedSpec)
            .contentType(ContentType.JSON)
        .when()
            .get("/user/isBlocked/{blockerId}/{blockedId}", blockerId, blockedId)
        .then()
            .statusCode(200)
            .extract()
            .asString();

        return Boolean.parseBoolean(response);
    }

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
