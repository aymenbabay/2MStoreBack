package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.CheckDto;
import com.example.meta.store.werehouse.Entities.Check;

@Mapper
public interface CheckMapper {

	Check mapToEntity(CheckDto dto);
	
	CheckDto mapToDto(Check entity);
	
}
