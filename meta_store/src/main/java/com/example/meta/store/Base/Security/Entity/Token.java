package com.example.meta.store.Base.Security.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="token")
public class Token {

	  @Id
	  @GeneratedValue
	  public Integer id;

	  @Column(unique = true)
	  public String token;


	  public boolean revoked;

	  public boolean expired;

	  @ManyToOne
	  @JoinColumn(name = "user_id")
	  public User user;
}
