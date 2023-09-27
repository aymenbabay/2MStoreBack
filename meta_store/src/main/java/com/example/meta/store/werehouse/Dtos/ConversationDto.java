package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.Set;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.Base.Security.Dto.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationDto extends BaseDto<Long> implements Serializable{

	private UserDto user1;
	
	private UserDto user2;
	
	private Set<MessageDto> message;
}
