package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.meta.store.Base.Entity.BaseEntity;

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
@Table(name="checkMode")
public class Check extends BaseEntity<Long> implements Serializable{

	private String number;
	
	private Double amount;
	
	private String agency;
	
	private LocalDateTime delay;
	
	private String bankaccount;

	@ManyToOne 
	@JoinColumn(name = "invoiceId")
	private Invoice invoice;
}
