package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.ClientCompanyDto;
import com.example.meta.store.werehouse.Entities.ClientCompany;

@Mapper
public interface ClientCompanyMapper {

	ClientCompanyDto mapToDto(ClientCompany entity);
}
