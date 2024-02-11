package com.example.meta.store.werehouse.Services;

import java.text.DecimalFormat;
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
import com.example.meta.store.werehouse.Dtos.CashDto;
import com.example.meta.store.werehouse.Dtos.ClientDto;
import com.example.meta.store.werehouse.Dtos.ProviderCompanyDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.ClientCompany;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Invetation;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.ProviderCompanyMapper;
import com.example.meta.store.werehouse.Mappers.ProviderMapper;
import com.example.meta.store.werehouse.Repositories.ClientCompanyRepository;
import com.example.meta.store.werehouse.Repositories.ClientRepository;
import com.example.meta.store.werehouse.Repositories.InvetationRepository;
import com.example.meta.store.werehouse.Repositories.ProviderCompanyRepository;
import com.example.meta.store.werehouse.Repositories.ProviderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProviderService extends BaseService<Provider, Long> {

	private final ProviderRepository providerRepository;
	
	private final ClientRepository clientRepository ;
	
	private final ClientCompanyRepository clientCompanyRepository;
		
	private final InvetationRepository invetationClientProviderRepository;

	private final ProviderCompanyRepository providerCompanyRepository;
	
	private final ProviderMapper providerMapper;
	
	private final ProviderCompanyMapper providerCompanyMapper;

    DecimalFormat df = new DecimalFormat("#.###");
    
	private final Logger logger = LoggerFactory.getLogger(ProviderService.class);

	public List<ProviderDto> getAllMyVirtaul(Company company){
		List<Provider> providers = providerRepository.findAllMyVirtualByCompanyId(company.getId());
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
				provider.setVirtual(true);
				provider.setIsVisible(PrivacySetting.ONLY_ME);
				providerRepository.save(provider);
				ProviderCompany providerCompany = new ProviderCompany();
				providerCompany.setCompany(company);
				providerCompany.setProvider(provider);
				providerCompany.setMvt((double)0);
				providerCompany.setCredit((double)0);
				providerCompanyRepository.save(providerCompany);
				return new ResponseEntity<ProviderDto>(HttpStatus.ACCEPTED);
		}
			throw new RecordIsAlreadyExist("Provider Code Is Already Exist Please Choose Another One");
		
	}
	

	public ProviderDto upDateMyVirtualProviderById( ProviderDto providerDto, Company company) {
		Optional<Provider> provider = providerRepository.findById(providerDto.getId());
		if(provider.isEmpty()) {
			throw new RecordNotFoundException("there is no provider with id: "+providerDto.getId());
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
		provider1.setIsVisible(provider.get().getIsVisible());
		provider1.setVirtual(provider.get().isVirtual());
		providerRepository.save(provider1);
		return  providerDto;
		
	}


	public void addExistProvider(Long id, Company company) {
		Invetation invetationClientProvider = new Invetation();
		ResponseEntity<Provider> provider = super.getById(id);
		logger.warn("add exist provider in provider service ");
		invetationClientProvider.setCompanySender(company);
		invetationClientProvider.setProvider(provider.getBody());
		invetationClientProvider.setStatus(Status.INWAITING);
		invetationClientProviderRepository.save(invetationClientProvider);
	}

	
	public Provider addMeAsProvider(Company company) {
		
		Optional<Provider> provider = providerRepository.findByCode(company.getCode());
		if(provider.isPresent()) {
			throw new RecordIsAlreadyExist("This Provider Code Is Already Exist Please Choose Another One");
		}
		Provider provider1 = new Provider();
		provider1.setCode(company.getCode());
		provider1.setCompany(company);
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
		providerRepository.save(provider1);
		logger.warn("just befor provider company ");
		ProviderCompany providerCompany = new ProviderCompany();
		providerCompany.setCompany(company);
		providerCompany.setProvider(provider1);
		providerCompany.setCredit((double)0);
		logger.warn("just after adding provider1 provider company "+provider1.getId());
		providerCompany.setMvt((double)0);
		logger.warn("just after adding mvt company ");
		providerCompanyRepository.save(providerCompany);
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
		Optional<ProviderCompany> providercompany = providerCompanyRepository.findByProviderIdAndCompanyId(id, myCompany.getId());
		if(providercompany.isEmpty()) {
			throw new RecordNotFoundException("this provider is not yours");
		}
		ProviderCompany providerCompany = providercompany.get();
		if(providerCompany.getMvt() !=0) {
			providerCompany.setDeleted(true);
			return;
		}
		if(providerCompany.getProvider().isVirtual()) {
			super.deleteById(id);
			providerCompanyRepository.deleteByProviderId(id);
			return;
		}
		providerCompanyRepository.deleteByProviderIdAndCompanyId(id,myCompany.getId());
		invetationClientProviderRepository.deleteByProviderIdAndCompanySenderId(id, myCompany.getId());
				
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
	
	public List<ProviderCompanyDto> getAllMyProvider(Company company) {
		logger.warn("in the first line of get all my provider");
		logger.warn("id of company in  get all my provider"+company.getId());
		List<ProviderCompany> providers = providerCompanyRepository.findAllMyProvider(company.getId());
		if(providers == null) {
			throw new RecordNotFoundException("There Is No Provider Yet");
		}
		List<ProviderCompanyDto> providersDto = new ArrayList<>();
		for(ProviderCompany i : providers) {
			logger.warn("in for loop");
			ProviderCompanyDto providerDto = providerCompanyMapper.mapToDto(i);
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
			//throw new RecordNotFoundException("you are not a provider ");
			return Optional.of(new Provider());
		}
		return provider;
	}

	public Long getMeProviderId(Long companyId) {
		Optional<Provider> providerId = providerRepository.findByCompanyIdAndIsVirtual(companyId,false);
		return providerId.get().getId();
	}


	public boolean checkProviderById(Long providerId, Long companyId) {
		logger.warn("check provider in provider service just before the function ");
		return providerRepository.checkProvider(providerId, companyId);
		
	}


	public List<ProviderDto> getAllProvidersContaining(Company company, String search) {
		List<Provider> providers = providerRepository.findAllByNameContainingOrCodeContainingAndCompanyId(search, company.getId());
		List<ProviderDto> providersDto = new ArrayList<>();
		for(Provider i : providers) {
			ProviderDto providerDto = providerMapper.mapToDto(i);
			providersDto.add(providerDto);
		}
		return providersDto;
	}


	public void paymentInpact(Long providerCompanyId, Long myCompanyId, Double amount) {
		Provider providr = getMeAsProvider(providerCompanyId).get();
		ProviderCompany provider = providerCompanyRepository.findByProviderIdAndCompanyId(providr.getId(), myCompanyId).get();
		if(provider.getCredit() > amount) {
			String deff = df.format(provider.getCredit()-amount);
			deff = deff.replace(",", ".");
		provider.setCredit(Double.parseDouble(deff));
		}
		else {
			String deff = df.format(amount-provider.getCredit());
			deff = deff.replace(",", ".");
			provider.setAdvance(Double.parseDouble(deff));
			provider.setCredit((double)0);
		}
	}


	
	// --------------------------------------properly work -----------------------------------------------





	//@Cacheable(value = "provider", key = "#root.methodName")



	//@Cacheable(value = "provider", key = "#root.methodName")











	
}
