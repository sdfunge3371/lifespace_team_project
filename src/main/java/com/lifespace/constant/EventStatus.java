package com.lifespace.constant;

public enum EventStatus {

	CANCELLED("已取消"),
	HELD("已舉辦"),
	SCHEDULED("尚未舉辦");
	
	private final String description;
	
	EventStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}	
}
