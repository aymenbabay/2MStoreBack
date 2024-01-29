package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name="purchaseOrder")
public class PurchaseOrder extends BaseEntity<Long> implements Serializable {

	@OneToMany
	@JoinTable(name="OrderLine")
	private Set<PurchaseOrderLine> lines;
	
	@ManyToOne
	private Company company;
	
	@ManyToOne
	private Client client;
	
	@ManyToOne
	private PassingClient pclient;
	
	private Status status;
	
	private String orderNumber;
}
