package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Enums.DeliveryStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class OrderDeliveryDto extends BaseDto<Long> implements Serializable {

	private DeliveryDto delivery;
	
	private PurchaseOrderLineDto order;
	
	private DeliveryStatus status;
	
	private String note;
	
	private Boolean deliveryCofrimed;
	
}
