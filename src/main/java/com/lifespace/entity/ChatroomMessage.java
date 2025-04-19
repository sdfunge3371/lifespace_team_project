package com.lifespace.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "chatroom_message")
public class ChatroomMessage implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_message_id")
    private Integer chatroomMessageId;
    
    @Column(name = "admin_id")
    private String adminId;
    
    @Column(name = "member_id")
    private String memberId;
    
    @Column(name = "content")
    private String content;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "clickstatus")
    private Integer clickstatus;
    
    @Column(name = "chat_photo")
    private byte[] chatPhoto;
    
    @Column(name = "send_time")
    private Timestamp sendTime;
    
    // 關聯到 Member 實體
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;
    
    // 關聯到 Admin 實體
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", insertable = false, updatable = false)
    private Admin admin;
    
    // 建構子
    public ChatroomMessage() {
    }
    
    // Getter 和 Setter 方法
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
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}