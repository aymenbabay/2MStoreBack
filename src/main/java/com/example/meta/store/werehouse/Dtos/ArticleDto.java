package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.SubCategory;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDto extends BaseDto<Long> implements Serializable {


    private static final long serialVersionUID = 12345678117L;
    
	private String libelle;
	
	private String code;

	private String unit;
	
	private String discription;
	
	private Double cost;

	private Double quantity;
	
	private Double minQuantity;
	
	
	private Double margin;
	
	private String barcode;
	
	private Double tva;
	
	private Category category;
	
	private SubCategory subCategory;
	
	private Provider provider;
	
	private String image;

}
