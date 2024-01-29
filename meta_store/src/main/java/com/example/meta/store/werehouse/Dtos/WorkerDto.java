package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.Base.Security.Dto.UserDto;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Entities.Company;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class WorkerDto extends BaseDto<Long> implements Serializable {
	

    private static final long serialVersionUID = 12345678126L;
    
	private String name;
	
	private String phone;
	
	private String email;
	
	private String address;
	
	private Double salary;
	
	private String jobtitle;
	
	private String department;
	
	private long totdayvacation;

	private boolean statusvacation;
	
	private UserDto user;

	private CompanyDto company;
}
