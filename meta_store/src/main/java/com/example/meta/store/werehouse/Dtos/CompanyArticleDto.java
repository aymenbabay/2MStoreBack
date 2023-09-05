package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.SubCategory;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyArticleDto extends BaseDto<Long> implements Serializable {


    private static final long serialVersionUID = 12345678120L;
    
	private double quantity;

	private double minQuantity;
	
	private double margin;
	
	private double cost;
	
	private Company company;
	
	private Article article;
	
	private Category category;
	
	private SubCategory subCategory;
}
