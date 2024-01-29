package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="BankTransfer")
public class BankTransfer extends BaseEntity<Long> implements Serializable{

	private String transactionId;
	
	private Double amount;
	
	private String agency;

	private String bankaccount;
	
	@ManyToOne
	@JoinColumn(name = "invoiceId")
	private Invoice invoice;
	
	
}
