package com.example.chat_service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "favourite_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavouriteMessage {
    @Id
    private String id;
    
    @DBRef
    private Message message;
    
    @Indexed
    private String userId;
}
