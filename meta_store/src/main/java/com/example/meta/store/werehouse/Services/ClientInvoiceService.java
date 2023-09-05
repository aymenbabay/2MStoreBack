package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.ClientInvoiceDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.ClientInvoice;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Enums.InvoiceStatus;
import com.example.meta.store.werehouse.Mappers.ClientInvoiceMapper;
import com.example.meta.store.werehouse.Repositories.ClientInvoiceRepository;
import com.example.meta.store.werehouse.Repositories.CommandLineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientInvoiceService extends BaseService<ClientInvoice, Long>{
	
	private final ClientInvoiceRepository clientInvoiceRepository;
	
	private final ClientInvoiceMapper clientInvoiceMapper;
	
	private final ClientService clientService;
	
	private final ProviderService providerService;
	
	private final ArticleService articleService;
	
	private final CommandLineRepository commandLineRepository;
	
	
	public void addClientInvoiceService(Long clientId, Long providerCompanyId, Invoice invoice) {
	
		ResponseEntity<Client> client = clientService.getById(clientId);
		Provider provider = providerService.getMeAsProvider(providerCompanyId);
		ClientInvoice clientInvoice = new ClientInvoice();
		clientInvoice.setIsAccepted(InvoiceStatus.INWAITING);
		clientInvoice.setClient(client.getBody());
		clientInvoice.setProvider(provider);
		clientInvoice.setInvoice(invoice);
		clientInvoiceRepository.save(clientInvoice);
		
	}


	public List<ClientInvoiceDto> getInvoiceNotifications(Client client, Provider provider) {
		List<ClientInvoice> clientInvoices = clientInvoiceRepository.findAllByClientIdOrProviderId(client.getId(), provider.getId());
		if(clientInvoices.isEmpty()) {
			throw new RecordNotFoundException("there is no invoice not accepted");
		}
		List<ClientInvoiceDto> clientInvoicesDto = new ArrayList<>();
		for(ClientInvoice i : clientInvoices) {
			ClientInvoiceDto clientInvoiceDto = clientInvoiceMapper.mapToDto(i);
			clientInvoicesDto.add(clientInvoiceDto);
		}
		return clientInvoicesDto;
	}


	public void accepted(Long invoiceCode, Long clientId) {
		Optional<ClientInvoice> ci = clientInvoiceRepository.findByInvoiceCode(invoiceCode,clientId);
		ClientInvoice clientInvoice = ci.get();
		List<CommandLine> commandLines = commandLineRepository.findByInvoiceId(clientInvoice.getInvoice().getId());
		articleService.impactInvoice(commandLines, clientId);
		clientInvoice.setIsAccepted(InvoiceStatus.ACCEPTED);
		clientInvoiceRepository.save(clientInvoice);
	}


	public void refused(Long invoiceCode, Long clientId) {
		Optional<ClientInvoice> ci = clientInvoiceRepository.findByInvoiceCode(invoiceCode,clientId);
		ClientInvoice clientInvoice = ci.get();
		clientInvoice.setIsAccepted(InvoiceStatus.REFUSED);
		clientInvoiceRepository.save(clientInvoice);
	}

	
}
