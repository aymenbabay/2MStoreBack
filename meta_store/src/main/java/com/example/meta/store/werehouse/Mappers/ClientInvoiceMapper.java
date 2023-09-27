package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.InvoiceReturnDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;

@Mapper
public interface ClientInvoiceMapper {

	
	InvoiceReturnDto mapClientToClientInvoice(Client entity);

	InvoiceReturnDto mapCompanyToClientInvoice(Company entity);
	
	InvoiceReturnDto mapProviderToClientInvoice(Provider entity);
}
