package com.lifespace.dto;

import java.sql.Timestamp;


public class NewsDTO implements java.io.Serializable{

	private String newsId;
	private String newsTitle;
	private String adminId;
	private String newsContent;
	private Timestamp newsStartDate;
	private Timestamp newsEndDate;
	private Timestamp createdTime;
	private byte[] newsImg;
	
	private String newsCategoryId;
	private String newsCategoryName;
	private Integer newsStatusId;
	private String newsStatusName;
	
	public String getNewsCategoryName() {
		return newsCategoryName;
	}
	public void setNewsCategoryName(String newsCategoryName) {
		this.newsCategoryName = newsCategoryName;
	}
	public String getNewsStatusName() {
		return newsStatusName;
	}
	public void setNewsStatusName(String newsStatusName) {
		this.newsStatusName = newsStatusName;
	}
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
	public Timestamp getNewsStartDate() {
		return newsStartDate;
	}
	public void setNewsStartDate(Timestamp newsStartDate) {
		this.newsStartDate = newsStartDate;
	}
	public Timestamp getNewsEndDate() {
		return newsEndDate;
	}
	public void setNewsEndDate(Timestamp newsEndDate) {
		this.newsEndDate = newsEndDate;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	public Integer getNewsStatusId() {
		return newsStatusId;
	}
	public void setNewsStatusId(Integer newsStatusId) {
		this.newsStatusId = newsStatusId;
	}
	public byte[] getNewsImg() {
		return newsImg;
	}
	public void setNewsImg(byte[] newsImg) {
		this.newsImg = newsImg;
	}
	
	
}
