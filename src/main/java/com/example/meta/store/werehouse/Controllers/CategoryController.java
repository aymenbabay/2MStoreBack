package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.CategoryDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.CategoryService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/category")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService UserService;
	
	private final CompanyService companyService;
	

	@GetMapping("/getbycompany")
	public List<CategoryDto> getCategoryByCompany(){
		Company company = getCompany();
		return categoryService.getCategoryByCompany(company);
	}
	
	@GetMapping("/l/{name}")
	public CategoryDto getCategoryById(@PathVariable String name){
		Company company = getCompany();
	return categoryService.getByLibelleAndCompanyId(company, name)	;

	}
	
	@PostMapping("/add")//file
	public ResponseEntity<CategoryDto> insertCategory(
			@RequestParam("categoryDto") String categoryDto, 
			@RequestParam(value="file",required=false) MultipartFile file) throws JsonMappingException, JsonProcessingException
	{
		Company company = getCompany();
		return categoryService.insertCategory(categoryDto,company,file);
	}
	
	@PutMapping("/update")
	public ResponseEntity<CategoryDto> upDateCategory(
			@RequestParam String categoryDto,
			@RequestParam(value="file", required=false) MultipartFile file) throws JsonMappingException, JsonProcessingException{
		Company company = getCompany();
		return categoryService.upDateCategory(categoryDto,company,file);
	}
	
	
	@DeleteMapping("/delete/{id}")
	public void deleteCategoryById(@PathVariable Long id){
		Company company = getCompany();
		categoryService.deleteCategoryById(id,company);
	}
	
	private Company getCompany() {
		Long userId = UserService.findByUserName(authenticationFilter.userName).getId();
		Company company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company;
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}
}
