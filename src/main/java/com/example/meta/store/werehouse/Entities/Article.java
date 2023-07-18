package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
	@Column(unique = true)
	private String code;

	private String unit;
	
	private String discription;
	
	@Positive(message = "Cost Field Must Be A Positive Number")
	private Double cost;

	@Positive(message = "Quantity Field Must Be A Positive Number")
	private Double quantity;
	
	private Double minQuantity;
	
	
	@Positive(message = "Selling_Price Field Must Be A Positive Number")
	private Double margin;
	
	private String barcode;
	
	private Double tva;
	
	@ManyToOne(optional = true,fetch=FetchType.EAGER)
	@JoinColumn(name = "categoryId")
	private Category category;
	
	@ManyToOne(optional = true,fetch=FetchType.EAGER)
	@JoinColumn(name = "subCategoryId")
	private SubCategory subCategory;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "providerId")
	private Provider provider;
	
	private String image;

}
