package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.NotPermissonException;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.InvetationClientProviderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.InvetationClientProviderMapper;
import com.example.meta.store.werehouse.Repositories.InvetationClientProviderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InvetationService extends BaseService<InvetationClientProvider, Long> {
	
	private final InvetationClientProviderRepository invetationClientProviderRepository;
	
	private final InvetationClientProviderMapper invetationClientProviderMapper;
	
	private final ProviderService providerService;
	
	private final ClientService clientService;

	private final Logger logger = LoggerFactory.getLogger(InvetationService.class);
	
	public List<InvetationClientProviderDto> getInvetation(Client client, Provider provider, Company company) {
		logger.warn("invetation service get invetation function 1");
		List<InvetationClientProvider> invetations =	invetationClientProviderRepository.findAllByClientIdOrProviderIdOrCompanyId(client.getId(),provider.getId(),company.getId());
		logger.warn("invetation service get invetation function 2");
		List<InvetationClientProviderDto> invetationsDto = new ArrayList<>();
		logger.warn("invetation service get invetation function 3");
		for(InvetationClientProvider i : invetations) {
			logger.warn("invetation service get invetation function 4");
			InvetationClientProviderDto dto =  invetationClientProviderMapper.mapToDto(i);
			invetationsDto.add(dto);
			logger.warn("invetation service get invetation function 4"+dto.getId());
		}
		return invetationsDto;
	}

	public void requestResponse(Optional<Client> client, Optional<Provider> provider, Long id, Status status) {
		logger.warn("invetation service in the first line of request response function");
		Optional<InvetationClientProvider> invi = invetationClientProviderRepository.findById(id);
		logger.warn("invetation service in the second line of request response function");
		if(invi.isEmpty()) {
			logger.warn("invetation service in the therd line of request response function");
			throw new RecordNotFoundException("there is no request with id "+id);
		}
		InvetationClientProvider invetation = invi.get();
			if(status == Status.ACCEPTED) {
				logger.warn("invetation service in the forth line of request response function");
				if(invetation.getClient() != null) {
					logger.warn("company id in request response in invetation service "+invetation.getClient().getCompany().getId());
					clientService.acceptedClientInvetation(invetation);	
				}
				else {
					logger.warn("invetation service in the fifth line of request response function");
					clientService.acceptedProviderInvetation(invetation.getProvider());
				}
				invetation.setStatus(Status.ACCEPTED);				
			}else {
				invetation.setStatus(Status.REFUSED);								
			}
			invetationClientProviderRepository.save(invetation);
		
		
	}

	public void cancelRequestOrDeleteFriend(Client client, Provider provider, Long id) {
		InvetationClientProvider invetation = super.getById(id).getBody();
		Company company = client.getCompany();
		if(invetation == null) {
			throw new RecordNotFoundException("there is no invetation ");
		}
		if(invetation.getCompany() != company && invetation.getClient() != client && invetation.getProvider() != provider) {
			throw new NotPermissonException("you dont have permission to do that ");
		}
		if(invetation.getStatus() == Status.INWAITING) {
			invetationClientProviderRepository.delete(invetation);
			return;
		}
		
		
	}

	

}
