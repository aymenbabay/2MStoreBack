package com.example.meta.store.Base.AppConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {

	  @Bean
	    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
	        ObjectMapper objectMapper = builder.build();
	        SimpleModule module = new SimpleModule();
	        module.addDeserializer(GrantedAuthority.class, new GrantedAuthorityDeserializer());
	        objectMapper.registerModule(module);
	        return objectMapper;
	    }
}
