package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.NotPermissonException;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderDto;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderLineDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.PassingClient;
import com.example.meta.store.werehouse.Enums.Status;
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
		Optional<Client> client = getClient();
		logger.warn(client.get().getId()+" <= client id ");
		Optional<PassingClient> pClient = getPassingClient();
		logger.warn(pClient.get().getId()+" <= pClient id 1 ");
		if(client.get().getId() == null && pClient.get().getId() == null) {			
		 pClient = CreatePassingClient();
		 logger.warn(pClient.get().getId()+" <= pClient id 2");
		 purchaseOrderService.addPurchaseOrder(purchaseOrderDto,null,pClient.get());
		 return;
		}
		purchaseOrderService.addPurchaseOrder(purchaseOrderDto,client.get(),pClient.get());
	}
	
	@GetMapping("get_order")
	public List<PurchaseOrderDto> getAllMyPerchaseOrder(){
		Optional<Client> client = getClient();
		Optional<PassingClient> pClient = getPassingClient();
		return purchaseOrderService.getAllMyPurchaseOrder(client.get(), pClient.get());
	}
	
	@GetMapping("{id}")
	public PurchaseOrderDto getOrderById(@PathVariable Long id) {
		Optional<PassingClient> passingClient = getPassingClient();
		Optional<Client> client = getClient();
		return purchaseOrderService.getOrderById(id,client, passingClient);
	}
	
	@GetMapping("{id}/{status}")
	public void OrderResponse(@PathVariable Long id, @PathVariable Status status) {
		Optional<Company> company = getCompany();
		if(company.get().getId() == null) {
			throw new NotPermissonException("you dont have permission to do that :)");
		}
		purchaseOrderService.OrderResponse(id,status,company.get());
	}
	
	@GetMapping("cancel/{id}")
	public void cancelOrder(@PathVariable Long id) {
		Optional<Client> client = getClient();
		Optional<PassingClient> passingClient = Optional.of(new PassingClient()) ;
		if(client.get().getId() == null) {
			 passingClient = getPassingClient();
			
		}
		purchaseOrderService.cancelOrder(client.get(),passingClient.get(), id);
	}
	
	@PutMapping("")
	public void UpdatePurchaseOrderLine(@RequestBody PurchaseOrderLineDto purchaseOrderLineDto) {
		Optional<Client> client = getClient();
		Optional<PassingClient> pClient = getPassingClient();
		purchaseOrderService.UpdatePurchaseOrderLine(purchaseOrderLineDto,client.get(),pClient.get());
	} 
	
	private Optional<PassingClient> CreatePassingClient() {
		User user = userService.findByUserName(authenticationFilter.userName);
		Optional<PassingClient> client = clientService.CreatePassingClient(user);
	return client;
	}
	
	private Optional<PassingClient> getPassingClient() {
		User user = userService.findByUserName(authenticationFilter.userName);
		Optional<PassingClient> client = clientService.findPassingClientBUser(user);
	return client;
	}
	
	private Optional<Client> getClient(){
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return Optional.of(new Client());
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
