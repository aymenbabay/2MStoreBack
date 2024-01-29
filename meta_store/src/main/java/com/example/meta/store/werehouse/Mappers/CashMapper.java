package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.CashDto;
import com.example.meta.store.werehouse.Entities.Cash;

@Mapper
public interface CashMapper {

	Cash mapToEntity(CashDto dto);
	
	CashDto mapToDto(Cash entity);
	
}
