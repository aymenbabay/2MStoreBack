package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.CompanyDto;
import com.example.meta.store.werehouse.Entities.Company;

@Mapper
public interface CompanyMapper {


	Company mapToEntity (CompanyDto dto);

	CompanyDto mapToDto (Company entity);
}
