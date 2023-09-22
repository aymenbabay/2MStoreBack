package com.example.meta.store.werehouse.Dtos;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Enums.InvoiceStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientInvoiceDto extends BaseDto<Long> {

	private String providerName;
	
	private Long providerId;
	
	private String providerPhone;
	
	private String providerMatriculeFiscal;
	
	private String providerAddress;
	
	private Long clientId;
	
	private String clientName;
	
	private String clientPhone;
	
	private String clientMatriculeFiscal;
	
	private String clientAddress;
	
	private Long invoice;
	
	private String invoiceDate;
	
	private Long invoiceId;
	
	private InvoiceStatus isAccepted;
	
}
