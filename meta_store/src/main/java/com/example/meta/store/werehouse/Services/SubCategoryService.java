package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.SubCategoryDto;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.SubCategory;
import com.example.meta.store.werehouse.Mappers.SubCategoryMapper;
import com.example.meta.store.werehouse.Repositories.SubCategoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SubCategoryService extends BaseService<SubCategory, Long>{


	private final SubCategoryMapper subCategoryMapper;
	
	private final SubCategoryRepository subCategoryRepository;
	
	private final CategoryService categoryService;
	
	private final ImageService imageService;
	
	private final ObjectMapper objectMapper;
	
	public ResponseEntity<SubCategoryDto> upDateSubCategory( String dto, Company company, MultipartFile file) throws JsonMappingException, JsonProcessingException {
		SubCategoryDto subCategoryDto = objectMapper.readValue(dto, SubCategoryDto.class);
		Optional<SubCategory> subCategory = subCategoryRepository.findByIdAndCompanyId(subCategoryDto.getId(),company.getId());
		if(subCategory.isPresent()) {
			SubCategory categ = subCategoryMapper.mapToEntity(subCategoryDto);
			if(subCategoryDto.getCategory().getId() != subCategory.get().getCategory().getId()) {
				ResponseEntity<Category> category = categoryService.getById(subCategoryDto.getCategory().getId());
				if(category == null) {
					throw new RecordNotFoundException("there is no category with libelle: "+subCategoryDto.getCategory().getLibelle());
				}
				categ.setCategory(category.getBody());
			}
			if(file != null) {

				String newFileName = imageService.insertImag(file,company.getUser().getUsername(), "subcategory");
				categ.setImage(newFileName);
			}
			else {				
			categ.setImage(subCategory.get().getImage());
			}
			categ.setCompany(company);
			subCategoryRepository.save(categ);
			return ResponseEntity.ok(subCategoryDto);
			
			
		}else {
			throw new RecordNotFoundException("SubCategory Not Found");
		}
	}

	public Optional<SubCategory> getByLibelle(String libelle, Long companyId) {
		return subCategoryRepository.findByLibelleAndCompanyId(libelle, companyId);
	}

	public List<SubCategory> getAllByCompanyId(Long companyId) {
		return subCategoryRepository.findAllByCompanyId(companyId);
	}

	public ResponseEntity<SubCategory> getByLibelleAndCompanyId(String name, Long companyId) {
		Optional<SubCategory> categ = subCategoryRepository.findByLibelleAndCompanyId(name,companyId);
		if(!categ.isEmpty()) {
		SubCategory subCategory = categ.get();
		return ResponseEntity.ok(subCategory);
		}
		else return null;
	}
	
	public Optional<SubCategory> getByIdAndCompanyId(Long id , Long companyId) {
		return subCategoryRepository.findByIdAndCompanyId(id, companyId);
	}

	public List<SubCategoryDto> getByCompanyIdAndCategoryId(Long id, Long categoryId) {

		List<SubCategory> subCategory = subCategoryRepository.findAllByCompanyIdAndCategoryId(id,categoryId);
		if(subCategory.isEmpty()) {
			throw new RecordNotFoundException("there is no sub category inside this category");
		}
		List<SubCategoryDto> listSubCategoryDto = new ArrayList<>();
		for(SubCategory i: subCategory) {
			SubCategoryDto subCategoryDto = subCategoryMapper.mapToDto(i);
			listSubCategoryDto.add(subCategoryDto);
		}
		return listSubCategoryDto;
		
	}

	public ResponseEntity<List<SubCategoryDto>> getSubCategoryByCompany(Company company) {
		List<SubCategory> subCategorys = getAllByCompanyId(company.getId());
		if(subCategorys.isEmpty()) {
			throw new RecordNotFoundException("there is no subCategory");
		}
		List<SubCategoryDto> subCategorysDto = new ArrayList<>();
		for(SubCategory i : subCategorys) {
			SubCategoryDto subCategoryDto = subCategoryMapper.mapToDto(i);
			subCategorysDto.add(subCategoryDto);
		}
		return ResponseEntity.ok(subCategorysDto);
		}

	public ResponseEntity<SubCategoryDto> getSubCategoryById(String name, Company company) {
		ResponseEntity<SubCategory> subCategory = getByLibelleAndCompanyId(name,company.getId());
		if(subCategory == null) {
			 throw new RecordNotFoundException("There Is No SubCategory With Libelle : "+name);
		}
		SubCategoryDto dto = subCategoryMapper.mapToDto(subCategory.getBody());
		return ResponseEntity.ok(dto);
		
	}

	public List<SubCategoryDto> getAllSubCategoryByCompanyIdAndCategoryId(Long categoryId, Company company) {
		List<SubCategoryDto> subCategoryDto = getByCompanyIdAndCategoryId(company.getId(),categoryId);
		if(subCategoryDto == null) {
			throw new RecordNotFoundException("there is no sub category inside this category");
		}
		return subCategoryDto;
	}

	public ResponseEntity<SubCategoryDto> insertSubCategory(String subCatDto, Company company, MultipartFile file) throws JsonMappingException, JsonProcessingException{
		SubCategoryDto subCategoryDto = objectMapper.readValue(subCatDto, SubCategoryDto.class);
		ResponseEntity<SubCategory> subCategory1 = getByLibelleAndCompanyId(subCategoryDto.getLibelle(),company.getId());
	if(subCategory1 != null)  {
		throw new RecordIsAlreadyExist("is already exist");
	}
	
	SubCategory subCategory = subCategoryMapper.mapToEntity(subCategoryDto);
	if(file != null) {

		String newFileName = imageService.insertImag(file,company.getUser().getUsername(), "subcategory");
		subCategory.setImage(newFileName);
	}
	subCategory.setCompany(company);
	super.insert(subCategory);
	return new ResponseEntity<SubCategoryDto>(HttpStatus.ACCEPTED);
	
	}

	public void deleteSubCategoryById(Long id, Company company) {
		Optional<SubCategory> subCategory = getByIdAndCompanyId(id,company.getId());
		if(subCategory.isEmpty()) {
			throw new RecordNotFoundException("This SubCategory with id: "+id+" Does Not Exist");
		}
	 super.deleteById(id);
	
	}

	public void addDefaultSubCategory(Company company1, Category category) {
		SubCategory subCategory = new SubCategory();
		subCategory.setCategory(category);
		subCategory.setCode("subCode");
		subCategory.setCompany(company1);
		subCategory.setLibelle("sub category");
		subCategoryRepository.save(subCategory);
		
	}

	public SubCategory getDefaultSubCategory(Company company) {
		Optional<SubCategory> subCategory = subCategoryRepository.findByLibelleAndCompanyId("sub category", company.getId());
		return subCategory.get();
	}
}
