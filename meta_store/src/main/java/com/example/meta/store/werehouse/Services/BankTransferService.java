package com.example.meta.store.werehouse.Services;

import java.util.Optional;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.BankTransferDto;
import com.example.meta.store.werehouse.Entities.BankTransfer;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Mappers.BankTransferMapper;
import com.example.meta.store.werehouse.Repositories.BankTransferRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BankTransferService extends BaseService<BankTransfer, Long>{
	
	private final BankTransferRepository bankTransferRepository;
	
	private final BankTransferMapper bankTransferMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;
	
	public void invoiceBankTransferPayment(Company company, BankTransferDto bankTransferDto) {
		if(bankTransferDto.getInvoice().getCompany().getId() != company.getId()) {
			throw new PermissionDeniedDataAccessException("you don't have permission to do that", null);
		}
		if(!bankTransferDto.getInvoice().getPaid()) {
		BankTransfer bankTransfer = bankTransferMapper.mapToEntity(bankTransferDto);
		bankTransferRepository.save(bankTransfer);
		clientService.paymentInpact(bankTransfer.getInvoice().getClient().getId(),bankTransfer.getInvoice().getCompany().getId(),bankTransfer.getAmount(), bankTransfer.getInvoice());
		providerService.paymentInpact(bankTransfer.getInvoice().getCompany().getId(),bankTransfer.getInvoice().getClient().getCompany().getId(),bankTransfer.getAmount());
		}
		
	}

}
