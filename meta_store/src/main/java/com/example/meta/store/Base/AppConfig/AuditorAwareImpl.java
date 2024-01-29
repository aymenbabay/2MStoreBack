package com.example.meta.store.Base.AppConfig;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Entity.User;


public class AuditorAwareImpl implements AuditorAware<Long>{


	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	 public AuditorAwareImpl(JwtAuthenticationFilter jwtAuthenticationFilter) {
	        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	    }
	 
	 @Override
	    public Optional<Long> getCurrentAuditor() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && authentication.isAuthenticated()) {
	            Object principal = authentication.getPrincipal();
	            if (principal instanceof User) {
	                return Optional.of(((User) principal).getId());
	            } else if (principal instanceof String) {
	                // Handle the case where the principal is a String (if applicable)
	                return Optional.empty(); // Or provide a default Long value if needed
	            }
	        }
	        return Optional.empty();
	    }
}
