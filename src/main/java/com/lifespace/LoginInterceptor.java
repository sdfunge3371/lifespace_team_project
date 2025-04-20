//package com.lifespace;
//
//import org.springframework.web.servlet.HandlerInterceptor;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//public class LoginInterceptor implements HandlerInterceptor {
//	
//	  @Override
//	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//	        HttpSession session = request.getSession(false); // 不創新的 session
//	        if (session != null && session.getAttribute("loginMember") != null) {
//	            return true; // 通過，繼續往 Controller 去
//	        }
//
//	        // 攔截，回傳 401（未登入）
//	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//	        response.getWriter().write("請先登入");
//	        return false;
//	    }
//
//}