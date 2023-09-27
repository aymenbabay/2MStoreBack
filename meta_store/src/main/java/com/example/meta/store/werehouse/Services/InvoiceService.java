package com.example.meta.store.werehouse.Services;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.InvoiceReturnDto;
import com.example.meta.store.werehouse.Controllers.ArticleController;
import com.example.meta.store.werehouse.Dtos.InvoiceDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.ClientInvoiceMapper;
import com.example.meta.store.werehouse.Mappers.InvoiceMapper;
import com.example.meta.store.werehouse.Repositories.CommandLineRepository;
import com.example.meta.store.werehouse.Repositories.InvoiceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InvoiceService extends BaseService<Invoice, Long>{


	private final InvoiceMapper invoiceMapper;
	
	private final InvoiceRepository invoiceRepository;
	
	private final ClientService clientService;
	
	private final CommandLineRepository commandLineRepository;
	
	private final ArticleService articleService;
	
	private final ClientInvoiceMapper clientInvoiceMapper;
	
	private final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
	
	public List<InvoiceDto> getMyInvoiceAsProvider(Long companyId) {
		List<Invoice> invoices =  invoiceRepository.findAllByCompanyId(companyId);
		List<InvoiceDto> invoicesDto = new ArrayList<>();
		for(Invoice i : invoices) {
			InvoiceDto invoiceDto = invoiceMapper.mapToDto(i);
			invoicesDto.add(invoiceDto);
		}
		System.out.println("invoice service before return invoices dto get my invoice as provider "+invoicesDto.get(0).getClient());
		return invoicesDto;
	}
	
	public List<InvoiceDto> getInvoicesAsClient(Company company) {
		Client client = clientService.getMeAsClient(company).get();
		List<Invoice> invoices = invoiceRepository.findAllByClientId(client.getId());
		List<InvoiceDto> invoicesDto = new ArrayList<>();
		for(Invoice i : invoices) {
			InvoiceDto invoiceDto = invoiceMapper.mapToDto(i);
			invoicesDto.add(invoiceDto);
		}
		return invoicesDto;
	}

	public Long getLastInvoice(Long companyId) {
		Optional<Invoice> invoice = invoiceRepository.lastInvoice(companyId);
		if(invoice.isEmpty()) {
			return  (long) 20230001;
		}
		return (long) (invoice.get().getCode()+1);
	}


	public ResponseEntity<InputStreamResource> export(Company company, List<CommandLine> commandLines,
			List<Article> articles) {
		

		Optional<Invoice> invoice = invoiceRepository.lastInvoice(company.getId());
		ByteArrayInputStream bais = ExportInvoicePdf.invoicePdf(commandLines,invoice.get(),company,articles);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=invoice.pdf");
		 ResponseEntity<InputStreamResource> response = ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bais));


		    return response;
	}


	public Invoice addInvoice(Company company, Long clientId) {
		Long invoiceCode = getLastInvoice(company.getId());
		ResponseEntity<Client> client = clientService.getById(clientId);
		System.out.println(client.getBody().getId()+" client id");
		Invoice invoice = new Invoice();
		invoice.setCode(invoiceCode);
		invoice.setClient(client.getBody());
		invoice.setCompany(company);
		invoice.setStatus(Status.INWAITING);
		invoiceRepository.save(invoice);
		return invoice;
	}

////////////////////////// client service method ////

	public List<InvoiceDto> getInvoiceNotifications(Client client) {
		logger.warn("client id "+client.getId()+" company id "+client.getCompany().getId());
		List<Invoice> invoices = invoiceRepository.findAllByClientIdOrCompanyId(client.getId(), client.getCompany().getId());
		if(invoices.isEmpty()) {
			throw new RecordNotFoundException("there is no invoice not accepted");
		}
		List<InvoiceDto> invoicesDto = new ArrayList<>();
		for(Invoice i : invoices) {
			InvoiceDto invoiceDto = invoiceMapper.mapToDto(i);
			InvoiceReturnDto clientInvoice = clientInvoiceMapper.mapClientToClientInvoice(i.getClient());
			InvoiceReturnDto companyInvoice = clientInvoiceMapper.mapCompanyToClientInvoice(i.getCompany());
			invoiceDto.setClient(clientInvoice);
			invoiceDto.setCompany(companyInvoice);
			invoicesDto.add(invoiceDto);
		}
		return invoicesDto;
	}
	

	public void accepted(Long code, Long clientId) {
		Invoice invoice = getInvoice(code,clientId);
		List<CommandLine> commandLines = commandLineRepository.findAllByInvoiceId(invoice.getId());
		articleService.impactInvoice(commandLines, clientId);
		invoice.setStatus(Status.ACCEPTED);
		invoiceRepository.save(invoice);
	}

	public void refused(Long code, Long clientId) {
		Invoice invoice = getInvoice(code,clientId);
		invoice.setStatus(Status.REFUSED);
		invoiceRepository.save(invoice);
	}

	private Invoice getInvoice(Long code, Long clientId) {
		Optional<Invoice> invoice = invoiceRepository.findByCodeAndClientId(code,clientId);
	return invoice.get();
	}

	public void cancelInvoice(Company company, Long id) {
		Invoice invoice = super.getById(id).getBody();
		if(company.getId() != invoice.getCompany().getId()) {
			throw new PermissionDeniedDataAccessException("you dont have permission", null);
		}
		List<CommandLine> commandLines = commandLineRepository.findAllByInvoiceId(invoice.getId());
		
		for(CommandLine i : commandLines) {
			Article article = articleService.findById(i.getArticle().getId());
			article.setQuantity(article.getQuantity()+i.getQuantity());
			commandLineRepository.delete(i);
		}
		invoiceRepository.delete(invoice);
		
	}
	



	

}
