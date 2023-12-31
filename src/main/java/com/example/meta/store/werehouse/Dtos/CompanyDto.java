package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Provider;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto extends BaseDto<Long> implements Serializable {

    private static final long serialVersionUID = 12345678102L;
    
	private String name;
	
	private String code;
	
	private String codecp;
	
	private String matfisc;
	
	private String address;
	
	private String phone;
	
	private String bankaccountnumber;

	private double margin;
	
	@Email
	private String email;

	private String indestrySector;
	
	private String capital;
	
	private String logo;
	
	private int workForce;
		
	private double rate;
	
	private int raters;
	
	private User user;
	
	private Provider provider;
	
	private Client client;
		
	
}
