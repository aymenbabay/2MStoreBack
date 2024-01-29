package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.BankTransferDto;
import com.example.meta.store.werehouse.Entities.BankTransfer;

@Mapper
public interface BankTransferMapper {

	BankTransfer mapToEntity(BankTransferDto dto);
	
	BankTransferDto maptoDto(BankTransfer entity);
	
}
