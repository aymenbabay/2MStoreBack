package com.example.meta.store.werehouse.Entities;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ClientInvoice")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientInvoice extends BaseEntity<Long> {


    private static final long serialVersionUID = 12345678116L;
    
	@ManyToOne(fetch=FetchType.LAZY)
	private Provider provider;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Client client;
	
	@OneToOne
	private Invoice invoice;
	
	private InvoiceStatus isAccepted;
}
