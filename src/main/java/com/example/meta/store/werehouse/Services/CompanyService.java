package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.example.meta.store.werehouse.Dtos.CompanyDto;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Mappers.CompanyMapper;
import com.example.meta.store.werehouse.Repositories.CompanyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

	public ResponseEntity<CompanyDto> insertCompany(String company, MultipartFile file, User user)
			throws JsonMappingException, JsonProcessingException {
		boolean exist = companyRepository.existsByUserId(user.getId());
		if (exist) {
			throw new RecordIsAlreadyExist("You Already have A Company");
		}
		CompanyDto companyDto = new ObjectMapper().readValue(company, CompanyDto.class);
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
		super.insert(company1);
		 providerService.addMeAsProvider(company1);
		 clientService.addMeAsClient(company1);
		Category category =   categoryService.addDefaultCategory(company1);
		subCategoryService.addDefaultSubCategory(company1, category);
		return new ResponseEntity<CompanyDto>(HttpStatus.ACCEPTED);
	}

	//contient un erreur
	public ResponseEntity<CompanyDto> upDateCompany(String companyDto, MultipartFile file,Optional<Company> compan) throws JsonMappingException, JsonProcessingException {
		Company company = compan.get();
		CompanyDto companyDto1 = new ObjectMapper().readValue(companyDto, CompanyDto.class);
		boolean existName = companyRepository.existsByName(companyDto1.getName());
		if(!company.getName().equals(companyDto1.getName())  && existName
				   && !company.getId().equals(companyDto1.getId())) {
		System.out.println(companyDto1.getName().toString()+" com "+company.getName());
			throw new RecordIsAlreadyExist("This Name Is Already Exist Please Choose Another One");
		
		}
		boolean existCode = companyRepository.existsByCode(companyDto1.getCode());
		if(!company.getCode().equals(companyDto1.getCode()) && existCode 
				   && !company.getId().equals(companyDto1.getId())
		      ) {
			throw new RecordIsAlreadyExist("this code is already exist please choose another one");
		}
		boolean existCodeCP = companyRepository.existsByCodecp(companyDto1.getCodecp());
		if(!company.getCodecp().equals(companyDto1.getCodecp()) && existCodeCP
				   && !company.getId().equals(companyDto1.getId())) {

			throw new RecordIsAlreadyExist("this codecp is already exist please choose another one");
		}
		boolean existMatfisc = companyRepository.existsByMatfisc(companyDto1.getMatfisc());
		if(!company.getCodecp().equals(companyDto1.getCodecp()) && existMatfisc
				   && !company.getId().equals(companyDto1.getId())) {

			throw new RecordIsAlreadyExist("this matricule fiscale is already exist please choose another one");
		}
		company = companyMapper.mapToEntity(companyDto1);
		if (file != null) {

			String newFileName = imageService.insertImag(file, company.getUser().getUsername(), "company");
			company.setLogo(newFileName);
		}
		company.setPhone("97 896 547");
		companyRepository.save(company);
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

	public Company findCompanyIdByUserId(Long userId) {
		Optional<Company> company = companyRepository.findByUserId(userId);
		return company.get();
	}

	public Company findByClientId(Long clientId) {
		Long id = clientService.findCompanyIdByCientId(clientId);
		Optional<Company> company = companyRepository.findById(id);
		return company.get();
	}

	

//	public boolean findByBankaccountnumber(String bankaccountnumber,Long id) {
//		Optional<Company> company = companyRepository.findByBankaccountnumber(bankaccountnumber);
//		if(company.isPresent() && ) {
//			throw new RecordIsAlreadyExist("this account number is related with another provider "+ company.get().getName());
//			}
//		return false; 
//	}



}
