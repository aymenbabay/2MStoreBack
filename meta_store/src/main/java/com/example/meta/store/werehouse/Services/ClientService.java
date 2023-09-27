package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.NotPermissonException;
import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.InvoiceController;
import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.ClientMapper;
import com.example.meta.store.werehouse.Mappers.ProviderMapper;
import com.example.meta.store.werehouse.Repositories.ClientRepository;
import com.example.meta.store.werehouse.Repositories.InvetationClientProviderRepository;
import com.example.meta.store.werehouse.Repositories.InvoiceRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ClientService extends BaseService<Client, Long>{
	
	private final ClientRepository clientRepository;
		
	private final InvoiceRepository invoiceRepository;
	
	private final InvetationClientProviderRepository invetationClientProviderRepository;
	
	private final ProviderService providerService;
		
	private final ClientMapper clientMapper;

	private final ProviderMapper providerMapper;
	
	private final Logger logger = LoggerFactory.getLogger(ClientService.class);
	
	public Client addMeAsClient(Company company) {
		Optional<Client> client = clientRepository.findByIsVirtualFalseAndCompanyId(company.getId());
		if(client.isPresent()) {
			throw new RecordIsAlreadyExist("You Are Already Client");
		}
		Optional<Client> client1 = clientRepository.findByCodeAndCompanyId(company.getCode(), company.getId());
		
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
		meClient.setIsVisible(company.getIsVisible());
		Optional<Provider> provider = providerService.getMeAsProvider(company.getId());
		Set<Provider> providers = new HashSet<>();
		providers.add(provider.get());
		meClient.setProviders(providers);
		clientRepository.save(meClient);
		return null;
		
	}
	
	public ResponseEntity<ClientDto> insertClient(ClientDto clientDto,Company company) {
		
		Optional<Client> client2 = clientRepository.findByCodeAndCompanyId(clientDto.getCode(), company.getId());
		if( client2.isEmpty())  {
				Client client = clientMapper.mapToEntity(clientDto);
				client.setCompany(company);
				client.setVirtual(true);
				client.setIsVisible(PrivacySetting.ONLY_ME);
				Optional<Provider> provider = providerService.getMeAsProvider(company.getId());
				Set<Provider> providers = new HashSet<>();
				providers.add(provider.get());
				client.setProviders(providers);
				super.insert(client);
				return new ResponseEntity<ClientDto>(HttpStatus.ACCEPTED);
		}else 
			throw new RecordIsAlreadyExist("Client Code Is Already Exist Please Choose Another One");
		
	}
	

	

	public void addExistClient(Long id, Company company) {
		ResponseEntity<Client> client = super.getById(id);
		InvetationClientProvider invetationClientProvider = new InvetationClientProvider();
		invetationClientProvider.setClient(client.getBody());
		invetationClientProvider.setCompany(company);
		invetationClientProvider.setStatus(Status.INWAITING);
		invetationClientProviderRepository.save(invetationClientProvider);
	}




	public List<ClientDto> getAllMyClient(Company company) {
		Long providerId = providerService.getMeProviderId(company.getId());
		List<Client> clients = clientRepository.getAllMyClients(providerId);
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
		Client client = super.getById(id).getBody();
		if(client == null) {
			throw new RecordNotFoundException("Client Not Found");
		}
		
		if( client.getCompany() != null) {
			if(client.getCompany().getId() != company.getId()) {
				throw new NotPermissonException("You Have No Permission");
			}
		
		}
		if(!client.getCreatedBy().equals(company.getUser().getUsername())) {
			throw new NotPermissonException("You Have No Permission");
		}
			Optional<Client> client1 = clientRepository.findByCodeAndCompanyId(clientDto.getCode(),company.getId());
			if(client1.isPresent() && client1.get().getId() != id) {
				throw new RecordIsAlreadyExist("This Client Code Is Already Exist for "+client1.get().getName()+", Please Choose Another One");
			}
			Client client2 = clientMapper.mapToEntity(clientDto);
			client2.setCompany(company);
			client2.setProviders(client.getProviders());
			client2.setIsVisible(client.getIsVisible());
			client2.setVirtual(client.isVirtual());
			client2.setCredit(client.getCredit());
			client2.setMvt(client.getMvt());
			clientRepository.save(client2);
			return clientDto;
		
	}


	public void deleteClientById(Long id, Company company) {
		ResponseEntity<Client> client = super.getById(id);
	        if (client == null || company == null) {
	        	throw new RecordNotFoundException("Client Not Found ");
	        }
	        Client clientt = client.getBody();
	        if(clientt.getCompany() == company) {
	        	
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
	        Optional<Provider> provider = providerService.getMeAsProvider(company.getId());
	        clientt.getProviders().remove(provider.get());
	       
		
	}


	public Optional<Client> getByCompanyId(Long companyId) {
		Optional<Client> client = clientRepository.findByIsVirtualFalseAndCompanyId(companyId);
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


	public Optional<Client> getMeAsClient(Company company) {
		Optional<Client> client = clientRepository.findByIsVirtualFalseAndCompanyId(company.getId());
		if(client.isEmpty()) {
			throw new RecordNotFoundException("you are not a client ");
		}
		return client;
	}

	public List<ClientDto> getAllClient(String var, Provider provider) {
		List<Client> clients = new ArrayList<>();
		if(provider == null) {
			clients = clientRepository.findAllByIsVisibleTrueAndNameContainingOrCodeContaining(var);
		}else {			
			clients = clientRepository.findAllByIsVisibleAndNameContainingOrCodeContaining(var,provider.getCompany().getId(), provider.getId());
		}
		if(clients.isEmpty()) {
			throw new RecordNotFoundException("there is no client containing "+var +" word");
		}
		List<ClientDto> clientsDto = new ArrayList<>();
		for(Client i : clients) {
			ClientDto client = clientMapper.mapToDto(i);
			clientsDto.add(client);
			}		
		return clientsDto;
	}

	public List<ProviderDto> getAllProviderContaining(String var, Client client, Provider provider) {
		logger.warn(var+" "+provider.getCompany().getId());
		List<Provider> providers = clientRepository.findProviderByIsVisibleAndNameAndCodeContaining(var,client.getId(),provider.getId(), provider.getCompany().getId());
		List<ProviderDto> dtos = new ArrayList<>();
		for(Provider i : providers) {
			logger.warn(var+" for loop "+provider.getCompany().getId());
			ProviderDto providerDto = providerMapper.mapToDto(i);
			dtos.add(providerDto);
		}
		return dtos;
	}
	
	
	public Long findCompanyIdByCientId(Long clientId) {
		return clientRepository.findCompanyIdById(clientId);
	}

	public boolean checkClient(Long clientId, Long providerId) {
		Optional<Client> isClient = clientRepository.findById(clientId);
		for(Provider i : isClient.get().getProviders()) {
			if(i.getId() == providerId) {
				return true;
			}
		}
		return false;
	}

		public void acceptedClientInvetation(InvetationClientProvider invetation) {
		Optional<Provider> cProvider = providerService.getMeAsProvider(invetation.getCompany().getId());
		Client client = super.getById(invetation.getClient().getId()).getBody();
		Set<Provider> providers = new HashSet<>();
		providers.add(cProvider.get());
		providers.addAll(client.getProviders());
		client.setProviders(providers);
		logger.warn("providersz size = "+client.getProviders().size());
		clientRepository.save(client);
	}

		public void acceptedProviderInvetation(Provider provider) {
			Optional<Client> cClient = getByCompanyId(provider.getCompany().getId());
			Set<Provider> providers = new HashSet<>();
			providers.add(provider);
			providers.addAll(cClient.get().getProviders());
			cClient.get().setProviders(providers);
			clientRepository.save(cClient.get());
		}

		public List<ClientDto> getAllMyContaining(String search,Client clientt) {
			List<Client> clients = clientRepository.findAllByNameContainingOrCodeContainingAndCompanyId(search,clientt.getCompany().getId(),clientt.getId());
			if(clients.isEmpty()) {
				throw new RecordNotFoundException("there is no client containig "+search +" nedher name ether code");
			}
			List<ClientDto> clientsDto = new ArrayList<>();
			for(Client i : clients) {
				ClientDto client = clientMapper.mapToDto(i);
				clientsDto.add(client);
				}		
			return clientsDto;
		}

		public void deleteClientFromMyList(Long id) {
			// TODO Auto-generated method stub
			
		}

	//------------------------------------------ properly work ----------------------------------------------------


	
	
	
}
