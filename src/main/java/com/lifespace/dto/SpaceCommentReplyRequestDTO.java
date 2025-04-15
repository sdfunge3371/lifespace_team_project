package com.lifespace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SpaceCommentReplyRequestDTO {

	@NotNull
	private String orderId;
	
	private String commentReplyContent;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCommentReplyContent() {
		return commentReplyContent;
	}

	public void setCommentReplyContent(String commentReplyContent) {
		this.commentReplyContent = commentReplyContent;
	}

}
