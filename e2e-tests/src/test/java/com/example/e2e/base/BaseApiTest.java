package com.example.e2e.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

/**
 * Base class for all API tests.
 * Configures REST Assured and sets up common test behaviors.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {

    protected RequestSpecification requestSpec;

    // Configure these based on your actual services
    protected static final String BASE_URI = "http://localhost";
    protected static final int USER_SERVICE_PORT = 8086;
    protected static final int CHAT_SERVICE_PORT = 8080;
    protected static final int NOTIFICATION_SERVICE_PORT = 8082;
    protected static final int GROUP_CHAT_SERVICE_PORT = 8083;

    @BeforeAll
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Base request specification with common settings
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
        
        // Service-specific setup can be done in subclasses
        setupServiceSpecificConfig();
    }
    
    /**
     * Override this method in service-specific test classes
     * to configure any service-specific setup.
     */
    protected abstract void setupServiceSpecificConfig();

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
}
