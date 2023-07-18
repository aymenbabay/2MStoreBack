package com.example.meta.store.Base.Security.Entity;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

	@NotBlank(message = "User Name Field Must Be Not Empty")
	private String username;

	@NotBlank(message = "Password Field Must Be Not Empty")
	private String password;
}
