package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

	@ManyToOne()
	@JoinColumn(name = "companyId")
	private Company company;
	
	@ManyToOne()
	@JoinColumn(name = "clientId")
	private Client client;
	
	@ManyToOne()
	@JoinColumn(name = "pclientId")
	private PassingClient pclient;
	
	@ManyToMany()
	@JoinTable(name = "order_article",
	joinColumns = @JoinColumn(name = "orderId"),
	inverseJoinColumns = @JoinColumn(name= "articleId"))
	private Set<Article> articles = new HashSet<>();
	

	@Positive(message = "Quantity Field Must Be A Positive Number")
	private Double quantity;
	
	private String comment;
}
