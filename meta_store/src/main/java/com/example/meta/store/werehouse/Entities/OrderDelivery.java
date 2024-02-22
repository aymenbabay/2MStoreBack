package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.DeliveryStatus;

import jakarta.persistence.Entity;
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
@Table(name="orderDelivery")
public class OrderDelivery extends BaseEntity<Long> implements Serializable {

	@ManyToOne()
	private Delivery delivery;
	
	@ManyToOne()
	private PurchaseOrderLine order;
	
	private DeliveryStatus status;
	
	private String note;
	
	private Boolean deliveryCofrimed;
	
	
}
