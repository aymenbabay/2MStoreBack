package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.CommandLine;

@Mapper
public interface CommandLineMapper {


    @Mapping(source = "companyarticle", target = "companyarticle.id")
	CommandLine mapToEntity(CommandLineDto dto);
	

    @Mapping(source = "companyarticle.id", target = "companyarticle")
	CommandLineDto mapToDto(CommandLine entity);
}
