package com.example.chat_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id // I was considering making the id a combination of participant IDs, but ig keeping it consistent with other models is better for now
    private String chatId;

    @Indexed // Indexing participant IDs for faster lookups
    private String participantOneId;

    @Indexed // Indexing participant IDs for faster lookups
    private String participantTwoId;
}