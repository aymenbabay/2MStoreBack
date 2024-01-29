package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.Base.Security.Dto.UserDto;
import com.example.meta.store.werehouse.Enums.Nature;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassingClientDto extends BaseDto<Long> implements Serializable{

private Nature nature;
	
	@OneToOne()
    @JoinColumn(name ="userId")
    private UserDto user;
}
