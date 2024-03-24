package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.meta.store.werehouse.Dtos.CompanyDto;
import com.example.meta.store.werehouse.Dtos.ProviderCompanyDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;

@Mapper
public interface ProviderMapper {

	Provider mapToEntity(ProviderDto dto);
	
	ProviderDto mapToDto(Provider entity);
	
	  @BeforeMapping
	    default void mapCompanyToProviderCompany(Provider entity, @MappingTarget ProviderDto dto) {
	        for (ProviderCompany providerCompany : entity.getCompanies()) {
	                ProviderCompanyDto providerCompanyDto = new ProviderCompanyDto();
	                providerCompanyDto.setAdvance(providerCompany.getAdvance());
	                providerCompanyDto.setMvt(providerCompany.getMvt());
	                providerCompanyDto.setCredit(providerCompany.getCredit());
	                providerCompanyDto.setId(providerCompany.getId());
	                dto.setProvidercompany(providerCompanyDto);
	                break;
	            
	        }
	    }
}
