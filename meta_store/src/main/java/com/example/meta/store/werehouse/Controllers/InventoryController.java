package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.InventoryDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.InventoryService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/inventory")
@RequiredArgsConstructor
public class InventoryController {

	
	private final InventoryService inventoryService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;
	
	
	@GetMapping("/getbycompany/{id}")
	public List<InventoryDto> getInventoryByCompany(@PathVariable Long id){
		Company company;
		company = getCompany();
		if(company.getId() != id && company.getBranches().stream().anyMatch(branche -> branche.getId().equals(id))) {
			company = companyService.getById(id).getBody();
		}
		return inventoryService.getInventoryByCompanyId(company.getId());
	}
	
	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Company company = new Company();
			company = companyService.findByUserId(userId);
				if(company== null) {
					Long companyId = workerService.findCompanyIdByUserId(userId);
					if(companyId != null) {
						company = companyService.getById(companyId).getBody();						
					}
					
				}
		return company;
	}
	
	
}
