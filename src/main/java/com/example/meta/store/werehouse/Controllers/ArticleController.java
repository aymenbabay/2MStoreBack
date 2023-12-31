package com.example.meta.store.werehouse.Controllers;

import java.util.List;

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
import com.example.meta.store.werehouse.Dtos.CompanyArticleDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Services.ArticleService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.ProviderService;
import com.example.meta.store.werehouse.Services.WorkerService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/article")
@RequiredArgsConstructor
public class ArticleController {


	private final ArticleService articleService;

	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	private final WorkerService workerService;
	
	private final ProviderService providerService;
	
	@PostMapping("/add")
	public ResponseEntity<ArticleDto> insertArticle(
			 @RequestParam(value ="file", required = false) MultipartFile file,
			 @RequestParam("article") String article)
			throws Exception{
		Provider provider = getProvider();
		return articleService.insertArticle(file,article,provider);
	}
	
	
	@GetMapping("/getAllMyArticle")
	public List<CompanyArticleDto> getAllMyArticle() {
		Provider provider = getProvider();
		return articleService.getAllProvidersArticleByProviderId(provider);
	}
	
	@PutMapping("/update")
	public ResponseEntity<ArticleDto> upDateArticle(
			 @RequestParam(value ="file", required = false) MultipartFile file,
			 @RequestParam("article") String article) throws Exception{
		Provider provider = getProvider();
		return articleService.upDateArticle(file,article, provider);
	}
	
	@PutMapping("/updatecompanyarticle")
	public void upDateCompanyArticle(@RequestBody CompanyArticleDto companyArticleDto) {
		System.out.println(companyArticleDto.getId()+" aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa1");
		
		articleService.upDateCompanyArticle(companyArticleDto);
	}
//	-------------------------------------------------properly work------------------------------------------------
	
	@GetMapping("/{articlenamecontaining}")
	public List<ArticleDto> getByNameContaining(@PathVariable String articlenamecontaining ){
		Provider provider = getProvider(); 
		return articleService.getByNameContaining(articlenamecontaining,provider.getId());
	}
	
	@GetMapping("/{id}/{quantity}")
	public void addQuantity(@PathVariable Long quantity, @PathVariable Long id) {
		Provider provider = getProvider();
		articleService.addQuantity(id,quantity,provider);
	}
	
	@GetMapping("/getbyprovider/{id}")
	public List<ArticleDto> getAllArticleByProviderId(@PathVariable Long id){
		if(id !=(long)0) {
			ResponseEntity<Provider> provider = providerService.getById(id);
			return articleService.getAllArticleByProviderId(provider.getBody());
		}
		Provider provider = getProvider();
		return articleService.getAllArticleByProviderId(provider);
	}
	
	@GetMapping("/articlebyid/{id}")
	public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id){
		return articleService.getArticleById(id);
	}
	

	
	@PostMapping("/add_exist")
	public void insertExistArticle(@RequestBody String companyArticleDto) {
		Company company = getCompany();
		articleService.insertExistArticle( company);
	}
	

	@DeleteMapping("delete/{id}")
	public ResponseEntity<String> deleteArticleById(@PathVariable Long id){
		Long companyId = getCompany().getId();
		return articleService.deleteByIdAndCompanyId(id,companyId);
	}
	
	@GetMapping("/getrandom")
	public List<ArticleDto> getaertt(){
		return articleService.getdgdgeg();
	}
	
	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Company company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company;
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return company2.getBody();
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}
	
	private Provider getProvider() {
		Company company = getCompany();
		Provider provider = providerService.getMeAsProvider(company.getId());
		return provider;
	}
	
}
