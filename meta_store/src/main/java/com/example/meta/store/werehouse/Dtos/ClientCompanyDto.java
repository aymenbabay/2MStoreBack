package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientCompanyDto extends BaseDto<Long> implements Serializable {


	private ProviderDto client;
	
	private CompanyDto company;
	
	private Double mvt;
	
	private Double credit;
	
	private Double advance;
}
