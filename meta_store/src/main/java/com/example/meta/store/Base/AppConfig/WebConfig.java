package com.example.meta.store.Base.AppConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class WebConfig {


	private final UserRepository UserRepository;

	  
	@Bean
	public AuditorAware<Long> auditorAware(JwtAuthenticationFilter jwtAuthenticationFilter) {
	    return new AuditorAwareImpl(jwtAuthenticationFilter);
	}

	

	  @Bean
	  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		  return config.getAuthenticationManager();
	  }
	  
	  @Bean
		public UserDetailsService userDetailsService() {
			return username -> UserRepository.findByUsername(username)
					.orElseThrow(() -> new RecordNotFoundException("user Not Found"));
		}
		
		  @Bean
		  public AuthenticationProvider authenticationProvider() {
			  DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
			  authProvider.setUserDetailsService(userDetailsService());
			  authProvider.setPasswordEncoder(passwordEncoder());
			  return authProvider;
		  } 

		
		@Bean
		public PasswordEncoder passwordEncoder() {
			// TODO Auto-generated method stub
			return new BCryptPasswordEncoder();
		}
	
		
}
