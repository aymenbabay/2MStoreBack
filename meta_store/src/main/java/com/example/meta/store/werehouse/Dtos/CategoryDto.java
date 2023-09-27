package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Company;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto extends BaseDto<Long> implements Serializable{


    private static final long serialVersionUID = 12345678118L;
    
	private String code;

	private String libelle;
	
	private String image;
	
	private CompanyDto company;
	
}
