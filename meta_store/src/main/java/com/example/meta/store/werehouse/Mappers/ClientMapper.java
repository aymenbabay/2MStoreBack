package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;

@Mapper
public interface ClientMapper {
 
	Client mapToEntity(ClientDto dto);
	
	ClientDto mapToDto(Client entity);


    @Mapping(source = "codecp", target = "code")
	Client mapCompanyToClient(Company company);
}
