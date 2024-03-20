package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.NotPermissonException;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.PaymentDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Payment;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.PaymentMapper;
import com.example.meta.store.werehouse.Repositories.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService extends BaseService<Payment, Long>{

	private final PaymentRepository paymentRepository;
	
	private final PaymentMapper paymentMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;
	

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	public void paymentResponse(Status response, Long id, Company company) {
		Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("there is no payment"));
		if(!payment.getInvoice().getCompany().equals(company)) {
			throw new NotPermissonException("you dont have a permission");
		}
		if(response == Status.ACCEPTED) {			
			clientService.paymentInpact(payment.getInvoice().getClient().getId(),payment.getInvoice().getCompany().getId(),payment.getAmount(), payment.getInvoice());
			providerService.paymentInpact(payment.getInvoice().getCompany().getId(),payment.getInvoice().getClient().getCompany().getId(),payment.getAmount());
		}else {
			paymentRepository.delete(payment);
		}
		payment.setStatus(response);
		
		
	}

	
	/////////////////////////////////////////////////////// future work ///////////////////////////////////////////////////
	/////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	public List<PaymentDto> getAllMy(Long clientId, Long companyId){
		List<Payment> payments = paymentRepository.findAllByCompanyIdOrClientId(companyId, clientId);
		return mapper(payments);
	}

	public PaymentDto getMyById(Long id) {
		Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("there is no payment "));
		
		PaymentDto paymentDto = paymentMapper.mapToDto(payment);
		return paymentDto;
		
	}


	
	
	private List<PaymentDto> mapper(List<Payment> payments){
		if(payments.isEmpty()) {
			throw new RecordNotFoundException("there is no pyment ");
		}
		List<PaymentDto> paymentsDto = new ArrayList<>();
		for(Payment i : payments) {
			PaymentDto paymentDto = paymentMapper.mapToDto(i);
			paymentsDto.add(paymentDto);
		}
		return paymentsDto;
	}
	
}
