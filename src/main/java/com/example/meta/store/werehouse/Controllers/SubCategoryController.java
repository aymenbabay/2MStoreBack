package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.example.meta.store.werehouse.Dtos.SubCategoryDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.SubCategory;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.SubCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/subcategory")
@RequiredArgsConstructor
public class SubCategoryController {

	
	private final SubCategoryService subCategoryService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	@GetMapping("/getbycompany")
	public ResponseEntity<List<SubCategoryDto>> getSubCategoryByCompany(){
		Company company = getCompany();
		return subCategoryService.getSubCategoryByCompany(company);
	}
	
	@GetMapping("/l/{name}")
	public ResponseEntity<SubCategoryDto> getSubCategoryById(@PathVariable String name){
		Company company = getCompany();
		return subCategoryService.getSubCategoryById(name,company);
		
	}
	
	@GetMapping("/{categoryId}")
	public List<SubCategoryDto> getAllSubCategoriesByCompanyIdAndCategoryId(@PathVariable Long categoryId){
		Company company = getCompany();
		return subCategoryService.getAllSubCategoryByCompanyIdAndCategoryId(categoryId, company);
	}
	
	@PostMapping("/add")
	public ResponseEntity<SubCategoryDto> insertSubCategory(@RequestParam("sousCategory") String sousCategoryDto,
			@RequestParam(value = "file",required=false) MultipartFile file) throws JsonMappingException, JsonProcessingException{
		Company company = getCompany();
		return subCategoryService.insertSubCategory(sousCategoryDto,company,file);
	}
	
	@PutMapping("/update")
	public ResponseEntity<SubCategoryDto> upDateSubCategory(
			@RequestParam("sousCategory") String sousCategoryDto,
			@RequestParam(value="file",required=false) MultipartFile file) throws JsonMappingException, JsonProcessingException{
		Company company = getCompany();
		return subCategoryService.upDateSubCategory(sousCategoryDto,company,file);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteSubCategoryById(@PathVariable Long id){
		Company  company = getCompany();
		 subCategoryService.deleteSubCategoryById(id,company);
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
