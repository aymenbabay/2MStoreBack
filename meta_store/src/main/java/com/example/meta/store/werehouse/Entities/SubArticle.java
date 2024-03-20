package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="sub_article")
public class SubArticle extends BaseEntity<Long> implements Serializable {

	@ManyToOne
	@JoinColumn(name = "parentArticle_id")
	private Article parentArticle;
	
	@ManyToOne
	@JoinColumn(name = "childArticle_id")
	private Article childArticle;
	
	private Double quantity;
	
	
}
