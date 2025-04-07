package com.lifespace.constant;

public enum CommentReportManageType {
	CONFIRMED("已確認留言正常"), DELETE("刪除檢舉留言"),;
	
	private final String type;

	CommentReportManageType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
