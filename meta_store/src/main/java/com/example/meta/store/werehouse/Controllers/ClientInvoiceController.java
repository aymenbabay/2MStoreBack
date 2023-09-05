package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.ClientInvoiceDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Services.ClientInvoiceService;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.ProviderService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/werehouse/clientinvoice/")
@RequiredArgsConstructor
public class ClientInvoiceController {

	private final ClientInvoiceService clientInvoiceService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;
	
	private final Logger logger = LoggerFactory.getLogger(ClientInvoiceController.class);

	@GetMapping("getnotaccepted")
	public List<ClientInvoiceDto> getInvoiceNotifications(){
		Client client = getMeAsClient();
		Provider provider = getMeAsProvider();
		return clientInvoiceService.getInvoiceNotifications(client, provider);
	}
	
	@GetMapping("response/{type}/{invoice}")
	public void statusInvoice(@PathVariable String type, @PathVariable Long invoice) {
		Client client = getMeAsClient();
		switch (type) {
		case "ACCEPT": {
			logger.warn(type);
			clientInvoiceService.accepted(invoice,client.getId());
			break;
		}
		case "REFUSE":{
			logger.warn(type);
			clientInvoiceService.refused(invoice,client.getId());
			break;
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}
	
	private Provider getMeAsProvider() {
		Company company = getCompany();
		return providerService.getMeAsProvider(company.getId());
	}
	
	private Client getMeAsClient() {
		Company company = getCompany();
		Client client = clientService.getMeAsClient(company);
		return client;
	}
	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Company company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company;
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return company2.getBody();
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}
}
