package com.example.meta.store.Base.Security.Dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends BaseDto<Long> implements Serializable {

    private static final long serialVersionUID = 1234567811L;
	 
	private String phone;
	
	private String address;
	
	private String userName;
	
	private String email;
	
	private String resetToken;
	
	private LocalDateTime dateToken;
	
}
