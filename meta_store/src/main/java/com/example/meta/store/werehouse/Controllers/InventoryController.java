package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.InventoryDto;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.InventoryService;

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
	
	
	@GetMapping("/getbycompany")
	public List<InventoryDto> getInventoryByCompany(){
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Long companyId = companyService.findCompanyIdByUserId(userId).get().getId();
		return inventoryService.getInventoryByCompanyId(companyId);
	}
	
	
}
