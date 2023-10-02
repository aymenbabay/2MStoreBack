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
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.InvetationService;
import com.example.meta.store.werehouse.Services.ProviderService;
import com.example.meta.store.werehouse.Services.WorkerService;

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
		Optional<Client> client = getClient();
		Optional<Provider> provider = getProvider();
		Optional<Company> company = getCompany();
		return invetationService.getInvetation(client.get(),provider.get(),company.get());
	}
	
	@GetMapping("response/{status}/{id}")
	public void requestResponse(@PathVariable Long id, @PathVariable Status status) {
		
		logger.warn("invetation controller in  the second line of request response function ");
		invetationService.requestResponse(id,status);
	}
	
	@GetMapping("cancel/{id}")
	public void cancelRequestOrDeleteFriend(@PathVariable Long id) {
		Client client = getClient().get();
		Provider provider = getProvider().get();
		invetationService.cancelRequestOrDeleteFriend(client, provider, id);
	}
	
	private Optional<Provider> getProvider() {
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return null;
		}
		Optional<Provider> provider = providerService.getMeAsProvider(company.get().getId());
		return provider;
	}
	
	private Optional<Client> getClient(){
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return null;
		}
		Optional<Client> client = clientService.getMeAsClient(company.get());
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
