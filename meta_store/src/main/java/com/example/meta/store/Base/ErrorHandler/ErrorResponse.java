package com.example.meta.store.Base.ErrorHandler;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

	private Boolean success;
	
	private String message;
	
	private LocalDateTime date;
	
	private List<String> details;

	public ErrorResponse(String message, List<String> details) {
		super();
		this.message = message;
		this.details = details;
		this.success = Boolean.FALSE;
		this.date = LocalDateTime.now();
	}
}
