package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="provider_company")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderCompany extends BaseEntity<Long> implements Serializable {


	@ManyToOne()
	@JoinColumn(name = "providerId")
	private Provider provider;
	
	@ManyToOne()
	@JoinColumn(name = "companyId")
	private Company company;
	
	private Double mvt;
	
	private Double credit;

	private boolean isDeleted;
	
	private Double advance;
}
