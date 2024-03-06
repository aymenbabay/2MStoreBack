package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankTransferDto extends BaseDto<Long> implements Serializable{
	
	private String transactionId;
	
	private Double amount;
	
	private String agency;
	
	private InvoiceDto invoice;

	private String bankAccount;
}
