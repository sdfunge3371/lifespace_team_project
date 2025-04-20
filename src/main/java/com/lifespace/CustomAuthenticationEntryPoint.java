package com.lifespace;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//決定沒登入時要導去哪個登入頁面

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	 @Override
	    public void commence(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            AuthenticationException authException) throws IOException {

	        String uri = request.getRequestURI();
	        if (uri.startsWith("/admin")) {
	            response.sendRedirect("/admin/loginAdmin");
	        } else {
	            response.sendRedirect("/lifespace/login");
	        }
	    }

}
