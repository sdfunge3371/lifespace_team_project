package com.lifespace;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lifespace.service.EventService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class ActivityParticipantFilter extends OncePerRequestFilter {
	
	@Autowired
    private EventService eventService;
	
	@Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        
     // 只對這些特定 URL 做過濾
        if (uri.startsWith("/activity/comment.html")) {
            
        	//確認是否有session
        	HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect("/login.html");
                return;
            }
            
            //確認是否有此會員資料
            String memberId = SessionUtils.getLoginMemberId(session);
            if (memberId == null) {
                response.sendRedirect("/login.html");
                return;
            }
            
            //確認是否有參與活動
            String activityId = request.getParameter("activityId"); // 須從前端帶入參數
            if (activityId == null) {
                response.sendRedirect("/error.html"); // 沒帶活動編號，導錯頁
                return;
            }
            
            //
            boolean joined = eventService.checkMemberEventStatus(activityId, memberId);
            if (!joined) {
                response.sendRedirect("homepage.html"); // 回首頁
                return;
            }
        }
        
     // 通過條件，繼續往後執行
        filterChain.doFilter(request, response);
	}

}
