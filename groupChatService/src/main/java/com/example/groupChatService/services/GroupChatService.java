package com.example.groupChatService.services;


import com.example.groupChatService.dto.GroupChatRequest;
import com.example.groupChatService.dto.GroupUpdateRequest;
import com.example.groupChatService.models.GroupChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.groupChatService.repositories.GroupChatRepo;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupChatService {
    private final GroupChatRepo groupChatRepo;
    @Autowired
    public GroupChatService(GroupChatRepo groupChatRepo) {
        this.groupChatRepo = groupChatRepo;
    }
    public GroupChat addGroupChat(GroupChatRequest groupChatRequest){
        String name= groupChatRequest.getName();
        String creatorId= groupChatRequest.getCreatorId();
        List<String> admins= new ArrayList<String>();
        List<String> members= groupChatRequest.getMembers()==null? new ArrayList<String>(): groupChatRequest.getMembers();
        admins.add(creatorId);
        members.add(creatorId);
        if(name==null || name.isEmpty()){
            throw new RuntimeException("Group chat name cannot be null or empty");
        }
        if(creatorId==null || creatorId.isEmpty()){
            throw new RuntimeException("Creator ID cannot be null or empty");
        }
        GroupChat.groupChatBuilder builder = new GroupChat.groupChatBuilder(name,creatorId ,admins,members);
        if (groupChatRequest.getDescription() != null) {
            builder.setDescription(groupChatRequest.getDescription());
        }
        if (groupChatRequest.getEmoji() != null) {
            builder.setEmoji(groupChatRequest.getEmoji());
        }
        if (groupChatRequest.getAdminOnlyMessages()!=null) {
            builder.setAdminOnlyMessages(groupChatRequest.getAdminOnlyMessages());
        }
        GroupChat groupChat= builder.build();
        return groupChatRepo.save(groupChat);
    }
    public List<GroupChat> getAllGroupChat(){
        return groupChatRepo.findAll();
    }
    public GroupChat getGroupChatById(String id) {
        GroupChat groupChat = groupChatRepo.findById(id).orElse(null);
        if (groupChat == null) {
            throw new RuntimeException("Group chat not found with id: " + id);
        }
        return groupChat;
    }
    public GroupChat updateGroupChat(String id, GroupUpdateRequest groupUpdateRequest){
        GroupChat groupChat= groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        if (groupUpdateRequest.getName() != null) {
            builder.setName(groupUpdateRequest.getName());
        }
        if (groupUpdateRequest.getDescription() != null) {
            builder.setDescription(groupUpdateRequest.getDescription());
        }
        if (groupUpdateRequest.getEmoji() != null) {
            builder.setEmoji(groupUpdateRequest.getEmoji());
        }
        //should we refuse update members if creatorId is not in the list of memberss?
        if (groupUpdateRequest.getMembers() != null) {
            if(!groupUpdateRequest.getMembers().contains(groupChat.getCreatorId())) {
                groupUpdateRequest.getMembers().add(groupChat.getCreatorId());
            }
            builder.setMembers(groupUpdateRequest.getMembers());
        }
        //should we refuse update admins if creatorId is not in the list of admins?
        if (groupUpdateRequest.getAdmins() != null) {
            if(!groupUpdateRequest.getAdmins().contains(groupChat.getCreatorId())){
                groupUpdateRequest.getAdmins().add(groupChat.getCreatorId());
            }
            builder.setAdmins(groupUpdateRequest.getAdmins());
        }
        if(groupUpdateRequest.getAdminOnlyMessages()!=null){
            builder.setAdminOnlyMessages(groupUpdateRequest.getAdminOnlyMessages());
        }
        return groupChatRepo.save(builder.build());
    }
    public String deleteGroupChat(String id) {
        GroupChat groupChat = groupChatRepo.findById(id).orElse(null);
        if (groupChat == null) {
            throw new RuntimeException("Group chat not found with id: " + id);
        }
        groupChatRepo.deleteById(id);
        return "Group chat deleted successfully with id: " + id;
    }





}
