package com.lifespace.model;

import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "space_usage")
public class SpaceUsage implements java.io.Serializable{
	@Id
	@Column(name = "space_usage_id")
	@GeneratedValue(generator = "custom-id")
	@GenericGenerator(name = "custom-id", strategy = "com.lifespace.util.SpaceUsageCustomStringIdGenerator")
	private String spaceUsageId;
	
	@NotBlank(message = "空間用途不得空白")
	@Column(name = "space_usage_name", unique = true)
	private String spaceUsageName;
	
	@Column(name = "created_time", insertable = false)
	private Timestamp createdTime;

	// One to Many
	// 空間用途對照表
	
	
	// Getters & Setters
	public String getSpaceUsageId() {
		return spaceUsageId;
	}

	public void setSpaceUsageId(String spaceUsageId) {
		this.spaceUsageId = spaceUsageId;
	}

	public String getSpaceUsageName() {
		return spaceUsageName;
	}

	public void setSpaceUsageName(String spaceUsageName) {
		this.spaceUsageName = spaceUsageName;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	
	// One to Many相關Getters & Setters
	
}
