package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.example.meta.store.Base.Entity.BaseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillDto extends BaseDto<Long> implements Serializable{

	private String number;
	
	private Double amount;
	
	private String agency;
	
	private String bankAccount;
	
	private Date delay;
	
	private InvoiceDto invoice;
	
}
