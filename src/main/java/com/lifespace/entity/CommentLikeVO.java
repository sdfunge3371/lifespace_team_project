package com.lifespace.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "comment_like")
public class CommentLikeVO implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "like_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer likeId;
	
	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private Member member;
	
//	@Column(name = "member_id")
//	@NotEmpty(message="會員編號: 請勿空白")
//	@Pattern(regexp = "^M\\\\d{3}$", message = "會員編號: 格式必須為M001, M002, ...")
//	private String memberId;
	
	@ManyToOne
	@JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
	private CommentsVO commentsVO;
	
//	@Column(name = "comment_id")
//	@NotEmpty(message="留言編號: 請勿空白")
//	@Pattern(regexp = "^C\\\\d{3}$", message = "留言編號: 格式必須為C001, C002, ...")
//	private String commentId;
	
	@Column(name = "created_time", insertable = false)
	@UpdateTimestamp
	private Timestamp createdTime;
		
	public CommentLikeVO() {
	}	
	
	public Integer getLikeId() {
		return likeId;
	}
	
	public void setLikeId(Integer likeId) {
		this.likeId = likeId;
	}
	
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
//	public String getMemberId() {
//		return memberId;
//	}
//	
//	public void setMemberId(String memberId) {
//		this.memberId = memberId;
//	}
	
	public CommentsVO getCommentsVO() {
		return commentsVO;
	}

	public void setCommentsVO(CommentsVO commentsVO) {
		this.commentsVO = commentsVO;
	}
	
//	public String getCommentId() {
//		return commentId;
//	}
//	
//	public void setCommentId(String commentId) {
//		this.commentId = commentId;
//	}
	
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	
}
