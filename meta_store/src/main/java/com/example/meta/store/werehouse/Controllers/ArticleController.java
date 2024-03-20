package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.meta.store.Base.ErrorHandler.NotPermissonException;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.ArticleDto;
import com.example.meta.store.werehouse.Dtos.SubArticleRelationDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Services.ArticleService;
import com.example.meta.store.werehouse.Services.ClientService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.ProviderService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/article/")
@RequiredArgsConstructor
public class ArticleController {


	private final ArticleService articleService;

	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;
	
	private final ProviderService providerService;

	private final ClientService clientService;

	private final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	
	

	/////////////////////////////////////// real work ////////////////////////////////////////////////////////
	@GetMapping("getrandom")
	public List<ArticleDto> findRandomArticles(){
		Optional<Client> client = getClient();
		Optional<Provider> provider = getProvider();
		if(client.isPresent()) {
			return articleService.findRandomArticlesPub(client.get(), provider.get(), null);
		}
		User user = userService.findByUserName(authenticationFilter.userName);
		return articleService.findRandomArticlesPub(null, null, user);
	}
	
	@GetMapping("get_all_articles/{id}/{offset}/{pageSize}")
	public List<ArticleDto> getAllArticleByProviderId(@PathVariable Long id, @PathVariable int offset, @PathVariable int pageSize){
		logger.warn("sgssbs");
			Optional<Client> client = getClient();
			Long providerId=null;
			Client client1 = null;
			if(client.isPresent()) {				
				 providerId = providerService.getMeProviderId(id);
				 client1 = client.get();
			}
			return articleService.getAllArticleByCompanyId(id,client1,providerId,offset, pageSize);
	}
	
	@GetMapping("getAllMyArticle/{id}/{offset}/{pageSize}")
	public List<ArticleDto> getAllMyArticle(@PathVariable Long id, @PathVariable int offset, @PathVariable int pageSize) {
		logger.warn("id: "+id+" offset: "+offset+" page size: "+pageSize);
		Company company = getCompany().orElseThrow(() -> new RecordNotFoundException("you dont have a company"));
		return articleService.getAllProvidersArticleByProviderId(company,id,offset, pageSize);
	}
	
	
	@GetMapping("category/{categoryId}/{companyId}")
	public List<ArticleDto> getAllArticelsByCategoryId(@PathVariable Long categoryId, @PathVariable Long companyId){
		Client client = getClient().get();
		return articleService.getAllArticleByCategoryId(categoryId, companyId, client);
	}
	
	@GetMapping("sub_category/{subcategoryId}/{companyId}")
	public List<ArticleDto> getAllArticleBySubCategoryIdAnd( @PathVariable Long subcategoryId, @PathVariable Long companyId) {
		Client client = getClient().get();
		return articleService.getAllArticleBySubCategoryIdAndCompanyId(subcategoryId, companyId,client);
	}
	
	
	@PostMapping("add")
	public ResponseEntity<ArticleDto> insertArticle(
			 @RequestParam(value ="file", required = false) MultipartFile file,
			 @RequestParam("article") String article)
			throws Exception{
		Optional<Provider> provider = getProvider();
		return articleService.insertArticle(file,article,provider.get());
	}
	
	@GetMapping("{id}/{quantity}")
	public void addQuantity(@PathVariable Double quantity, @PathVariable Long id) {
		Optional<Provider> provider = getProvider();
		articleService.addQuantity(id,quantity,provider.get());
	}
	
	
	@PutMapping("update")
	public ResponseEntity<ArticleDto> upDateArticle(
			 @RequestParam(value ="file", required = false) MultipartFile file,
			 @RequestParam("article") String article) throws Exception{
		Optional<Provider> provider = getProvider();
		return articleService.upDateArticle(file,article, provider.get());
	}
	
	
	private Optional<Company> getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company.isPresent()) {
			return company;
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return Optional.of(company2.getBody());
		}
			return Optional.empty();
	}
	
	private Optional<Provider> getProvider() {
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return Optional.empty();
		}
		Optional<Provider> provider = providerService.getMeAsProvider(company.get().getId());
		return provider;
	}
	
	private Optional<Client> getClient(){
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return Optional.empty();
		}
		Optional<Client> client = clientService.getMeAsClient(company.get().getId());
			return client;
	}

	@DeleteMapping("delete/{id}")
	public ResponseEntity<String> deleteArticleById(@PathVariable Long id){
		Optional<Provider> provider = getProvider();
		return articleService.deleteByCompanyArticleId(id,provider.get());
	}
	
	@PostMapping("child/{parentid}")
	public void addChildToParentArticle(@PathVariable Long parentid, @RequestBody SubArticleRelationDto subArticle) {
		
			logger.warn("quantity "+ subArticle.getQuantity()+" child id "+subArticle.getChildArticle().getId());
			
		
	}
	
	/////////////////////////////////////// future work ////////////////////////////////////////////////////////
	@GetMapping("{articlenamecontaining}")
	public List<ArticleDto> getByNameContaining(@PathVariable String articlenamecontaining ){
		Optional<Provider> provider = getProvider(); 
		return articleService.getByNameContaining(articlenamecontaining,provider.get().getId());
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	

	


	
	

	
	
	

//	---------------------------------------------------------------- EXCEPTION -----------------------------------------------------------------------------
	
}
