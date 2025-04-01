package com.lifespace.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SpaceCommentRequest {

	@NotNull
	private String orderId;
	
	@NotBlank
	private Integer rating = 1;
	  
	private String comments;
	  
	private List<MultipartFile> photos;
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<MultipartFile> getPhotos() {
		return photos;
	}

	public void setPhotos(List<MultipartFile> photos) {
		this.photos = photos;
	}

	public SpaceCommentRequest(@NotBlank Integer rating, String comments, List<MultipartFile> photos) {
		this.rating = rating;
		this.comments = comments;
		this.photos = photos;
	}

	public SpaceCommentRequest() {
		
	}
	  
}
