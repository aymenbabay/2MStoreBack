package com.example.meta.store.werehouse.Services;

import java.util.Optional;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.BillDto;
import com.example.meta.store.werehouse.Entities.Bill;
import com.example.meta.store.werehouse.Entities.Bill;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Mappers.BillMapper;
import com.example.meta.store.werehouse.Repositories.BillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BillService extends BaseService<Bill, Long>{
	
	private final BillRepository billRepository;
	
	private final BillMapper billMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;
	
	public void invoiceBillPayment(Company company, BillDto billDto) {
		if(billDto.getInvoice().getCompany().getId() != company.getId()) {
			throw new PermissionDeniedDataAccessException("you don't have permission to do that", null);
		}
		if(!billDto.getInvoice().getPaid()) {			
		Bill bill = billMapper.mapToEntity(billDto);
		billRepository.save(bill);
		clientService.paymentInpact(bill.getInvoice().getClient().getId(),bill.getInvoice().getCompany().getId(),bill.getAmount(), bill.getInvoice());
		providerService.paymentInpact(bill.getInvoice().getCompany().getId(),bill.getInvoice().getClient().getCompany().getId(),bill.getAmount());
		}
		
		
	}

}
