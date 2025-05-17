package com.example.e2e.user;

import com.example.e2e.base.BaseApiTest;
import com.example.e2e.service.UserTestService;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for User API.
 */
public class UserApiTests extends BaseApiTest {

    private RequestSpecification userServiceSpec;
    private UserTestService userTestService;

    @Override
    protected void setupServiceSpecificConfig() {
        // Configure the user service specific request specification
        userServiceSpec = getSpecForService(USER_SERVICE_PORT);
        userTestService = new UserTestService(userServiceSpec);
    }

    @AfterAll
    public void cleanupTestData() {
        userTestService.deleteAllCreatedUsers();
    }

    @Test
    @DisplayName("Should get all users")
    public void shouldGetAllUsers() {
        // Create a test user to ensure there's at least one in the database
        Map<String, Object> createdUser = userTestService.registerUser();
        UUID createdUserId = UUID.fromString(createdUser.get("id").toString());

        // Alternative: use the test service
        List<Map<String, Object>> users = userTestService.getAllUsers();
        assertThat(users).isNotEmpty();

        // Verify the created user is in the list
        boolean foundUser = users.stream()
            .anyMatch(user ->
                          createdUserId.toString().equals(user.get("id").toString()) &&
                     createdUser.get("username").equals(user.get("username")) &&
                     createdUser.get("email").equals(user.get("email")));

        assertThat(foundUser)
            .as("Created user with ID %s should be present in the list of all users", createdUserId)
            .isTrue();
    }

    @Test
    @DisplayName("Should register a new user")
    public void shouldRegisterUser() {
        // Create random test user data using the service
        Map<String, Object> userData = userTestService.generateRandomUserData();

        // Override email if needed for specific test requirements
        userData.put("email", "test" + System.currentTimeMillis() + "@example.com");

        Map<String, Object> userResponse = userTestService.registerUser(userData);
        assertThat(userResponse).containsKey("id");
        assertThat(userResponse.get("username")).isEqualTo(userData.get("username"));
    }
}
