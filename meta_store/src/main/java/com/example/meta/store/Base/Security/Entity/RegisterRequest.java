package com.example.meta.store.Base.Security.Entity;


import com.example.meta.store.Base.Entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest extends BaseEntity<Long> {

    private static final long serialVersionUID = 1234567814L;
    
		private String phone;

		private String address;


		@NotBlank(message = "User Name Field Must Be Not Empty")
		@Column(unique = true)
		private String username;
		
		private String email;

		@NotBlank(message = "Password Field Must Not Be Empty")
		private String password;
		
		private Double longitude;
		
		private Double latitude;
		
}
