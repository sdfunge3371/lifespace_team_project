package com.lifespace.constant;

public enum SpaceUsageStatus {
	DELETED("已刪除"),
	AVAILABLE("可用");
	
	private final String description;
	
	SpaceUsageStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}	
}
