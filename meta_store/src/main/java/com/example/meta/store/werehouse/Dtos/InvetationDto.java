package com.example.meta.store.werehouse.Dtos;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.Base.Security.Dto.UserDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Enums.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvetationDto extends BaseDto<Long>{

	private InvoiceReturnDto client;
	
	private InvoiceReturnDto provider;
		
	private InvoiceReturnDto companySender;

	private Company companyReciver;
	
	private UserDto user;
	
	private Double salary;
	
	private String jobtitle;
	
	private String department;
	
	private long totdayvacation;

	private boolean statusvacation;
	
	private Status status;
	
	private Type type;
}
