package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.Date;

import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="vacation_werehouse")
public class Vacation extends BaseEntity<Long> implements Serializable {
 

    private static final long serialVersionUID = 12345678115L;
		
	private int year;
	
	private Date startdate;
	
	private Date enddate;
	
	
	@ManyToOne()
	@JoinColumn(name = "worker_id",referencedColumnName = "id")
	private Worker worker;
	
	@ManyToOne()
	@JoinColumn(name = "company_id",referencedColumnName = "id")
	private Company company;
}
