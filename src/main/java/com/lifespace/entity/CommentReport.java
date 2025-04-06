package com.lifespace.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.lifespace.constant.CommentReportManageType;
import com.lifespace.constant.CommentReportReportReason;
import com.lifespace.constant.CommentReportStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "comment_report")
public class CommentReport implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "report_id", updatable = false)
	@GeneratedValue(generator = "custom-id")
	@GenericGenerator(name = "custom-id", strategy = "com.lifespace.util.CommentReportCustomStringIdGenerator")
	private String reportId;
	
	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private Member member;
	
//	@Column(name = "member_id")
//	@NotEmpty(message="會員編號: 請勿空白")
//	@Pattern(regexp = "^M\\\\d{3}$", message = "會員編號: 格式必須為M001, M002, ...")
//	private String memberId;
	
	@ManyToOne
	@JoinColumn(name = "admin_id", referencedColumnName = "admin_id")
	private Admin admin;
	
//	@Column(name = "admin_id")
//	@NotEmpty(message="管理者編號: 請勿空白")
//	@Pattern(regexp = "^A\\\\d{3}$", message = "管理者編號: 格式必須為A001, A002, ...")
//	private String adminId;
	
	@ManyToOne
	@JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
	private Comments comments;
	
//	@Column(name = "comment_id")
//	@NotEmpty(message="留言編號: 請勿空白")
//	@Pattern(regexp = "^C\\\\d{3}$", message = "留言編號: 格式必須為C001, C002, ...")
//	private String commentId;
	
	@Column(name = "manage_type")
	private CommentReportManageType manageType;
	
	@Column(name = "close_time", insertable = false)
	@UpdateTimestamp
	private Timestamp closeTime;
	
	@Column(name = "report_message")
	@NotEmpty(message="檢舉內容: 請勿空白")
	private String reportMessage;
	
	@Column(name = "report_reason")
	@NotEmpty(message="檢舉原因: 請勿空白")
	private CommentReportReportReason reportReason;
	
	@Column(name = "status")
	@NotNull(message="案件處理狀態: 請勿空白")
	private CommentReportStatus status;
	
//	private Timestamp createdTime;
	
	public CommentReport() {
	}
	
	public String getReportId() {
		return reportId;
	}
	
	public void setReportId(String reportId) {
		this.reportId = reportId;
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
	
//	public String getAdminId() {
//		return adminId;
//	}
//	
//	public void setAdminId(String adminId) {
//		this.adminId = adminId;
//	}
	
//	public String getCommentId() {
//		return commentId;
//	}
//	
//	public void setCommentId(String commentId) {
//		this.commentId = commentId;
//	}
	
	public CommentReportManageType getManageType() {
		return manageType;
	}
	
	public void setManageType(CommentReportManageType manageType) {
		this.manageType = manageType;
	}
	
	public Timestamp getCloseTime() {
		return closeTime;
	}
	
	public void setCloseTime(Timestamp closeTime) {
		this.closeTime = closeTime;
	}
	
	public String getReportMessage() {
		return reportMessage;
	}
	
	public void setReportMessage(String reportMessage) {
		this.reportMessage = reportMessage;
	}
	
	public CommentReportReportReason getReportReason() {
		return reportReason;
	}
	
	public void setReportReason(CommentReportReportReason reportReason) {
		this.reportReason = reportReason;
	}
	
	public CommentReportStatus getStatus() {
		return status;
	}
	
	public void setStatus(CommentReportStatus status) {
		this.status = status;
	}
	
//	public Timestamp getCreatedTime() {
//		return createdTime;
//	}
//	public void setCreatedTime(Timestamp createdTime) {
//		this.createdTime = createdTime;
//	}
}
