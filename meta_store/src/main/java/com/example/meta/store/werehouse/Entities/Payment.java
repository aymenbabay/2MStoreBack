package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.PaymentMode;
import com.example.meta.store.werehouse.Enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name="payment")
public class Payment extends BaseEntity<Long> implements Serializable {

	private Double amount;
	
	private LocalDateTime delay;
	
	private String agency;

	private String bankAccount;
	
	private String number;
	
	private String transactionId;
	
	private Status status;

	private PaymentMode type;
	
	@ManyToOne
	@JoinColumn(name = "invoiceId")
	private Invoice invoice;
}
