package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.VacationDto;
import com.example.meta.store.werehouse.Entities.Vacation;

@Mapper
public interface VacationMapper {

	Vacation mapToEntity(VacationDto dto);
	
	VacationDto mapToDto(Vacation entity);
}
