package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	private final Logger logger = LoggerFactory.getLogger(CompanyController.class);
	
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
		Company company = getCompany();
		return companyService.upDateCompany(companyDto, file,company);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id){
		return companyService.getCompanyById(id);		
	}
	
	@GetMapping("/mycompany/{id}")
	public CompanyDto getMe(@PathVariable Long id) {
		logger.warn("begin of get me function ");
		Company company = getCompany();
		logger.warn("just afyer get company in get me function ");
		if(!company.getId().equals(id)) {
			   boolean exists = company.getBranches().stream()
                       .anyMatch(branch -> branch.getId().equals(id));
			   if(!exists) {
				   throw new RecordNotFoundException("you don't have a company");
			   }
		}
		return companyService.getMe(company,id);
	}
	
	@GetMapping("/hascompany")
	public boolean hasCompany() {
		Company company = getCompany();
		if(company.getId() == null) {
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
	
	@GetMapping("get_my_company_id")
	public Long getMyCompanyId() {
		return getCompany().getId();
	}
	
	@GetMapping("search/{branshe}")
	public List<CompanyDto> searchCompanyContaining(@PathVariable String branshe){
		return companyService.getCompanyContaining(branshe);
	}
	
	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Company company = new Company();
			company = companyService.findByUserId(userId);
			logger.warn("befor if condition "+ company);
				if(company== null) {
					logger.warn("inside if condition ");
					Long companyId = workerService.findCompanyIdByUserId(userId);
					logger.warn("just after long companyId "+ companyId);
					if(companyId != null) {
						company = companyService.getById(companyId).getBody();						
					}
					
				}
		return company;
	}
}
