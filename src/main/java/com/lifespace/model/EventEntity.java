package com.lifespace.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="event")
public class EventEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	 @Id
	 @Column(name="event_id")
	 private String eventId;
	 
	 @Column(name="event_name")
	 private String eventName;

	 @Column(name="event_date")
	 private Timestamp eventDate;
	 
	 @Column(name="event_start_time")
	 private Timestamp eventStartTime;
	 
	 @Column(name="event_end_time")
	 private Timestamp eventEndTime;
	 
	 @Column(name="event_category")
	 private String eventCategory;
	 
	 @Column(name="space_id")
	 private String spaceId;
	 
	 @Column(name = "member_id")
	 private String memberId;
	 
	 @Column(name="number_of_participants")
	 private Integer numberOfParticipants = 0;
	 
	 @Column(name="maximum_of_participants")
	 private Integer maximumOfParticipants;
	 
	 @Column(name="event_briefing")
	 private String eventBriefing;

	 @Column(name="remarks")
	 private String remarks;
	 
	 @Column(name="host_speaking")
	 private String hostSpeaking;
	 
	 @Column(name="created_time")
	 private Timestamp createdTime;

	 //OneToMany關聯，一個Event有多個EventMember
	 @OneToMany(mappedBy = "event")
	 private List<EventMemberEntity> eventMembers;
	 
	 @OneToMany(mappedBy = "event")
	 private List<EventPhotoEntity> eventPhotos;
	 
	// 新增一個方法來獲取照片 URL 的列表
	    public List<String> getPhotoUrls() {
	        if (eventPhotos != null) {
	            return eventPhotos.stream()
	                    .map(EventPhotoEntity::getPhoto) // Assuming getPhoto() returns the URL
	                    .collect(Collectors.toList());
	        }
	        return null;
	    }
	    
	 
	public String getEventId() {
		return eventId;
	}

	public EventEntity() {
		
	}

	public EventEntity(String eventId, String eventName, Timestamp eventDate, Timestamp eventStartTime,
			Timestamp eventEndTime, String eventCategory, String spaceId, String memberId, Integer numberOfParticipants,
			Integer maximumOfParticipants, String eventBriefing, String remarks, String hostSpeaking,
			Timestamp createdTime) {
		super();
		this.eventId = eventId;
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.eventStartTime = eventStartTime;
		this.eventEndTime = eventEndTime;
		this.eventCategory = eventCategory;
		this.spaceId = spaceId;
		this.memberId = memberId;
		this.numberOfParticipants = numberOfParticipants;
		this.maximumOfParticipants = maximumOfParticipants;
		this.eventBriefing = eventBriefing;
		this.remarks = remarks;
		this.hostSpeaking = hostSpeaking;
		this.createdTime = createdTime;
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
		
	    
}
