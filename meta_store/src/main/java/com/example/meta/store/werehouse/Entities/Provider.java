package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.werehouse.Enums.PrivacySetting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	@PositiveOrZero(message = "Mvt Field Must Be A Positive Number Or Zero")
    private Double mvt;

    @Column(unique = true)
	private String email;
    

	@PositiveOrZero(message = "Credit Field Must Be A Positive Number Or Zero")
    private Double credit;
    
    private String nature;
    
    //eager because error of serialization when get all my provider
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "companyId")
    private Company company;
    
}
