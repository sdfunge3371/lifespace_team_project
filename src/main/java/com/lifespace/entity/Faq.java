package com.lifespace.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity	//要加上@Entity才能成為JPA的一個Entity類別
@Table(name = "faq")
public class Faq {
	
	@Id
	@Column(name = "faq_id")
	@GeneratedValue(generator = "faq_id")
	@GenericGenerator(name = "faq_id", strategy = "com.lifespace.util.FaqCustomStringIdGenerator")
	private String faqId;
	
	@Column(name = "admin_id")
	private String adminId;
	
	@Column(name = "faq_ask", nullable = false)
	private String faqAsk;
	
	@Column(name = "faq_answer", nullable = false)
	private String faqAnswer;
	
	@Column(name = "faq_status", nullable = false)
	private Integer faqStatus;
	
	@Column(name = "created_time")
	private Timestamp createTime; 
	


	public Faq() {
		super();
		
	}
	public Faq(String faqId, String adminId, String faqAsk, String faqAnswer, Integer faqStatus, Timestamp createTime) {
		super();
		this.faqId = faqId;
		this.adminId = adminId;
		this.faqAsk = faqAsk;
		this.faqAnswer = faqAnswer;
		this.faqStatus = faqStatus;
		this.createTime = createTime;
	}
	
	
	
	public String getFaqId() {
		return faqId;
	}
	public void setFaqId(String faqId) {
		this.faqId = faqId;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getFaqAsk() {
		return faqAsk;
	}
	public void setFaqAsk(String faqAsk) {
		this.faqAsk = faqAsk;
	}
	public String getFaqAnswer() {
		return faqAnswer;
	}
	public void setFaqAnswer(String faqAnswer) {
		this.faqAnswer = faqAnswer;
	}
	public Integer getFaqStatus() {
		return faqStatus;
	}
	public void setFaqStatus(Integer faqStatus) {
		this.faqStatus = faqStatus;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
