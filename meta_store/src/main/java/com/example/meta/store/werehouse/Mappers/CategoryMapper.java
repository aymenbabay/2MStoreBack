package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.CategoryDto;
import com.example.meta.store.werehouse.Entities.Category;

@Mapper
public interface CategoryMapper {


	Category mapToEntity(CategoryDto dto);
	
	CategoryDto mapToDto(Category entity);
}
