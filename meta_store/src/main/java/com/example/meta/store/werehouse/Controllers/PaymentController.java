package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.example.meta.store.werehouse.Dtos.PaymentDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Payment;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Services.BankTransferService;
import com.example.meta.store.werehouse.Services.BillService;
import com.example.meta.store.werehouse.Services.CashService;
import com.example.meta.store.werehouse.Services.CheckService;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.PaymentService;
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

	private final ClientService clientService;
	
	private final PaymentService paymentService;

	private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	@PostMapping("cash")
	public void invoiceCashPayment(@RequestBody CashDto cashDto) {
		Client client = getClient();
		cashService.invoiceCashPayment(client, cashDto);
	}
	
	@PostMapping("check")
	public void invoiceCheckPayment(@RequestBody CheckDto checkDto) {
		Client client = getClient();
		checkService.invoiceCheckPayment(client, checkDto);
	}
	
	@PostMapping("bill")
	public void invoiceBillPayment(@RequestBody BillDto billDto) {
		Client client = getClient();
		billService.invoiceBillPayment(client, billDto);
	}
	
	@PostMapping("bank")
	public void invoiceBankTransferPayment(@RequestBody BankTransferDto bankTransferDto) {
		Client client = getClient();
		bankTransferService.invoiceBankTransferPayment(client, bankTransferDto);
	}
	
	@GetMapping("get_all_my")
	public List<PaymentDto> getAllMy(){
		Client client = getClient();
		return paymentService.getAllMy(client.getId(), client.getCompany().getId());
	}
	
	@GetMapping("get_all_my_as_company")
	public List<PaymentDto> getAllMyAsCompany(){
		Company company = getCompany().orElseThrow(() -> new RecordNotFoundException("you don't have a company"));
		return paymentService.getAllMy(null,company.getId());
	}
	
	@GetMapping("get_all_my_as_client")
	public List<PaymentDto> getAllMyAsClient(){
		Client client = getClient();
		return paymentService.getAllMy(client.getId(), null);
		
	}
	
	@GetMapping("{id}")
	public PaymentDto getMyById(@PathVariable Long id ) {
		return paymentService.getMyById(id);
	}
	
	@GetMapping("{response}/{id}")
	public void paymentResponse(@PathVariable Status response, @PathVariable Long id) {
		Company company = getCompany().orElseThrow(() -> new RecordNotFoundException("you dont have a company"));
		paymentService.paymentResponse(response, id, company);
	}
	
	private Client getClient() {
		Company company = getCompany().get();
		Client client = clientService.findByCompanyId(company.getId()).orElseThrow(() -> new RecordNotFoundException("you are not a client"));
		return client;
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
