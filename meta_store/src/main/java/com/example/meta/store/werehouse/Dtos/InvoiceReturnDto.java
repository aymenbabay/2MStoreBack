package com.example.meta.store.werehouse.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceReturnDto {

	private Long id;
	
	private String name;

	private String phone;
	
	private String address;
	
	private String matfisc;
	
	private String indestrySector;
	
	private String email;
	
	private Boolean paid;
	
}

