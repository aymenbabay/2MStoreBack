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
@Table(name="werehouse_invoice")
public class Invoice extends BaseEntity<Long> implements Serializable {


    private static final long serialVersionUID = 12345678113L;
    
	private Long code;
		
	private Double tot_tva_invoice;
			
	private Double prix_invoice_tot;
	
	private Double prix_article_tot;
	
	private Boolean status;
	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "clientId")
	private Client client;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "companyId")
	private Company company;
	
}
