package com.lifespace.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.GenericGenerator;

import com.lifespace.constant.EventStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name="event")
public class Event implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	 @Id
	 @Column(name="event_id")
	 @GeneratedValue(generator = "event_id")
	 @GenericGenerator(name = "event_id", strategy = "com.lifespace.util.EventCustomStringIdGenerator")
	 private String eventId;
	 
	 @Column(name="event_name")
	 private String eventName;

	 @Column(name="event_start_time")
	 private Timestamp eventStartTime;
	 
	 @Column(name="event_end_time")
	 private Timestamp eventEndTime;
	 
	 @ManyToOne
	 @JoinColumn(name = "event_category_id", referencedColumnName = "event_category_id")
	 private EventCategory eventCategory;
	  
	 @Enumerated(EnumType.STRING)
	 @Column(name="event_status")
	 private EventStatus eventStatus = EventStatus.SCHEDULED;
	 
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
	 private List<EventMember> eventMembers;
	 
	 @OneToMany(mappedBy = "event")
	 private List<EventPhoto> eventPhotos;

	 @OneToMany(mappedBy = "event")
	 @OrderBy ("orderId asc")
	 private Set<Orders> orders = new HashSet<>() ;
	 
	// 新增一個方法來獲取照片 URL 的列表
	    public List<String> getPhotoUrls() {
	        if (eventPhotos != null) {
	            return eventPhotos.stream()
	                    .map(EventPhoto::getPhoto) // Assuming getPhoto() returns the URL
	                    .collect(Collectors.toList());
	        }
	        return null;
	    }
	    
	 
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

	public EventCategory getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(EventCategory eventCategory) {
		this.eventCategory = eventCategory;
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


	public EventStatus getEventStatus() {
		return eventStatus;
	}


	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}
		
	    
}
