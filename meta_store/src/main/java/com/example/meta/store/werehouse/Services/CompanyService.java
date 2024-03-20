package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Entity.Role;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.RoleService;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.ArticleController;
import com.example.meta.store.werehouse.Controllers.CompanyController;
import com.example.meta.store.werehouse.Dtos.CompanyDto;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Mappers.CompanyMapper;
import com.example.meta.store.werehouse.Repositories.CompanyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService extends BaseService<Company, Long> {

	private final CompanyRepository companyRepository;

	private final CompanyMapper companyMapper;

	private final RoleService roleService;

	private final UserService userService;

	private final ProviderService providerService;

	private final ClientService clientService;

	private final ImageService imageService;
	
	private final CategoryService categoryService;
	
	private final SubCategoryService subCategoryService;
	
	private final ObjectMapper objectMapper;


	private final Logger logger = LoggerFactory.getLogger(CompanyController.class);
	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	
	
	public ResponseEntity<CompanyDto> insertCompany(String company, MultipartFile file, User user)
			throws JsonMappingException, JsonProcessingException {
		boolean exist = companyRepository.existsByUserId(user.getId());
		if (exist) {
			throw new RecordIsAlreadyExist("You Already have A Company");
		}
		CompanyDto companyDto = objectMapper.readValue(company, CompanyDto.class);
		boolean existName = companyRepository.existsByName(companyDto.getName());
		if (existName) {
			throw new RecordIsAlreadyExist("This Name Is Already Exist Please Choose Another One");
		}
		Company company1 = companyMapper.mapToEntity(companyDto);
		if (file != null) {
			
			String newFileName = imageService.insertImag(file, user.getUsername(), "company");
			company1.setLogo(newFileName);
		}
		company1.setUser(user);
		company1.setRaters(0);
		company1.setRate((long) 0);
		Set<Role> role = new HashSet<>();
		ResponseEntity<Role> role2 = roleService.getById((long) 1);
		role.add(role2.getBody());
		//	role.addAll(user.getRoles());
		user.setRoles(role);
		userService.save(user);
		companyRepository.save(company1);
		providerService.addMeAsProvider(company1);
		clientService.addMeAsClient(company1);
		Category category =   categoryService.addDefaultCategory(company1);
		subCategoryService.addDefaultSubCategory(company1, category);
		return new ResponseEntity<CompanyDto>(HttpStatus.ACCEPTED);
	}
	
	//must give a boolean response if the user want to syncrounize clientCompany and providerCompany informations
	public ResponseEntity<CompanyDto> upDateCompany(String companyDto, MultipartFile file)
			throws JsonMappingException, JsonProcessingException {
		CompanyDto companyDto1 = objectMapper.readValue(companyDto, CompanyDto.class);
		Company company = companyRepository.findById(companyDto1.getId()).orElseThrow(() -> new RecordNotFoundException("you don't have a company"));
		if(!company.getName().equals(companyDto1.getName()))
		{
			boolean existName = companyRepository.existsByName(companyDto1.getName());
			if(existName) {				
				throw new RecordIsAlreadyExist("This Name Is Already Exist Please Choose Another One");
			}
		}
		
		if(!company.getCode().equals(companyDto1.getCode()) ) {
			boolean existCode = companyRepository.existsByCode(companyDto1.getCode());
			if(existCode) {				
				throw new RecordIsAlreadyExist("this code is already exist please choose another one");
			}
		}
		
		if(!company.getMatfisc().equals(companyDto1.getMatfisc())) {
			boolean existMatfisc = companyRepository.existsByMatfisc(companyDto1.getMatfisc());
			if(existMatfisc) {				
				throw new RecordIsAlreadyExist("this matricule fiscale is already related by another company");
			}
		}
		if(!company.getBankaccountnumber().equals(companyDto1.getBankaccountnumber())) {
			boolean existBanckAccount = companyRepository.existsByBankaccountnumber(companyDto1.getBankaccountnumber());
			if(existBanckAccount) {
				throw new RecordIsAlreadyExist("this banck account is already related by another company ");
			}
		}
		Company updatedCompany = companyMapper.mapToEntity(companyDto1);
		updatedCompany.setParentCompany(company.getParentCompany());
		updatedCompany.setUser(company.getUser());
		if (file != null) {
			String newFileName = imageService.insertImag(file, company.getUser().getUsername(), "company");
			updatedCompany.setLogo(newFileName);
		}
		else {			
			updatedCompany.setLogo(company.getLogo());
		}
		company = updatedCompany;
		companyRepository.save(company);
		
		return ResponseEntity.ok(companyDto1);
		
		
	}
	
	public Company findByUserId(Long userId) {
		Optional<Company> company = companyRepository.findByUserId(userId);
		if (company.isEmpty()) {
			return null;
		}
		return company.get();
	}
	
	public Optional<Company> findCompanyIdByUserId(Long userId) {
		Optional<Company> company = companyRepository.findByUserId(userId);
		return company;
	}
	
	public List<CompanyDto> getCompanyContaining(String branshe, Long id) {
		List<Company> companies = companyRepository.findByNameContaining(branshe,id);
		if(companies.isEmpty()) {
			throw new RecordNotFoundException("there is no company with name containing: "+branshe);
		}
		List<CompanyDto> companiesDto = new ArrayList<>();
		for(Company i : companies) {
			CompanyDto companyDto = companyMapper.mapToDto(i);
			companiesDto.add(companyDto);
		}
		return companiesDto;
	}
	
	public ResponseEntity<CompanyDto> getCompanyById(Long id) {
		Optional<Company> company = companyRepository.findById(id);
		if (company.isEmpty()) {
			throw new RecordNotFoundException("you do not have a company");
		}
		CompanyDto companyDto = companyMapper.mapToDto(company.get());
		return ResponseEntity.ok(companyDto);
	}
	
	public CompanyDto getMe(Company company, Long id) {
		Company companyReturnd = company;
		if(!company.getId().equals(id)) {
			Company branshe = companyRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("there is no company with id: "+id));
			companyReturnd = branshe;
		}
		CompanyDto companyDto = companyMapper.mapToDto(companyReturnd);
		return companyDto;
	}
	
	public void rateCompany(long id, double rate) {
		Company company = companyRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("there is no company with id: "+id));		
		Double rates = round((company.getRate()*company.getRaters()+rate)/(company.getRaters()+1));
		company.setRate(rates);
		company.setRaters(company.getRaters()+1);
	}
	
	public List<CompanyDto> getAllCompany() {
		List<Company> companies = super.getAll();
		if(!companies.isEmpty() && companies != null && !companies.equals(null)) {
			List<CompanyDto> companysDto = new ArrayList<>();
			for(Company i :companies) {
				CompanyDto companyDto = companyMapper.mapToDto(i);
				companysDto.add(companyDto);
			}
			return companysDto;
		}
		throw new RecordNotFoundException("There Is No Company");
	}

	public void acceptedInvetation(Company companySender, Company companyReciver) {
		Set<Company> companies = companyReciver.getBranches();
		companies.add(companySender);
		companyReciver.setBranches(companies);
		companySender.setParentCompany(companyReciver);
		
	}

	public List<CompanyDto> getBranches(Company company) {
		List<CompanyDto> companiesDto = new ArrayList<>();
		if(company.getBranches().size() >0 ) {
			for(Company i : company.getBranches()) {
				CompanyDto companyDto = companyMapper.mapToDto(i);
				companiesDto.add(companyDto);
			}
		}
		return companiesDto;
	}

	public CompanyDto getMyParent(Company company) {
		CompanyDto companyDto = companyMapper.mapToDto(company.getParentCompany());
		return companyDto;
	}

	private double round(double value) {
	    return Math.round(value * 10.0) / 10.0;
	}

	


}
