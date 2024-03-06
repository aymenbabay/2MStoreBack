package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashDto extends BaseDto<Long> implements Serializable{

	
	private Double amount;

	private Status status;
	
	private InvoiceDto invoice;
	
}
