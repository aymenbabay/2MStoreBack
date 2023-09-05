package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.NotPermissonException;
import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Mappers.ClientMapper;
import com.example.meta.store.werehouse.Mappers.ProviderMapper;
import com.example.meta.store.werehouse.Repositories.ClientRepository;
import com.example.meta.store.werehouse.Repositories.InvoiceRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ClientService extends BaseService<Client, Long>{
	
	private final ClientRepository clientRepository;
		
	private final ClientMapper clientMapper;
	
	private final ProviderService providerService;
		
	private final InvoiceRepository invoiceRepository;
	
	public Client addMeAsClient(Company company) {
		Optional<Client> client = clientRepository.findByCompanyId(company.getId());
		if(client.isPresent()) {
			throw new RecordIsAlreadyExist("You Are Already Client");
		}
		Optional<Client> client1 = clientRepository.findByCode(company.getCode());
		if(client1.isPresent()) {
			throw new RecordIsAlreadyExist("This Code is already found Please Try another Code");
		}
		Client meClient = clientMapper.mapCompanyToClient(company);
		meClient.setCredit((double)0);
		meClient.setMvt((double)0);
		meClient.setNature("personne Moral");
		meClient.setCompany(company);
		meClient.setVirtual(false);
		meClient.setEmail(company.getEmail());
		Provider provider = providerService.getMeAsProvider(company.getId());
		Set<Provider> providers = new HashSet<>();
		providers.add(provider);
		meClient.setProviders(providers);
		clientRepository.save(meClient);
		return null;
		
	}
	
	public ResponseEntity<ClientDto> insertClient(ClientDto clientDto,Company company) {
		
		Optional<Client> client2 = clientRepository.findByCode(clientDto.getCode());
		if( client2.isEmpty())  {
				Client client = clientMapper.mapToEntity(clientDto);
				client.setCompany(company);
				client.setVirtual(true);
				Provider provider = providerService.getMeAsProvider(company.getId());
				Set<Provider> providers = new HashSet<>();
				providers.add(provider);
				client.setProviders(providers);
				super.insert(client);
				return new ResponseEntity<ClientDto>(HttpStatus.ACCEPTED);
		}else 
			throw new RecordIsAlreadyExist("Client Code Is Already Exist Please Choose Another One");
		
	}
	

	

	public ResponseEntity<String> addExistClient(Long id, Company company) {
		ResponseEntity<Client> client = super.getById(id);
		if(client == null) {
			throw new RecordNotFoundException("This Client Is Not Exist Please Create it For You");
		} 
			Provider provider = providerService.getMeAsProvider(company.getId());
			Set<Provider> providers = new HashSet();
			providers.add(provider);
			providers.addAll(client.getBody().getProviders());
			client.getBody().setProviders(providers);
			clientRepository.save(client.getBody());
			return null;
		
	}




	public List<ClientDto> getAllMyClient(Company company) {
		Long providerId = providerService.getMeProviderId(company.getId());
		List<Client> clients = clientRepository.getAllClients(providerId);
		if(clients == null) {
			throw new RecordNotFoundException("There Is No Client Yet");
		}
		List<ClientDto> clientsDto = new ArrayList<>();
		for(Client i : clients) {
			ClientDto clientDto = clientMapper.mapToDto(i);
			clientsDto.add(clientDto);
		}
		return clientsDto;
	}




	public ClientDto upDateMyClientById(Long id, ClientDto clientDto, Company company) {
		ResponseEntity<Client> client = super.getById(id);
		if(client == null) {
			throw new RecordNotFoundException("Client Not Found");
		}
		
		if( client.getBody().getCompany() != null) {
			if(client.getBody().getCompany().getId() != company.getId()) {
				throw new NotPermissonException("You Have No Permission");
			}
		
		}
		if(!client.getBody().getCreatedBy().equals(company.getUser().getUsername())) {
			System.out.println(company.getUser().getUsername());
			throw new NotPermissonException("You Have No Permission");
		}
			Optional<Client> client1 = clientRepository.findByCode(clientDto.getCode());
			if(client1.isPresent() && client1.get().getId() != id) {
				throw new RecordIsAlreadyExist("This Client Code Is Already Exist Please Choose Another One");
			}
			Client client3 = clientMapper.mapToEntity(clientDto);
			client3.setCompany(company);
			client3.setProviders(client.getBody().getProviders());
			super.insert(client3);
			return clientDto;
		
	}


	public void deleteClientById(Long id, Company company) {
		ResponseEntity<Client> client = super.getById(id);
	        if (client == null || company == null) {
	        	throw new RecordNotFoundException("Client Not Found ");
	        }
	        Client clientt = client.getBody();
	        if(clientt.getCompany().getId() == company.getId()) {
	        	
	        if(clientt.isVirtual() == false) {
	        	throw new NotPermissonException("you can not delete this client :) ");
	        }
	        	boolean isExist = InvoiceRepository.existsByClientId(clientt.getId());
	        	if(isExist) {
	        		clientt.setProviders(null);
	        		clientRepository.save(clientt);
	        		return;
	        	}
	        	super.deleteById(id);
	        	return;
	        
	        }
	        Provider provider = providerService.getMeAsProvider(clientt.getCompany().getId());
	        clientt.getProviders().remove(provider);
	       
		
	}


	public Optional<Client> getByCompanyId(Long companyId) {
		Optional<Client> client = clientRepository.findByCompanyId(companyId);
		return client;
	}



	public List<ClientDto> getAllPermissionClient() {
		List<Client> clients = clientRepository.findAllHasCompanyId();
		if(clients == null) {
			throw new RecordNotFoundException("There Is No Client Yet");
		}
		List<ClientDto> clientsDto = new ArrayList<>();
		for(Client i : clients) {
			ClientDto clientDto = clientMapper.mapToDto(i);
			clientsDto.add(clientDto);
		}
		return clientsDto;
	}


	public Client getMeAsClient(Company company) {
		Optional<Client> client = clientRepository.findByCompanyId(company.getId());
		if(client.isEmpty()) {
			throw new RecordNotFoundException("you are not a client ");
		}
		return client.get();
	}

	public List<ClientDto> getAllClient(Company company) {
		List<Client> clients = clientRepository.findAllClient();
		List<ClientDto> clientsDto = new ArrayList<>();
		for(Client i : clients) {
			ClientDto client = clientMapper.mapToDto(i);
			clientsDto.add(client);
			}
		return clientsDto;
	}

	public Long findCompanyIdByCientId(Long clientId) {
		return clientRepository.findCompanyIdById(clientId);
	}

	//------------------------------------------ properly work ----------------------------------------------------


	
	
	
}
