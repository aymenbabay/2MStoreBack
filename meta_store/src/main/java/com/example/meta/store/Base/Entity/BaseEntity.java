package com.example.meta.store.Base.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity<ID> implements Serializable{

    private static final long serialVersionUID = 1234567813L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private ID id;
	
	@CreatedBy
	@Column(updatable = false)
	private Long createdBy;
	
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdDate;
	
	@LastModifiedBy
	private Long lastModifiedBy;
	
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	
	
}
