package com.example.meta.store.werehouse.Services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.PaymentController;
import com.example.meta.store.werehouse.Dtos.CashDto;
import com.example.meta.store.werehouse.Entities.Cash;
import com.example.meta.store.werehouse.Entities.ClientCompany;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Mappers.CashMapper;
import com.example.meta.store.werehouse.Repositories.CashRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

public class CashService extends BaseService<Cash, Long> {
	
	private final CashRepository cashRepository;
	
	private final CashMapper cashMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;

	private final Logger logger = LoggerFactory.getLogger(CashService.class);
	
	public void invoiceCashPayment(Company company, CashDto cashDto) {
		if(cashDto.getInvoice().getCompany().getId() != company.getId()) {
			throw new PermissionDeniedDataAccessException("you don't have permission to do that", null);
		}
		if(!cashDto.getInvoice().getPaid()) {			
		Cash cash = cashMapper.mapToEntity(cashDto);
		cashRepository.save(cash);
		clientService.paymentInpact(cash.getInvoice().getClient().getId(),cash.getInvoice().getCompany().getId(),cash.getAmount(), cash.getInvoice());
		providerService.paymentInpact(cashDto.getInvoice().getCompany().getId(),cashDto.getInvoice().getClient().getCompany().getId(),cashDto.getAmount());
		}
	}

}
