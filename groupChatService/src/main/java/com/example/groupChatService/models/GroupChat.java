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
    private String colorTheme;
    private String creatorId;
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

    public String getColorTheme() {
        return colorTheme;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public boolean isAdminOnlyMessages() {
        return adminOnlyMessages;
    }

    public GroupChat() {
    }

    private GroupChat(groupChatBuilder groupChatBuilder) {
        if(groupChatBuilder.getId()!=null){
            this.id=groupChatBuilder.getId();
        }
        this.name = groupChatBuilder.name;
        this.description = groupChatBuilder.description;
        this.emoji = groupChatBuilder.emoji;
        this.colorTheme=groupChatBuilder.colorTheme;
        this.creatorId = groupChatBuilder.creatorId;
        this.members = groupChatBuilder.members;
        this.admins = groupChatBuilder.admins;
        this.adminOnlyMessages = groupChatBuilder.adminOnlyMessages;

    }

    public groupChatBuilder toBuilder() {
        return new groupChatBuilder(this.name, this.creatorId,new ArrayList<String>(this.admins),new ArrayList<>(this.members))
                .setDescription(this.description)
                .setEmoji(this.emoji)
                .setColorTheme(colorTheme)
                .setAdminOnlyMessages(this.adminOnlyMessages);
    }

    public static class groupChatBuilder {
        private String id;
        private String name;
        private String description;
        private String emoji;
        private String colorTheme;
        private List<String> members;
        private List<String> admins;
        private boolean adminOnlyMessages;
        private String creatorId;

        public groupChatBuilder(String name,String creatorId,List<String> admins, List<String> members) {
            this.name = name;
            this.creatorId = creatorId;
            this.admins = new ArrayList<String>(admins);
            this.members = new ArrayList<String>(members);
        }

        public groupChatBuilder setName(String name) {
            this.name = name;
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

        public groupChatBuilder setColorTheme(String colorTheme) {
            this.colorTheme = colorTheme;
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public groupChatBuilder addMember(String memberId){
            this.members.add(memberId);
            return this;
        }
        public groupChatBuilder removeMember(String memberId){
            this.members.remove(memberId);
            return this;
        }
        public groupChatBuilder makeAdmin(String memberId){
            this.admins.add(memberId);
            return this;
        }
        public groupChatBuilder removeAdmin(String memberId){
            this.admins.remove(memberId);
            return this;
        }


        public GroupChat build() {
            return new GroupChat(this);
        }
    }

}
