package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.PrivacySetting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="provider")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Provider extends BaseEntity<Long> implements Serializable {


    private static final long serialVersionUID = 12345678104L;
    
    private String name;
    
    @Column(unique = true)
    private String code;
    
    private boolean isVirtual;

    private PrivacySetting isVisible;

    @Column(unique = true)
	private String bankaccountnumber;

    @Column(unique = true)
	private String matfisc;
	
	private String phone;
	
	private String address;
	
	private String indestrySector;

    @Column(unique = true)
	private String email;
    
    private String nature;
    
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "companyId")
    private Company company;
  
    @OneToMany(mappedBy = "provider")
    private Set<ProviderCompany> companies;
    
}
