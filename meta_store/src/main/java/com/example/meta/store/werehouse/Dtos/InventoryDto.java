package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Company;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class InventoryDto extends BaseDto<Long> implements Serializable {


    private static final long serialVersionUID = 12345678121L;

	private Double current_quantity;
	
	private Double out_quantity;
	
	private Double in_quantity; 
	
	private String libelle_article;
	
	private String articleCode;
	
	private String bestClient;
	
	private Double articleCost;
	
	private Double articleSelling;
	
	private Company company;
	
	private Article article;
	
}
