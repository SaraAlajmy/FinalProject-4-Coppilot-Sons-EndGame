package com.example.e2e.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Service for testing email functionality using Maildrop API
 */
public class MailhogService {
    private static final Logger logger = LoggerFactory.getLogger(MailhogService.class);
    private static final String MAILHOG_API_URL = "http://localhost";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RequestSpecification requestSpec;

    public MailhogService() {
        this.requestSpec = io.restassured.RestAssured.given()
                                                     .contentType(ContentType.JSON)
                                                     .baseUri(MAILHOG_API_URL)
                                                     .port(8025);
    }

    public List<Map<String, Object>> getMailboxMessages(String email) {
        logger.info("Fetching messages for mailbox: {}", email);

        return given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .when()
            .get("/api/v1/messages")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(List.class);
    }


    /**
     * Delete a message
     *
     * @param mailbox   The mailbox name
     * @param messageId The ID of the message to delete
     * @return True if successful
     */
    public boolean deleteMessage(String mailbox, String messageId) {
        logger.info("Deleting message {} from mailbox {}", messageId, mailbox);

        return true;
    }

    public Map<String, Object> getMatchingMail(
        String email,
        String subjectContains,
        String contentContains
    ) {
        var messages = getMailboxMessages(email);

        for (var message : messages) {
            var content = (Map<String, Object>) message.get("Content");
            var headers =  (Map<String, Object>) content.get("Headers");
            var subject = headers.get("Subject").toString();
            var to = ((List<String>) headers.get("To")).getFirst().toString();
            var raw = (Map<String, Object>) message.get("Raw");
            var body = raw.get("Data").toString().replaceAll("=\r\n", "").replaceAll("\r\n", "");

            if (to.equals(email) && subject.contains(subjectContains) && body.contains(contentContains)) {
                return message;
            }
        }

        return null;
    }

    /**
     * Extract a mailbox name from an email address
     *
     * @param email Email address in the format name@maildrop.cc
     * @return The mailbox name (part before the @)
     */
    public String extractMailboxFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        return email.split("@")[0];
    }

    public void emptyMailbox() {
        logger.info("Emptying Mailhog");

        var response = given()
            .spec(requestSpec)
            .contentType(ContentType.JSON)
            .when()
            .delete("/api/v1/messages")
            .then()
            .statusCode(200);
    }
}
