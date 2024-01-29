package com.example.meta.store.werehouse.Services;

import java.util.Optional;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.CheckDto;
import com.example.meta.store.werehouse.Entities.Cash;
import com.example.meta.store.werehouse.Entities.Check;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Mappers.CheckMapper;
import com.example.meta.store.werehouse.Repositories.CheckRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CheckService extends BaseService<Check, Long>{
	
	private final CheckRepository checkRepository;
	
	private final CheckMapper checkMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;
	
	public void invoiceCheckPayment(Company company, CheckDto checkDto) {
		if(checkDto.getInvoice().getCompany().getId() != company.getId()) {
			throw new PermissionDeniedDataAccessException("you don't have permission to do that", null);
		}
		if(!checkDto.getInvoice().getPaid()) {			
		Check check = checkMapper.mapToEntity(checkDto);		
		checkRepository.save(check);
		clientService.paymentInpact(check.getInvoice().getClient().getId(),check.getInvoice().getCompany().getId(),checkDto.getAmount(), check.getInvoice());
		providerService.paymentInpact(check.getInvoice().getCompany().getId(),check.getInvoice().getClient().getCompany().getId(),checkDto.getAmount());
		}
		
	}

}
