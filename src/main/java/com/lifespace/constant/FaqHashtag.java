package com.lifespace.constant;

import java.util.Optional;

// FAQ 分類 Hashtag，用於 Redis Key 與前端分類按鈕對應
// 
public enum FaqHashtag {

	USAGE("使用規範"),
	RENTAL("租賃相關"),
	PAYMENT("付款方式"),
	OTHER("其他");
	
	// 每個列舉常數的資料欄位
	// Java 編譯器自動產生public static final FaqHashtag USAGE = new FaqHashtag("使用規範");
	private final String displayName;
	
	// 建構子（只能 Enum 自己用）
	private FaqHashtag(String displayName) {
		this.displayName = displayName;
	}
	
	// 提供外部讀取 displayName 的方法
	public String getDisplayName() {
		return displayName;
	}
	
	// 前端傳來的「按鈕文字」轉成Enum，例如「使用規範」轉成 USAGE
	public static Optional<FaqHashtag> fromDisplayName(String displayName) {
		// values() 是Enum類別的內建方法，會回傳一個包含所有列舉常數的陣列（FaqHashtag[]）
		for (FaqHashtag tag : values()) {
			// 比對enum顯示名稱與接收的字串是否相等
			if (tag.getDisplayName().equals(displayName)) {
				//包裝一個「非空」的值
				return Optional.of(tag);
			}
		}
		// 迴圈結束沒找到就回傳一個空的Optional
			return Optional.empty();	
	}
}

// 把 Enum 轉成 Stream → Arrays.stream(FaqHashtag.values())
// public static Optional<FaqHashtag> fromDisplayName(String displayName) {
// 使用 filter 篩選出符合顯示名稱的列舉
//使用 findFirst() 回傳第一個符合的值（包在 Optional 裡）
// return Arrays.stream(FaqHashtag.values()) // 把 Enum 轉成 Stream
//             .filter(tag -> tag.getDisplayName().equals(displayName)) // 篩選符合條件的項目
//             .findFirst(); // 回傳第一個找到的包成 Optional
//}
