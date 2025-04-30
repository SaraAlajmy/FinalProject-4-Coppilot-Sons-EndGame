package com.example.groupChatService.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "groupChat")
public class GroupChat {
    @Id
    private String id;
    private String name;
    private String description;
    private String emoji;
    private List<String> members;
    private List<String> admins;
    private boolean adminOnlyMessages;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEmoji() {
        return emoji;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public boolean isAdminOnlyMessages() {
        return adminOnlyMessages;
    }

    private GroupChat(groupChatBuilder groupChatBuilder) {
        this.name = groupChatBuilder.name;
        this.description = groupChatBuilder.description;
        this.emoji = groupChatBuilder.emoji;
        this.members = groupChatBuilder.members;
        this.admins = groupChatBuilder.admins;
        this.adminOnlyMessages = groupChatBuilder.adminOnlyMessages;

    }

    public groupChatBuilder toBuilder() {
        return new groupChatBuilder(this.name, new ArrayList<String>(this.admins))
                .setDescription(this.description)
                .setEmoji(this.emoji)
                .setMembers(this.members != null ? new ArrayList<String>(this.members) : null)
                .setAdminOnlyMessages(this.adminOnlyMessages);
    }

    public static class groupChatBuilder {
        private String name;
        private String description;
        private String emoji;
        private List<String> members;
        private List<String> admins;
        private boolean adminOnlyMessages;

        public groupChatBuilder(String name,List<String> admins) {
            this.name = name;
            this.admins = new ArrayList<String>(admins);
        }

        public groupChatBuilder setName(String name, List<String> admins) {
            this.name = name;
            this.admins = admins;
            return this;
        }

        public groupChatBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public groupChatBuilder setEmoji(String emoji) {
            this.emoji = emoji;
            return this;
        }

        public groupChatBuilder setMembers(List<String> members) {
            this.members = members;
            return this;
        }
        public groupChatBuilder setAdmins(List<String> admins) {
            this.admins = admins;
            return this;
        }

        public groupChatBuilder setAdminOnlyMessages(boolean adminOnlyMessages) {
            this.adminOnlyMessages = adminOnlyMessages;
            return this;
        }

        public GroupChat build() {
            return new GroupChat(this);
        }
    }

}
