package com.lifespace.dto;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


public class FaqDTO implements java.io.Serializable{

	private String faqId;
	private String adminId;
	private String faqAsk;
	private String faqAnswer;
	private Integer faqStatus;
	private Timestamp createTime; 

	public FaqDTO() {
		super();
		
	}
	
	public FaqDTO(String faqId, String adminId, String faqAsk, String faqAnswer, Integer faqStatus, Timestamp createTime) {
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
