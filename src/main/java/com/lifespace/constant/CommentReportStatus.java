package com.lifespace.constant;

public enum CommentReportStatus {
	UNPROCESSED("檢舉案件尚未處理"), PROCESSED("檢舉案件已處理"),;
	
	private String type;

	private CommentReportStatus(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
