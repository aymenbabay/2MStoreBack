package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.ProviderService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/client/")
@RequiredArgsConstructor
public class ClientController {

	private final ClientService clientService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;
	
	private final ProviderService providerService;

	private final Logger logger = LoggerFactory.getLogger(ClientController.class);
	
	@GetMapping("get_all_my")
	public List<ClientDto> getAllMyClient(){
		Optional<Company> company = getCompany();
		return clientService.getAllMyClient(company.get());
	}
	
	@GetMapping("get_all_containing/{var}")
	public List<ClientDto> getAllClient(@PathVariable String var){
		Optional<Provider> provider = getProvider();
		return clientService.getAllClient(var,provider.get());
	}

	@GetMapping("get_all_my_containing/{value}")
	public List<ClientDto> getAllMyCointaining(@PathVariable String value){
		Client client = getClient();
		return clientService.getAllMyContaining(value,client);
	}
	
	@GetMapping("get_all_provider_containing/{var}")
	public List<ProviderDto> getAllProviderContaining(@PathVariable String var){
		logger.warn("get all provider containing in clinet controller");
		Client client = getClient();
		Optional<Provider> provider = getProvider();
		return clientService.getAllProviderContaining(var,client, provider.get());
	}
	  
	@GetMapping("add_as_client/{id}")
	public void addExistClient(@PathVariable Long id) {
		clientService.addExistClient(id, getCompany().get());
	}
	
	@PostMapping("add")
	public void insertClient(@RequestBody ClientDto clientDto) {
		Optional<Company> company = getCompany();
		clientService.insertClient(clientDto, company.get());
	}
	
	@PutMapping("/update/{id}")
	public void updateClient(@RequestBody ClientDto clientDto, @PathVariable Long id) {
		Optional<Company> company = getCompany();
		clientService.upDateMyClientById(id, clientDto, company.get());
	}
	
	@DeleteMapping("delete/{id}")
	public void deleteById(@PathVariable Long id) {
		Optional<Company> company = getCompany();
		clientService.deleteClientById(id, company.get());
	}
	
	@GetMapping("get_my_client_id")
	public Long getMyClientId() {
		return getClient().getId();
	}
	
	@GetMapping("checkClient/{id}")
	public boolean checkClient(@PathVariable Long id) {
		Optional<Provider> provider = getProvider();
		return clientService.checkClient(id,provider.get().getId());
	}
	
	private Client getClient() {
		Optional<Company> company = getCompany();
		return clientService.getMeAsClient(company.get()).get();
	}
	
	private Optional<Provider> getProvider() {
		Optional<Company> company = getCompany();
		return providerService.getMeAsProvider(company.get().getId());
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
		return Optional.ofNullable(company2.getBody());
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
		
	}
}
