package com.example.meta.store.Base.ErrorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
public class NotPermissonException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NotPermissonException(String message) {
		super(message);
	}
}
