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
import com.example.meta.store.werehouse.Dtos.ClientCompanyDto;
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
	
	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	@DeleteMapping("delete/{id}")
	public void deleteById(@PathVariable Long id) {
		Company company = getCompany();
		clientService.deleteClientByIdAndCompanyId(id, company);
	}
	
	@GetMapping("get_all_my/{id}")
	public List<ClientCompanyDto> getAllMyClient(@PathVariable Long id){
		Company company = getCompany();
		if(company.getId() != id && company.getBranches().stream().anyMatch(branche -> branche.getId().equals(id))) {
			company = companyService.getById(id).getBody();
		}
		return clientService.getAllMyClient(company);
	}

	@PostMapping("add")
	public void insertClient(@RequestBody ClientDto clientDto) {
		Company company = getCompany();
		clientService.insertClient(clientDto, company);
	}
	
	@PutMapping("/update")
	public void updateClient(@RequestBody ClientDto clientDto) {
		Company company = getCompany();
		clientService.upDateMyClientById( clientDto, company);
	}
		
	private Client getClient() {
		Company company = getCompany();
		return clientService.getMeAsClient(company.getId()).get();
	}
	
	@GetMapping("get_all_containing/{var}/{id}")
	public List<ClientCompanyDto> getAllClient(@PathVariable String var, @PathVariable Long id){
		Company company = getCompany();
		if(company.getId() != id && company.getBranches().stream().anyMatch(branche -> branche.getId().equals(id))) {
			company = companyService.getById(id).getBody();
		}
		return clientService.getAllClientContaining(var,company);
	}
	
	@GetMapping("get_all_my_containing/{value}/{id}")
	public List<ClientDto> getAllMyCointaining(@PathVariable String value, @PathVariable Long id){
		Company company = getCompany();
		if(company.getId() != id && company.getBranches().stream().anyMatch(branche -> branche.getId().equals(id))) {
			company = companyService.getById(id).getBody();
		}
		return clientService.getAllMyContaining(value,company);
	}

	@GetMapping("add_as_client/{id}")
	public void addExistClient(@PathVariable Long id) {
		clientService.addExistClient(id, getCompany());
	}

	@GetMapping("get_my_client_id")
	public Long getMyClientId() {
		return getClient().getId();
	}

	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company.isPresent()) {
			return company.get();
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
			ResponseEntity<Company> company2 = companyService.getById(companyId);
			return (company2.getBody());
		}
		throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
		
	}
	/////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////
	@GetMapping("checkClient/{id}/{companyId}")
	public boolean checkClient(@PathVariable Long id, @PathVariable Long companyId) {
		Company company = getCompany();
		if(company.getId() != id && company.getBranches().stream().anyMatch(branche -> branche.getId().equals(id))) {
			return clientService.checkClient(id,companyId);
		}
		return clientService.checkClient(id,company.getId());
	}
	
	@GetMapping("{search}")
	public List<ClientDto> getAllClientContaininga(@PathVariable String search){
		Company company = getCompany();
		return clientService.getAllClientContaininga(search, company.getId());
	}
}
