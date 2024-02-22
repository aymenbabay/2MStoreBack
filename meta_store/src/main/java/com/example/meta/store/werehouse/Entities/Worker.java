package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.Security.Entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "worker")
public class Worker extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 12345678107L;
    
	private String name;
	
	private String phone;
	
	private String email;
	
	private String address;
	
	private Double salary;
	
	private String jobtitle;
	
	private String department;
	
	private long totdayvacation;

	private boolean statusvacation;

	private long remainingday;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "user_id",referencedColumnName = "id")
	private User user;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "companyId")
	private Company company;
}
