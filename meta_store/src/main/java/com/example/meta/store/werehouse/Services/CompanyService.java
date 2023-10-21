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
		role.addAll(user.getRoles());
		user.setRoles(role);
		userService.save(user);
		companyRepository.save(company1);
		logger.warn("just befor add me as provider");
		 providerService.addMeAsProvider(company1);
		 logger.warn("just after add me as provider");
		 clientService.addMeAsClient(company1);
		Category category =   categoryService.addDefaultCategory(company1);
		subCategoryService.addDefaultSubCategory(company1, category);
		return new ResponseEntity<CompanyDto>(HttpStatus.ACCEPTED);
	}

	//contient un erreur
	public ResponseEntity<CompanyDto> upDateCompany(String companyDto, MultipartFile file,Optional<Company> compan)
			throws JsonMappingException, JsonProcessingException {
		
		Optional<Company> cmpany = companyRepository.findById(compan.get().getId());
		Company company = cmpany.get();
		CompanyDto companyDto1 = objectMapper.readValue(companyDto, CompanyDto.class);
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
			throw new RecordIsAlreadyExist("this matricule fiscale is already exist please choose another one");
			}
		}
		if(!company.getBankaccountnumber().equals(companyDto1.getBankaccountnumber())) {
			boolean existBanckAccount = companyRepository.existsByBankaccountnumber(companyDto1.getBankaccountnumber());
			if(existBanckAccount) {
				throw new RecordIsAlreadyExist("this banck account is already in use ");
			}
		}
		// Company updatedCompany = companyMapper.mapToEntity(companyDto1);
		 Company updatedCompany = company;
		if (file != null) {

			String newFileName = imageService.insertImag(file, company.getUser().getUsername(), "company");
			updatedCompany.setLogo(newFileName);
		}
		updatedCompany.setPhone("97 896 547");
		companyRepository.save(updatedCompany);
		return ResponseEntity.ok(companyDto1);
		
	
}
	public Optional<Company> findByUserId(Long userId) {
		Optional<Company> company = companyRepository.findByUserId(userId);
		if (company.isEmpty()) {
			throw new RecordNotFoundException("you do not have a company");
		}
		return company;
	}

	public ResponseEntity<CompanyDto> getCompanyById(Long id) {
		Optional<Company> company = companyRepository.findById(id);
		if (company.isEmpty()) {
			throw new RecordNotFoundException("you do not have a company");
		}
		CompanyDto companyDto = companyMapper.mapToDto(company.get());
		return ResponseEntity.ok(companyDto);
	}

	public CompanyDto getMe(Company company) {
		CompanyDto companyDto = companyMapper.mapToDto(company);
		return companyDto;
	}

	public void rateCompany(long id, long rate) {
		Optional<Company> company = companyRepository.findById(id);
		if(company.isEmpty()) {
			throw new RecordNotFoundException("there is no company with id: "+id);
		}
		
		company.get().setRaters(company.get().getRaters()+1);
		double number = (company.get().getRate()+(double)rate)/company.get().getRaters();
		company.get().setRate(number);
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

	public Optional<Company> findCompanyIdByUserId(Long userId) {
		Optional<Company> company = companyRepository.findByUserId(userId);
		return company;
	}


	


}
