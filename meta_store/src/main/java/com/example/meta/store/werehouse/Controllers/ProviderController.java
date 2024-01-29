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
import com.example.meta.store.werehouse.Dtos.ProviderCompanyDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.ProviderService;
import com.example.meta.store.werehouse.Services.WorkerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/provider")
@RequiredArgsConstructor
public class ProviderController {

	private final ProviderService providerService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;

	private final Logger logger = LoggerFactory.getLogger(ProviderController.class);
	
	
	@PostMapping("/add")
	public ResponseEntity<ProviderDto> insertProvider(@RequestBody  ProviderDto providerDto){
		Company company = getCompany();
		return providerService.insertProvider(providerDto, company);
	}
	
	@GetMapping("/add_as_provider/{id}")
	public void addExistProvider(@PathVariable Long id){
		Company company = getCompany();
		logger.warn("add as provider provider controller "+id);
		 providerService.addExistProvider(id,company);
	}
	
	@GetMapping("/get_all")
	public List<ProviderDto> getAll(){
		return providerService.getAllProviders();
	}
	
	@GetMapping("/get_all_my_virtual")
	public List<ProviderDto> getAllMyVirtual() {
		Company company = getCompany();
		return providerService.getAllMyVirtaul(company);
	}
	
	@GetMapping("/get_all_my")
	public List<ProviderCompanyDto> getAllMy(){
		Company company = getCompany();
		return providerService.getAllMyProvider(company);
	}
	
	@GetMapping("/get_my_by_code/{code}")
	public ProviderDto getMyByCode(@PathVariable @Valid String code) {
		Company company = getCompany();
		return providerService.getMyByCodeAndCompanyId(code,company);
	}

	@GetMapping("/get_my_by_name/{name}")
	public List<ProviderDto> getMyByName(@PathVariable @Valid String name) {

		return null;
}

	@GetMapping("/get_all_by_code/{code}")
	public ProviderDto getAllByCode(@PathVariable String code) {
		return providerService.getProviderByCode(code);
				
	}
	
	@GetMapping("/get_all_by_name/{name}")
	public List<ProviderDto> getAllByName(@PathVariable String name) {
		return null;
				
	}
	
	@PutMapping("/update")
	public ProviderDto upDateMyProviderById( @RequestBody @Valid ProviderDto providerDto) {
		System.out.println("haw fi update provider"+providerDto.getId());
		Company company = getCompany();
		return providerService.upDateMyVirtualProviderById(providerDto,company);
	}
//	
//	@DeleteMapping("/delete_my/{id}")
//	public void deleteMyProvider(@PathVariable Long id) {
//		Company company = getCompany();
//		providerService.deleteVirtualProviderById(id,company);
//		
//	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteProvider(@PathVariable Long id) {
		Company company = getCompany();
		providerService.deleteProviderById(id,company);
	}
	
	@GetMapping("/get_my_provider_id")
	public Long getMyProviderId() {
		Company company = getCompany();
		return providerService.getMeProviderId(company.getId());
	}
	
	@GetMapping("check_provider/{id}")
	public boolean checkProviderById(@PathVariable Long id) {
		logger.warn("check provider in provider controller");
	Company company = getCompany();
	return providerService.checkProviderById(id,company.getId());
	}
	
	@GetMapping("get_all_provider_containing/{search}")
	public List<ProviderDto> getAllProviderContaining(@PathVariable String search){
		Company company = getCompany();
		return providerService.getAllProvidersContaining(company, search);
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

}
