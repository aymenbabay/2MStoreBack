package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.MessageDto;
import com.example.meta.store.werehouse.Entities.Message;

@Mapper
public interface MessageMapper {

	Message mapToEntity(MessageDto dto);
	
	MessageDto mapToDto(Message entity);
}
