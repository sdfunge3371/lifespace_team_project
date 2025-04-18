package com.lifespace;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


//登入成功後，要正確導回前台 or 後台首頁，並寫入 Spring Security 的「登入狀態」！
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	@Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 取出登入 email 或使用者名稱
        String email = authentication.getName();

        // 寫入 Session（給自己用的）
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", email);

//        // 寫入 Spring Security 的認證（給 Spring Security 用的）
//        Authentication auth = new UsernamePasswordAuthenticationToken(
//                email,
//                null,
//                List.of(new SimpleGrantedAuthority("ROLE_MEMBER")) // 可依照需求改成 ROLE_ADMIN
//        );
//        SecurityContextHolder.getContext().setAuthentication(auth);

        // 依照 Referer 判斷來自哪個登入頁面
        String referer = request.getHeader("Referer");

        if (referer != null && referer.contains("loginAdmin")) {
            response.sendRedirect("/backend_index.html");
        } else {
            response.sendRedirect("/homepage.html");
        }
    }

}
