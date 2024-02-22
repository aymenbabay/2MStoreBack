package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.ArticleDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Category;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Inventory;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;
import com.example.meta.store.werehouse.Entities.SubCategory;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Mappers.ArticleMapper;
import com.example.meta.store.werehouse.Repositories.ArticleRepository;
import com.example.meta.store.werehouse.Repositories.ProviderCompanyRepository;
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
		
	private final ProviderCompanyRepository providerCompanyRepository;
	
	private final ArticleMapper articleMapper; 
		
	private final InventoryService inventoryService;

	private final ImageService imageService;
	
	private final CategoryService categoryService;
	
	private final SubCategoryService subCategoryService;
	
	private final ProviderService providerService;
	
	private final ObjectMapper objectMapper;

	private final Logger logger = LoggerFactory.getLogger(ArticleService.class);
	
    DecimalFormat df = new DecimalFormat("#.###");
	
    public ResponseEntity<ArticleDto> insertArticle( MultipartFile file, String article, Provider provider)
			throws JsonMappingException, JsonProcessingException {
    	logger.warn("insert article service first of function ");
		ArticleDto articleDto = objectMapper.readValue(article, ArticleDto.class);
		logger.warn("insert article service first of function 1");
		Article article1 = articleMapper.mapToEntity(articleDto);
		logger.warn("insert article service first of function 2");
		if(file != null) {
			logger.warn("insert article service first of function 3");
			String newFileName = imageService.insertImag(file,provider.getCompany().getUser().getUsername(), "article");
			article1.setImage(newFileName);
		}
		if(article1.getProvider() == null) {
			logger.warn("insert article service first of function 4");
			article1.setProvider(provider);
		}
		if(articleDto.getCategory()==null) {
			logger.warn("insert article service first of function 5");
			Category category = categoryService.getDefaultCategory(provider.getCompany());
			article1.setCategory(category);		
		}
		if(articleDto.getSubCategory()==null) {
			logger.warn("insert article service first of function 6");
			SubCategory subCategory = subCategoryService.getDefaultSubCategory(provider.getCompany());
			article1.setSubCategory(subCategory);
		}
		logger.warn("insert article service first of function 7");
		super.insert(article1);
		logger.warn("insert article service first of function 8");
		article1.setSharedPoint(provider.getCompany().getUser().getUsername());
		logger.warn("insert article service first of function 9");
		if(provider.getCompany().getIsVisible() == PrivacySetting.ONLY_ME) {			
			logger.warn("insert article service first of function 10");
		article1.setIsVisible(PrivacySetting.ONLY_ME);
		}
		if(provider.getCompany().getIsVisible() == PrivacySetting.CLIENT && articleDto.getIsVisible() == PrivacySetting.PUBLIC) {
			logger.warn("insert article service first of function 11");
			article1.setIsVisible(PrivacySetting.CLIENT);
		}
		logger.warn("insert article service first of function 12");
		article1.setCompany(provider.getCompany());
		logger.warn("insert article service first of function 13");
		inventoryService.makeInventory(article1, provider.getCompany());
		logger.warn("insert article service first of function 14");
		return ResponseEntity.ok(articleDto);
	}
	
	public List<ArticleDto> getAllProvidersArticleByProviderId(Company company, Long id) {
		logger.warn("id from path : "+id);
		logger.warn("id from company : "+company.getId());
		List<ArticleDto> articlesDto = new ArrayList<ArticleDto>();
		List<Article> articles = new ArrayList<>();
		if(company.getId() != id) {
			for(Company i : company.getBranches()) {
				if(i.getId() == id) {
					logger.warn("id from branches : "+i.getId());
					articles = articleRepository.findAllMyByCompanyId(id);		
				}
			}
		}else {			
		 articles = articleRepository.findAllMyByCompanyId(company.getId());			
		}
		for(Article i : articles) {
			logger.warn("articel id from for loop "+i.getId());
			ArticleDto articleDto = articleMapper.mapToDto(i);
			articlesDto.add(articleDto);
		}
		return articlesDto;
	}

	public ResponseEntity<ArticleDto> upDateArticle( MultipartFile file, String article, Provider provider) 
			throws JsonMappingException, JsonProcessingException {
		logger.warn("update article in article service "+article);
		ArticleDto articleDto = objectMapper.readValue(article, ArticleDto.class);
		Article updatedArticle = articleMapper.mapToEntity(articleDto);
		Optional<Article> article1;
		
		article1 = articleRepository.findById(articleDto.getId());	
			 if(!article1.get().getCompany().equals(provider.getCompany())) {
				 return null;
			 }
		
		if(article1.isEmpty()) {
			throw new RecordNotFoundException("there is no article with id: "+articleDto.getId());
		}
		if(file == null) {
			updatedArticle.setImage(article1.get().getImage());
			updatedArticle.setSharedPoint(article1.get().getSharedPoint());
		}else {
			String newFileName = imageService.insertImag(file,provider.getCompany().getUser().getUsername(), "article");
			updatedArticle.setImage(newFileName);
		}
		if(updatedArticle.getProvider() == null) {
			updatedArticle.setProvider(provider);
		}
		if(updatedArticle.getQuantity() != article1.get().getQuantity()) {
			addQuantity(article1.get().getId(), updatedArticle.getQuantity()-article1.get().getQuantity(), provider);
		}
		
		updatedArticle.setCompany(provider.getCompany());
		if(articleDto.getIsVisible() != article1.get().getIsVisible()) {
			if(provider.getCompany().getIsVisible() == PrivacySetting.ONLY_ME) {			
				updatedArticle.setIsVisible(PrivacySetting.ONLY_ME);
				}
			if(provider.getCompany().getIsVisible() == PrivacySetting.CLIENT && articleDto.getIsVisible() == PrivacySetting.PUBLIC) {
					updatedArticle.setIsVisible(PrivacySetting.CLIENT);
				}
		}else {			
			updatedArticle.setIsVisible(article1.get().getIsVisible());
		}
		article1 = Optional.of(articleRepository.save(updatedArticle));
		return null;
		}

	
	
	public void addQuantity(Long id, Double quantity, Provider provider) {
		Optional<Article> article = articleRepository.findById(id);
		if(article.isEmpty()) {
			throw new RecordNotFoundException("there is no article with id: "+id);
		}
		article.get().setQuantity(article.get().getQuantity()+quantity);
		articleRepository.save(article.get());
		inventoryService.addQuantity(article.get(),quantity, provider.getCompany());

	}
	
	public List<ArticleDto> getAllArticleByCompanyId(Long companyId,Client client, Long providerId) {
		Long myCompanyId = client.getCompany().getId();
		List<Article> articles;
		if(myCompanyId == companyId) {
			articles = articleRepository.findAllMyByCompanyId(myCompanyId);			
		}else {			
		 articles = articleRepository.findAllByCompanyId(companyId,client.getId(),providerId);
		}
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
	
	public List<ArticleDto> findRandomArticlesPub(Client client, Provider provider) {
		logger.warn("random article in article service just before the list ");
		List<Article> article = new ArrayList<>();
		if(client == null) {			
		 article = articleRepository.findRandomArticles(51.122,51.125,PrivacySetting.PUBLIC);
		}
		else {
			article = articleRepository.findRandomArticlesPro(51.122,51.125,provider.getId(),client.getId());
			logger.warn("random article in article service just after the list"+article.get(0).getCode());
		}
		
		if(article.isEmpty()) {
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
	
	
	public ResponseEntity<String> deleteByCompanyArticleId(Long articleId, Provider provider) {
		Optional<Article> article = articleRepository.findById(articleId);
			if(article.isEmpty()) {
				throw new RecordNotFoundException("This Article Does Not Exist");
			}
			Article art = article.get();			
			if(art.getProvider().getId() == provider.getId() ||
					art.getProvider().isVirtual() == true &&
					art.getProvider().getCompany() == provider.getCompany() ) {
				Inventory inventory = inventoryService.findByArticleIdAndCompanyId(articleId, provider.getCompany().getId());
				if(inventory.getOut_quantity() == 0) {
				inventory.setArticle(null);	
				inventoryService.deleteById(inventory.getId());
				articleRepository.deleteById(articleId);
				}
				else {
					art.setProvider(null);
					articleRepository.save(art);
				}
			}
			return ResponseEntity.ok("successfuly deleted");
	}

	public Article findById(Long articleId) {
		Optional<Article>  article = articleRepository.findById(articleId);
		return article.get();
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
			String articleCost = df.format(commandLines.get(i).getArticle().getCost() + (commandLines.get(i).getArticle().getCost()*commandLines.get(i).getArticle().getTva()+ commandLines.get(i).getArticle().getCost()*commandLines.get(i).getArticle().getMargin())/100);
			articleCost = articleCost.replace(",", ".");
			double qte  ;
			if(art.isPresent()) {
				 article = art.get();
				 qte= (commandLines.get(i).getQuantity());
				 logger.warn(articleCost+" article cost "+article.getQuantity()+" article quantity "+qte +" qte");
				article.setQuantity(qte+article.getQuantity());
				//do not remove the above line in case of the provider has augmented the article price
				article.setCost(Double.parseDouble(articleCost));	
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
			article.setCost(Double.parseDouble(articleCost));
			article.setSharedPoint(commandLines.get(i).getArticle().getSharedPoint());
			articleRepository.save(article);
			logger.warn("new article saved");
			}
			logger.warn("just before invontory impact function ");
			
			inventoryService.impactInvoiceOnClient(company,commandLines.get(i), article);
		}
		Invoice invoice = commandLines.get(0).getInvoice();
		Long providerId = providerService.getMeProviderId(invoice.getCompany().getId());
		ProviderCompany providerCompany = providerCompanyRepository.findByProviderIdAndCompanyId(providerId,company.getId()).get();
		String credit = df.format(providerCompany.getCredit()+ invoice.getPrix_invoice_tot());
		credit = credit.replace(",",".");
		providerCompany.setCredit(Double.parseDouble(credit));
		String mvt = df.format(providerCompany.getMvt() + invoice.getPrix_invoice_tot());
		mvt = mvt.replace(",", ".");
		providerCompany.setMvt(Double.parseDouble(mvt));
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

	public List<ArticleDto> getAllArticleBySubCategoryIdAndCompanyId(Long subcategoryId,
			Long companyId, Client client) {
		Long companId = client.getCompany().getId();
		List<Article> articles;
		if(companyId == companId) {
			logger.warn("getAllArticleBySubCategoryIdAndCompanyId mrigel");
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

	


	
}
