package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.PurchaseOrderLineDto;
import com.example.meta.store.werehouse.Entities.PurchaseOrderLine;

@Mapper
public interface PurchaseOrderLineMapper {

	PurchaseOrderLine mapToEntity(PurchaseOrderLineDto dto);
	
	PurchaseOrderLineDto mapToDto(PurchaseOrderLine entity);
}
