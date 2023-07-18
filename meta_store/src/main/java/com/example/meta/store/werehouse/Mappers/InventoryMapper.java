package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.InventoryDto;
import com.example.meta.store.werehouse.Entities.Inventory;

@Mapper
public interface InventoryMapper {

	Inventory mapToEntity(InventoryDto dto);
	
	InventoryDto mapToDto(Inventory entity);
}
