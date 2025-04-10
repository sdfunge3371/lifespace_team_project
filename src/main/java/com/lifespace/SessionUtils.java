package com.lifespace;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {

	   private static final String MEMBER_SESSION_KEY = "loginMember";
	   private static final String ADMIN_SESSION_KEY = "loginAdmin";
	  
	   //------------------------會員---------------------------------- 
	   // 取得會員 ID
	    public static String getLoginMemberId(HttpSession session) {
	        Object memberId = session.getAttribute(MEMBER_SESSION_KEY);
	        return memberId != null ? memberId.toString() : null;
	    }
	    
	    // 判斷會員是否已登入
	    public static boolean isMemberLogin(HttpSession session) {
	        return session.getAttribute(MEMBER_SESSION_KEY) != null;
	    }

	    // 登出時移除登入資料
	    public static void removeMemberLogin(HttpSession session) {
	        session.removeAttribute(MEMBER_SESSION_KEY);
	    }
	    
	    
	  //------------------------管理員---------------------------------- 
	 // 取得管理員 ID
	    public static String getLoginAdminId(HttpSession session) {
	        Object adminId = session.getAttribute(ADMIN_SESSION_KEY);
	        return adminId != null ? adminId.toString() : null;
	    }
	    
	    // 判斷會員是否已登入
	    public static boolean isAdminLogin(HttpSession session) {
	        return session.getAttribute(ADMIN_SESSION_KEY) != null;
	    }

	    // 登出時移除登入資料
	    public static void removeAdminLogin(HttpSession session) {
	        session.removeAttribute(ADMIN_SESSION_KEY);
	    }
	    
	    
	    
	    
	
}
