package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Mappers.CommandLineMapper;
import com.example.meta.store.werehouse.Repositories.CommandLineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandLineService extends BaseService<CommandLine, Long> {

	
	private final CommandLineMapper commandLineMapper;

	private final InvoiceService invoiceService;
	
	private final ArticleService articleService;
	
	private final InventoryService inventoryService;
	
	private final CommandLineRepository commandLineRepository;
	
	private final Logger logger = LoggerFactory.getLogger(CommandLineService.class);

    DecimalFormat df = new DecimalFormat("#.###");

	public ResponseEntity<InputStreamResource> insertLine(List<CommandLineDto> commandLinesDto, Company company, 
			Long clientId, String type) {
		
		
		List<CommandLine> commandLines = new ArrayList<>();
		List<Article> articles = new ArrayList<>();
		Invoice invoice ;
		if(commandLinesDto.get(0).getId() == null) {
			 invoice = invoiceService.addInvoice(company,clientId);			
		}else {
			 invoice = invoiceService.getById(commandLinesDto.get(0).getInvoice().getId()).getBody();
			 logger.warn("before delete");
			 commandLineRepository.deleteAllByInvoiceId(invoice.getId());
			 logger.warn("after delete");
				
		}
		
		for(CommandLineDto i : commandLinesDto) {
			
			Article article = articleService.findById(i.getArticle().getId());
			articles.add(article);
			if(article.getQuantity()-i.getQuantity()<0) {
				throw new RecordNotFoundException("There Is No More "+article.getLibelle());
			}
		CommandLine commandLine = commandLineMapper.mapToEntity(i);
//		if(i.getId() != null) {
//			CommandLine command = commandLineRepository.findById(i.getId()).get();
//			commandLineRepository.delete(command);
//		}
		commandLine.setInvoice(invoice);
		String prix_article_tot = df.format(i.getQuantity()*article.getCost()*article.getMargin());
		prix_article_tot = prix_article_tot.replace(",", ".");
		commandLine.setPrixArticleTot(Double.parseDouble(prix_article_tot));
		String tot_tva = df.format(article.getTva()*i.getQuantity()*article.getCost()*article.getMargin()/100);
		tot_tva = tot_tva.replace(",", "."); 
		commandLine.setTotTva(Double.parseDouble(tot_tva));
		commandLines.add(commandLine);
		}
		super.insertAll(commandLines);
		List<CommandLine> commandLine = commandLineRepository.findAllByInvoiceId(invoice.getId());
		double totHt= 0;
		double totTva= 0;
		double totTtc= 0;
		for(CommandLine i : commandLine) {
			 totHt += i.getPrixArticleTot();
			 totTva += i.getTotTva();
			 totTtc += totHt+totTva;
			 logger.warn("total ttc "+totTtc);
		}
		logger.warn("total ttc "+totTtc);
		invoice.setPrix_article_tot(totHt);
		invoice.setTot_tva_invoice(totTva);
		invoice.setPrix_invoice_tot(totTtc);
		invoiceService.insert(invoice);
		inventoryService.impacteInvoice(company,commandLinesDto,articles);
		if (type.equals("pdf-save-client") ) {	
			return invoiceService.export(company,commandLines,articles);
		}
		return null;
	}

	public List<CommandLineDto> getCommandLines(Long invoiceId) {
		List<CommandLine> commandLines = commandLineRepository.findAllByInvoiceId(invoiceId);
		List<CommandLineDto> commandLinesDto = new ArrayList<>();
		for(CommandLine i : commandLines) {
			CommandLineDto commandLineDto = commandLineMapper.mapToDto(i);
			commandLinesDto.add(commandLineDto);
		}
		return commandLinesDto;
	}





}
