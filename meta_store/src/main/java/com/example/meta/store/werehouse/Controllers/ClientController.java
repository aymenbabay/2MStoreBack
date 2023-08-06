package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.WorkerService;

import jakarta.websocket.server.PathParam;
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
	
	@GetMapping("get_all_my")
	public List<ClientDto> getAllMyClient(){
		Company company = getCompany();
		return clientService.getAllMyClient(company);
	}
	
	@GetMapping("get_all")
	public List<ClientDto> getAllClient(){
		Company company = getCompany();
		return clientService.getAllClient(company);
	}
	
	@GetMapping("add_exist/{id}")
	public void addExistClient(@PathVariable Long id) {
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		clientService.addExistClient(id, getCompany());
	}
	
	@PostMapping("add")
	public void insertClient(@RequestBody ClientDto clientDto) {
		Company company = getCompany();
		clientService.insertClient(clientDto, company);
	}
	
	@PutMapping("/update/{id}")
	public void updateClient(@RequestBody ClientDto clientDto, @PathVariable Long id) {
		Company company = getCompany();
		clientService.upDateMyClientById(id, clientDto, company);
	}
	
	private Client getClient() {
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
