package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.BillDto;
import com.example.meta.store.werehouse.Entities.Bill;

@Mapper
public interface BillMapper {

	Bill mapToEntity(BillDto dto);
	
	BillDto mapToDto(Bill entity);
	
}
