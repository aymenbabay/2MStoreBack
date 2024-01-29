package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Table(name="bill")
public class Bill extends BaseEntity<Long> implements Serializable{

	private String number;
	
	private Double amount;
	
	private String agency;
	
	private LocalDateTime delay;

	
	@ManyToOne
	@JoinColumn(name = "invoiceId")
	private Invoice invoice;
	
	
}
