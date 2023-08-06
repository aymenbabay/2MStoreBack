package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.CommandLine;

@Mapper
public interface CommandLineMapper {


    @Mapping(source = "companyArticle", target = "companyArticle.id")
	CommandLine mapToEntity(CommandLineDto dto);
	

   @Mapping(source = "companyArticle.id", target = "companyArticle")
	CommandLineDto mapToDto(CommandLine entity);
}
