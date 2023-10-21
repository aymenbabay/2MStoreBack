package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client extends BaseEntity<Long> implements Serializable {


	//we can delete all entitis and dtos those related by provider and client and make all in one entity and dto
	
    private static final long serialVersionUID = 12345678103L;
    
    private String name; 
    
    @Column(unique = true)
    private String code;
    
    private String nature;
    
    private boolean isVirtual;
    
    private PrivacySetting isVisible;

    @Column(unique = true)
	private String bankaccountnumber;

    @Column(unique = true)
	private String matfisc;

    @Column(unique = true)
	private String phone;
	
	private String address;

	private String indestrySector;

    @Column(unique = true)
	private String email;
        	
    @ManyToOne
    @JoinColumn(name = "companyId")
    private Company company;
    
    
    
}
