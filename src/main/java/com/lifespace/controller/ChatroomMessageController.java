package com.lifespace.controller;

import com.lifespace.SessionUtils;
import com.lifespace.dto.ChatroomMessageDTO;
import com.lifespace.service.ChatroomMessageService;
import com.lifespace.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatroom")
public class ChatroomMessageController {
    
    @Autowired
    private ChatroomMessageService chatroomMessageService;
    
    @Autowired
    private MemberService memberService;
    
    // 獲取會員的所有聊天記錄
    @GetMapping("/messages/{memberId}")
    public ResponseEntity<List<ChatroomMessageDTO>> getMemberMessages(@PathVariable String memberId) {
        List<ChatroomMessageDTO> messages = chatroomMessageService.getMemberMessages(memberId);
        return ResponseEntity.ok(messages);
    }
    
    // 獲取會員是否有聊天記錄
    @GetMapping("/has-messages")
    public ResponseEntity<?> hasChatMessages(HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }
        
        boolean hasMessages = chatroomMessageService.hasChatMessages(memberId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasMessages", hasMessages);
        return ResponseEntity.ok(response);
    }
    
    // 獲取當前登入會員的所有聊天記錄
    @GetMapping("/my-messages")
    public ResponseEntity<?> getMyMessages(HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }
        
        List<ChatroomMessageDTO> messages = chatroomMessageService.getMemberMessages(memberId);
        return ResponseEntity.ok(messages);
    }
    
    // 獲取所有會員的最後一條訊息
    @GetMapping("/latest-messages")
    public ResponseEntity<List<ChatroomMessageDTO>> getLatestMessageForEachMember() {
        List<ChatroomMessageDTO> messages = chatroomMessageService.getLatestMessageForEachMember();
        return ResponseEntity.ok(messages);
    }
    
    // 根據會員ID模糊搜尋最後一條訊息
    @GetMapping("/search/{memberId}")
    public ResponseEntity<List<ChatroomMessageDTO>> searchLatestMessageByMemberId(@PathVariable String memberId) {
        List<ChatroomMessageDTO> messages = chatroomMessageService.getLatestMessageForEachMemberByMemberIdContaining(memberId);
        return ResponseEntity.ok(messages);
    }
    
    // 更新最後一條訊息的點擊狀態
    @PostMapping("/update-click-status/{memberId}")
    public ResponseEntity<String> updateClickStatus(@PathVariable String memberId) {
        chatroomMessageService.updateClickStatusForLatestMessage(memberId, 1);
        return ResponseEntity.ok("點擊狀態已更新");
    }
    
    // 新增文字訊息 (會員發送給管理員)
    @PostMapping("/send-message")
    public ResponseEntity<?> sendTextMessage(@RequestParam String content, HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }
        
        // 管理員ID暫時固定為A001，實際應用中可能需要根據會員分配特定管理員
        String adminId = "A001";
        ChatroomMessageDTO message = chatroomMessageService.addTextMessage(adminId, memberId, content, 0, 0);
        return ResponseEntity.ok(message);
    }
    
    // 新增照片訊息 (會員發送給管理員)
    @PostMapping(value = "/send-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendPhotoMessage(@RequestParam("photo") MultipartFile photo, HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }
        
        try {
            // 管理員ID暫時固定為A001
            String adminId = "A001";
            ChatroomMessageDTO message = chatroomMessageService.addPhotoMessage(adminId, memberId, photo, 0, 0);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上傳照片失敗");
        }
    }
    
    // 新增文字訊息 (管理員發送給會員)
    @PostMapping("/admin/send-message/{memberId}")
    public ResponseEntity<?> adminSendTextMessage(@PathVariable String memberId, @RequestParam String content, HttpSession session) {
        String adminId = SessionUtils.getLoginAdminId(session);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("管理員尚未登入");
        }
        
        ChatroomMessageDTO message = chatroomMessageService.addTextMessage(adminId, memberId, content, 1, 1);
        return ResponseEntity.ok(message);
    }
    
    // 新增照片訊息 (管理員發送給會員)
    @PostMapping(value = "/admin/send-photo/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> adminSendPhotoMessage(@PathVariable String memberId, @RequestParam("photo") MultipartFile photo, HttpSession session) {
        String adminId = SessionUtils.getLoginAdminId(session);
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("管理員尚未登入");
        }
        
        try {
            ChatroomMessageDTO message = chatroomMessageService.addPhotoMessage(adminId, memberId, photo, 1, 1);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上傳照片失敗");
        }
    }
    
    // 獲取照片
    @GetMapping(value = "/image/{messageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getChatImage(@PathVariable Integer messageId) {
        // 此處簡化，實際應用可能需要先查詢消息再返回照片
        return ResponseEntity.ok().body(null); // 實際應該返回照片數據
    }
}