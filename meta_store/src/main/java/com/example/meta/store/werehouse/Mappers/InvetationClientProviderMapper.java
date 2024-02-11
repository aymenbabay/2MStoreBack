package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.InvetationDto;
import com.example.meta.store.werehouse.Dtos.WorkerDto;
import com.example.meta.store.werehouse.Entities.Invetation;

@Mapper
public interface InvetationClientProviderMapper {

	Invetation mapToEntity(InvetationDto dto);
	
	InvetationDto mapToDto(Invetation entity);
	

   @Mapping(source = "user.username", target = "name")
   @Mapping(source = "user.address", target = "address")
   @Mapping(source = "user.email", target = "email")
   @Mapping(source = "user.phone", target = "phone")
	WorkerDto mapInvetationToWorker(Invetation entity);
}
