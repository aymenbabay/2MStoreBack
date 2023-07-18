package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Provider;

@Mapper
public interface ProviderMapper {

	Provider mapToEntity(ProviderDto dto);
	
	ProviderDto mapToDto(Provider entity);
}
