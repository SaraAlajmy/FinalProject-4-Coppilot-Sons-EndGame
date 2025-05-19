package com.example.e2e.base;

import com.example.e2e.service.*;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter    .FilterContext;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Base class for all API tests.
 * Configures REST Assured and sets up common test behaviors.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {
    protected Faker faker = new Faker();
    
    protected UserTestService userTestService;
    protected ChatTestService chatTestService;
    protected MessageTestService messageTestService;
    protected NotificationTestService notificationTestService;
    protected MailhogService mailhogService;
    protected GroupChatTestService groupChatTestService;
    protected GroupMessageTestService groupMessageTestService;

    protected RequestSpecification requestSpec;

    // Configure these based on your actual services
    protected static final String BASE_URI = "http://localhost";

    protected static final int USER_SERVICE_PORT = 8086;
    protected static final int CHAT_SERVICE_PORT = 8080;
    protected static final int NOTIFICATION_SERVICE_PORT = 8082;
    protected static final int GROUP_CHAT_SERVICE_PORT = 8083;
    protected static final int API_GATEWAY_PORT = 8765;

    protected static final String LOGGED_IN_EMAIL = "mathew.hanybb@gmail.com";

    protected Map<String, Object> loggedInUser;
    protected String accessToken;

    @BeforeAll
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Get the base URL from the environment variable or use the default
        String baseUrl = System.getenv("BASE_URL");

        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = BASE_URI + ":" + API_GATEWAY_PORT;
        }

        // Base request specification with common settings
        requestSpec = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .build();


//        RequestSpecification userServiceSpec = getSpecForService(USER_SERVICE_PORT);
//        RequestSpecification chatServiceSpec = getSpecForService(CHAT_SERVICE_PORT);
//        RequestSpecification notificationServiceSpec = getSpecForService(NOTIFICATION_SERVICE_PORT);
//        RequestSpecification groupChatServiceSpec = getSpecForService(GROUP_CHAT_SERVICE_PORT);
        RequestSpecification unauthorizedSpec = getSpecForService(API_GATEWAY_PORT);
        RequestSpecification authorizedSpec = getSpecForService(API_GATEWAY_PORT)
            .filter((req, res, ctx) -> {
                req.header("Authorization", "Bearer " + accessToken);
                return ctx.next(req, res);
            });

        userTestService = new UserTestService(authorizedSpec, unauthorizedSpec);

        notificationTestService = new NotificationTestService(authorizedSpec);
        groupChatTestService = new GroupChatTestService(authorizedSpec, userTestService);
        groupMessageTestService = new GroupMessageTestService(authorizedSpec);
        mailhogService = new MailhogService();
        chatTestService = new ChatTestService(authorizedSpec, userTestService);
        messageTestService = new MessageTestService(authorizedSpec);
    }

    @BeforeEach
    public void login() {        
        loggedInUser = userTestService.registerUserWithEmail(LOGGED_IN_EMAIL);
        var username = (String) loggedInUser.get("username");
        accessToken = userTestService.loginUser(username);
    }

    /**
     * Get a request specification for a specific service.
     *
     * @param port The port number for the service
     * @return RequestSpecification configured for the specific service
     */
    protected RequestSpecification getSpecForService(int port) {
        return new RequestSpecBuilder()
            .addRequestSpecification(requestSpec)
            .setPort(port)
            .build();
    }

    protected void loginAs(Map<String, Object> userData) {
        if (userData == null) {
            loggedInUser = null;
            accessToken = null;
            return;
        }
        
        loggedInUser = userData;
        var userName = (String) userData.get("username");
        accessToken = userTestService.loginUser(userName);
    }

    protected void loggedAs(Map<String, Object> userData, Runnable action) {
        var previousUser = loggedInUser;
        loginAs(userData);

        try {
            action.run();
        } finally {
            loginAs(previousUser);
        }
    }

    protected void loggedAs(User userData, Runnable action) {
        var user = userData.toMap();
        loggedAs(user, action);
    }

    @Builder
    public record User(
        String id,
        String email,
        String username
    ) {
        public Map<String, Object> toMap() {
            return Map.of(
                "id", id,
                "email", email,
                "username", username
            );
        }
    }
}
