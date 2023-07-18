package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.SubCategoryDto;
import com.example.meta.store.werehouse.Entities.SubCategory;

@Mapper
public interface SubCategoryMapper {

	SubCategory mapToEntity(SubCategoryDto dto);
	
	SubCategoryDto mapToDto(SubCategory entity);
}
