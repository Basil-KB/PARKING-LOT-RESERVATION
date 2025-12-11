/*
package com.valcare.parkinglot.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // remove the spring security and help to  skip the initial login credential
				// For swagger-ui direct open without credential
public class SecurityConfig {
	 @Bean
	     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
	        return http.build();
	    }
	}*/
