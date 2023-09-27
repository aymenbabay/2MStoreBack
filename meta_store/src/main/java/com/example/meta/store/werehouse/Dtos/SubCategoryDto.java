package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Company;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryDto extends BaseDto<Long> implements Serializable{


    private static final long serialVersionUID = 12345678123L;
    
	private String code;

	private String libelle;
	
	private String image;
	
	private CategoryDto category;
	
	private CompanyDto company;
}
