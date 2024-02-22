package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.Base.Security.Dto.UserDto;
import com.example.meta.store.werehouse.Enums.DeliveryCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryDto extends BaseDto<Long> implements Serializable {

	private UserDto user;
	
	private  Long rate;
	
	private DeliveryCategory category;
	
	
}
