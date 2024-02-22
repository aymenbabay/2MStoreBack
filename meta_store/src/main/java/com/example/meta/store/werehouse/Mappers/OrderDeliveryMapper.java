package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.OrderDeliveryDto;
import com.example.meta.store.werehouse.Entities.OrderDelivery;

@Mapper
public interface OrderDeliveryMapper {

	OrderDelivery mapToEntity(OrderDeliveryDto dto);
	
	OrderDeliveryDto mapToDto(OrderDelivery entity);
}
