package com.lifespace.constant;

public enum CommentsCommentHide {
	VISIBEL("留言顯示"), HIDDEN("留言隱藏"),;

	private final String type;
	
	CommentsCommentHide(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
