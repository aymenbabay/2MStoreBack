package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Company;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProviderDto extends BaseDto<Long> implements Serializable{


	//we can delete all entitis and dtos those related by provider and client and make all in one entity and dto
	
    private static final long serialVersionUID = 12345678106L;
	
    	private String name;
    
	    private String code;
	    
	    private String nature;
	    
	  	private CompanyDto company;

		private String bankaccountnumber;

		private String matfisc;
		
		private String indestrySector;
		
		private String phone;
		
		private String address;
		
		private String email;
		
		private boolean isVirtual;
		
}
