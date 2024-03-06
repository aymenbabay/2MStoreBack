package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.BankTransferDto;
import com.example.meta.store.werehouse.Dtos.BillDto;
import com.example.meta.store.werehouse.Dtos.CashDto;
import com.example.meta.store.werehouse.Dtos.CheckDto;
import com.example.meta.store.werehouse.Dtos.PaymentDto;
import com.example.meta.store.werehouse.Entities.Payment;

@Mapper
public interface PaymentMapper {

	Payment mapCashToPayment(CashDto dto);
	
	Payment mapCheckToPayment(CheckDto dto);
	
	Payment mapBillToPayment(BillDto dto);
	
	Payment mapBanktransferToPayment(BankTransferDto dto);
	
	PaymentDto mapToDto(Payment entity);
	
	
}
