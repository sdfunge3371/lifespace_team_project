package com.lifespace;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.lifespace.entity.Member;
import com.lifespace.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



@Configuration
public class SecurityConfig  {
	
	private  MemberService memberService;
	
	 public SecurityConfig(MemberService memberService) {
	        this.memberService = memberService;
	    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 
		http.cors(); //啟用跨域支援
		http.csrf().disable(); //先關掉CSRF（測試階段方便，但上線建議開啟）
         
		http.authorizeHttpRequests()
         .requestMatchers(                 //這些頁面需要登入
        		 "/myAccount.html",
                 "/events_for_user.html",
                 "/frontend_orders.html",
                 "/favorite_space.html"
          ).authenticated()   
         .anyRequest().permitAll();  // 其他都允許
         

        //表單登入設計
         http.formLogin()
             .loginPage("/login.html")   // ✅ 自訂登入頁
             .defaultSuccessUrl("/homepage.html", true) // 登入成功後導向
             .permitAll();
         
             
         //第三方登入(google)
         http.oauth2Login()
         .loginPage("/login.html")
         .userInfoEndpoint()
             .userService(oauth2UserService()) // Google 登入後取得 email
             .and()
         .successHandler(googleLoginSuccessHandler());
         
            
         //登出設定    
         http.logout()
             .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
             .logoutSuccessUrl("/homepage.html")
             .permitAll();
		 
		
     return http.build();
	}
	
	
	//自訂登入成功後的處理邏輯(寫入session)
	public AuthenticationSuccessHandler googleLoginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                org.springframework.security.core.Authentication authentication)
                    throws IOException, ServletException {

                OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                String email = oauthUser.getAttribute("email");
                String name = oauthUser.getAttribute("name");

                // 找不到就建立新會員
                Member member = memberService.findByEmail(email)
                        .orElseGet(() -> memberService.createGoogleMember(email, name));

                // 寫入 session
                HttpSession session = request.getSession();
                session.setAttribute("loginMember",member.getMemberId());

                // 導向首頁
                response.sendRedirect("/homepage.html");
            }
        };
    }

    //預設的 userService（讓 Spring 幫你 call Google API）
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new DefaultOAuth2UserService();
    }
	
	

}
