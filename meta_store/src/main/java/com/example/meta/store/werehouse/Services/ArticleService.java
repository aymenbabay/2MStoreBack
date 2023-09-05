package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
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
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.CompanyArticle;
import com.example.meta.store.werehouse.Entities.Inventory;
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
	
	private final CategoryService categoryService;
	
	private final SubCategoryService subCategoryService;
	
	private final CompanyService companyService;
	
	private final ObjectMapper objectMapper;
	
    DecimalFormat df = new DecimalFormat("#.###");
	
    public ResponseEntity<ArticleDto> insertArticle( MultipartFile file, String article, Provider provider)
			throws JsonMappingException, JsonProcessingException {
		ArticleDto articleDto = objectMapper.readValue(article, ArticleDto.class);
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
		companyArticle.setCost(article1.getCost());
		companyArticle.setCompany(provider.getCompany());
		companyArticle.setQuantity(article1.getQuantity());
		companyArticle.setMinQuantity(article1.getMinQuantity());
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
		ArticleUpdateDto articleDto = objectMapper.readValue(article, ArticleUpdateDto.class);
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
	
	
	public void addQuantity(Long id, Long quantity, Provider provider) {
		Optional<CompanyArticle> article = companyArticleRepository.findById(id);
		if(article.isEmpty()) {
			throw new RecordNotFoundException("there is no article with id: "+id);
		}
		article.get().setQuantity(article.get().getQuantity()+quantity);
		companyArticleRepository.save(article.get());
		inventoryService.addQuantity(article.get(),quantity, provider.getCompany());

	}
	
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
	
	public List<ArticleDto> getdgdgeg() {
		List<Article> article = articleRepository.findRandomArticles(51.122,51.125);
		if(article== null) {
			throw new RecordNotFoundException("No Article");
		}
			List<ArticleDto> articlesDto = new ArrayList<>();
			for(Article i:article) {
				System.out.println(" article setrvice get dgdgdgd code of article"+ i.getCode());
			ArticleDto dto = articleMapper.mapToDto(i);
			articlesDto.add(dto);
	}
			return articlesDto;
	}
	
	public ResponseEntity<String> deleteByCompanyArticleId(Long companyArticleId, Long companyId) {
		Optional<CompanyArticle> companyArticle = companyArticleRepository.findById(companyArticleId);
			if(companyArticle.isEmpty()) {
				throw new RecordNotFoundException("This Article Does Not Exist");
			}
			CompanyArticle companyarticle = companyArticle.get();			
			if(companyarticle.getCompany().getId() == companyId) {
				Inventory inventory = inventoryService.findByCompanyArticleIdAndCompanyId(companyArticleId, companyId);
				companyarticle.setArticle(null);
				if(inventory.getOut_quantity() == 0) {
				inventory.setCompanyArticle(null);	
				inventoryService.deleteById(inventory.getId());
				}
				companyArticleRepository.deleteById(companyArticleId);
			}
			return ResponseEntity.ok("successfuly deleted");
	}

	public Article findByCompanyArticleId(Long articleId) {
		Optional<CompanyArticle>  article = companyArticleRepository.findById(articleId);
		return article.get().getArticle();
	}
	public CompanyArticle findCompanyArticleById(Long companyArticleId) {
		Optional<CompanyArticle> companyArticle = companyArticleRepository.findById(companyArticleId);
		return companyArticle.get();
	}

	//by the article to client , List<CompanyArticle> companyArticles
	public void impactInvoice(List<CommandLine> commandLines, Long clientId, List<Article> articles) {
		Company company = companyService.findByClientId(clientId);
		Category category = categoryService.getDefaultCategory(company);
		SubCategory subCategory = subCategoryService.getDefaultSubCategory(company);
		System.out.println("before for loop in article service inpact invoice");
		CompanyArticle article;
		for(int i =0; i < commandLines.size();i++) {
			System.out.println("in for loop before optional of companyarticle in article service inpact invoice");
			Optional<CompanyArticle> companyarticle = companyArticleRepository.findByArticleIdAndCompanyId(articles.get(i).getId(),company.getId());
			System.out.println("in for loop after optional of companyarticle in article service inpact invoice");
			String articleCost = df.format(articles.get(i).getCost() + (articles.get(i).getCost()*articles.get(i).getTva()+ articles.get(i).getCost()*articles.get(i).getMargin())/100);
			if(companyarticle.isPresent()) {
				System.out.println("in if loop in article service inpact invoice");
				 article = companyarticle.get();
				article.setQuantity(commandLines.get(i).getQuantity()+article.getQuantity());
				//if the provider has augmented the article price
				article.setCost(Double.parseDouble(articleCost));	
			}else {				
				System.out.println("in the else loop in article service inpact invoice");
				 article = new CompanyArticle();
				 article.setCompany(company);
				 article.setQuantity(commandLines.get(i).getQuantity());
				 article.setMargin(company.getMargin());
				 article.setArticle(articles.get(i));
			article.setCategory(category);
			article.setSubCategory(subCategory);
			article.setCost(Double.parseDouble(articleCost));
			companyArticleRepository.save(article);
			}
			inventoryService.impactInvoiceOnClient(company,article,Double.parseDouble(articleCost));
			Optional<CompanyArticle> companyArt = companyArticleRepository.findById(commandLines.get(i).getCompanyArticle().getId());
			CompanyArticle art = companyArt.get();
			art.setQuantity(art.getQuantity()-commandLines.get(i).getQuantity());
			
		}
		
		System.out.println("get out of for loop in article service inpact invoice");
	}

//	-------------------------------------------------Exception work------------------------------------------------

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

	
}
