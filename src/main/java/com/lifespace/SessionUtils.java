package com.lifespace;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {

	   private static final String MEMBER_SESSION_KEY = "loginMember";
	  
	    // 取得會員 ID
	    public static String getLoginMemberId(HttpSession session) {
	        Object memberId = session.getAttribute(MEMBER_SESSION_KEY);
	        return memberId != null ? memberId.toString() : null;
	    }
	    
	    // 判斷會員是否已登入
	    public static boolean isLogin(HttpSession session) {
	        return session.getAttribute(MEMBER_SESSION_KEY) != null;
	    }

	    // 登出時移除登入資料
	    public static void removeLogin(HttpSession session) {
	        session.removeAttribute(MEMBER_SESSION_KEY);
	    }
	
	
}
