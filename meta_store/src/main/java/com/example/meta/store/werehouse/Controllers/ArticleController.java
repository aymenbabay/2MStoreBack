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

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.ArticleDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
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
	
	
	@GetMapping("getAllMyArticle")
	public List<ArticleDto> getAllMyArticle() {
		Optional<Provider> provider = getProvider();
		return articleService.getAllProvidersArticleByProviderId(provider.get());
	}
	
	@PutMapping("update")
	public ResponseEntity<ArticleDto> upDateArticle(
			 @RequestParam(value ="file", required = false) MultipartFile file,
			 @RequestParam("article") String article) throws Exception{
		logger.warn("update article in article controller 1");
		Optional<Provider> provider = getProvider();
		logger.warn("update article in article controller 2");
		return articleService.upDateArticle(file,article, provider.get());
	}
		

//	@GetMapping("getbyprovider/{id}")
//	public List<ArticleDto> getAllArticleByProviderId(@PathVariable Long id){
//		if(id !=(long)0) {
//			ResponseEntity<Provider> provider = providerService.getById(id);
//			return articleService.getAllArticleByProviderId(provider.getBody());
//		}
//		Optional<Provider> provider = getProvider();
//		return articleService.getAllArticleByProviderId(provider.get());
//	}
//	

	@GetMapping("getrandom")
	public List<ArticleDto> findRandomArticles(){
		Optional<Client> client = getClient();
		Optional<Provider> provider = getProvider();
		if(client == null) {
			return articleService.findRandomArticlesPub(null, null);
		}
		return articleService.findRandomArticlesPub(client.get(), provider.get());
		
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<String> deleteArticleById(@PathVariable Long id){
		Optional<Provider> provider = getProvider();
		return articleService.deleteByCompanyArticleId(id,provider.get());
	}
	
	private Optional<Company> getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company;
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return Optional.of(company2.getBody());
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}
	
	private Optional<Provider> getProvider() {
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return null;
		}
		Optional<Provider> provider = providerService.getMeAsProvider(company.get().getId());
		return provider;
	}
	
	private Optional<Client> getClient(){
		Optional<Company> company = getCompany();
		if(company.isEmpty()) {
			return null;
		}
		Optional<Client> client = clientService.getMeAsClient(company.get());
		return client;
	}

	

//	---------------------------------------------------------------- EXCEPTION -----------------------------------------------------------------------------
	@GetMapping("{articlenamecontaining}")
	public List<ArticleDto> getByNameContaining(@PathVariable String articlenamecontaining ){
		Optional<Provider> provider = getProvider(); 
		return articleService.getByNameContaining(articlenamecontaining,provider.get().getId());
	}
}
