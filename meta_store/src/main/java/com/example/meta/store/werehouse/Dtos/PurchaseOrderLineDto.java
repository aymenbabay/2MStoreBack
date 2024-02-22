package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.PassingClient;
import com.example.meta.store.werehouse.Entities.PurchaseOrder;
import com.example.meta.store.werehouse.Enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderLineDto extends BaseDto<Long> implements Serializable {

	
	private ArticleDto article;
	
	private Double quantity;
	
	private String comment;
	
	private Status status;
	
	private Boolean delivery;

	private PurchaseOrder purchaseorder;
}
