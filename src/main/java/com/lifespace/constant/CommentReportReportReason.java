package com.lifespace.constant;

public enum CommentReportReportReason {
	HATEFUL_CONTENT("仇恨言論"),
	FALSE_INFORMATION("不實資訊"),
	SPAM_MESSAGE("垃圾訊息"),
	MARKETING_OR_ADS("推銷/廣告"),
	HARASSMENT_CONTENT("騷擾內容"),;
	
	private String type;

	CommentReportReportReason(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
