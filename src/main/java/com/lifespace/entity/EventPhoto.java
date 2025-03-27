package com.lifespace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;


@Entity
@Table(name= "event_photo")
public class EventPhoto {

	@Id
	@Column(name= "photo_id")
	private String photoId;

	@Column(name = "event_id")
	private String eventId;

	@Column(name ="photo")
	private String photo;

	@Column(name= "created_time")
	private Timestamp createdTime;


	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
}