package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.ClientInvoiceController;
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Dtos.InventoryDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.CompanyArticle;
import com.example.meta.store.werehouse.Entities.Inventory;
import com.example.meta.store.werehouse.Mappers.InventoryMapper;
import com.example.meta.store.werehouse.Repositories.CompanyArticleRepository;
import com.example.meta.store.werehouse.Repositories.InventoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService extends BaseService<Inventory, Long> {

	private final InventoryMapper inventoryMapper;
	
	private final InventoryRepository inventoryRepository;
	
	private final CompanyService companyService;
	
	private final CompanyArticleRepository companyArticleRepository;
	

	private final Logger logger = LoggerFactory.getLogger(InventoryService.class);
	
    DecimalFormat df = new DecimalFormat("#.###");

	public List<InventoryDto> getInventoryByCompanyId(Long companyId) {
		List<Inventory> inventory = inventoryRepository.findByCompanyId(companyId);
		if(inventory == null) {
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need");
	}
			List<InventoryDto> inventoriesDto = new ArrayList<>();
			for(Inventory i:inventory) {
		InventoryDto inventoryDto = inventoryMapper.mapToDto(i);
		inventoriesDto.add(inventoryDto);
			}
			return inventoriesDto;
	}
	
	public ResponseEntity<InventoryDto> makeInventory(CompanyArticle companyArticle, Company company){
		Inventory inventory = new Inventory();
		String articleCost = df.format((companyArticle.getCost() + companyArticle.getCost()*companyArticle.getArticle().getTva()/100) *companyArticle.getQuantity());
		inventory.setIn_quantity(companyArticle.getQuantity());
		inventory.setCompany(company);
		inventory.setOut_quantity((double)0);
		inventory.setCompanyArticle(companyArticle);
		inventory.setArticleCost(Double.parseDouble(articleCost));
		inventory.setArticleSelling((double)0);
		inventoryRepository.save(inventory);
		return null;
		 
	}

	public Optional<Inventory> getArticleCompanyArticleId( Long companyarticleId, Company company) {
		return inventoryRepository.findByCompanyarticleIdAndCompanyId(companyarticleId,company.getId());
	}

	public void addQuantity(CompanyArticle companyArticle, Long quantity, Company company) {
		Optional<Inventory> inventori = inventoryRepository.findByCompanyarticleIdAndCompanyId(companyArticle.getId(),company.getId());
		Inventory inventory = inventori.get();
		String articleCost = df.format((companyArticle.getCost() + companyArticle.getCost()*companyArticle.getArticle().getTva()/100) *quantity);
		inventory.setIn_quantity(inventory.getIn_quantity()+quantity);
		inventory.setArticleCost(inventory.getArticleCost()+Double.parseDouble(articleCost));
		inventoryRepository.save(inventory);
		
	}


	public void updateArticle(Long companyarticleId, Double deference, Company company) {
		Optional<Inventory> inventori = inventoryRepository.findByCompanyarticleIdAndCompanyId(companyarticleId,company.getId());
		Inventory inventory = inventori.get();
		if(deference!=0) {
			inventory.setIn_quantity(inventory.getIn_quantity()-deference);
		}
		inventoryRepository.save(inventory);
	}

	public void impacteInvoice( Company company, List<CommandLineDto> commandLinesDto, List<Article> articles, List<CompanyArticle> companyArticles, Long clientId) {
		Company clientCompany = companyService.findByClientId(clientId);
		for(CommandLineDto i : commandLinesDto) {
//			
		Optional<Inventory> providerInventory = inventoryRepository.findByCompanyarticleIdAndCompanyId(i.getCompanyArticle(),company.getId());
		System.out.println("after provider inventory . get() inventory service "+i.getCompanyArticle()+" "+clientCompany.getId());
		System.out.println("after provider inventory . get() inventory service "+i.getCompanyArticle()+" "+company.getId());
		Inventory providerInventori = providerInventory.get();
		System.out.println("efore provider inventory . get() inventory service");
		providerInventori.setOut_quantity(providerInventori.getOut_quantity()+i.getQuantity());
		for(CompanyArticle a : companyArticles) {
			String articleCost = df.format((a.getCost() + a.getCost() * a.getArticle().getTva() * a.getMargin()/100) * i.getQuantity());
			System.out.println("in for loop provider inventory . get() inventory service");
			if(a.getId().equals(i.getCompanyArticle())) {
				
				providerInventori.setArticleSelling(providerInventori.getArticleSelling() + Double.parseDouble(articleCost));
			}
		
		inventoryRepository.save(providerInventori);

		}
		}
		
	}

	public Inventory findByCompanyArticleIdAndCompanyId(Long companyArticleId, Long companyId) {
		Optional<Inventory> inventory = inventoryRepository.findByCompanyarticleIdAndCompanyId(companyArticleId, companyId);
		return inventory.get();
	}

	public void impactInvoiceOnClient(Company company, CompanyArticle article, double parseDouble) {
		Optional<Inventory> clientInventory = inventoryRepository.findByCompanyarticleIdAndCompanyId(article.getId(),company.getId());
		if(clientInventory.isPresent()) {
			Inventory clientInventori = clientInventory.get();
			clientInventori.setIn_quantity(clientInventori.getIn_quantity()+article.getQuantity());
			clientInventori.setArticleCost(clientInventori.getArticleCost()+parseDouble);
		}else {
			Inventory clientInventori = new Inventory();
			logger.warn("inventory service impact invoice on client else");
		//	Optional<CompanyArticle> companyArt = companyArticleRepository.findByArticleIdAndCompanyId(a.getArticle().getId(), clientCompany.getId());
			clientInventori.setCompanyArticle(article);
			clientInventori.setCompany(company);
			clientInventori.setArticleCost(parseDouble);
			clientInventori.setArticleSelling((double)0);
			clientInventori.setIn_quantity(article.getQuantity());
			clientInventori.setOut_quantity((double)0);
			inventoryRepository.save(clientInventori);
				
				
			}
		
	}
}
