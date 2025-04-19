package com.lifespace.mapper;

import com.lifespace.dto.ChatroomMessageDTO;
import com.lifespace.entity.ChatroomMessage;

import java.util.List;
import java.util.stream.Collectors;

public class ChatroomMessageMapper {

    public static ChatroomMessageDTO toChatroomMessageDTO(ChatroomMessage chatroomMessage) {
        ChatroomMessageDTO dto = new ChatroomMessageDTO();
        
        dto.setChatroomMessageId(chatroomMessage.getChatroomMessageId());
        dto.setAdminId(chatroomMessage.getAdminId());
        dto.setMemberId(chatroomMessage.getMemberId());
        dto.setContent(chatroomMessage.getContent());
        dto.setStatus(chatroomMessage.getStatus());
        dto.setClickstatus(chatroomMessage.getClickstatus());
        dto.setChatPhoto(chatroomMessage.getChatPhoto());
        dto.setSendTime(chatroomMessage.getSendTime());
        
        // 添加會員名稱（如果會員存在）
        if (chatroomMessage.getMember() != null) {
            dto.setMemberName(chatroomMessage.getMember().getMemberName());
        }
        
        return dto;
    }
    
    public static List<ChatroomMessageDTO> toChatroomMessageDTOList(List<ChatroomMessage> chatroomMessages) {
        return chatroomMessages.stream()
                .map(ChatroomMessageMapper::toChatroomMessageDTO)
                .collect(Collectors.toList());
    }
}