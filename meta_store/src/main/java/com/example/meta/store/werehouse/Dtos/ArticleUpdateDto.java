package com.example.meta.store.werehouse.Dtos;

import com.example.meta.store.Base.Entity.BaseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleUpdateDto extends BaseDto<Long>{

	private String libelle;
	
	private String code;

	private String unit;
	
	private String discription;
	
	private Double cost;

	private Double quantity;
	
	private Double minQuantity;
	
	private Double sellingPrice;
	
	private String barcode;

	private Double margin;
	
	private Double tva;
	
	private Long category;
	
	private Long subCategory;
	
	private Long provider;
	
	private String image;
	
	private Long company;
	
	private Long article;
}
