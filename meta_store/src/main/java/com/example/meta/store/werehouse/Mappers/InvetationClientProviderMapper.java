package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.InvetationClientProviderDto;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;

@Mapper
public interface InvetationClientProviderMapper {

	InvetationClientProvider mapToEntity(InvetationClientProviderDto dto);
	
	InvetationClientProviderDto mapToDto(InvetationClientProvider entity);
}
