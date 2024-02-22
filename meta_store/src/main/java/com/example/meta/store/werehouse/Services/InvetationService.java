package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.NotPermissonException;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Entity.Role;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Enums.RoleEnum;
import com.example.meta.store.Base.Security.Repository.UserRepository;
import com.example.meta.store.Base.Security.Service.RoleService;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.InvetationDto;
import com.example.meta.store.werehouse.Dtos.WorkerDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Invetation;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.Worker;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Enums.Type;
import com.example.meta.store.werehouse.Mappers.InvetationClientProviderMapper;
import com.example.meta.store.werehouse.Repositories.InvetationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InvetationService extends BaseService<Invetation, Long> {
	
	private final InvetationRepository invetationClientProviderRepository;
	
	private final InvetationClientProviderMapper invetationClientProviderMapper;
	
	private final RoleService roleService;

	private final UserService userService;
	
	private final ClientService clientService;
	
	private final WorkerService workerService;

	private final CompanyService companyService;

	private final Logger logger = LoggerFactory.getLogger(InvetationService.class);
	
	public List<InvetationDto> getInvetation(Client client, Provider provider, Company company, Long userId) {
		logger.warn("invetation service get invetation function 1");
		List<Invetation> invetations =	invetationClientProviderRepository.findAllByClientIdOrProviderIdOrCompanyIdOrUserId(client.getId(),provider.getId(),company.getId(), userId);
		logger.warn("invetation service get invetation function 2");
		List<InvetationDto> invetationsDto = new ArrayList<>();
		logger.warn("invetation service get invetation function 3");
		for(Invetation i : invetations) {
			logger.warn("invetation service get invetation function 4");
			InvetationDto dto =  invetationClientProviderMapper.mapToDto(i);
			invetationsDto.add(dto);
			logger.warn("invetation service get invetation function 4"+dto.getId());
		}
		return invetationsDto;
	}

	public void requestResponse(Long id, Status status) {
		logger.warn("invetation service in the first line of request response function");
		Invetation invetation = invetationClientProviderRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("there is no request with id "+id));
		logger.warn("invetation service in the second line of request response function");
		
			if(status == Status.ACCEPTED) {
				switch (invetation.getType()){ 
				case WORKER:	
					WorkerDto workerDto = invetationClientProviderMapper.mapInvetationToWorker(invetation);
					Set<Role> role = new HashSet<>();
					ResponseEntity<Role> role2 = roleService.getById((long) 3);
					role.add(role2.getBody());

					invetation.getUser().setRoles(role);
					userService.save(invetation.getUser());
					workerService.insertWorker(workerDto, invetation.getCompanySender());
				break;
				case PARENT:
					companyService.acceptedInvetation(invetation.getCompanySender(),invetation.getCompanyReciver());
				break;
				case PROVIDER:
					clientService.acceptedInvetation(invetation);					
				break;
				case CLIENT:
					clientService.acceptedInvetation(invetation);					
				break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + invetation.getType());
				}
				invetation.setStatus(Status.ACCEPTED);				
			}else {
				invetation.setStatus(Status.REFUSED);								
			}
			invetationClientProviderRepository.save(invetation);		
	}

	public void cancelRequestOrDeleteFriend(Client client, Provider provider, Long id) {
		Invetation invetation = super.getById(id).getBody();
		Company company = client.getCompany();
		if(invetation == null) {
			throw new RecordNotFoundException("there is no invetation ");
		}
		if(invetation.getCompanySender() != company && invetation.getClient() != client && invetation.getProvider() != provider) {
			throw new NotPermissonException("you dont have permission to do that ");
		}
		if(invetation.getStatus() == Status.INWAITING) {
			invetationClientProviderRepository.delete(invetation);
			return;
		}
		
		
	}

	public void sendWorkerInvetation(Company company, Worker worker) {
		Invetation invet = invetationClientProviderRepository.findByWorkerId(worker.getId());
		if(invet != null) {
			throw new RecordNotFoundException("this user is already worker");
		}
		Invetation invetation = new Invetation();
		invetation.setCompanySender(company);
		invetation.setUser(worker.getUser());
		invetation.setDepartment(worker.getDepartment());
		invetation.setSalary(worker.getSalary());
		invetation.setJobtitle(worker.getJobtitle());
		invetation.setTotdayvacation(worker.getTotdayvacation());
		invetation.setStatusvacation(worker.isStatusvacation());
		invetation.setStatus(Status.INWAITING);
		invetation.setType(Type.WORKER);
		invetationClientProviderRepository.save(invetation);
		
	}

	public void sendParentInvetation(Company company, Company reciver) {
		Invetation invetation = new Invetation();
		invetation.setCompanySender(company);
		invetation.setCompanyReciver(reciver);
		invetation.setType(Type.PARENT);
		invetation.setStatus(Status.INWAITING);
		invetationClientProviderRepository.save(invetation);
	}

	

}
