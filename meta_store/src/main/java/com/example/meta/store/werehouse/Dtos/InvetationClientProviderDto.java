package com.example.meta.store.werehouse.Dtos;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvetationClientProviderDto extends BaseDto<Long>{

	private InvoiceReturnDto client;
	
	private InvoiceReturnDto provider;
		
	private InvoiceReturnDto company;
	
	private Status status;
}
