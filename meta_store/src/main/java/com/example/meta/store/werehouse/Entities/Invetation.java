package com.example.meta.store.werehouse.Entities;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name="invetation")
public class Invetation extends BaseEntity<Long> {

	@ManyToOne()
	private Client client;
	
	@ManyToOne()
	private Provider provider;
	
	@OneToOne()
	private User user;
	
	@ManyToOne()
	@JoinColumn(name="company_sender_id")
	private Company companySender;
	
	@ManyToOne()
	@JoinColumn(name="company_reciver_id")
	private Company companyReciver;
	

	private Double salary;
	
	private String jobtitle;
	
	private String department;
	
	private long totdayvacation;

	private boolean statusvacation;
	
	private Status status;
}
