package com.lifespace.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CommentsDTO {
	private String commentId;
    private String commentMessage;
    private Timestamp commentTime;
    private String memberName;
    private String eventMemberId;
    private String imageUrl;
    private String organizerName;
    private Timestamp orderStart;
    private Timestamp orderEnd;
    private String spaceLocation;
    

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

	public Timestamp getOrderStart() {
		return orderStart;
	}

	public void setOrderStart(Timestamp orderStart) {
		this.orderStart = orderStart;
	}

	public Timestamp getOrderEnd() {
		return orderEnd;
	}

	public void setOrderEnd(Timestamp orderEnd) {
		this.orderEnd = orderEnd;
	}

	public String getSpaceLocation() {
		return spaceLocation;
	}

	public void setSpaceLocation(String spaceLocation) {
		this.spaceLocation = spaceLocation;
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
