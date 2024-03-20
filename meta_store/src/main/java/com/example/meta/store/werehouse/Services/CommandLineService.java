package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.ClientCompany;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Entities.ProviderCompany;
import com.example.meta.store.werehouse.Mappers.CommandLineMapper;
import com.example.meta.store.werehouse.Repositories.ClientCompanyRepository;
import com.example.meta.store.werehouse.Repositories.CommandLineRepository;
import com.example.meta.store.werehouse.Repositories.ProviderCompanyRepository;

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
	
	private final ClientCompanyRepository clientCompanyRepository;
	
	private final ProviderCompanyRepository providerCompanyRepository;
	
	private final Logger logger = LoggerFactory.getLogger(CommandLineService.class);


	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	public ResponseEntity<InputStreamResource> insertLine(List<CommandLineDto> commandLinesDto, Company company, 
			Long clientId, Double discount, String type) {
		List<CommandLine> commandLines = new ArrayList<>();
		Invoice invoice ;
		if(commandLinesDto.get(0).getId() == null) {
			invoice = invoiceService.addInvoice(company,clientId);	
		}else {
			invoice = invoiceService.getById(commandLinesDto.get(0).getInvoice().getId()).getBody();
			commandLineRepository.deleteAllByInvoiceId(invoice.getId());
		}
		invoice.setDiscount(discount);
		for(CommandLineDto i : commandLinesDto) {
			Article article = articleService.findById(i.getArticle().getId());
			if(article.getQuantity()-i.getQuantity()<0) {
				throw new RecordNotFoundException("There Is No More "+article.getLibelle());
			}
			article.setQuantity(article.getQuantity() - i.getQuantity());
			CommandLine commandLine = commandLineMapper.mapToEntity(i);
			commandLine.setInvoice(invoice);
			double prix_article_tot = round(i.getQuantity()*(article.getCost()+ article.getCost()*article.getMargin()/100));
			commandLine.setPrixArticleTot(prix_article_tot);
			double tot_tva = round(article.getTva()*prix_article_tot/100);
			commandLine.setTotTva(tot_tva);
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
		}
		Double prixArticleTot = round(totHt);
		Double TotTvaInvoice = round(totTva);
		Double prixInvoiceTot = round(totTtc);
		invoice.setPrix_article_tot(prixArticleTot);
		invoice.setTot_tva_invoice(TotTvaInvoice);
		invoice.setPrix_invoice_tot(prixInvoiceTot);
		invoiceService.insert(invoice);
		inventoryService.impacteInvoice(company,commandLines);
		ClientCompany clientCompany = clientCompanyRepository.findByClientIdAndCompanyId(clientId, company.getId()).get();
		//	ProviderCompany providerCompany = providerCompanyRepository.findByProviderIdAndCompanyId(commandLinesDto.get(0).getArticle().getProvider().getId(), clientCompany.getClient().getCompany().getId()).get();
		Double credit = round(clientCompany.getCredit()+invoice.getPrix_invoice_tot());
		Double mvt = round(clientCompany.getMvt()+ invoice.getPrix_invoice_tot());
		clientCompany.setCredit(credit);
		clientCompany.setMvt(mvt);
		if (type.equals("pdf-save-client") ) {	
			return invoiceService.export(company,commandLines);
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
	
	private double round(double value) {
		return Math.round(value * 100.0) / 100.0; 
	}
	




}
