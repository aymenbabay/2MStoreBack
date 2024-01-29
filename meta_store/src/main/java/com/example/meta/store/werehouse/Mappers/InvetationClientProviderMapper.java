package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.InvetationClientProviderDto;
import com.example.meta.store.werehouse.Dtos.WorkerDto;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;

@Mapper
public interface InvetationClientProviderMapper {

	InvetationClientProvider mapToEntity(InvetationClientProviderDto dto);
	
	InvetationClientProviderDto mapToDto(InvetationClientProvider entity);
	

   @Mapping(source = "user.username", target = "name")
   @Mapping(source = "user.address", target = "address")
   @Mapping(source = "user.email", target = "email")
   @Mapping(source = "user.phone", target = "phone")
	WorkerDto mapInvetationToWorker(InvetationClientProvider entity);
}
