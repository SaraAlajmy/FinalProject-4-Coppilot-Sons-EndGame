package com.example.e2e.service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class NotificationTestService {

    private final RequestSpecification notificationServiceSpec;
    private final Map<String, String> createdUserIds = new HashMap<>();

    public NotificationTestService(RequestSpecification notificationServiceSpec) {
        this.notificationServiceSpec = notificationServiceSpec;
    }

    public Map<String, Object> getUserNotificationSettings(String userId) {
        return given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notification-settings")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Map.class);
    }

    public boolean getNotificationStatus(String userId, String notificationType, String strategyType) {
        return given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notification-settings/" + notificationType + "/" + strategyType)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Boolean.class);
    }

    public void enableNotification(String userId, String notificationType, String strategyType) {
        given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notification-settings/" + notificationType + "/" + strategyType + "/enable")
                .then()
                .statusCode(200);
    }

    public void disableNotification(String userId, String notificationType, String strategyType) {
        given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notification-settings/" + notificationType + "/" + strategyType + "/disable")
                .then()
                .statusCode(200);
    }

    public void muteAllNotifications(String userId) {
        given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notification-settings/mute-all")
                .then()
                .statusCode(200);
    }

    public void unmuteAllNotifications(String userId) {
        given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notification-settings/unmute-all")
                .then()
                .statusCode(200);
    }
    
    /**
     * Get all notifications for a specific user
     * @param userId The user ID
     * @return A list of all notifications for the user
     */
    public List<Map<String, Object>> getAllNotifications(String userId) {
        return given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/notifications/AllNotifications")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(List.class);
    }
    
    /**
     * Get all unread notifications for a specific user
     * @param userId The user ID
     * @return A list of unread notifications
     */
    public List<Map<String, Object>> getUnreadNotifications(String userId) {
        return given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/notifications/unread")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(List.class);
    }
    
    /**
     * Get all unread notifications grouped by sender
     * @param userId The user ID
     * @return A map with sender IDs as keys and lists of notifications as values
     */
    public Map<String, List<Map<String, Object>>> getUnreadNotificationsGroupedBySender(String userId) {
        return given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/notifications/unread/grouped")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(Map.class);
    }
    
    /**
     * Get the count of unread notifications for a user
     * @param userId The user ID
     * @return The number of unread notifications
     */
    public int getUnreadNotificationCount(String userId) {
        return given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .get("/notifications/unread/count")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Integer.class);
    }
    
    /**
     * Mark a specific notification as read
     * @param notificationId The notification ID to mark as read
     * @return The response message
     */
    public String markNotificationAsRead(String notificationId) {
        return given()
                .spec(notificationServiceSpec)
                .contentType(ContentType.JSON)
                .queryParam("notificationId", notificationId)
                .when()
                .post("/notifications/read")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
    }
    
    /**
     * Mark all notifications as read for a user
     * @param userId The user ID
     * @return The response message
     */
    public String markAllNotificationsAsRead(String userId) {
        return given()
                .spec(notificationServiceSpec)
                .header("userId", userId)
                .contentType(ContentType.JSON)
                .when()
                .post("/notifications/markAllRead")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
    }

    public int deleteAllNotifications() {
        return given()
            .spec(notificationServiceSpec)
            .contentType(ContentType.JSON)
            .when()
            .delete("/api/seed/notifications")
            .then()
            .extract()
            .statusCode();
    }


    public void cleanup() {
        deleteAllNotifications();
    }
}
