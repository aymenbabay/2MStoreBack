package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.ProviderCompanyDto;
import com.example.meta.store.werehouse.Entities.ProviderCompany;

@Mapper
public interface ProviderCompanyMapper {

	ProviderCompanyDto mapToDto(ProviderCompany entity);
	
}
