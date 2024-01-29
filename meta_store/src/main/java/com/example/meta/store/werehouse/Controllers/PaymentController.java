package com.example.meta.store.werehouse.Controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.BankTransferDto;
import com.example.meta.store.werehouse.Dtos.BillDto;
import com.example.meta.store.werehouse.Dtos.CashDto;
import com.example.meta.store.werehouse.Dtos.CheckDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.BankTransferService;
import com.example.meta.store.werehouse.Services.BillService;
import com.example.meta.store.werehouse.Services.CashService;
import com.example.meta.store.werehouse.Services.CheckService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/werehouse/payment/")
@RequiredArgsConstructor
public class PaymentController {

	private final CashService cashService;
	
	private final CheckService checkService;
	
	private final BillService billService;
	
	private final BankTransferService bankTransferService;

	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;

	private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	@PostMapping("cash")
	public void invoiceCashPayment(@RequestBody CashDto cashDto) {
		Company company = getCompany().get();
		cashService.invoiceCashPayment(company, cashDto);
	}
	
	@PostMapping("check")
	public void invoiceCheckPayment(@RequestBody CheckDto checkDto) {
		Company company = getCompany().get();
		checkService.invoiceCheckPayment(company, checkDto);
	}
	
	@PostMapping("bill")
	public void invoiceBillPayment(@RequestBody BillDto billDto) {
		Company company = getCompany().get();
		billService.invoiceBillPayment(company, billDto);
	}
	
	@PostMapping("bank")
	public void invoiceBankTransferPayment(@RequestBody BankTransferDto bankTransferDto) {
		Company company = getCompany().get();
		bankTransferService.invoiceBankTransferPayment(company, bankTransferDto);
	}
	
	
	private Optional<Company> getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company;
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return Optional.of(company2.getBody());
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}
}
