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

/**
 * Service for testing email functionality using Maildrop API
 */
public class MaildropService {
    private static final Logger logger = LoggerFactory.getLogger(MaildropService.class);
    private static final String MAILDROP_API_URL = "https://api.maildrop.cc/graphql";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RequestSpecification requestSpec;

    public MaildropService() {
        this.requestSpec = io.restassured.RestAssured.given()
                                                     .contentType(ContentType.JSON)
                                                     .baseUri(MAILDROP_API_URL);
    }

    /**
     * Get all messages in a mailbox
     *
     * @param mailbox The mailbox name (before the @ in username@maildrop.cc)
     * @return List of messages in the mailbox
     */
    public List<Map<String, Object>> getMailboxMessages(String mailbox) {
        logger.info("Fetching messages for mailbox: {}", mailbox);

        String query = String.format(
            "{\"query\":\"query { inbox(mailbox:\\\"%s\\\") { id headerfrom subject date } }\"}",
            mailbox
        );

        Response response = requestSpec
            .body(query)
            .post();

        response.then().statusCode(200);

        try {
            JsonNode root = objectMapper.readTree(response.getBody().asString());
            JsonNode inboxNode = root.path("data").path("inbox");

            List<Map<String, Object>> messages = new ArrayList<>();
            if (inboxNode.isArray()) {
                for (JsonNode msgNode : inboxNode) {
                    Map<String, Object> message = new HashMap<>();
                    message.put("id", msgNode.path("id").asText());
                    message.put("from", msgNode.path("headerfrom").asText());
                    message.put(
                        "subject",
                        msgNode.path("subject").asText().replaceAll("[\n\r]", "")
                    );
                    message.put("date", msgNode.path("date").asText());
                    messages.add(message);
                }
            }

            logger.info("Found {} messages in mailbox {}", messages.size(), mailbox);
            return messages;
        } catch (Exception e) {
            logger.error("Error parsing mailbox messages", e);
            throw new RuntimeException("Failed to parse mailbox messages", e);
        }
    }

    /**
     * Get a specific message by ID
     *
     * @param mailbox   The mailbox name
     * @param messageId The ID of the message to retrieve
     * @return The message content
     */
    public Map<String, Object> getMessage(String mailbox, String messageId) {
        logger.info("Fetching message {} from mailbox {}", messageId, mailbox);

        String query = String.format(
            "{\"query\":\"query { message(mailbox:\\\"%s\\\", id:\\\"%s\\\") { id headerfrom subject date data html } }\"}",
            mailbox,
            messageId
        );

        Response response = requestSpec
            .body(query)
            .post();

        response.then().statusCode(200);

        try {
            JsonNode root = objectMapper.readTree(response.getBody().asString());
            JsonNode messageNode = root.path("data").path("message");

            if (messageNode.isMissingNode()) {
                logger.warn("Message not found: {}", messageId);
                return null;
            }

            Map<String, Object> message = new HashMap<>();
            message.put("id", messageNode.path("id").asText());
            message.put("from", messageNode.path("headerfrom").asText());
            message.put("subject", messageNode.path("subject").asText().replaceAll("[\n\r]", ""));
            message.put("date", messageNode.path("date").asText());
            message.put("data", messageNode.path("data").asText());
            message.put("html", messageNode.path("html").asText());

            return message;
        } catch (Exception e) {
            logger.error("Error fetching message", e);
            throw new RuntimeException("Failed to fetch message", e);
        }
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

        String query = String.format(
            "{\"query\":\"mutation { delete(mailbox:\\\"%s\\\", id:\\\"%s\\\") }\"}",
            mailbox, messageId
        );

        Response response = requestSpec
            .body(query)
            .post();

        response.then().statusCode(200);

        try {
            JsonNode root = objectMapper.readTree(response.getBody().asString());
            return root.path("data").path("delete").asBoolean();
        } catch (Exception e) {
            logger.error("Error deleting message", e);
            throw new RuntimeException("Failed to delete message", e);
        }
    }

    public Map<String, Object> getMatchingMail(String email, String subjectContains, String contentContains) {
        String mailbox = extractMailboxFromEmail(email);
        List<Map<String, Object>> messages = getMailboxMessages(mailbox);
        for (Map<String, Object> message : messages) {
            String subject = (String) message.get("subject");
            if (subject != null && subject.contains(subjectContains)) {
                String messageId = (String) message.get("id");
                var mailData = getMessage(mailbox, messageId);
                if (mailData != null) {
                    String content = (String) mailData.get("data");
                    if (content != null && content.contains(contentContains)) {
                        return mailData;
                    }
                }
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

    public void emptyMailbox(String mailbox) {
        logger.info("Emptying mailbox: {}", mailbox);

        List<Map<String, Object>> messages = getMailboxMessages(mailbox);
        for (Map<String, Object> message : messages) {
            String messageId = (String) message.get("id");
            deleteMessage(mailbox, messageId);
        }

        logger.info("Mailbox {} emptied", mailbox);
    }
}
