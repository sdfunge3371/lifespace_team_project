package com.lifespace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
//@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http
         .cors()  // <--- ✅ 這行是關鍵
         .and()
         .csrf().disable()  // <--- 測試階段可先關，避免 POST 時錯誤
         .authorizeRequests()
             .anyRequest().permitAll();

     return http.build();
	}

}
