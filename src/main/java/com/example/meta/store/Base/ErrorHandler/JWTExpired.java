package com.example.meta.store.Base.ErrorHandler;

import org.springframework.security.core.AuthenticationException;

public class JWTExpired extends AuthenticationException{

	
	public JWTExpired(String msg) {
		super(msg);
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return super.getMessage();
	}
}
