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
	 @GeneratedValue(generator = "space_comment_reply_id")
	 @GenericGenerator(name = "space_comment_reply_id", strategy = "com.lifespace.util.SpaceCommentReplyCustomStringIdGenerator")
	 private String spaceCommentReplyId;
	 
	 @OneToOne
     @JoinColumn(name = "order_id", referencedColumnName = "order_id") 
	 private Orders orders;

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

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
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
