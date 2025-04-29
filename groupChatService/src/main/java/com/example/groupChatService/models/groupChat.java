package com.example.groupChatService.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "groupChat")
public class groupChat {
    @Id
    private String id;
    private String name;
    private String description;
    private String emoji;
    private List<String> members;
    private List<String> admins;
    private boolean adminOnlyMessages;



}
