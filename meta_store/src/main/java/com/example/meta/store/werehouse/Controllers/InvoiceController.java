package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.InvoiceDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.InvoiceService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/invoice/")
@RequiredArgsConstructor
public class InvoiceController {

	private final InvoiceService invoiceService;

	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;
	
	private final ClientService clientService;

	private final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	@GetMapping("getlastinvoice")
	public Long getLastInvoiceCode() {
		Company company = getCompany();
		return invoiceService.getLastInvoice(company.getId());
	}
	
	@GetMapping("getMyInvoiceAsProvider/{id}")
	public List<InvoiceDto> getMyInvoiceAsProvider(@PathVariable Long id){
		Optional<Company> company = getMyCompany();
		if(company.get().getId() != null) {
			if(company.get().getId() != id &&  company.get().getBranches().stream().anyMatch(branche -> branche.getId().equals(id))) {
				return invoiceService.getMyInvoiceAsProvider(id, null);
			}
			return invoiceService.getMyInvoiceAsProvider(company.get().getId(),null);
		}
		company = getHisCompany();
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		return invoiceService.getMyInvoiceAsProvider(company.get().getId(),userId);
		
	}
	
	@GetMapping("getMyInvoiceAsClient/{id}")
	public List<InvoiceDto> getInvoicesAsClient(@PathVariable Long id){
		Company company= getCompany();
		if(company.getId() != id && company.getBranches().stream().anyMatch(branche -> branche.getId().equals(id))) {
			return invoiceService.getInvoicesAsClient(id);			
		}
		return invoiceService.getInvoicesAsClient(company.getId());
	}
	
	@GetMapping("getnotaccepted")
	public List<InvoiceDto> getInvoiceNotifications(){
		Client client = getMeAsClient();
		Optional<Company> company = getHisCompany();
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		return invoiceService.getInvoiceNotifications(client,company,userId);
	}
	
	@GetMapping("cancel_invoice/{id}")
	public void cancelInvoice(@PathVariable Long id) {
		Company company = getCompany();
		invoiceService.cancelInvoice(company, id);
	}
	
	private Optional<Company> getMyCompany(){
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company.isPresent()) {			
			return company;
		}
		return Optional.of(new Company());
		
	}
	
	private Optional<Company> getHisCompany() {
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return Optional.of(company2.getBody());
		}
		return null;
	}
	
	private Company getCompany() {
		Optional<Company> company = getMyCompany();
		if(company.get().getId() != null) {
			return company.get();
		}
		 company = getHisCompany();
		if(company.get().getId() != null) {
			return company.get();
		}
		
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");			
	}
	
	
	private Client getMeAsClient() {
		Optional<Company> company = getMyCompany();
		Client client = clientService.getMeAsClient(company.get().getId()).get();
		return client;
	}
	

	@GetMapping("response/{type}/{invoice}")
	public void statusInvoice(@PathVariable String type, @PathVariable Long invoice) {
		Client client = getMeAsClient();
		switch (type) {
		case "ACCEPT": {
			invoiceService.accepted(invoice,client);
			break;
		}
		case "REFUSE":{
			invoiceService.refused(invoice,client.getId());
			break;
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}
	
	
}
