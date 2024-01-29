package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.PrivacySetting;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
@Table(name="client_company")
public class ClientCompany extends BaseEntity<Long> implements Serializable{

	@ManyToOne()
	@JoinColumn(name = "clientId")
	private Client client;
	
	@ManyToOne()
	@JoinColumn(name = "companyId")
	private Company company;
	
	private Double mvt;
	
	private Double credit;
	
	private boolean isDeleted;

	private Double advance;
	
	
}
