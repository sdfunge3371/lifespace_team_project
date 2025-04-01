package com.lifespace.dto;

import jakarta.validation.constraints.NotBlank;


public class UpdateFaqDTO {
	@NotBlank(message = "查無資料")
	private String faqId;

	@NotBlank(message = "標題請勿空白")
	private String faqAsk;

	@NotBlank(message = "內容請勿空白")
	private String faqAnswer;

	public String getFaqId() {
		return faqId;
	}

	public void setFaqId(String faqId) {
		this.faqId = faqId;
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
