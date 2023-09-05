package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="company_article")
public class CompanyArticle extends BaseEntity<Long> implements Serializable{

    private static final long serialVersionUID = 12345678111L;
    
	private double quantity;

	private double minQuantity;
	
	private double margin;
	
	private double cost;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "categoryId")
	private Category category;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "subCategoryId")
	private SubCategory subCategory;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companyId")
	private Company company;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "articleId")
	private Article article;
}
