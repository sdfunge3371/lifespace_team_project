package com.lifespace.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public class EventRequest {

	 	@NotBlank
	 	private String eventName;
	 	
	    @NotNull
	    private Timestamp eventDate;
	 
	    @NotNull
	    private Timestamp eventStartTime;
	    
	    @NotNull
	    private Timestamp eventEndTime;
 
	    @NotBlank
	    private String eventCategory;
	    
	    @NotBlank
	    private String spaceId;
	    
	    @NotBlank
	    private String memberId;
	    
	    @NotNull
	    private Integer maximumOfParticipants;
	    
	    private String eventBriefing;
	    
	    private String remarks;

	    private String hostSpeaking;
	    
	    private List<MultipartFile> photos; // 接收多個檔案

		public String getEventName() {
			return eventName;
		}

		public void setEventName(String eventName) {
			this.eventName = eventName;
		}

		public Timestamp getEventDate() {
			return eventDate;
		}

		public void setEventDate(Timestamp eventDate) {
			this.eventDate = eventDate;
		}

		public Timestamp getEventStartTime() {
			return eventStartTime;
		}

		public void setEventStartTime(Timestamp eventStartTime) {
			this.eventStartTime = eventStartTime;
		}

		public Timestamp getEventEndTime() {
			return eventEndTime;
		}

		public void setEventEndTime(Timestamp eventEndTime) {
			this.eventEndTime = eventEndTime;
		}

		public String getEventCategory() {
			return eventCategory;
		}

		public void setEventCategory(String eventCategory) {
			this.eventCategory = eventCategory;
		}

		public String getSpaceId() {
			return spaceId;
		}

		public void setSpaceId(String spaceId) {
			this.spaceId = spaceId;
		}

		public String getMemberId() {
			return memberId;
		}

		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}

		public Integer getMaximumOfParticipants() {
			return maximumOfParticipants;
		}

		public void setMaximumOfParticipants(Integer maximumOfParticipants) {
			this.maximumOfParticipants = maximumOfParticipants;
		}

		public String getEventBriefing() {
			return eventBriefing;
		}

		public void setEventBriefing(String eventBriefing) {
			this.eventBriefing = eventBriefing;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public String getHostSpeaking() {
			return hostSpeaking;
		}

		public void setHostSpeaking(String hostSpeaking) {
			this.hostSpeaking = hostSpeaking;
		}

		public List<MultipartFile> getPhotos() {
			return photos;
		}

		public void setPhotos(List<MultipartFile> photos) {
			this.photos = photos;
		}
	    
	    
}
