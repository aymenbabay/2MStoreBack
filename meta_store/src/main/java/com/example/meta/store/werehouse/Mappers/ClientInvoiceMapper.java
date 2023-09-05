package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.ClientInvoiceDto;
import com.example.meta.store.werehouse.Entities.ClientInvoice;


@Mapper
public interface ClientInvoiceMapper {

	@Mapping(source = "invoice", target = "invoice.code")
	@Mapping(source = "invoiceDate", target = "invoice.lastModifiedDate")
	@Mapping(source = "invoiceId", target = "invoice.id")
    @Mapping(source = "providerName", target = "provider.name")
	@Mapping(source = "providerPhone", target = "provider.phone")
	@Mapping(source = "providerAddress", target = "provider.address")
	@Mapping(source = "providerMatriculeFiscal", target = "provider.matfisc")
	@Mapping(source = "providerId", target = "provider.id")
	@Mapping(source = "clientName", target = "client.name")
	@Mapping(source = "clientId", target = "client.id")
	@Mapping(source = "clientPhone", target = "client.phone")
	@Mapping(source = "clientAddress", target = "client.address")
	@Mapping(source = "clientMatriculeFiscal", target = "client.matfisc")
	ClientInvoice mapToEntity (ClientInvoiceDto dto);
	

    @Mapping(source = "invoice.code", target = "invoice")
    @Mapping(source = "invoice.lastModifiedDate", target = "invoiceDate")
    @Mapping(source = "invoice.id", target = "invoiceId")
    @Mapping(source = "provider.name", target = "providerName")
    @Mapping(source = "provider.phone", target = "providerPhone")
    @Mapping(source = "provider.address", target = "providerAddress")
    @Mapping(source = "provider.matfisc", target = "providerMatriculeFiscal")
    @Mapping(source = "provider.id", target = "providerId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.phone", target = "clientPhone")
    @Mapping(source = "client.address", target = "clientAddress")
    @Mapping(source = "client.matfisc", target = "clientMatriculeFiscal")
	ClientInvoiceDto mapToDto (ClientInvoice entity);
}
