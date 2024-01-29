package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
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
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.InvoiceController;
import com.example.meta.store.werehouse.Dtos.CashDto;
import com.example.meta.store.werehouse.Dtos.ClientCompanyDto;
import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Dtos.InvoiceDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Cash;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.ClientCompany;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.example.meta.store.werehouse.Entities.PassingClient;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;
import com.example.meta.store.werehouse.Enums.Nature;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.ClientCompanyMapper;
import com.example.meta.store.werehouse.Mappers.ClientMapper;
import com.example.meta.store.werehouse.Mappers.ProviderMapper;
import com.example.meta.store.werehouse.Repositories.ClientCompanyRepository;
import com.example.meta.store.werehouse.Repositories.ClientRepository;
import com.example.meta.store.werehouse.Repositories.InvetationClientProviderRepository;
import com.example.meta.store.werehouse.Repositories.InvoiceRepository;
import com.example.meta.store.werehouse.Repositories.PassingClientRepository;
import com.example.meta.store.werehouse.Repositories.ProviderCompanyRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ClientService extends BaseService<Client, Long>{
	
	private final ClientRepository clientRepository;
			
	private final PassingClientRepository passingClientRepository;
	
	private final InvetationClientProviderRepository invetationClientProviderRepository;
	
	private final ProviderService providerService;
		
	private final ClientMapper clientMapper;

	private final ClientCompanyMapper clientCompanyMapper;
	
	private final ClientCompanyRepository clientCompanyRepository;
	
	private final ProviderCompanyRepository providerCompanyRepository;	
	
	private final InvoiceRepository invoiceRepository;

    DecimalFormat df = new DecimalFormat("#.###");
    
	private final Logger logger = LoggerFactory.getLogger(ClientService.class);
	
	public Client addMeAsClient(Company company) {
		Optional<Client> client = clientRepository.getByCompanyIdAndIsVirtualFalse(company.getId());
		if(client.isPresent()) {
			throw new RecordIsAlreadyExist("You Are Already Client");
		}
		Optional<Client> client1 = clientRepository.findByCodeAndCompanyId(company.getCode(), company.getId());
		
		if(client1.isPresent()) {
			throw new RecordIsAlreadyExist("This Code is already found Please Try another Code");
		}
		logger.warn("just before mapping to client ");
		Client meClient = clientMapper.mapCompanyToClient(company);
		logger.warn("just after  mapping to client ");
		meClient.setNature("personne Moral");
		meClient.setCompany(company);
		meClient.setVirtual(false);
		meClient.setEmail(company.getEmail());
		meClient.setIsVisible(company.getIsVisible());
		logger.warn("just before client repository save new client ");
		clientRepository.save(meClient);
		ClientCompany clientCompany = new ClientCompany();
		logger.warn("just after adding new clientCompany id =>  "+meClient.getId());
		clientCompany.setClient(meClient);
		logger.warn("just before setting client ");
		clientCompany.setMvt((double)0);
		clientCompany.setCredit((double)0);
		clientCompany.setCompany(company);
		logger.warn("just after clientCompany repository save new clientCompany id company "+company.getId());
		clientCompanyRepository.save(clientCompany);
		return null;
		
	}
	
	public ResponseEntity<ClientDto> insertClient(ClientDto clientDto,Company company) {
		
		Optional<Client> client2 = clientRepository.findByCodeAndCompanyId(clientDto.getCode(), company.getId());
		if( client2.isPresent())  {
			throw new RecordIsAlreadyExist("Client Code Is Already Exist Please Choose Another One");
		}
				Client client = clientMapper.mapToEntity(clientDto);
				client.setVirtual(true);
				client.setIsVisible(PrivacySetting.ONLY_ME);
				client.setCompany(company);
				super.insert(client);
				ClientCompany clientCompany = new ClientCompany();
				clientCompany.setClient(client);
				clientCompany.setCompany(company);
				clientCompany.setMvt((double)0);
				clientCompany.setCredit((double)0);
				clientCompanyRepository.save(clientCompany);
				return new ResponseEntity<ClientDto>(HttpStatus.ACCEPTED);
	}
	

	

	public void addExistClient(Long id, Company company) {
		ResponseEntity<Client> client = super.getById(id);
		if(client.getBody().isVirtual()) {
			Optional<ClientCompany> clientCompany = clientCompanyRepository.findByClientIdAndCompanyId(id, company.getId());
			clientCompany.get().setDeleted(false);
			return;
		}
		InvetationClientProvider invetationClientCompany = new InvetationClientProvider();
		invetationClientCompany.setClient(client.getBody());
		invetationClientCompany.setCompany(company);
		invetationClientCompany.setStatus(Status.INWAITING);
		invetationClientProviderRepository.save(invetationClientCompany);
	}




	public List<ClientCompanyDto> getAllMyClient(Client client) {
		logger.warn("client id => "+client.getId()+" company id =>"+client.getCompany().getId());
		List<ClientCompany> clients = clientCompanyRepository.getAllMyClients(client.getCompany().getId());
		if(clients == null) {
			throw new RecordNotFoundException("There Is No Client Yet");
		}
		List<ClientCompanyDto> clientsDto = new ArrayList<>();
		for(ClientCompany i : clients) {
			logger.warn("client id ="+i.getId()+" company id = "+i.getCompany().getId());
			ClientCompanyDto clientDto = clientCompanyMapper.mapToDto(i);
			clientsDto.add(clientDto);
		}
		return clientsDto;
	}



//maybe there is a problem because of mapping
	public ClientDto upDateMyClientById( ClientDto clientDto, Company company) {
		Client client = super.getById(clientDto.getId()).getBody();
		if(client == null) {
			throw new RecordNotFoundException("Client Not Found");
		}
		
		if( client.getCompany() != null) {
			if(client.getCompany() != company) {
				throw new NotPermissonException("You Have No Permission");
			}
		
		}
		if(!client.getCreatedBy().equals(company.getUser().getUsername())) {
			throw new NotPermissonException("You Have No Permission");
		}
			Optional<Client> client1 = clientRepository.findByCodeAndCompanyId(clientDto.getCode(),company.getId());
			if(client1.isPresent() && client1.get().getId() != clientDto.getId()) {
				throw new RecordIsAlreadyExist("This Client Code Is Already Exist for "+client1.get().getName()+", Please Choose Another One");
			}
			Client client2 = clientMapper.mapToEntity(clientDto);
			client2.setIsVisible(client.getIsVisible());
			client2.setVirtual(client.isVirtual());
			clientRepository.save(client2);
			return clientDto;
		
	}


	public void deleteClientById(Long id, Company company) {
		Optional<ClientCompany> clientcompany = clientCompanyRepository.findByClientIdAndCompanyId(id, company.getId());
	        if (clientcompany.isEmpty()) {
	        	throw new RecordNotFoundException("Client Not Found ");
	        }
	        ClientCompany clientCompany = clientcompany.get();
	        if(clientCompany.getMvt() != 0) {
	        	clientCompany.setDeleted(true);
	        	return;
	        } 
	        if(clientCompany.getClient().isVirtual()) {
        		super.deleteById(id);
        		clientCompanyRepository.deleteByClientId(id);	
        		return;
        	}
	        clientCompanyRepository.deleteByClientIdAndCompanyId(id, company.getId());
	        invetationClientProviderRepository.deleteByClientIdAndCompanyId(id, company.getId());
	       
		
	}



//maybe unuse
	public List<ClientDto> getAllPermissionClientt() {
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

//maybe unuse
	public Optional<Client> getMeAsClient(Company company) {
		Optional<Client> client = clientRepository.getByCompanyIdAndIsVirtualFalse(company.getId());
		return client;
	}

	public List<ClientDto> getAllClientContaining(String var, Company company) {
		List<Client> clients = new ArrayList<>();
		if(company == null) {
			clients = clientRepository.findAllByIsVisibleTrueAndNameContainingOrCodeContaining(var);
		}else {			
			clients = clientRepository.findAllByIsVisibleAndNameContainingOrCodeContaining(var,company.getId());
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
	

	public boolean checkClient(Long clientId, Long companyId) {
		boolean exists = clientCompanyRepository.existsByClientIdAndCompanyIdAndIsDeletedFalse(clientId,companyId);
		return exists;
	}

	
		public void acceptedInvetation(InvetationClientProvider invetation) {
			ClientCompany clientCompany = new ClientCompany();
			ProviderCompany providerCompany = new ProviderCompany();
			boolean existClient = false;
			boolean existProvider = false;
			if(invetation.getClient() != null) {	
				clientCompany.setCompany(invetation.getCompany());
				clientCompany.setClient(invetation.getClient());
				Optional<Provider> provider = providerService.getMeAsProvider(invetation.getCompany().getId());
				existProvider = providerCompanyRepository.existsByProviderIdAndCompanyId(provider.get().getId(), invetation.getClient().getCompany().getId());
				if(!existProvider) {
					providerCompany.setCompany(invetation.getClient().getCompany());
					providerCompany.setProvider(provider.get());					
				}
			}
			else {
					providerCompany.setProvider(invetation.getProvider());
					providerCompany.setCompany(invetation.getCompany());
					Optional<Client> client = getMeAsClient(invetation.getCompany());
					existClient = clientCompanyRepository.existsByClientIdAndCompanyId(client.get().getId(), invetation.getProvider().getCompany().getId());
					if(!existClient) {						
						clientCompany.setCompany(invetation.getProvider().getCompany());
						clientCompany.setClient(client.get());
					}
			}
			if(!existClient) {
			clientCompany.setMvt((double)0);
			clientCompany.setCredit((double)0);
			clientCompanyRepository.save(clientCompany);
			}
			if(!existProvider) {				
			providerCompany.setMvt((double)0);
			providerCompany.setCredit((double)0);
			providerCompanyRepository.save(providerCompany);
			}
			invetation.setStatus(Status.ACCEPTED);
	}


		public List<ClientDto> getAllMyContaining(String search,Client clientt) {
			List<Client> clients = clientRepository.findAllByNameContainingOrCodeContainingAndCompanyId(search,clientt.getCompany().getId());
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

		public PassingClient findPassingClientBUser(User user) {
			Optional<PassingClient> client = passingClientRepository.findByUserId(user.getId());
			if(client.isEmpty()) {
				client = addMeAsClientByUser(user);
			}
			return client.get();
		}

		public Optional<PassingClient> addMeAsClientByUser(User user) {
			PassingClient client = new PassingClient();
			client.setUser(user);
			client.setNature(Nature.INDIVIDUAL);
			passingClientRepository.save(client);
			return Optional.of(client);
		}

		public void paymentInpact(Long clientId, Long companyId, Double amount, Invoice invoice) {
			ClientCompany client = clientCompanyRepository.findByClientIdAndCompanyId(clientId, companyId).get();
			if(client.getCredit() > amount) {				
			client.setCredit(client.getCredit()-amount);
			}
			else {
				logger.warn(" client credit =>"+client.getCredit()+" ,amount=> "+amount);
				String deff = df.format(client.getAdvance() + amount-client.getCredit());
				deff = deff.replace(",", ".");
				client.setAdvance( Double.parseDouble(deff));
				client.setCredit((double)0);
				Invoice invoicee = invoiceRepository.findById(invoice.getId()).get();
				invoicee.setPaid(true);
				invoiceRepository.save(invoicee);
			}
		}

	

	//------------------------------------------ properly work ----------------------------------------------------


	
	
	
}
