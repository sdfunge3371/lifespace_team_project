package com.lifespace.dto;

import java.sql.Timestamp;
import java.util.List;

public class NewsCategoryDTO implements java.io.Serializable {

	private String newsCategoryId;
	private String categoryName;
	private Timestamp createdTime;
	private List<NewsDTO> newsList;




	public List<NewsDTO> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<NewsDTO> newsList) {
		this.newsList = newsList;
	}

	public String getNewsCategoryId() {
		return newsCategoryId;
	}

	public void setNewsCategoryId(String newsCategoryId) {
		this.newsCategoryId = newsCategoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

}
