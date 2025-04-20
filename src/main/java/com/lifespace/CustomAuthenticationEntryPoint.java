package com.lifespace;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//決定沒登入時要導去哪個登入頁面

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	//因為不想再動到大家的檔案(本來是可以全部放在一個叫做admin的資料夾做管理)
	//轉而用比較土法煉鋼的方式，但至少不會影響到大家的路徑
	//並且寫一個小方法包裝起來
	 private boolean isBackendPage(String uri) {
         return uri.equals("/admin/news") ||
                uri.equals("/admin/member") ||
                uri.equals("/admin/admin") ||
                uri.equals("/admin/faq") ||
                uri.equals("/admin/space_comment") ||
                uri.equals("/admin/branch") ||
                uri.equals("/admin/rental_item") ||
                uri.equals("/admin/listSpaces") ||
                uri.equals("/admin/index");
     }
	
	  @Override
	    public void commence(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            AuthenticationException authException
	    ) throws IOException, ServletException {

	        String uri = request.getRequestURI();
	        
	       


	        if (isBackendPage(uri)) {
	            response.sendRedirect("/admin/loginAdmin"); // 管理員導向後台登入頁
	        } else {
	            response.sendRedirect("/lifespace/login"); // 其他導向前台登入頁
	        }
	    }

}
