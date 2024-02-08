package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderDto extends BaseDto<Long> implements Serializable{

	private Set<PurchaseOrderLineDto> lines;
	
	
	private CompanyDto company;
	
	
	private ClientDto client;
	
	
	private PassingClientDto pclient;
	
	private String orderNumber;
}
