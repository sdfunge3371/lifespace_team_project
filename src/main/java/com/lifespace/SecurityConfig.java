package com.lifespace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
//@EnableWebSecurity
@Configuration
public class SecurityConfig  {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http
         .cors() //啟用跨域支援
         .and()
         .csrf().disable() //先關掉CSRF（測試階段方便，但上線建議開啟）
         .authorizeHttpRequests()
         .requestMatchers(  //這些頁面需要登入
        		 "/myAccount.html",
                 "/events_for_user.html",
                 "/frontend_orders.html",
                 "/favorite_space.html"
          ).authenticated()   
         .anyRequest().permitAll()  // 其他都允許
         .and()
         .formLogin()
             .loginPage("/login.html")   // ✅ 自訂登入頁
             .defaultSuccessUrl("/index.html", true) // 登入成功後導向
             .permitAll()
         .and()
         .logout()
             .logoutUrl("/logout")
             .logoutSuccessUrl("/index.html")
             .permitAll();
     
     return http.build();
	}

}
