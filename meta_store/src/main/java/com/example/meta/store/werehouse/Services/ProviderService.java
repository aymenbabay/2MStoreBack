package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.ArticleController;
import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.ProviderMapper;
import com.example.meta.store.werehouse.Repositories.ClientRepository;
import com.example.meta.store.werehouse.Repositories.InvetationClientProviderRepository;
import com.example.meta.store.werehouse.Repositories.ProviderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProviderService extends BaseService<Provider, Long> {

	private final ProviderRepository providerRepository;
	
	private final ProviderMapper providerMapper;
		
	private final ClientRepository clientRepository ;

	private final InvetationClientProviderRepository invetationClientProviderRepository;
	
	private final Logger logger = LoggerFactory.getLogger(ProviderService.class);

	public List<ProviderDto> getAllMyVirtaul(Company company){
		Long id = clientRepository.findidByCompanyId(company.getId());
		List<Provider> providers = providerRepository.findAllByCompanyId(id);
		if(providers == null) {
			throw new RecordNotFoundException("there is no provider yet");
		}
		List<ProviderDto> dtos = new ArrayList<>();
		for(Provider i : providers) {
			ProviderDto providerDto = providerMapper.mapToDto(i);
			dtos.add(providerDto);
		}
		return dtos;
	}

	
	public ResponseEntity<ProviderDto> insertProvider( ProviderDto providerDto, Company company) {	
		Optional<Provider> provider2 = providerRepository.findByCodeAndCompanyId(providerDto.getCode(), company.getId());
		if( provider2.isEmpty())  {
				Provider provider = providerMapper.mapToEntity(providerDto);
				provider.setCompany(company);
				provider.setVirtual(true);
				provider.setIsVisible(PrivacySetting.ONLY_ME);
				providerRepository.save(provider);
				Optional<Client> client = clientRepository.findByIsVirtualFalseAndCompanyId(company.getId());
				Set<Provider> providers = new HashSet<>();
				providers.addAll(client.get().getProviders());
				providers.add(provider);
				client.get().setProviders(providers);
				return new ResponseEntity<ProviderDto>(HttpStatus.ACCEPTED);
		}
			throw new RecordIsAlreadyExist("Provider Code Is Already Exist Please Choose Another One");
		
	}
	

	public ProviderDto upDateMyVirtualProviderById(Long id, ProviderDto providerDto, Company company) {
		Provider provider = providerRepository.findById(id).get();
		System.out.println("1aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		if(provider == null) {
			throw new RecordNotFoundException("there is no provider with id: "+id);
		}
//		Optional<Provider> provider2 = providerRepository.findByCodeAndCompanyId(providerDto.getCode(), company.getId());
//		System.out.println("2aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//		if(provider2.isPresent() && provider.get().getId() != id) {
//			throw new RecordIsAlreadyExist("this code is already in use "+provider2.get().getCompany().getName());
//		}
//		Optional<Provider> pro = providerRepository.findByBankaccountnumber(providerDto.getBankaccountnumber());
//		System.out.println("3aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//		if(pro.isPresent() && provider.get().getId() != id) {
//			throw new RecordIsAlreadyExist("this account number is related with another provider "+pro.get().getCompany().getName());
//		}
//		Optional<Provider> vpro = providerRepository.findByMatfisc(providerDto.getMatfisc());
//		System.out.println("4aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//		if(vpro.isPresent() && provider.get().getId() != id) {
//			throw new RecordIsAlreadyExist("this matricule fiscl is related by another provider "+vpro.get().getCompany().getName());
//		}
		Provider provider1 = providerMapper.mapToEntity(providerDto);
		provider1.setCompany(company);
		provider1.setIsVisible(provider.getIsVisible());
		provider1.setMvt(provider.getMvt());
		provider1.setCredit(provider.getCredit());
		provider1.setVirtual(provider.isVirtual());
		providerRepository.save(provider1);
		return  providerDto;
		
	}


	public void addExistProvider(Long id, Company company) {
		InvetationClientProvider invetationClientProvider = new InvetationClientProvider();
		ResponseEntity<Provider> provider = super.getById(id);
		logger.warn("add exist provider in provider service ");
		invetationClientProvider.setCompany(company);
		invetationClientProvider.setProvider(provider.getBody());
		invetationClientProvider.setStatus(Status.INWAITING);
		invetationClientProviderRepository.save(invetationClientProvider);
	}

	
	public Provider addMeAsProvider(Company company) {
		
		Optional<Provider> provider = providerRepository.findByCode(company.getCodecp());
		if(provider.isPresent()) {
			throw new RecordIsAlreadyExist("This Provider Code Is Already Exist Please Choose Another One");
		}
		Provider provider1 = new Provider();
		provider1.setCode(company.getCodecp());
		provider1.setCompany(company);
		provider1.setCredit((double)0);
		provider1.setMvt((double)0);
		provider1.setNature("personne Moral");
		provider1.setVirtual(false);
		provider1.setBankaccountnumber(company.getBankaccountnumber());
		provider1.setMatfisc(company.getMatfisc());
		provider1.setName(company.getName());
		provider1.setPhone(company.getPhone());
		provider1.setIndestrySector(company.getIndestrySector());
		provider1.setAddress(company.getAddress());
		provider1.setEmail(company.getEmail());
		provider1.setIsVisible(company.getIsVisible());
		super.insert(provider1);
		return null;
	}
	

	public List<ProviderDto> getVirtualByCompanyId(Long id) {
		List<Provider> provider = providerRepository.findAllByCompanyIdAndIsVirtual(id,true);
		List<ProviderDto> dtos = new ArrayList<>();
		for(Provider i : provider) {
			ProviderDto dto = providerMapper.mapToDto(i);
			dtos.add(dto);
		}
		return dtos;
	}

	
	public void deleteVirtualProviderById(Long id, Company company) {
		Optional<Provider> provider = providerRepository.findById(id);
		if(provider.isEmpty()) {
			throw new RecordNotFoundException("there is no provider with id: "+id);
		}
		if(provider.get().getCompany() != company) {
			throw new RecordNotFoundException("You do not have a permession to do that");
		}
		providerRepository.delete(provider.get());
	}

	public void deleteProviderById(Long id, Company myCompany) {
		Optional<Provider> provider = providerRepository.findById(id);
		Provider hisProvider = provider.get();
		if(provider.isEmpty()) {
			throw new RecordNotFoundException("there is no provider with id: "+id);
		}
		Optional<Client> client = clientRepository.findByIsVirtualFalseAndCompanyId(myCompany.getId());
		Optional<Client> hisClient = clientRepository.findByIsVirtualFalseAndCompanyId(hisProvider.getCompany().getId());
		Optional<Provider> myProvider = getMeAsProvider(myCompany.getId());
		Client myClient = client.get();
		boolean existRelation = false;
		for(Provider i : myClient.getProviders()) {
			if(i == hisProvider) {
				existRelation = true;
			}
		}
		if(!existRelation) {
			throw new RecordNotFoundException("is already not your provider");
		}
		myClient.getProviders().remove(hisProvider);
		invetationClientProviderRepository.deleteByClientOrProviderAndCompany(hisClient.get(), myClient, hisProvider, myProvider.get(), hisProvider.getCompany(), myCompany);
				
	}
	

	public ProviderDto getMyByCodeAndCompanyId(String code, Company company) {
		Optional<Provider> provider = providerRepository.findByCodeAndCompanyId(code,company.getId());
		if(provider.isPresent()) {
			ProviderDto providerDto = providerMapper.mapToDto(provider.get());
			return providerDto;
		}else throw new RecordNotFoundException("There Is No Provider Has Code : "+code);
	}
	
	public ProviderDto getProviderByCode(String code) {
		Optional<Provider> provider = providerRepository.findByCode(code);
		if(provider.isEmpty()) {
			throw new RecordNotFoundException("There Is No Provider With Code: "+code);
		}
			ProviderDto providerDto = providerMapper.mapToDto(provider.get());
			return providerDto;		
	}
	
	public List<ProviderDto> getAllMyProvider(Company company) {
		Optional<Client> client = clientRepository.findByIsVirtualFalseAndCompanyId(company.getId());
		List<Provider> providers = clientRepository.findAllProvider(client.get().getId());
		if(providers == null) {
			throw new RecordNotFoundException("There Is No Provider Yet");
		}
		List<ProviderDto> providersDto = new ArrayList<>();
		for(Provider i : providers) {
			ProviderDto providerDto = providerMapper.mapToDto(i);
			providersDto.add(providerDto);
		}
		return providersDto;
	}
	

	public List<ProviderDto> getAllProviders(){
		List<Provider> providers = providerRepository.findAll();
		if(providers == null) {
			throw new RecordNotFoundException("no provider yet");
		}
		List<ProviderDto> providersDto = new ArrayList<>();
		for(Provider i : providers) {
			ProviderDto dto = providerMapper.mapToDto(i);
			providersDto.add(dto);
		}
		return providersDto;
	}

	public boolean existsById(Long id) {
		boolean existProvider = providerRepository.existsById(id);
		if(!existProvider) {
			throw new RecordNotFoundException("there is no Provider with id "+id);
		}
		return true;
	}


	public Optional<Provider> getMeAsProvider(Long companyId) {
		Optional<Provider> provider = providerRepository.findByCompanyIdAndIsVirtual(companyId,false);
		if(provider.isEmpty()) {
			throw new RecordNotFoundException("you are not a provider ");
		}
		return provider;
	}

	public Long getMeProviderId(Long companyId) {
		Optional<Provider> providerId = providerRepository.findByCompanyIdAndIsVirtual(companyId,false);
		return providerId.get().getId();
	}


	public boolean checkProviderById(Long providerId, Long companyId) {
		logger.warn("check provider in provider service just before the function ");
		return clientRepository.checkProvider(providerId, companyId);
		
	}





//	public Provider getMeProvider(Long id) {
//		Optional<Provider> provider = providerRepository.findByCompanyIdAndIsVirtual(id,false);
//		return provider.get();
//	}




	




	
	// --------------------------------------properly work -----------------------------------------------





	//@Cacheable(value = "provider", key = "#root.methodName")



	//@Cacheable(value = "provider", key = "#root.methodName")











	
}
