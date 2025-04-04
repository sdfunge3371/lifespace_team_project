package com.lifespace.entity;

import java.sql.Timestamp;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity	//要加上@Entity才能成為JPA的一個Entity類別
@Table(name = "news_status")
public class NewsStatusVO {

	@Id
	@Column(name = "news_status_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer newsStatusId;
	
	@Column(name = "Status_name")
	private String statusName;
	
	@Column(name = "created_time")
	private Timestamp createdTime;
	
	// fetch 預設為 LAZY
	@OneToMany(mappedBy = "newsStatus",cascade = CascadeType.ALL)
	@OrderBy("news_id asc")
	@JsonManagedReference //搭配使用，保留輸出的那一端
	private Set<NewsVO> newsSet;
	
	public NewsStatusVO() {
		super();
	}
	
	public NewsStatusVO(Integer newsStatusId) {
		super();
		this.newsStatusId = newsStatusId;
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
	public Set<NewsVO> getNewsSet() {
		return newsSet;
	}
	public void setNewsSet(Set<NewsVO> newsSet) {
		this.newsSet = newsSet;
	}
	
}
