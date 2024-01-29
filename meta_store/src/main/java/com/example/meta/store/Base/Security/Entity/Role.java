package com.example.meta.store.Base.Security.Entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.Security.Enums.RoleEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Transactional
public class Role extends BaseEntity<Long> implements Serializable{


    private static final long serialVersionUID = 123456789L;

	private RoleEnum name;
	
	
   
}
