package com.lifespace.dto;

import java.sql.Timestamp;
import java.util.List;

public class NewsStatusDTO implements java.io.Serializable{


	private Integer newsStatusId;
	private String statusName;
	private Timestamp createdTime;
	private List<NewsDTO> newsList;
	
	
	public List<NewsDTO> getNewsList() {
		return newsList;
	}
	public void setNewsList(List<NewsDTO> newsList) {
		this.newsList = newsList;
	}
	public Integer getNewsStatusId() {
		return newsStatusId;
	}
	public void setNewsStatusId(Integer newsStatusId) {
		this.newsStatusId = newsStatusId;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

}
