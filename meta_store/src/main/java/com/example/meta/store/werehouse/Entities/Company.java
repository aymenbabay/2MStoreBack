package com.example.meta.store.werehouse.Entities;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company")
public class Company extends BaseEntity<Long> implements Serializable{
	

    private static final long serialVersionUID = 12345678101L;
    
    @Column(unique = true)
	private String name;
	
	@Column(unique = true)
	private String code;

	@Column(unique = true)
	private String matfisc;
	
	private String address;
	
	private String phone;

	@Column(unique = true)
	private String bankaccountnumber;

	@Email
	private String email;

	private String indestrySector;
	
	private String capital;
	
	private String logo;
	
	private int workForce;
	
	private double margin;
		
	private double rate;
	
	private int raters;
	
	private PrivacySetting isVisible;
	
	@OneToOne()
	@JoinColumn(name = "userId")
	private User user;
	
	@OneToMany(mappedBy = "parentCompany")
	@JsonIgnore
	private Set<Company> branches;
	    
	@ManyToOne
	@JoinColumn(name = "parent_company_id")
	private Company parentCompany;
	    
	

	
}
