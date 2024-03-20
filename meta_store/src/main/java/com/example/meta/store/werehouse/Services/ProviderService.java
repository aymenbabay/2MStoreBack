package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.ProviderCompanyDto;
import com.example.meta.store.werehouse.Dtos.ProviderDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Invetation;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;
import com.example.meta.store.werehouse.Enums.PrivacySetting;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Enums.Type;
import com.example.meta.store.werehouse.Mappers.ProviderCompanyMapper;
import com.example.meta.store.werehouse.Mappers.ProviderMapper;
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
		
	private final InvetationRepository invetationClientProviderRepository;

	private final ProviderCompanyRepository providerCompanyRepository;
	
	private final ProviderMapper providerMapper;
	
	private final ProviderCompanyMapper providerCompanyMapper;
    
	private final Logger logger = LoggerFactory.getLogger(ProviderService.class);

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	
	public void deleteProviderById(Long id, Company myCompany) {
		ProviderCompany providerCompany = providerCompanyRepository.findByProviderIdAndCompanyId(id, myCompany.getId()).orElseThrow(() -> new RecordNotFoundException("this provider is already not yours"));
		if(providerCompany.getMvt() !=0) {
			providerCompany.setDeleted(true);
			return;
		}
		if(providerCompany.getProvider().isVirtual()) {
			deleteVirtualProviderById(id, myCompany);
			return;
		}
		providerCompanyRepository.deleteByProviderIdAndCompanyId(id,myCompany.getId());
		invetationClientProviderRepository.deleteByProviderIdAndCompanySenderId(id, myCompany.getId());
		
	}

	public List<ProviderCompanyDto> getAllMyProvider(Company company) {
		List<ProviderCompany> providers = providerCompanyRepository.findAllMyProvider(company.getId());
		if(providers == null) {
			throw new RecordNotFoundException("There Is No Provider Yet");
		}
		List<ProviderCompanyDto> providersDto = new ArrayList<>();
		for(ProviderCompany i : providers) {
			ProviderCompanyDto providerDto = providerCompanyMapper.mapToDto(i);
			providersDto.add(providerDto);
		}
		return providersDto;
	}
	
	public List<ProviderCompanyDto> getAllProvidersContaining(Company company, String search) {
		List<ProviderCompany> providers = providerCompanyRepository.findAllByNameContainingOrCodeContainingAndCompanyId(search, company.getId());
		List<ProviderCompanyDto> providersDto = new ArrayList<>();
		for(ProviderCompany i : providers) {
			ProviderCompanyDto providerDto = providerCompanyMapper.mapToDto(i);
			providersDto.add(providerDto);
		}
		return providersDto;
	}

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
				providerCompany.setMvt(0.0);
				providerCompany.setCredit(0.0);
				providerCompanyRepository.save(providerCompany);
				return new ResponseEntity<ProviderDto>(HttpStatus.ACCEPTED);
		}
			throw new RecordIsAlreadyExist("Provider Code Is Already Exist Please Choose Another One");
		
	}
	

	public ProviderDto upDateMyVirtualProviderById( ProviderDto providerDto, Company company) {
		Provider provider = providerRepository.findById(providerDto.getId())
				.orElseThrow(() -> new RecordNotFoundException("there is no provider with id: "+providerDto.getId()));	
		Provider provider1 = providerMapper.mapToEntity(providerDto);
		provider.setCompany(company);
		provider.setIsVisible(provider1.getIsVisible());
		provider.setVirtual(provider1.isVirtual());
		providerRepository.save(provider);
		return  providerDto;
		
	}

	public void addExistProvider(Long id, Company company) {
		Invetation invetationClientProvider = new Invetation();
		ResponseEntity<Provider> provider = super.getById(id);
		invetationClientProvider.setCompanySender(company);
		invetationClientProvider.setProvider(provider.getBody());
		invetationClientProvider.setStatus(Status.INWAITING);
		invetationClientProvider.setType(Type.PROVIDER);
		invetationClientProviderRepository.save(invetationClientProvider);
	}

	public Provider addMeAsProvider(Company company) {		
		Optional<Provider> provide = providerRepository.findByCode(company.getCode());
		if(provide.isPresent()) {
			throw new RecordIsAlreadyExist("This Provider Code Is Already Exist Please Choose Another One");
		}
		Provider provider = new Provider();
		provider.setCode(company.getCode());
		provider.setCompany(company);
		provider.setNature("personne Moral");
		provider.setVirtual(false);
		provider.setBankaccountnumber(company.getBankaccountnumber());
		provider.setMatfisc(company.getMatfisc());
		provider.setName(company.getName());
		provider.setPhone(company.getPhone());
		provider.setIndestrySector(company.getIndestrySector());
		provider.setAddress(company.getAddress());
		provider.setEmail(company.getEmail());
		provider.setIsVisible(company.getIsVisible());
		providerRepository.save(provider);
		ProviderCompany providerCompany = new ProviderCompany();
		providerCompany.setCompany(company);
		providerCompany.setProvider(provider);
		providerCompany.setCredit(0.0);
		providerCompany.setAdvance(0.0);
		providerCompany.setMvt(0.0);
		providerCompanyRepository.save(providerCompany);
		return null;
	}	

	private void deleteVirtualProviderById(Long id, Company company) {
		Optional<Provider> provider = providerRepository.findById(id);
		if(provider.isEmpty()) {
			throw new RecordNotFoundException("there is no provider with id: "+id);
		}
		if(provider.get().getCompany() != company) {
			throw new RecordNotFoundException("You do not have a permession to do that");
		}
		providerRepository.delete(provider.get());
	}
	
	public ProviderDto getMyByCodeAndCompanyId(String code, Company company) {
		Optional<Provider> provider = providerRepository.findByCodeAndCompanyId(code,company.getId());
		if(provider.isPresent()) {
			ProviderDto providerDto = providerMapper.mapToDto(provider.get());
			return providerDto;
		} throw new RecordNotFoundException("There Is No Provider Has Code : "+code);
	}
	
	public ProviderDto getProviderByCode(String code) {
		Optional<Provider> provider = providerRepository.findByCode(code);
		if(provider.isEmpty()) {
			throw new RecordNotFoundException("There Is No Provider With Code: "+code);
		}
		ProviderDto providerDto = providerMapper.mapToDto(provider.get());
		return providerDto;		
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
	
	public Optional<Provider> getMeAsProvider(Long companyId) {
		Optional<Provider> provider = providerRepository.findByCompanyIdAndIsVirtual(companyId,false);
		if(provider.isEmpty()) {
			return Optional.of(new Provider());
		}
		return provider;
	}
	
	public Long getMeProviderId(Long companyId) {
		Optional<Provider> providerId = providerRepository.findByCompanyIdAndIsVirtual(companyId,false);
		return providerId.get().getId();
	}
	
	public boolean checkProviderById(Long providerId, Long companyId) {
		return providerRepository.checkProvider(providerId, companyId);
	}

	public void paymentInpact(Long providerCompanyId, Long myCompanyId, Double amount) {
		Provider provider = getMeAsProvider(providerCompanyId).orElseThrow(() -> new RecordNotFoundException("Provider not found"));
		ProviderCompany providerCompany = providerCompanyRepository.findByProviderIdAndCompanyId(provider.getId(), myCompanyId).orElseThrow(() -> new RecordNotFoundException("You are not their provider"));
		
		double credit = providerCompany.getCredit();
		double advance = providerCompany.getAdvance();
		
		if (credit >= (amount + advance)) {
			providerCompany.setCredit(round(credit - amount - advance));
			providerCompany.setAdvance(0.0);
		} else {
			providerCompany.setAdvance(round(amount - credit + advance));
			providerCompany.setCredit(0.0);
		}
	}
	
	private double round(double value) {
		return Math.round(value * 100.0) / 100.0; // Round to two decimal places
	}
	/////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////
	public List<ProviderDto> getVirtualByCompanyIdaa(Long id) {
		List<Provider> provider = providerRepository.findAllByCompanyIdAndIsVirtual(id,true);
		List<ProviderDto> dtos = new ArrayList<>();
		for(Provider i : provider) {
			ProviderDto dto = providerMapper.mapToDto(i);
			dtos.add(dto);
		}
		return dtos;
	}
	public boolean existsByIda(Long id) {
		boolean existProvider = providerRepository.existsById(id);
		if(!existProvider) {
			throw new RecordNotFoundException("there is no Provider with id "+id);
		}
		return true;
	}

	//@Cacheable(value = "provider", key = "#root.methodName")

	//@Cacheable(value = "provider", key = "#root.methodName")



	
}
