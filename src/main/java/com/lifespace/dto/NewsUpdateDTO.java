package com.lifespace.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//	不可在DTO中宣告byte[] newsImg，否則 Spring 會嘗試自動將MultipartFile轉成byte[]，導致綁定失敗。
//	建議改由Controller使用MultipartFile手動讀取圖片內容後，傳給 Service 儲存。
public class NewsUpdateDTO implements java.io.Serializable {

	private String newsId;
	
	@NotBlank(message = "標題請勿空白")
	private String newsTitle;

	@NotBlank(message = "內容請勿空白")
	private String newsContent;

	// 前端form或AJAX傳上來的都是字串格式的資料，Spring 無法自動轉型Timestamp
	// 所以用LocalDateTime後再到service進行型別轉換成Timestamp
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@NotNull(message = "請選擇起始日")
	private LocalDateTime newsStartDate;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@NotNull(message = "請選擇起始日")
	private LocalDateTime newsEndDate;

	@NotBlank(message = "請選擇類別")
	private String newsCategoryId;
	@NotNull(message = "請選擇狀態")
	private Integer newsStatusId;

	
	public String getNewsId() {
		return newsId;
	}

	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getNewsCategoryId() {
		return newsCategoryId;
	}

	public void setNewsCategoryId(String newsCategoryId) {
		this.newsCategoryId = newsCategoryId;
	}

	public String getNewsContent() {
		return newsContent;
	}

	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}

	public LocalDateTime getNewsStartDate() {
		return newsStartDate;
	}

	public void setNewsStartDate(LocalDateTime newsStartDate) {
		this.newsStartDate = newsStartDate;
	}

	public LocalDateTime getNewsEndDate() {
		return newsEndDate;
	}

	public void setNewsEndDate(LocalDateTime newsEndDate) {
		this.newsEndDate = newsEndDate;
	}

	public Integer getNewsStatusId() {
		return newsStatusId;
	}

	public void setNewsStatusId(Integer newsStatusId) {
		this.newsStatusId = newsStatusId;
	}
}
