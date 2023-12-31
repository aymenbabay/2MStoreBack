package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class InvoiceDto extends BaseDto<Long> implements Serializable {


    private static final long serialVersionUID = 123456781022L;
    
	private Long code;
	
	private Double tot_tva_invoice;
			
	private Double prix_invoice_tot;
	
	private Double prix_article_tot;
	
	private Boolean status;
	
	private LocalDateTime createdDate;

	private LocalDateTime LastModifiedDate;
	
	private String LastModifiedBy;
	
	private String CreatedBy;
	
	private Client client;
	
	private Company company;
}
