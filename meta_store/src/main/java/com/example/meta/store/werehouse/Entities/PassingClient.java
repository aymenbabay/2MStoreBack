package com.example.meta.store.werehouse.Entities;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Enums.Nature;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="passingClient")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PassingClient extends BaseEntity<Long> implements Serializable {

	private Nature nature;
	
	@OneToOne()
    @JoinColumn(name ="userId")
    private User user;
}
