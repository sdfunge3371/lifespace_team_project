package com.lifespace.dto;

import java.sql.Timestamp;
import java.util.Base64;

public class ChatroomMessageDTO {
    private Integer chatroomMessageId;
    private String adminId;
    private String memberId;
    private String memberName;
    private String content;
    private Integer status;
    private Integer clickstatus;
    private byte[] chatPhoto;
    private String chatPhotoBase64;
    private Timestamp sendTime;
    
    public ChatroomMessageDTO() {
    }
    
    public Integer getChatroomMessageId() {
        return chatroomMessageId;
    }

    public void setChatroomMessageId(Integer chatroomMessageId) {
        this.chatroomMessageId = chatroomMessageId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getClickstatus() {
        return clickstatus;
    }

    public void setClickstatus(Integer clickstatus) {
        this.clickstatus = clickstatus;
    }

    public byte[] getChatPhoto() {
        return chatPhoto;
    }

    public void setChatPhoto(byte[] chatPhoto) {
        this.chatPhoto = chatPhoto;
        if (chatPhoto != null) {
            this.chatPhotoBase64 = Base64.getEncoder().encodeToString(chatPhoto);
        }
    }
    
    public String getChatPhotoBase64() {
        return chatPhotoBase64;
    }
    
    public void setChatPhotoBase64(String chatPhotoBase64) {
        this.chatPhotoBase64 = chatPhotoBase64;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }
}