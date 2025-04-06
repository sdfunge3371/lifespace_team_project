package com.lifespace.dto;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class EventResponse {
 
	 private String eventId;
	 
	 private String eventName;
	 
	 private Timestamp eventStartTime;
	 
	 private Timestamp eventEndTime;
	 
	 private String eventCategoryName;
	 
	 private String eventStatus;
	 
	 private String spaceAddress;
	 
	 private String organizer;
	 
	 private Integer numberOfParticipants;
	 
	 private Integer maximumOfParticipants;
	 
	 private String eventBriefing;

	 private String remarks;
	 
	 private String hostSpeaking;
	 
	 private Timestamp createdTime;
	 
	 private List<String> photoUrls;
	 
	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
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

	public String getEventCategoryName() {
		return eventCategoryName;
	}

	public void setEventCategoryName(String eventCategoryName) {
		this.eventCategoryName = eventCategoryName;
	}

	public String getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}

	public String getSpaceAddress() {
		return spaceAddress;
	}

	public void setSpaceAddress(String spaceAddress) {
		this.spaceAddress = spaceAddress;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public Integer getNumberOfParticipants() {
		return numberOfParticipants;
	}

	public void setNumberOfParticipants(Integer numberOfParticipants) {
		this.numberOfParticipants = numberOfParticipants;
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

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public EventResponse() {

	}

	public List<String> getPhotoUrls() {
		return photoUrls;
	}

	public void setPhotoUrls(List<String> photoUrls) {
		this.photoUrls = photoUrls;
	}
	
	// 添加一個全參數構造函數，參數順序要與 SQL 查詢的結果列順序完全匹配
    public EventResponse(
        String eventId, 
        String eventName, 
        Timestamp eventStartTime,
        Timestamp eventEndTime, 
        String eventCategoryName,
        String eventStatus,
        Integer numberOfParticipants, 
        Integer maximumOfParticipants, 
        String eventBriefing, 
        String remarks,
        String hostSpeaking, 
        Timestamp createdTime,
        String spaceAddress,         
        String organizer,       
        String photoUrls           // 從查詢結果中的 GROUP_CONCAT(ep.photo) 映射
    ) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventCategoryName = eventCategoryName;
        this.eventStatus = eventStatus;
        this.numberOfParticipants = numberOfParticipants;
        this.maximumOfParticipants = maximumOfParticipants;
        this.eventBriefing = eventBriefing;
        this.remarks = remarks;
        this.hostSpeaking = hostSpeaking;
        this.createdTime = createdTime;
        this.organizer = organizer;
        this.spaceAddress = spaceAddress;
        
        // 如果 photoUrls 是字符串，需要將其轉換為 List
        if (photoUrls != null) {
            this.photoUrls = Arrays.asList(photoUrls.split(","));
        }
    }
	
}
