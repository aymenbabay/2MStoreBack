package com.example.meta.store.Base.AppConfig;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;


public class AuditorAwareImpl implements AuditorAware<String>{


	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	 public AuditorAwareImpl(JwtAuthenticationFilter jwtAuthenticationFilter) {
	        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	    }
	 
	 @Override
	    public Optional<String> getCurrentAuditor() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return Optional.empty();
	        }
	        return Optional.of(authentication.getName());
	    }
}
