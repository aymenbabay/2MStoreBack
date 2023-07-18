package com.example.meta.store.Base.ErrorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class RecordIsAlreadyExist extends RuntimeException  {

	private static final long serialVersionUID = 2L;
	
	public RecordIsAlreadyExist(String message) {
		super(message);
	}
}
