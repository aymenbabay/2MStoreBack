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
@Table(name="werehouse_line_command")
public class CommandLine extends BaseEntity<Long> implements Serializable{

    private static final long serialVersionUID = 12345678110L;
    
	private Double quantity;

	private Double totTva;

	private Double prixArticleTot;
	
	private Double discount;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "invoiceId")
	private Invoice invoice;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "articleId")
	private Article article;
}
