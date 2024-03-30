package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Unit;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDto extends BaseDto<Long> implements Serializable {


    private static final long serialVersionUID = 12345678117L;
    
	private String libelle;
	
	private String code;

	private Unit unit;
	
	private String discription;
	
	private Double cost;

	private Double quantity;
	
	private Double minQuantity;
	
	private String sharedPoint;
	
	private Double margin;
	
	private String barcode;
	
	private Double tva;
	
	private CategoryDto category;
	
	private SubCategoryDto subCategory;
	
	private ProviderDto provider;
	
	private CompanyDto company;
	
	private String image;
	
	private PrivacySetting isVisible;
	
	private Set<SubArticleRelationDto> subArticle;

}
