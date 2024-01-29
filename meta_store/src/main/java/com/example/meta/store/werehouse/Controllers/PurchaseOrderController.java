package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderDto;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderLineDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.PassingClient;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.PurchaseOrderService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/werehouse/order/")
@RequiredArgsConstructor
public class PurchaseOrderController {

	private final PurchaseOrderService purchaseOrderService;

	private final ClientService clientService;

	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;

	private final WorkerService workerService;
	
	private final Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);
	
	@PostMapping()
	public void addPurchaseOrder(@RequestBody List<PurchaseOrderLineDto> purchaseOrderDto) {
		logger.warn(purchaseOrderDto.get(0).getQuantity()+" quantity");
		logger.warn(purchaseOrderDto.size()+ " size");
		Optional<Client> client = getClient();
		if(client.isEmpty()) {			
		PassingClient pClient = getPassingClient();
		 purchaseOrderService.addPurchaseOrder(purchaseOrderDto,null,pClient);
		 return;
		}
		purchaseOrderService.addPurchaseOrder(purchaseOrderDto,client.get(),null);
	}
	
	@GetMapping("get_order")
	public List<PurchaseOrderDto> getAllMyPerchaseOrder(){
		Optional<Company> company = getCompany();
		return purchaseOrderService.getAllMyPurchaseOrder(company.get());
	}
	
	private PassingClient getPassingClient() {
		User user = userService.findByUserName(authenticationFilter.userName);
		PassingClient client = clientService.findPassingClientBUser(user);
	return client;
	}
	
	private Optional<Client> getClient(){
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return Optional.empty();
		}
		Optional<Client> client = clientService.getMeAsClient(company.get());
		return client;
	}
	
	private Optional<Company> getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company.isPresent()) {
			return company;
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return Optional.of(company2.getBody());
		}
		return Optional.empty();
	}
	
}
