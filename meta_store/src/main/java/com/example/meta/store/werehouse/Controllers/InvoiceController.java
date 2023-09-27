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
	
	@GetMapping("getlastinvoice")
	public Long getLastInvoiceCode() {
		Company company = getCompany();
		return invoiceService.getLastInvoice(company.getId());
	}
	
	@GetMapping("getMyInvoiceAsProvider")
	public List<InvoiceDto> getMyInvoiceAsProvider(){
		Company company = getCompany();
		return invoiceService.getMyInvoiceAsProvider(company.getId());
		
	}
	
	@GetMapping("getMyInvoiceAsClient")
	public List<InvoiceDto> getInvoicesAsClient(){
		Company company = getCompany();
		return invoiceService.getInvoicesAsClient(company);
	}
	
	@GetMapping("cancel_invoice/{id}")
	public void cancelInvoice(@PathVariable Long id) {
		Company company = getCompany();
		invoiceService.cancelInvoice(company, id);
	}
	
	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company.get();
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return company2.getBody();
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");			
	}
	
	/////////////////////// clinet invoice methods////////////////////
	
	@GetMapping("getnotaccepted")
	public List<InvoiceDto> getInvoiceNotifications(){
		Client client = getMeAsClient();
		return invoiceService.getInvoiceNotifications(client);
	}
	
	private Client getMeAsClient() {
		Company company = getCompany();
		Client client = clientService.getMeAsClient(company).get();
		return client;
	}
	

	@GetMapping("response/{type}/{invoice}")
	public void statusInvoice(@PathVariable String type, @PathVariable Long invoice) {
		Client client = getMeAsClient();
		switch (type) {
		case "ACCEPT": {
			logger.warn(type);
			invoiceService.accepted(invoice,client.getId());
			break;
		}
		case "REFUSE":{
			logger.warn(type);
			invoiceService.refused(invoice,client.getId());
			break;
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}
	
	
}
