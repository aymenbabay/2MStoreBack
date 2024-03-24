package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Enums.PrivacySetting;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SubArticleDto extends BaseDto<Long> implements Serializable{

    private static final long serialVersionUID = 123456781177L;
    
    private String libelle;
	
	private String code;

	private String unit;
	
	private Double cost;

	private Double quantity;
	

	private String barcode;
	
	private String sharedPoint;
	
	private String discription;
	
	private Double minQuantity;
	
	private Double margin;
	
	private Double tva;
	
	private PrivacySetting isVisible;
	
	private ProviderDto provider;

	private CategoryDto category;
	
	private SubCategoryDto subCategory;
	
	private String image;

}
