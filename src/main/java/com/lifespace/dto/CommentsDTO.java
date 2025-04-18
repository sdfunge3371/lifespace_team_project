package com.lifespace.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.lifespace.entity.Comments;
import com.lifespace.entity.EventMember;

public class CommentsDTO {
	private String commentId;
    private String commentMessage;
    private Timestamp commentTime;
    private String memberName;
    private String eventMemberId;
    private String imageUrl;
    private String organizerName;
    private LocalDateTime orderStart;
    private LocalDateTime orderEnd;
    private String spaceLocation;
    private String eventId;
    private EventMember eventMember;
    
    

//    private String branchAddr;
    
	public CommentsDTO() {
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getCommentMessage() {
		return commentMessage;
	}

	public void setCommentMessage(String commentMessage) {
		this.commentMessage = commentMessage;
	}

	public Timestamp getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(Timestamp commentTime) {
		this.commentTime = commentTime;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getEventMemberId() {
		return eventMemberId;
	}

	public void setEventMemberId(String eventMemberId) {
		this.eventMemberId = eventMemberId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getOrganizerName() {
		return organizerName;
	}

	public void setOrganizerName(String organizerName) {
		this.organizerName = organizerName;
	}

	public LocalDateTime getOrderStart() {
		return orderStart;
	}

	public void setOrderStart(LocalDateTime orderStart) {
		this.orderStart = orderStart;
	}

	public LocalDateTime getOrderEnd() {
		return orderEnd;
	}

	public void setOrderEnd(LocalDateTime orderEnd) {
		this.orderEnd = orderEnd;
	}

	public String getSpaceLocation() {
		return spaceLocation;
	}

	public void setSpaceLocation(String spaceLocation) {
		this.spaceLocation = spaceLocation;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public EventMember getEventMember() {
		return eventMember;
	}

	public void setEventMember(EventMember eventMember) {
		this.eventMember = eventMember;
	}

	// 將 DTO 轉成 Entity，用於儲存留言時使用
	public Comments toEntity() {
	    Comments entity = new Comments();
	    entity.setCommentMessage(this.commentMessage);

		if (this.eventMemberId != null) {
			EventMember eventMember = new EventMember();
			eventMember.setEventMemberId(this.eventMemberId);
			entity.setEventMember(eventMember);
		}

	    return entity;
	}

	
	
	
//	public void setSpaceLocation(String spaceName) {
//		
//	}
		

//	public String getBranchAddr() {
//		return branchAddr;
//	}
//
//	public void setBranchAddr(String branchAddr) {
//		this.branchAddr = branchAddr;
//	}
       
}