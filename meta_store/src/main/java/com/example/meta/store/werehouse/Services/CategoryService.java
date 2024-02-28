package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.ArticleController;
import com.example.meta.store.werehouse.Dtos.CategoryDto;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Mappers.CategoryMapper;
import com.example.meta.store.werehouse.Repositories.CategoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService extends BaseService<Category, Long>{


	private final CategoryMapper categoryMapper;
	
	private final CategoryRepository categoryRepository;
	
	private final ImageService imageService;
	
	private final ObjectMapper objectMapper;

	private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
	
	public ResponseEntity<CategoryDto> upDateCategory( String catDto, Company company, MultipartFile file) throws JsonMappingException, JsonProcessingException {
		CategoryDto categoryDto = objectMapper.readValue(catDto, CategoryDto.class);
		Category category = categoryRepository.findByIdAndCompanyId(categoryDto.getId(),company.getId())
				.orElseThrow(() -> new RecordNotFoundException("Category Not Found"));
			Category categ = categoryMapper.mapToEntity(categoryDto);
			if(file != null) {

				String newFileName = imageService.insertImag(file,company.getUser().getUsername(), "category");
				categ.setImage(newFileName);
			}else {

				categ.setImage(category.getImage());
			}
			categ.setCompany(company);
			categoryRepository.save(categ);
			return ResponseEntity.ok(categoryDto);
			
	}

	public Optional<Category> getByLibelle(String libelle, Long companyId) {
		return categoryRepository.findByLibelleAndCompanyId(libelle, companyId);
	}

	public List<Category> getAllByCompanyId(Long companyId) {
		return categoryRepository.findAllByCompanyId(companyId);
	}


	
	public Optional<Category> getByIdAndCompanyId(Long id , Long companyId) {
		return categoryRepository.findByIdAndCompanyId(id, companyId);
	}
	

	

	public List<CategoryDto> getCategoryByCompany(Company company, Long id) {
		logger.warn(id+" <== id ");
		List<Category> categorys;
		if(id == 0) {			
			categorys = getAllByCompanyId(company.getId());
		}else {
			categorys = getAllByCompanyId(id);
		}
		if(categorys.isEmpty()) {
			throw new RecordNotFoundException("there is no category");
		}
		List<CategoryDto> categorysDto = new ArrayList<>();
		for(Category i : categorys) {
			CategoryDto categoryDto = categoryMapper.mapToDto(i);
			categorysDto.add(categoryDto);
		}
		return categorysDto;
	}

	public CategoryDto getByLibelleAndCompanyId(Company company, String name) {
		Optional<Category> category = categoryRepository.findByLibelleAndCompanyId(name,company.getId());
		if(category.isEmpty()) {
			throw new RecordNotFoundException("There Is No Category With Libelle : "+name);
		}
		CategoryDto dto = categoryMapper.mapToDto(category.get());
		return dto;
	}

	public ResponseEntity<CategoryDto> insertCategory( String catDto, Company company, MultipartFile file)
			throws JsonMappingException, JsonProcessingException {

		System.out.println("insert service"+catDto);
		CategoryDto categoryDto = objectMapper.readValue(catDto, CategoryDto.class);
		Optional<Category> category1 = categoryRepository.findByLibelleAndCompanyId(categoryDto.getLibelle(),company.getId());
		if(category1.isPresent())  {
			throw new RecordIsAlreadyExist("Category "+categoryDto.getLibelle()+"is already exist");
		}
		Category category = categoryMapper.mapToEntity(categoryDto);
		if(file !=null) {

			String newFileName = imageService.insertImag(file,company.getUser().getUsername(), "category");
			category.setImage(newFileName);
		}
		category.setCompany(company);
		super.insert(category);
		return new ResponseEntity<CategoryDto>(HttpStatus.ACCEPTED);
	}

	public void deleteCategoryById(Long id, Company company) {
		Optional<Category> category = getByIdAndCompanyId(id,company.getId());
		if(category.isEmpty()) {
			throw new RecordNotFoundException("This Category Does Not Exist");
		}
		super.deleteById(id);		
	}

	public Category addDefaultCategory(Company company1) {
		Category category = new Category();
		category.setCode("cat");
		category.setCompany(company1);
		category.setLibelle("category");
		categoryRepository.save(category);
		return category;
		
	}

	public Category getDefaultCategory(Company company) {
		Optional<Category> category = categoryRepository.findByLibelleAndCompanyId("category", company.getId());
		return category.get();
	}
}
