package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.PurchaseOrderDto;
import com.example.meta.store.werehouse.Entities.PurchaseOrder;

@Mapper
public interface PurchaseOrderMapper {

	PurchaseOrder mapToEntity(PurchaseOrderDto dto);
	
	PurchaseOrderDto mapToDto(PurchaseOrder entity);
}
