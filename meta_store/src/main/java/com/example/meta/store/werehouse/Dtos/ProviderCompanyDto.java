package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderCompanyDto extends BaseDto<Long> implements Serializable {

	//we can delete all entitis and dtos those related by provider and client and make all in one entity and dto
	private ProviderDto provider;
	
	private CompanyDto company;
	
	private Double mvt;
	
	private Double credit;
}
