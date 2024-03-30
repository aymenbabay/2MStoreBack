package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.meta.store.werehouse.Dtos.ClientCompanyDto;
import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Dtos.ProviderCompanyDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.ClientCompany;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;

@Mapper
public interface ClientMapper {
 
	Client mapToEntity(ClientDto dto);
	
	ClientDto mapToDto(Client entity);

	Client mapCompanyToClient(Company company);
	
	  @BeforeMapping
	    default void mapCompanyToClientCompany(Client entity, @MappingTarget ClientDto dto) {
	        for (ClientCompany clientCompany : entity.getCompanies()) {
	                ClientCompanyDto clientCompanyDto = new ClientCompanyDto();
	                clientCompanyDto.setAdvance(clientCompany.getAdvance());
	                clientCompanyDto.setMvt(clientCompany.getMvt());
	                clientCompanyDto.setCredit(clientCompany.getCredit());
	                clientCompanyDto.setId(clientCompany.getId());
	                dto.setClientcompany(clientCompanyDto);
	                break;
	            
	        }
	    }
}
