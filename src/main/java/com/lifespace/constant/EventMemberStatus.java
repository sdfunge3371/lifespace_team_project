package com.lifespace.constant;

public enum EventMemberStatus {

	CANCELLED("已取消"),
	ATTENT("已參加"),
	QUEUED("候補");
	
	private final String description;
	
	EventMemberStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}	
	
}
