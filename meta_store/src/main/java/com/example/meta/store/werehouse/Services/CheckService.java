package com.example.meta.store.werehouse.Services;


import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.example.meta.store.werehouse.Dtos.CheckDto;
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
public class CheckService{
	
	private final PaymentRepository paymentRepository;
	
	private final PaymentMapper paymentMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;
	
	public void invoiceCheckPayment(Client client, CheckDto checkDto) {
		if(checkDto.getInvoice().getClient().getId() != client.getId() && checkDto.getInvoice().getCompany().getId() != client.getCompany().getId()) {
			throw new PermissionDeniedDataAccessException("you don't have permission to do that", null);
		}
		if(checkDto.getInvoice().getPaid() != PaymentStatus.PAID && checkDto.getInvoice().getStatus() == Status.ACCEPTED) {			
		Payment check = paymentMapper.mapCheckToPayment(checkDto);
		if(checkDto.getInvoice().getCompany().getId() == client.getCompany().getId()) {
			check.setStatus(Status.ACCEPTED);
			
			clientService.paymentInpact(check.getInvoice().getClient().getId(),check.getInvoice().getCompany().getId(),check.getAmount(), check.getInvoice());
			providerService.paymentInpact(check.getInvoice().getCompany().getId(),check.getInvoice().getClient().getCompany().getId(),check.getAmount());
			
		}else {
			check.setStatus(Status.INWAITING);			
		}
		check.setType(PaymentMode.CHECK);
		paymentRepository.save(check);
		}
		
	}

}
