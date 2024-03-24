package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="article")
public class Article extends BaseEntity<Long> implements Serializable{


    private static final long serialVersionUID = 12345678108L;
    
	@NotBlank(message = "Libelle Field Must Not Be Empty")
	private String libelle;
	
	@NotBlank(message = "Code Field Must Not Be Empty")
	private String code;

	private String unit;
	
	private String discription;
	
	@PositiveOrZero(message = "Cost Field Must Be A Positive Number")
	private Double cost;

	@PositiveOrZero(message = "Quantity Field Must Be A Positive Number")
	private Double quantity;
	
	private Double minQuantity;
	
	
	@PositiveOrZero(message = "Selling_Price Field Must Be A Positive Number")
	private Double margin;
	
	private String barcode;
	
	private Double tva;
	
	private String sharedPoint;
	
	private PrivacySetting isVisible;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "categoryId")
	private Category category;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "subCategoryId")
	private SubCategory subCategory;
	
	@ManyToOne()
	@JoinColumn(name = "providerId")
	private Provider provider;
	
	@ManyToOne
	@JoinColumn(name= "companyId")
	private Company company;
	
	private String image;

	@OneToMany(mappedBy = "parentArticle")
	private Set<SubArticle> subArticle;


	
}
