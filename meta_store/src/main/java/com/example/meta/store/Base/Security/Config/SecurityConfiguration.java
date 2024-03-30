package com.example.meta.store.Base.Security.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;


import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtAuthenticationFilter jwtAuthFilter;

	private final AuthenticationProvider authenticationProvider;
	
	private final LogoutHandler logoutHandler;
	
	@SuppressWarnings("removal")
	@Bean
	protected SecurityFilterChain securityFilterChaine (HttpSecurity http) throws Exception {
		
		http
		.csrf()
		.disable()
		.authorizeHttpRequests()
		.requestMatchers("/**") //"api/auth/**","/werehouse/**","/**"
			.permitAll()
		.anyRequest()
			.authenticated()
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authenticationProvider(authenticationProvider)
		.addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter.class)
		.logout()
		.logoutUrl("/api/auth/logout")
		.addLogoutHandler(logoutHandler)
		.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
		return http.build();
	}
	


}
