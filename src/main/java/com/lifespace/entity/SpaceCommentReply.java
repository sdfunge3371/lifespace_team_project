package com.lifespace.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "space_comment_reply")
public class SpaceCommentReply implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	 @Id
	 @Column(name="space_comment_reply_id")
	 private String spaceCommentReplyId;
	 
	 @OneToOne
     @JoinColumn(name = "order_id", referencedColumnName = "order_id") 
	 private Orders order;

	 @Column(name="comment_reply_content")
	 private String commentReplyContent;
	 
	 @Column(name="created_time")
	 private Timestamp createdTime;

	public String getSpaceCommentReplyId() {
		return spaceCommentReplyId;
	}

	public void setSpaceCommentReplyId(String spaceCommentReplyId) {
		this.spaceCommentReplyId = spaceCommentReplyId;
	}

	public Orders getOrder() {
		return order;
	}

	public void setOrder(Orders order) {
		this.order = order;
	}

	public String getCommentReplyContent() {
		return commentReplyContent;
	}

	public void setCommentReplyContent(String commentReplyContent) {
		this.commentReplyContent = commentReplyContent;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	 
}
