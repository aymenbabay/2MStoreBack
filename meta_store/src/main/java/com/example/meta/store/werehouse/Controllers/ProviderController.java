package com.example.meta.store.werehouse.Controllers;

import java.util.List;

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
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.ProviderService;

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
	
	
	
	@PostMapping("/add")
	public ResponseEntity<ProviderDto> insertProvider(@RequestBody  ProviderDto providerDto){
		Company company = getCompany();
		return providerService.insertProvider(providerDto, company);
	}
	
	@GetMapping("/add_exist/{id}")
	public ResponseEntity<String> addExistProvider(@PathVariable Long id){
		Company company = getCompany();
		return providerService.addExistProvider(id,company);
	}
	
	@GetMapping("/get_all")
	public List<ProviderDto> getAll(){
		return providerService.getAllProviders();
	}
	
	@GetMapping("/get_all_real")
	public List<ProviderDto> getAllReal(){
		System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
		return providerService.getAllRealProviders();
	}
	
	@GetMapping("/get_all_my_virtual")
	public List<ProviderDto> getAllMyVirtual() {
		Company company = getCompany();
		return providerService.getAllMyVirtaul(company);
	}
	
	@GetMapping("/get_all_my")
	public List<ProviderDto> getAllMy(){
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
	
	@PutMapping("/update/{id}")
	public ProviderDto upDateMyProviderById(@PathVariable Long id, @RequestBody @Valid ProviderDto providerDto) {
		System.out.println("haw fi update provider"+providerDto.getId());
		Company company = getCompany();
		return providerService.upDateMyVirtualProviderById(id,providerDto,company);
	}
	
	@DeleteMapping("/delete_my/{id}")
	public void deleteMyProvider(@PathVariable Long id) {
		Company company = getCompany();
		providerService.deleteVirtualProviderById(id,company);
		
	}
	
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
	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Company company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company;
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}

}
