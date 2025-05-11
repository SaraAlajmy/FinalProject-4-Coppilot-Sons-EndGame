package com.example.groupChatService.services;
import com.example.groupChatService.models.GroupMessage;
import com.example.groupChatService.repositories.GroupChatRepo;
import com.example.groupChatService.dto.NotificationPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener implements MessageListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final GroupChatRepo groupChatRepository;

    public NotificationListener(RabbitTemplate rabbitTemplate, GroupChatRepo groupChatRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.groupChatRepository = groupChatRepository;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void onNewMessage(GroupMessage message) {
        groupChatRepository.findById(message.getGroupId()).ifPresent(group -> {
            for (String memberId : group.getMembers()) {
                if (!memberId.equals(message.getSenderId())) {
                    NotificationPayload payload = new NotificationPayload();
                    payload.setRecipientUserId(memberId);
                    payload.setType(message.getContent().contains("@" + memberId) ? "group_mention" : "group_message");
                    payload.setSenderUserId(message.getSenderId());
                    payload.setMessageId(message.getId());
                    payload.setMessageText(message.getContent());
                    payload.setMessageTimestamp(message.getCreatedAt());
                    payload.setGroupId(group.getId());
                    payload.setGroupName(group.getName());
                    try {
                        String json = objectMapper.writeValueAsString(payload);
                        rabbitTemplate.convertAndSend("notificationQueue", json);
                        System.out.println("📨 Sent notification: " + json);
                    } catch (JsonProcessingException e) {
                        System.err.println("❌ Failed to serialize notification payload");
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
