package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.ArticleDto;
import com.example.meta.store.werehouse.Dtos.SubArticleDto;
import com.example.meta.store.werehouse.Dtos.SubArticleRelationDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Inventory;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;
import com.example.meta.store.werehouse.Entities.SubArticle;
import com.example.meta.store.werehouse.Entities.SubCategory;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Mappers.ArticleMapper;
import com.example.meta.store.werehouse.Repositories.ArticleRepository;
import com.example.meta.store.werehouse.Repositories.ProviderCompanyRepository;
import com.example.meta.store.werehouse.Repositories.SubArticleRepository;
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

	private final SubArticleRepository subArticleRepository;
		
	private final ProviderCompanyRepository providerCompanyRepository;
	
	private final ArticleMapper articleMapper; 
		
	private final InventoryService inventoryService;

	private final ImageService imageService;
	
	private final CategoryService categoryService;
	
	private final SubCategoryService subCategoryService;
	
	private final ProviderService providerService;
	
	private final ObjectMapper objectMapper;
	

	private final Logger logger = LoggerFactory.getLogger(ArticleService.class);
	

	/////////////////////////////////////// real work ////////////////////////////////////////////////////////
	public List<ArticleDto> findRandomArticlesPub(Client client, Provider provider, User user) {
		List<Article> article = new ArrayList<>();
		if(client == null) {
		 article = articleRepository.findRandomArticles(user.getLongitude(), user.getLatitude());
		}
		else {
			article = articleRepository.findRandomArticlesPro(provider.getId(),client.getId(), client.getCompany().getUser().getLongitude(), client.getCompany().getUser().getLatitude());
		}
		
		if(article.isEmpty()) {
			throw new RecordNotFoundException("No Article");
		}
			List<ArticleDto> articlesDto = new ArrayList<>();
			for(Article i:article) {
			ArticleDto dto = articleMapper.mapToDto(i);
			articlesDto.add(dto);
	}
			return articlesDto;
	}
	
	public List<ArticleDto> getAllArticleByCompanyId(Long companyId,Client client, Long providerId, int offset, int pageSize) {
		Long myCompanyId = client.getCompany().getId();
		Pageable pageable = PageRequest.of(offset, pageSize);
		Page<Article> articles;
		if(myCompanyId == companyId) {
			articles = articleRepository.findAllByCompanyIdOrderByCreatedDateDesc(myCompanyId,pageable);			
		}else {			
		 articles = articleRepository.findAllByCompanyId(companyId,client.getId(),providerId,pageable);
		}
		if(articles.isEmpty()) {
			throw new RecordNotFoundException("there is no article");
		}
		List<ArticleDto> articlesDto = new ArrayList<>();
		for(Article i : articles) {
			logger.warn(" child article id 1");
			ArticleDto articleDto = articleMapper.mapToDto(i);
			logger.warn(" child article id 2");
			articlesDto.add(articleDto);
		}
		return articlesDto;
	}
	

	//by the article to client 
	public void impactInvoice(List<CommandLine> commandLines, Client client) {
		
		Company company = client.getCompany();
		Category category = categoryService.getDefaultCategory(company);
		SubCategory subCategory = subCategoryService.getDefaultSubCategory(company);
		Provider provider = providerService.getMeAsProvider(company.getId()).get();
		Article article;
		//the code below convey that i add article to client table
		for(int i =0; i < commandLines.size();i++) {
			Optional<Article> art = articleRepository.findByCodeAndProviderId(commandLines.get(i).getArticle().getCode(), provider.getId());
			logger.warn("quantity of problem ==> "+commandLines.get(i).getQuantity());
			Double articleCost = round(commandLines.get(i).getArticle().getCost() + (commandLines.get(i).getArticle().getCost()*commandLines.get(i).getArticle().getTva()+ commandLines.get(i).getArticle().getCost()*commandLines.get(i).getArticle().getMargin())/100);
			
			double qte  ;
			if(art.isPresent()) {
				 article = art.get();
				 qte= (commandLines.get(i).getQuantity());
				article.setQuantity(qte+article.getQuantity());
				//do not remove the above line in case of the provider has augmented the article price
				article.setCost(articleCost);	
			}else {				
				Article ar = commandLines.get(i).getArticle();
				qte = commandLines.get(i).getQuantity();
				 article = new Article();
				 article.setLibelle(ar.getLibelle());
				 article.setCode(ar.getCode());
				 article.setUnit(ar.getUnit());
				 article.setDiscription(ar.getDiscription());
				 article.setMinQuantity(ar.getMinQuantity());
				 article.setBarcode(ar.getBarcode());
				 article.setTva(ar.getTva());
				 article.setImage(ar.getImage());
				 article.setProvider(provider);
				 article.setQuantity(qte);
				 article.setMargin(company.getMargin());
				 article.setCompany(company);
				 article.setIsVisible(company.getIsVisible());
			article.setCategory(category);
			article.setSubCategory(subCategory);
			article.setCost(articleCost);
			article.setSharedPoint(commandLines.get(i).getArticle().getSharedPoint());
			articleRepository.save(article);
			}
			inventoryService.impactInvoiceOnClient(company,commandLines.get(i), article);
		}
		Invoice invoice = commandLines.get(0).getInvoice();
		Long providerId = providerService.getMeProviderId(invoice.getCompany().getId());
		ProviderCompany providerCompany = providerCompanyRepository.findByProviderIdAndCompanyId(providerId,company.getId()).get();
		Double credit = round(providerCompany.getCredit()+ invoice.getPrix_invoice_tot());
		providerCompany.setCredit(credit);
		Double mvt = round(providerCompany.getMvt() + invoice.getPrix_invoice_tot());
		providerCompany.setMvt(mvt);
	}
	
	public List<ArticleDto> getAllProvidersArticleByProviderId(Company company, Long id, int offset, int pageSize) {
		logger.warn("articles size 1 ");
		List<ArticleDto> articlesDto = new ArrayList<ArticleDto>();
		logger.warn("articles size 2 ");
		Page<Article> articles = null;
		logger.warn("articles size 3 ");
		Pageable pageable = PageRequest.of(0,20);// a return
		if(company.getId() != id) {
			for(Company i : company.getBranches()) {
				if(i.getId() == id) {
					articles = articleRepository.findAllByCompanyIdOrderByCreatedDateDesc(id,pageable);		
				}
			}
		}else {
			logger.warn("articles size ");
		 articles = articleRepository.findAllByCompanyIdOrderByCreatedDateDesc(company.getId(),pageable);
		 logger.warn("articles size "+articles.getSize());
		 logger.warn("index of : "+articles.stream().toString().indexOf(1));
		}
		if(articles != null) {
			List<Article> articlesContent = articles.getContent();
			for(Article i : articlesContent) {
			ArticleDto articleDto = articleMapper.mapToDto(i);
			articlesDto.add(articleDto);
			
			}
		}
		return articlesDto;
	}
	
	public List<ArticleDto> getAllArticleByCategoryId(Long categoryId, Long companyId, Client client) {
		logger.warn("getAllArticleByCategoryId mrigel outside for loop ");
		List<Article> articles;
		Long myCompanyId = client.getCompany().getId();
		if(companyId == myCompanyId) {
			logger.warn("getAllArticleByCategoryId mrigel inside for loop ");
			articles = articleRepository.findAllMyByCategoryIdAndCompanyId(categoryId, myCompanyId);
		}else {
			articles = articleRepository.findAllByCategoryIdAndCompanyId(categoryId, companyId, client.getId());
		}

		if(articles == null) {
			throw new RecordNotFoundException("there is no article");
		}
		List<ArticleDto> articlesDto = new ArrayList<>();
		for(Article i : articles) {
			ArticleDto articleDto = articleMapper.mapToDto(i);
			articlesDto.add(articleDto);
		}
		return articlesDto;
	}
	
	public List<ArticleDto> getAllArticleBySubCategoryIdAndCompanyId(Long subcategoryId, Long companyId, Client client) {
		Long companId = client.getCompany().getId();
		List<Article> articles;
		if(companyId == companId) {
			articles = articleRepository.findAllMyBySubCategoryIdAndCompanyId(subcategoryId, companId);
		}else {
			articles = articleRepository.findAllBySubCategoryIdAndCompanyId(subcategoryId, companyId , client.getId());
		}
		if(articles == null) {
			throw new RecordNotFoundException("there is no article");
		}
		List<ArticleDto> articlesDto = new ArrayList<>();
		for(Article i : articles) {
			ArticleDto articleDto = articleMapper.mapToDto(i);
			articlesDto.add(articleDto);
		}
		return articlesDto;
	}
	
	
	
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
			if(articleDto.getCategory()==null) {
				Category category = categoryService.getDefaultCategory(provider.getCompany());
				article1.setCategory(category);		
			}
			if(articleDto.getSubCategory()==null) {
				SubCategory subCategory = subCategoryService.getDefaultSubCategory(provider.getCompany());
				article1.setSubCategory(subCategory);
			}
		
			super.insert(article1);
			article1.setSharedPoint(provider.getCompany().getUser().getUsername());
			if(provider.getCompany().getIsVisible() == PrivacySetting.ONLY_ME) {	
			article1.setIsVisible(PrivacySetting.ONLY_ME);
			}
			if(provider.getCompany().getIsVisible() == PrivacySetting.CLIENT && articleDto.getIsVisible() == PrivacySetting.PUBLIC) {
				article1.setIsVisible(PrivacySetting.CLIENT);
			}
			article1.setCompany(provider.getCompany());			
			inventoryService.makeInventory(article1, provider.getCompany());
			return ResponseEntity.ok(articleDto);
		}
	
		public void addQuantity(Long id, Double quantity, Provider provider) {
			Article article = articleRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("there is no article with id: "+id));
			Double Quantity = round(article.getQuantity()+quantity);
			article.setQuantity(Quantity);
			articleRepository.save(article);
			inventoryService.addQuantity(article,quantity, provider.getCompany());

		}
	
		public ResponseEntity<ArticleDto> upDateArticle( MultipartFile file, String article, Provider provider) 
				throws JsonMappingException, JsonProcessingException {
			ArticleDto articleDto = objectMapper.readValue(article, ArticleDto.class);
			Article updatedArticle = articleMapper.mapToEntity(articleDto);
			Article article1 =  articleRepository.findById(articleDto.getId()).orElseThrow(() -> new RecordNotFoundException("there is no article with id: "+articleDto.getId()));	
				 if(!article1.getCompany().equals(provider.getCompany())) {
					 return null;
				 }
			if(file == null) {
				updatedArticle.setImage(article1.getImage());
				updatedArticle.setSharedPoint(article1.getSharedPoint());
			}else {
				String newFileName = imageService.insertImag(file,provider.getCompany().getUser().getUsername(), "article");
				updatedArticle.setImage(newFileName);
			}
			if(updatedArticle.getProvider() == null) {
				updatedArticle.setProvider(provider);
			}
			if(updatedArticle.getQuantity() != article1.getQuantity()) {
				addQuantity(article1.getId(), updatedArticle.getQuantity()-article1.getQuantity(), provider);
			}
			
			updatedArticle.setCompany(provider.getCompany());
			if(articleDto.getIsVisible() != article1.getIsVisible()) {
				if(provider.getCompany().getIsVisible() == PrivacySetting.ONLY_ME) {			
					updatedArticle.setIsVisible(PrivacySetting.ONLY_ME);
					}
				if(provider.getCompany().getIsVisible() == PrivacySetting.CLIENT && articleDto.getIsVisible() == PrivacySetting.PUBLIC) {
						updatedArticle.setIsVisible(PrivacySetting.CLIENT);
					}
			}else {			
				updatedArticle.setIsVisible(article1.getIsVisible());
			}
			updatedArticle.setSharedPoint(provider.getCompany().getUser().getUsername());
			article1 = articleRepository.save(updatedArticle); // a verifier 
			return null;
			}
	
		
		private double round(double value) {
		    return Math.round(value * 100.0) / 100.0; 
		}
		
		public Article findById(Long articleId) {
			Optional<Article>  article = articleRepository.findById(articleId);
			return article.get();
		}
		
		public ResponseEntity<String> deleteByCompanyArticleId(Long articleId, Provider provider) {
			Article article = articleRepository.findById(articleId).orElseThrow(() -> new RecordNotFoundException("This Article Does Not Exist"));
							
				if(article.getProvider().getId() == provider.getId() ||
						article.getProvider().isVirtual() == true &&
								article.getProvider().getCompany() == provider.getCompany() ) {
					Inventory inventory = inventoryService.findByArticleIdAndCompanyId(articleId, provider.getCompany().getId());
					if(inventory.getOut_quantity() == 0) {
					inventory.setArticle(null);	
					inventoryService.deleteById(inventory.getId());
					articleRepository.deleteById(articleId);
					}
					else {
						article.setProvider(null);
						articleRepository.save(article);
					}
				}
				return ResponseEntity.ok("successfuly deleted");
		}
		
		
		
		
		
		
		

		
	/////////////////////////////////////// future work ////////////////////////////////////////////////////////
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
