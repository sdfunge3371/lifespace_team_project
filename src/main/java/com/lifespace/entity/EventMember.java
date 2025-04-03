package com.lifespace.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import com.lifespace.constant.EventMemberStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="event_member")
public class EventMember implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	
	 @Id
	 @Column(name="event_member_id")
	 @GeneratedValue(generator = "event_member_id")
	 @GenericGenerator(name = "event_member_id", strategy = "com.lifespace.util.EventMemberCustomStringIdGenerator")
	 private String eventMemberId;
	 
	 @ManyToOne
	 @JoinColumn(name = "event_id", referencedColumnName = "event_id")
	 private Event event;

	 @ManyToOne
	 @JoinColumn(name="member_id", referencedColumnName = "member_id")
	 private Member member;
	 
	 @Enumerated(EnumType.STRING)
	 @Column(name="participate_status")
	 private EventMemberStatus participateStatus = EventMemberStatus.ATTENT;
	 
	 @Column(name="created_time")
	 private Timestamp createdTime;

	public String getEventMemberId() {
		return eventMemberId;
	}

	public void setEventMemberId(String eventMemberId) {
		this.eventMemberId = eventMemberId;
	}

	

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public EventMemberStatus getParticipateStatus() {
		return participateStatus;
	}

	public void setParticipateStatus(EventMemberStatus participateStatus) {
		this.participateStatus = participateStatus;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

}
