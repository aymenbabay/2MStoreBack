package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.InvetationClientProviderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.Worker;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.InvetationService;
import com.example.meta.store.werehouse.Services.ProviderService;
import com.example.meta.store.werehouse.Services.WorkerService;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/invetation/")
@RequiredArgsConstructor
public class InvetationController {

	private final InvetationService invetationService;

	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;
	
	private final ProviderService providerService;

	private final ClientService clientService;
	
	private final Logger logger = LoggerFactory.getLogger(InvetationController.class);
	
	@GetMapping("get_invetation")
	public List<InvetationClientProviderDto> getInvetation(){
		Client client = getClient();
		Provider provider = getProvider();
		Optional<Company> company = getCompany();
		logger.warn("just after get company function");
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		logger.warn("just before get invetation function");
		return invetationService.getInvetation(client,provider,company.get(), userId);
	}
	
	@GetMapping("response/{status}/{id}")
	public void requestResponse(@PathVariable Long id, @PathVariable Status status) {
		
		logger.warn("invetation controller in  the second line of request response function ");
		invetationService.requestResponse(id,status);
	} 
	
	@GetMapping("cancel/{id}")
	public void cancelRequestOrDeleteFriend(@PathVariable Long id) {
		Client client = getClient();
		Provider provider = getProvider();
		invetationService.cancelRequestOrDeleteFriend(client, provider, id);
	}
	
	@PostMapping("worker")
	public void sendWorkerInvitation(@RequestBody Worker worker) {
		Optional<Company> company = getCompany();
		invetationService.sendWorkerInvetation(company.get(),worker);
	}
	
	private Provider getProvider() {
		logger.warn("begin of get provider function");
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return new Provider();
		}
		Provider provider = providerService.getMeAsProvider(company.get().getId()).get();
		return provider;
	}
	
	private Client getClient(){
		Optional<Company> company = getCompany();
		logger.warn("begin of get client function ");
		if(company.isEmpty()) {
			return new Client();
		}
		Client client = clientService.getMeAsClient(company.get()).get();
		return client;
	}

	
	private Optional<Company> getCompany() {
		logger.warn("begin of get company function");
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		logger.warn("second of get company function");
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		logger.warn("third of get company function");	
		if(company.isPresent()) {
			return company;			
		}
		return Optional.of(new Company());

		
	}
}
