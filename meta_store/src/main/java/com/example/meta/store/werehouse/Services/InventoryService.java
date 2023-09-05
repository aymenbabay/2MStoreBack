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
import com.example.meta.store.werehouse.Entities.Inventory;
import com.example.meta.store.werehouse.Mappers.InventoryMapper;
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
	
	public ResponseEntity<InventoryDto> makeInventory(Article article, Company company){
		Inventory inventory = new Inventory();
		String articleCost = df.format((article.getCost() + article.getCost()*article.getTva()/100) *article.getQuantity());
		inventory.setIn_quantity(article.getQuantity());
		inventory.setCompany(company);
		inventory.setOut_quantity((double)0);
		inventory.setArticle(article);
		inventory.setArticleCost(Double.parseDouble(articleCost));
		inventory.setArticleSelling((double)0);
		inventoryRepository.save(inventory);
		return null;
		 
	}

	public Optional<Inventory> getArticleCompanyArticleId( Long companyarticleId, Company company) {
		return inventoryRepository.findByArticleIdAndCompanyId(companyarticleId,company.getId());
	}

	public void addQuantity(Article article, Long quantity, Company company) {
		Optional<Inventory> inventori = inventoryRepository.findByArticleIdAndCompanyId(article.getId(),company.getId());
		Inventory inventory = inventori.get();
		String articleCost = df.format((article.getCost() + article.getCost()*article.getTva()/100) *quantity);
		inventory.setIn_quantity(inventory.getIn_quantity()+quantity);
		inventory.setArticleCost(inventory.getArticleCost()+Double.parseDouble(articleCost));
		inventoryRepository.save(inventory);
		
	}


	public void updateArticle(Long companyarticleId, Double deference, Company company) {
		Optional<Inventory> inventori = inventoryRepository.findByArticleIdAndCompanyId(companyarticleId,company.getId());
		Inventory inventory = inventori.get();
		if(deference!=0) {
			inventory.setIn_quantity(inventory.getIn_quantity()-deference);
		}
		inventoryRepository.save(inventory);
	}

	public void impacteInvoice( Company company, List<CommandLineDto> commandLinesDto, List<Article> articles, Long clientId) {
		Company clientCompany = companyService.findByClientId(clientId);
		for(CommandLineDto i : commandLinesDto) {
//			
		Inventory providerInventory = findByArticleIdAndCompanyId(i.getArticle(),company.getId());
		System.out.println("after provider inventory . get() inventory service "+i.getArticle()+" "+clientCompany.getId());
		System.out.println("after provider inventory . get() inventory service "+i.getArticle()+" "+company.getId());
		
		System.out.println("efore provider inventory . get() inventory service");
		providerInventory.setOut_quantity(providerInventory.getOut_quantity()+i.getQuantity());
		for(Article a : articles) {
			String articleCost = df.format((a.getCost() + a.getCost() * a.getTva() * a.getMargin()/100) * i.getQuantity());
			articleCost = articleCost.replace(",", ".");
			System.out.println("in for loop provider inventory . get() inventory service");
			if(a.getId().equals(i.getArticle())) {
				providerInventory.setArticleSelling(providerInventory.getArticleSelling() + Double.parseDouble(articleCost));
			}
		
		inventoryRepository.save(providerInventory);

		}
		}
		
	}

	public Inventory findByArticleIdAndCompanyId(Long companyArticleId, Long companyId) {
		Optional<Inventory> inventory = inventoryRepository.findByArticleIdAndCompanyId(companyArticleId, companyId);
		return inventory.get();
	}

	public void impactInvoiceOnClient(Company company, Article article, double parseDouble, double qte) {
		Optional<Inventory> clientInventory = inventoryRepository.findByArticleIdAndCompanyId(article.getId(),company.getId());
		if(clientInventory.isPresent()) {
			Inventory clientInventori = clientInventory.get();
			clientInventori.setIn_quantity(clientInventori.getIn_quantity()+qte);
			clientInventori.setArticleCost(clientInventori.getArticleCost()+parseDouble);
		}else {
			Inventory clientInventori = new Inventory();
			logger.warn("inventory service impact invoice on client else");
		//	Optional<CompanyArticle> companyArt = companyArticleRepository.findByArticleIdAndCompanyId(a.getArticle().getId(), clientCompany.getId());
			clientInventori.setArticle(article);
			clientInventori.setCompany(company);
			clientInventori.setArticleCost(parseDouble);
			clientInventori.setArticleSelling((double)0);
			clientInventori.setIn_quantity(article.getQuantity());
			clientInventori.setOut_quantity((double)0);
			inventoryRepository.save(clientInventori);
				
				
			}
		
	}

}
