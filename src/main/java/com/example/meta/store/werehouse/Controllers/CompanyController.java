package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.CompanyDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/werehouse/company")
@RequiredArgsConstructor
public class CompanyController {

	private final JwtAuthenticationFilter authenticationFilter;
	
	private final CompanyService companyService;
	
	private final UserService userService;
	
	private final WorkerService workerService;
	
	@GetMapping("/all")
	public List<CompanyDto> getAll(){
		return companyService.getAllCompany();
	}
	
	@PostMapping("/add")
	public ResponseEntity<CompanyDto> insertCompany( 
			@RequestParam("company") String company,
			@RequestParam(value ="file", required = false) MultipartFile file)throws Exception{
		User user = userService.findByUserName(authenticationFilter.userName);
		return companyService.insertCompany(company, file, user);
		}
	
	@PutMapping("/update")
	public ResponseEntity<CompanyDto> upDateCompany(
			@RequestParam("company") String companyDto,
			@RequestParam(value ="file", required = false) MultipartFile file
			) throws Exception{
		Optional<Company> company = getCompany();
		return companyService.upDateCompany(companyDto, file,company);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id){
		return companyService.getCompanyById(id);		
	}
	
	@GetMapping("/mycompany")
	public CompanyDto getMe() {
		Optional<Company> company = getCompany();
		return companyService.getMe(company.get());
	}
	
	@GetMapping("/hascompany")
	public boolean hasCompany() {
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			Long companyId = workerService.getByName(authenticationFilter.userName);
			if(companyId != null) {
				return true;
			}
			return false;
		}
		return true;
	}

	@GetMapping("/rate/{id}/{rate}")
	public void rateCompany(@PathVariable long id, @PathVariable long rate) {
		companyService.rateCompany(id,rate);
	}
	
	private Optional<Company> getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findByUserId(userId);
		return company;
	}
}
