package com.example.meta.store.Base.AppConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

	 @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/werehouse/**")
	                    .allowedOrigins("http://localhost:4200")
	                    .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")
	                    .allowedHeaders("*")
	                    .allowCredentials(true);
	            }
	        };
	    }
}
