package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ClientDto extends BaseDto<Long> implements Serializable {

	//we can delete all entitis and dtos those related by provider and client and make all in one entity and dto
	
	  private static final long serialVersionUID = 12345678105L;
	  
	  private String name;
	  
	  private String code;
	    
	  private String nature;
	  
	  private String bankaccountnumber;
	  
	  private String matfisc;
	  
	  private String phone;
	  
	  private String address;
	  
	  private String email;
	    	    
	  private CompanyDto company;

	  private String indestrySector;

	    private boolean isVirtual;
		
}
