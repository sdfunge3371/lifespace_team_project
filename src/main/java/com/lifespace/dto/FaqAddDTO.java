package com.lifespace.dto;

import jakarta.validation.constraints.NotBlank;




public class FaqAddDTO {
	@NotBlank(message = "請登入後台")
	private String adminId;

	@NotBlank(message = "標題請勿空白")
	private String faqAsk;

	@NotBlank(message = "內容請勿空白")
	private String faqAnswer;
	
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
}
