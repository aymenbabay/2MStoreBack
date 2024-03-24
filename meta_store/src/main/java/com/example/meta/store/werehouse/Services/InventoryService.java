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
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Dtos.InventoryDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Inventory;
import com.example.meta.store.werehouse.Entities.Invoice;
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
		

	private final Logger logger = LoggerFactory.getLogger(InventoryService.class);

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
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
	public void addQuantity(Article article, Double quantity, Company company) {
		Optional<Inventory> inventori = inventoryRepository.findByArticleIdAndCompanyId(article.getId(),company.getId());
		Inventory inventory = inventori.get();
		Double articleCost = round((article.getCost() + article.getCost()*article.getTva()/100) *quantity + inventory.getArticleCost());
		inventory.setIn_quantity(inventory.getIn_quantity()+quantity);
		inventory.setArticleCost(articleCost);
		inventoryRepository.save(inventory);
		
	}
	
	public Inventory findByArticleIdAndCompanyId(Long articleId, Long companyId) {
		Optional<Inventory> inventory = inventoryRepository.findByArticleIdAndCompanyId(articleId, companyId);
		return inventory.get();
	}
	
	public void impactInvoiceOnClient(Company company, CommandLine i, Article article) {
		Optional<Inventory> clientInventory = inventoryRepository.findByArticleIdAndCompanyId(article.getId(),company.getId());
		Double articleCost = round((i.getArticle().getCost() + (i.getArticle().getCost()*i.getArticle().getTva()+ i.getArticle().getCost()*i.getArticle().getMargin())/100)*i.getQuantity());
		Inventory clientInventori ;
		if(clientInventory.isPresent()) {
			clientInventori = clientInventory.get();
			clientInventori.setIn_quantity(clientInventori.getIn_quantity()+i.getQuantity());
			Double cumuleArticleCost = round(clientInventori.getArticleCost()+articleCost);
			clientInventori.setArticleCost(cumuleArticleCost);
			if(i.getDiscount() !=null) {				
				Double articleDiscount = round(clientInventori.getDiscountIn()+article.getCost()*i.getDiscount()/100);
				clientInventori.setDiscountIn(articleDiscount);
			}
			if(i.getInvoice().getDiscount() != null) {
				double articleDiscount = round(clientInventori.getDiscountIn()+article.getCost()*i.getInvoice().getDiscount()/100);
				clientInventori.setDiscountIn(articleDiscount);
			}
		}else {
			clientInventori = new Inventory();
			clientInventori.setArticle(article);
			clientInventori.setCompany(company);
			clientInventori.setArticleCost(articleCost);
			clientInventori.setArticleSelling(0.0);
			clientInventori.setOut_quantity(0.0);
			clientInventori.setDiscountIn(0.0);
			clientInventori.setDiscountOut(0.0);
			clientInventori.setIn_quantity(i.getQuantity());
			if(i.getDiscount() !=null) {				
				Double articleDiscount = round(clientInventori.getDiscountIn() +i.getArticle().getCost()*i.getDiscount()/100);
				clientInventori.setDiscountIn(articleDiscount);
			}
			if(i.getInvoice().getDiscount() != null) {
				Double articleDiscount = round(clientInventori.getDiscountIn() +i.getArticle().getCost()*i.getInvoice().getDiscount()/100);
				clientInventori.setDiscountIn(articleDiscount);
			}
		}
		inventoryRepository.save(clientInventori);
	}

	public void rejectInvoice(List<CommandLine> commandLines, Long companyId) {
		for(CommandLine i : commandLines) {
			Optional<Inventory> inventory = inventoryRepository.findByArticleIdAndCompanyId(i.getArticle().getId(),companyId);
			if(inventory.isPresent()) {
				Inventory inventori = inventory.get();
				inventori.setOut_quantity(inventori.getOut_quantity()-i.getQuantity());
				inventoryRepository.save(inventori);
			}
		}
	}
	
	public ResponseEntity<InventoryDto> makeInventory(Article article, Company company){
		Inventory inventory = new Inventory();
		Double articleCost = round((article.getCost() + article.getCost()*article.getTva()/100) *article.getQuantity());
		inventory.setIn_quantity(article.getQuantity());
		inventory.setCompany(company);
		inventory.setOut_quantity(0.0);
		inventory.setArticle(article);
		inventory.setArticleCost(articleCost);
		inventory.setArticleSelling(0.0);
		inventory.setDiscountIn(0.0);
		inventory.setDiscountOut(0.0);
		inventoryRepository.save(inventory);
		return null;
		
	}
	
	public void impacteInvoice( Company company, List<CommandLine> commandLinesDto) {
		for(CommandLine i : commandLinesDto) {
			Inventory providerInventory = findByArticleIdAndCompanyId(i.getArticle().getId(),company.getId());
			providerInventory.setOut_quantity(providerInventory.getOut_quantity()+i.getQuantity());
			if(i.getDiscount() != 0) {
				Double articleDiscount = round(providerInventory.getDiscountOut()+i.getArticle().getCost()*i.getDiscount()/100);
				providerInventory.setDiscountOut(articleDiscount);
			}
			if(i.getInvoice().getDiscount() !=0) {
				Double invoiceDiscount = round(providerInventory.getDiscountOut()+i.getArticle().getCost()*i.getInvoice().getDiscount()/100);
				providerInventory.setDiscountOut(invoiceDiscount);			
			}
			Double articleSelling = round((i.getArticle().getCost() + i.getArticle().getCost() * (i.getArticle().getTva() + i.getArticle().getMargin())/100+providerInventory.getArticleSelling()));
			providerInventory.setArticleSelling(articleSelling);
			inventoryRepository.save(providerInventory);	
		}
	}
	
	private double round(double value) {
		return Math.round(value * 100.0) / 100.0; 
	}
	/////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////
	public void updateArticle(Long companyarticleId, Double deference, Company company) {
		Optional<Inventory> inventori = inventoryRepository.findByArticleIdAndCompanyId(companyarticleId,company.getId());
		Inventory inventory = inventori.get();
		if(deference!=0) {
			inventory.setIn_quantity(inventory.getIn_quantity()-deference);
		}
		inventoryRepository.save(inventory);
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void impactOnSubArticle(Article childArticle, Double childQuantity) {
		Inventory inventory = findByArticleIdAndCompanyId(childArticle.getId(), childArticle.getCompany().getId());
		Double inventoryQuantity = round(inventory.getOut_quantity()+childQuantity);
		inventory.setOut_quantity(inventoryQuantity);
		Double inventorySelling = round(inventory.getArticleSelling()+ (childArticle.getCost() + childArticle.getCost()*(childArticle.getMargin() + childArticle.getTva())/100 )*childQuantity);
		inventory.setArticleSelling(inventorySelling);
		
	}



}
