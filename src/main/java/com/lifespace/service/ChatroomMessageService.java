package com.lifespace.service;

import com.lifespace.dto.ChatroomMessageDTO;
import com.lifespace.entity.ChatroomMessage;
import com.lifespace.entity.Member;
import com.lifespace.mapper.ChatroomMessageMapper;
import com.lifespace.repository.ChatroomMessageRepository;
import com.lifespace.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatroomMessageService {
    
    @Autowired
    private ChatroomMessageRepository chatroomMessageRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    // 獲取會員的所有聊天記錄
    public List<ChatroomMessageDTO> getMemberMessages(String memberId) {
        List<ChatroomMessage> messages = chatroomMessageRepository.findByMemberIdOrderBySendTimeAsc(memberId);
        
        return messages.stream().map(message -> {
            ChatroomMessageDTO dto = ChatroomMessageMapper.toChatroomMessageDTO(message);
            
            // 如果會員存在，設置會員名稱
            if (message.getMember() != null) {
                dto.setMemberName(message.getMember().getMemberName());
            } else {
                // 手動查詢會員名稱
                Optional<Member> memberOpt = memberRepository.findById(memberId);
                memberOpt.ifPresent(member -> dto.setMemberName(member.getMemberName()));
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    // 獲取所有會員的最後一條訊息
    public List<ChatroomMessageDTO> getLatestMessageForEachMember() {
        List<ChatroomMessage> latestMessages = chatroomMessageRepository.findLatestMessageForEachMember();
        
        return latestMessages.stream().map(message -> {
            ChatroomMessageDTO dto = ChatroomMessageMapper.toChatroomMessageDTO(message);
            
            // 手動查詢會員名稱
            Optional<Member> memberOpt = memberRepository.findById(message.getMemberId());
            memberOpt.ifPresent(member -> dto.setMemberName(member.getMemberName()));
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    // 根據會員ID模糊搜尋最後一條訊息
    public List<ChatroomMessageDTO> getLatestMessageForEachMemberByMemberIdContaining(String memberId) {
        List<ChatroomMessage> latestMessages = chatroomMessageRepository.findLatestMessageForEachMemberByMemberIdContaining(memberId);
        
        return latestMessages.stream().map(message -> {
            ChatroomMessageDTO dto = ChatroomMessageMapper.toChatroomMessageDTO(message);
            
            // 手動查詢會員名稱
            Optional<Member> memberOpt = memberRepository.findById(message.getMemberId());
            memberOpt.ifPresent(member -> dto.setMemberName(member.getMemberName()));
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    // 更新最後一條訊息的點擊狀態
    @Transactional
    public void updateClickStatusForLatestMessage(String memberId, Integer clickstatus) {
        chatroomMessageRepository.updateClickStatusForLatestMessage(memberId, clickstatus);
    }
    
    // 獲取特定會員的最後一條訊息
    public ChatroomMessageDTO getLatestMessageByMemberId(String memberId) {
        ChatroomMessage message = chatroomMessageRepository.findLatestMessageByMemberId(memberId);
        if (message == null) {
            return null;
        }
        
        ChatroomMessageDTO dto = ChatroomMessageMapper.toChatroomMessageDTO(message);
        
        // 手動查詢會員名稱
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        memberOpt.ifPresent(member -> dto.setMemberName(member.getMemberName()));
        
        return dto;
    }
    
    // 新增文字訊息
    @Transactional
    public ChatroomMessageDTO addTextMessage(String adminId, String memberId, String content, Integer status, Integer clickstatus) {
        ChatroomMessage message = new ChatroomMessage();
        message.setAdminId(adminId);
        message.setMemberId(memberId);
        message.setContent(content);
        message.setStatus(status);
        message.setClickstatus(clickstatus);
        message.setSendTime(Timestamp.from(Instant.now()));
        
        ChatroomMessage savedMessage = chatroomMessageRepository.save(message);
        
        // 轉換為DTO
        ChatroomMessageDTO dto = ChatroomMessageMapper.toChatroomMessageDTO(savedMessage);
        
        // 手動查詢會員名稱
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        memberOpt.ifPresent(member -> dto.setMemberName(member.getMemberName()));
        
        return dto;
    }
    
    // 新增照片訊息
    @Transactional
    public ChatroomMessageDTO addPhotoMessage(String adminId, String memberId, MultipartFile photo, Integer status, Integer clickstatus) throws IOException {
        ChatroomMessage message = new ChatroomMessage();
        message.setAdminId(adminId);
        message.setMemberId(memberId);
        message.setChatPhoto(photo.getBytes());
        message.setStatus(status);
        message.setClickstatus(clickstatus);
        message.setSendTime(Timestamp.from(Instant.now()));
        
        ChatroomMessage savedMessage = chatroomMessageRepository.save(message);
        
        // 轉換為DTO
        ChatroomMessageDTO dto = ChatroomMessageMapper.toChatroomMessageDTO(savedMessage);
        
        // 手動查詢會員名稱
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        memberOpt.ifPresent(member -> dto.setMemberName(member.getMemberName()));
        
        return dto;
    }
    
    // 檢查會員是否有聊天記錄
    public boolean hasChatMessages(String memberId) {
        List<ChatroomMessage> messages = chatroomMessageRepository.findByMemberIdOrderBySendTimeAsc(memberId);
        return !messages.isEmpty();
    }
}