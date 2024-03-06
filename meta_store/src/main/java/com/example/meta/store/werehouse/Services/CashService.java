package com.example.meta.store.werehouse.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.example.meta.store.werehouse.Dtos.CashDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Payment;
import com.example.meta.store.werehouse.Enums.PaymentMode;
import com.example.meta.store.werehouse.Enums.PaymentStatus;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.PaymentMapper;
import com.example.meta.store.werehouse.Repositories.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

public class CashService {
	
	private final PaymentRepository paymentRepository;
	
	private final PaymentMapper paymentMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;

	private final Logger logger = LoggerFactory.getLogger(CashService.class);
	
	public void invoiceCashPayment(Client client, CashDto cashDto) {
		if(cashDto.getInvoice().getClient().getId() != client.getId()) {
			throw new PermissionDeniedDataAccessException("you don't have permission to do that", null);
		}
		if(cashDto.getInvoice().getPaid() != PaymentStatus.PAID && cashDto.getInvoice().getStatus() == Status.ACCEPTED) {			
		Payment cash = paymentMapper.mapCashToPayment(cashDto);
		if(cashDto.getInvoice().getCompany().getId() == client.getCompany().getId()) {
			cash.setStatus(Status.ACCEPTED);
			
			clientService.paymentInpact(cash.getInvoice().getClient().getId(),cash.getInvoice().getCompany().getId(),cash.getAmount(), cash.getInvoice());
			providerService.paymentInpact(cash.getInvoice().getCompany().getId(),cash.getInvoice().getClient().getCompany().getId(),cash.getAmount());
			
		}else {		
			cash.setStatus(Status.INWAITING);
		}
		cash.setType(PaymentMode.CASH);
		paymentRepository.save(cash);
	}
	}

}
