package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.InvoiceDto;
import com.example.meta.store.werehouse.Entities.Invoice;

@Mapper
public interface InvoiceMapper {
	
	
	
	Invoice mapToEntity(InvoiceDto dto);

	
	InvoiceDto mapToDto(Invoice entity);
}
