package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.ArticleDto;
import com.example.meta.store.werehouse.Dtos.ArticleUpdateDto;
import com.example.meta.store.werehouse.Dtos.CategoryDto;
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Dtos.CompanyArticleDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.CompanyArticle;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.SubCategory;
import com.example.meta.store.werehouse.Mappers.ArticleMapper;
import com.example.meta.store.werehouse.Mappers.CompanyArticleMapper;
import com.example.meta.store.werehouse.Repositories.ArticleRepository;
import com.example.meta.store.werehouse.Repositories.CompanyArticleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService extends BaseService<Article, Long>{

	private final ArticleRepository articleRepository;
	
	private final CompanyArticleRepository companyArticleRepository;
	
	private final ArticleMapper articleMapper; 
	
	private final CompanyArticleMapper companyArticleMapper;
	
	private final InventoryService inventoryService;

	private final ImageService imageService;
	
	private final ProviderService providerService;
	
	private final ClientService clientService;
	
	private final CategoryService categoryService;
	
	private final SubCategoryService subCategoryService;
	
	private final CompanyService companyService;
	
	
	public ResponseEntity<ArticleDto> insertArticle( MultipartFile file, String article, Provider provider)
			throws JsonMappingException, JsonProcessingException {
		ArticleDto articleDto = new ObjectMapper().readValue(article, ArticleDto.class);
		Article article1 = articleMapper.mapToEntity(articleDto);
		if(file != null) {
			String newFileName = imageService.insertImag(file,provider.getCompany().getUser().getUsername(), "article");
			article1.setImage(newFileName);
		}
		if(article1.getProvider() == null) {
			article1.setProvider(provider);
		}
		CompanyArticle companyArticle = new CompanyArticle();
		if(articleDto.getCategory()==null) {
			Category category = categoryService.getDefaultCategory(provider.getCompany());
			article1.setCategory(category);
			companyArticle.setCategory(category);			
		}
		if(articleDto.getSubCategory()==null) {
			SubCategory subCategory = subCategoryService.getDefaultSubCategory(provider.getCompany());
			article1.setSubCategory(subCategory);
			companyArticle.setSubCategory(subCategory);
		}
		super.insert(article1);
		companyArticle.setArticle(article1);
		companyArticle.setCompany(provider.getCompany());
		companyArticle.setQuantity(article1.getQuantity());
		companyArticle.setMargin(article1.getMargin());
		companyArticleRepository.save(companyArticle);
		inventoryService.makeInventory(companyArticle, provider.getCompany());
		return ResponseEntity.ok(articleDto);
	}
	
	public List<CompanyArticleDto> getAllProvidersArticleByProviderId(Provider provider) {
		List<CompanyArticleDto> articlesDto = new ArrayList<CompanyArticleDto>();
		List<CompanyArticle> articles = companyArticleRepository.findByCompanyId(provider.getCompany().getId());
		for(CompanyArticle i : articles) {
			CompanyArticleDto articleDto = companyArticleMapper.mapToDto(i);
			articlesDto.add(articleDto);
		}
		return articlesDto;
	}

	public ResponseEntity<ArticleDto> upDateArticle( MultipartFile file, String article, Provider provider) 
			throws JsonMappingException, JsonProcessingException {
		ArticleUpdateDto articleDto = new ObjectMapper().readValue(article, ArticleUpdateDto.class);
		System.out.println(articleDto.getId()+"azzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"+provider.getCompany().getId());
		Article updatedArticle = articleMapper.mapToArticle(articleDto);
		Optional<Article> article1;
			 article1 = articleRepository.findById(articleDto.getId());	
			 if(!article1.get().getProvider().equals(provider)) {
				 return null;
			 }
		
		if(article1.isEmpty()) {
			throw new RecordNotFoundException("there is no article with id: "+articleDto.getId());
		}
		if(file == null) {
			updatedArticle.setImage(article1.get().getImage());
		}else {
			String newFileName = imageService.insertImag(file,provider.getCompany().getUser().getUsername(), "article");
			updatedArticle.setImage(newFileName);
		}
		if(updatedArticle.getProvider() == null) {
			updatedArticle.setProvider(provider);
		}
		article1 = Optional.of(articleRepository.save(updatedArticle));
		Optional<CompanyArticle> companyArticle = companyArticleRepository.findByArticleIdAndCompanyId(articleDto.getId(),provider.getCompany().getId());
		System.out.println(companyArticle.get().getMargin()+"azzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
		CompanyArticle updatedCompanyArticle = companyArticleMapper.mapToCompanyArticle(articleDto);
		updatedCompanyArticle.setArticle(article1.get());
		updatedCompanyArticle.setCompany(provider.getCompany());
		companyArticle = Optional.of(companyArticleRepository.save(updatedCompanyArticle));
		return null;
		}

	public void upDateCompanyArticle(CompanyArticleDto companyArticleDto) {
		Optional<CompanyArticle> companyArticleOptional = companyArticleRepository.findById(companyArticleDto.getId());
		if(companyArticleOptional == null) {
			throw new RecordNotFoundException("there is no article with id : "+companyArticleDto.getId());
		}
		CompanyArticle companyArticle = companyArticleMapper.mapToEntity(companyArticleDto);
		companyArticle.setArticle(companyArticleOptional.get().getArticle());
		companyArticle.setCompany(companyArticleOptional.get().getCompany());
		companyArticleOptional = Optional.of(companyArticleRepository.save(companyArticle));
	}
	
//	-------------------------------------------------properly work------------------------------------------------

	

	// i use it in command Line service
//	@Cacheable(value = "article", key = "#root.methodName + '_' + #company.id + '_' + #code_article")
//	public Article findByCodeAndCompanyId(String code_article, Company company) {
//		Optional<CompanyArticle> article = companyArticleRepository.findByCodeAndCompanyId(code_article,company.getId());
//		if(article.isEmpty()) {
//			throw new RecordNotFoundException("Article with code: "+code_article+" Not Found");
//		}
//		return article.get();
//	}

	


	public void insertExistArticle( Company company ) {
		
	}
	
	
	public void addQuantity(Long id, Long quantity, Provider provider) {
		Optional<Article> article = articleRepository.findByIdAndProviderId(id,provider.getId());
		if(article.isEmpty()) {
			throw new RecordNotFoundException("there is no article with id: "+id);
		}
		article.get().setQuantity(article.get().getQuantity()+quantity);
		articleRepository.save(article.get());
		inventoryService.addQuantity(article.get(), provider.getCompany().getId(),quantity);

	}

	public ResponseEntity<String> deleteByIdAndCompanyId(Long id, Long companyId) {
		Long providerId = providerService.getMeProviderId(companyId);
		Optional<Article> article = articleRepository.findByIdAndProviderId(id, providerId);
			if(article.isEmpty()) {
				throw new RecordNotFoundException("This Article Does Not Exist");
			}
			//	articleRepository.deleteByIdAndCompanyId(id,companyId);
				inventoryService.deleteByArticleCode(article.get().getCode(), companyId);
			return ResponseEntity.ok("successfuly deleted");
	}

	public List<ArticleDto> getByNameContaining(String articlenamecontaining, Long providerId) {
		List<Article> article = new ArrayList<>();
		article = articleRepository.findAllByLibelleAndProviderIdContaining(articlenamecontaining,providerId);
		if(!article.isEmpty()) {
	List<ArticleDto> articleDto = new ArrayList<>();
	for(Article i : article) {
			ArticleDto artDto =  articleMapper.mapToDto(i);
			articleDto.add(artDto);
	}
	return articleDto;}
		throw new RecordNotFoundException("there is no record cointaining "+articlenamecontaining);

	}

	//@Cacheable(value = "article", key = "#root.methodName + '_' + #company.id")
	public List<ArticleDto> getAllArticleByProviderId(Provider provider) {
		List<Article> articles = articleRepository.findAllByProviderId(provider.getId());
		if(articles.isEmpty()) {
			throw new RecordNotFoundException("there is no article");
		}
		List<ArticleDto> articlesDto = new ArrayList<>();
		for(Article i : articles) {
			ArticleDto articleDto = articleMapper.mapToDto(i);
			articlesDto.add(articleDto);
		}
		return articlesDto;
	}

	//@Cacheable(value = "article", key = "#root.methodName + '_' + #company.id")
	public ResponseEntity<ArticleDto> getArticleById(Long id) {
		Optional<Article> article = articleRepository.findById(id);
		if(article.isEmpty()) {
			 throw new RecordNotFoundException("There Is No Article With id : "+id);
		}
		ArticleDto dto = articleMapper.mapToDto(article.get());
		return ResponseEntity.ok(dto);
	}


	public List<ArticleDto> getdgdgeg() {
		List<Article> article = articleRepository.findRandomArticles();
		if(article== null) {
			throw new RecordNotFoundException("No Article");
		}
			List<ArticleDto> articlesDto = new ArrayList<>();
			for(Article i:article) {
			ArticleDto dto = articleMapper.mapToDto(i);
			articlesDto.add(dto);
	}
			return articlesDto;
	}

	public Article findByCompanyArticleId(Long articleId) {
		Optional<CompanyArticle>  article = companyArticleRepository.findById(articleId);
		return article.get().getArticle();
	}

	public void impactInvoice(List<CommandLineDto> commandLinesDto, Long clientId, List<Article> articles) {
		Company company = companyService.findByClientId(clientId);
		System.out.println("befor getDefaultCategory  in article servcie");
		Category category = categoryService.getDefaultCategory(company);
		System.out.println("after getDefaultCategory  in article service");
		SubCategory subCategory = subCategoryService.getDefaultSubCategory(company);
		System.out.println("after getDefaultSubCategory in article service");
		CompanyArticle companyArticle = new CompanyArticle();
		for(int i =0; i < commandLinesDto.size();i++) {
			companyArticle.setCompany(company);
			companyArticle.setQuantity(commandLinesDto.get(i).getQuantity());
			companyArticle.setMargin(company.getMargin());
			companyArticle.setArticle(articles.get(i));
			companyArticle.setCategory(category);
			companyArticle.setSubCategory(subCategory);
		}
		companyArticleRepository.save(companyArticle);
	}






//	@Cacheable(value = "article", key = "#root.methodName + '_' + #company.id")
//	public List<ArticleDto> getAllMyVirtual(Company company) {
//		List<Article> articles = articleRepository.findAllMyVirtual(company.getId());
//		List<ArticleDto> articlesDto = new ArrayList<>();
//		for(Article i : articles) {
//			ArticleDto articleDto = articleMapper.mapToDto(i);
//			articlesDto.add(articleDto);
//		}
//		return articlesDto;
//	}
}
