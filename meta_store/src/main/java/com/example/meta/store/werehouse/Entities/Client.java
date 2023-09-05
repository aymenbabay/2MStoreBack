package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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


    private static final long serialVersionUID = 12345678103L;
    
    private String name; 
    
    @Column(unique = true)
    private String code;
    
    private Double mvt;
    
    private Double credit;
    
    private String nature;
    
    private boolean isVirtual;

	private String bankaccountnumber;

	private String matfisc;

	private String phone;
	
	private String address;

	private String indestrySector;
	
	private String email;
    
	@JsonIgnore
    @ManyToMany()
    @JoinTable(name= "client_provider",
    joinColumns = @JoinColumn(name="clientId"),
    inverseJoinColumns = @JoinColumn(name= "providerId"))
    private Set<Provider> providers = new HashSet<>();
    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name= "companyId")
	private Company company;
	
    
    
    
}
