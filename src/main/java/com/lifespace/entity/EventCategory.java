package com.lifespace.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="event_category")
public class EventCategory implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	 @Id
	 @Column(name="event_category_id")
	 private String eventCategoryId;
	 
	 @Column(name="event_category_name")
	 private String eventCategoryName;

	 @Column(name="created_time")
	 private Timestamp createdName;

	public String getEventCategoryId() {
		return eventCategoryId;
	}

	public void setEventCategoryId(String eventCategoryId) {
		this.eventCategoryId = eventCategoryId;
	}

	public String getEventCategoryName() {
		return eventCategoryName;
	}

	public void setEventCategoryName(String eventCategoryName) {
		this.eventCategoryName = eventCategoryName;
	}

	public Timestamp getCreatedName() {
		return createdName;
	}

	public void setCreatedName(Timestamp createdName) {
		this.createdName = createdName;
	}
	 
}
