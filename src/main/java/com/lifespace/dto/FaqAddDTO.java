package com.lifespace.dto;

import jakarta.validation.constraints.NotBlank;

// 處理「新增 FAQ」時表單資料的接收&提供欄位驗證（表單驗證錯誤時可以回傳錯誤訊息給前端）

public class FaqAddDTO {
	
	//@NotBlank：確保欄位不可為 null 且不能是空白字串（""）
	//若驗證不通過，Spring Boot 會將錯誤訊息包成 BindingResult 回傳給使用者
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
