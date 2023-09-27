package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.ConversationDto;
import com.example.meta.store.werehouse.Entities.Conversation;

@Mapper
public interface ConversationMapper {

	Conversation mapToEntity(ConversationDto dto);
	
	ConversationDto mapToDto(Conversation entity);
}
