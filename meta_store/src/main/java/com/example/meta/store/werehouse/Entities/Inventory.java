package com.example.meta.store.werehouse.Entities;


import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Entity;
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
@Table(name="inventory_werehouse")
public class Inventory extends BaseEntity<Long> implements Serializable{


    private static final long serialVersionUID = 12345678112L;
    	
	private Double out_quantity;
	
	private Double in_quantity; 
		
	private String bestClient;
	
	private Double articleCost;
	
	private Double articleSelling;

	@ManyToOne()
	@JoinColumn(name = "company_id",referencedColumnName = "id")
	private Company company;
	
	@ManyToOne()
	@JoinColumn(name = "articleId", referencedColumnName = "id")
	private Article article;
	
}
