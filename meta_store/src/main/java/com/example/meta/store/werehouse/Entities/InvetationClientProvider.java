package com.example.meta.store.werehouse.Entities;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name="invetation")
public class InvetationClientProvider extends BaseEntity<Long> {

	@ManyToOne()
	private Client client;
	
	@ManyToOne()
	private Provider provider;
	
	@ManyToOne()
	private Company company;
	
	private Status status;
}
