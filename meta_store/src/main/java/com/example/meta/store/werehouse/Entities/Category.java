package com.example.meta.store.werehouse.Entities;


import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="category_werehouse")
public class Category  extends BaseEntity<Long>  implements Serializable{
	

    private static final long serialVersionUID = 12345678109L;
    
	@NotBlank(message = "Code Field Must Not Be Empty")
	private String code;

	@NotBlank(message = "Libelle Field Must Not Be Empty")
	private String libelle;
	
	private String image;
	
	@ManyToOne()
	@JoinColumn(name = "companyId")
	private Company company;
	
}
