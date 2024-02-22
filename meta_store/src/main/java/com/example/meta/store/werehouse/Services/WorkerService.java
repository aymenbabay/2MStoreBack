package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.ArticleController;
import com.example.meta.store.werehouse.Dtos.VacationDto;
import com.example.meta.store.werehouse.Dtos.WorkerDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Vacation;
import com.example.meta.store.werehouse.Entities.Worker;
import com.example.meta.store.werehouse.Mappers.VacationMapper;
import com.example.meta.store.werehouse.Mappers.WorkerMapper;
import com.example.meta.store.werehouse.Repositories.VacationRepository;
import com.example.meta.store.werehouse.Repositories.WorkerRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Service
@Transactional
@RequiredArgsConstructor
public class WorkerService extends BaseService<Worker, Long> {

	private final WorkerRepository workerRepository;
	
	private final WorkerMapper workerMapper;
	
	private final VacationMapper vacationMapper ;

	private final VacationRepository vacationRepository;

	private final UserService userService;


	private final Logger logger = LoggerFactory.getLogger(WorkerService.class);
	
	public ResponseEntity<WorkerDto> upDateWorker( WorkerDto workerDto, Company company) {
		Optional<Worker> worker = workerRepository.findByIdAndCompanyId(workerDto.getId(),company.getId());
		if(worker.isPresent()) {
			Worker categ = workerMapper.mapToEntity(workerDto);
			categ.setCompany(company);
			workerRepository.save(categ);
			return ResponseEntity.ok(workerDto);
			
		}else {
			throw new RecordNotFoundException("Worker Not Found");
		}
	}

	public Optional<Worker> getByName(String libelle, Long companyId) {
		return workerRepository.findByNameAndCompanyId(libelle, companyId);
	}

	public List<Worker> getAllByCompanyId(Long companyId) {
		return workerRepository.findAllByCompanyId(companyId);
	}

	public ResponseEntity<Worker> getByNameAndCompanyId(String name, Long companyId) {
		Optional<Worker> categ = workerRepository.findByNameAndCompanyId(name,companyId);
		if(!categ.isEmpty()) {
		Worker worker = categ.get();
		return ResponseEntity.ok(worker);
		}
		else return null;
	}
	
	public Optional<Worker> getByIdAndCompanyId(Long id , Long companyId) {
		return workerRepository.findByIdAndCompanyId(id, companyId);
	}

	public Long getByName(String name) {
		return workerRepository.findByName(name);
	}

	public Long getCompanyIdByUserName(String userName) {
		Long companyId = workerRepository.findByName(userName);
		if(companyId != null) {
			return companyId;
		}
		return null;
	}

	public ResponseEntity<List<WorkerDto>> getWorkerByCompany(Company company) {
		logger.warn("get worker by company 1");
		List<Worker> workers = getAllByCompanyId(company.getId());
		logger.warn("get worker by company 2");
		if(workers.isEmpty()) {
			logger.warn("get worker by company 3");
			throw new RecordNotFoundException("there is no worker");
		}
		logger.warn("get worker by company 4");
		List<WorkerDto> workersDto = new ArrayList<>();
		for(Worker i : workers) {
			logger.warn("get worker by company 5");
			WorkerDto workerDto = workerMapper.mapToDto(i);
			workersDto.add(workerDto);
		}
		return ResponseEntity.ok(workersDto);
	}

	public ResponseEntity<WorkerDto> getWorkerById(String name, Company company) {
		ResponseEntity<Worker> worker = getByNameAndCompanyId(name,company.getId());
		if(worker == null) {
			 throw new RecordNotFoundException("There Is No Worker With Libelle : "+name);
		}
		WorkerDto dto = workerMapper.mapToDto(worker.getBody());
		return ResponseEntity.ok(dto);
		
	}

	public ResponseEntity<WorkerDto> insertWorker(@Valid WorkerDto workerDto, Company company) {
		Long worker1 = getByName(workerDto.getName());
		if(worker1 !=null)  {
			throw new RecordIsAlreadyExist("is already worker");
		}

		Worker worker = new Worker();
		
		worker = workerMapper.mapToEntity(workerDto);
		worker.setCompany(company);
		worker.setRemainingday(worker.getTotdayvacation());
		if(workerDto.getUser() != null) {
			User user = userService.findByUserName(workerDto.getName());
			worker.setUser(user);
			
		}
		workerRepository.save(worker);
		return new ResponseEntity<WorkerDto>(HttpStatus.ACCEPTED);
		
	}

	public void deleteWorkerById(Long id, Company company) {
		Optional<Worker> worker = getByIdAndCompanyId(id,company.getId());
		if(worker.isEmpty()) {
			throw new RecordNotFoundException("This Worker Does Not Exist");
		}
	 super.deleteById(id);
	}

	public void addVacation(VacationDto vacationDto, Company company) {
		Vacation vacation = new Vacation();
			vacation = vacationMapper.mapToEntity(vacationDto);
		//Vacation vacation = vacationRepository.findById(vacationDto.getId()).orElseThrow(() -> new RecordNotFoundException("there is no worker with id : "+vacationDto.getId()));
		long differenceInDays = TimeUnit.DAYS.convert(vacationDto.getEnddate().getTime() - vacationDto.getStartdate().getTime()+86_400_000L, TimeUnit.MILLISECONDS);
		int year = getYearFromDate(vacationDto.getStartdate());
		vacation.setYear(year);
		vacation.setCompany(company);
		Worker worker = workerRepository.findById(vacation.getWorker().getId()).orElseThrow(() -> new RecordNotFoundException("this worker not found"));
		worker.setRemainingday(worker.getRemainingday()-differenceInDays);  
		//	vacation.setUsedday(vacation.getUsedday()+differenceInDays);
		//vacation.setStartdate(vacationDto.getStartdate());
		//vacation.setEnddate(vacationDto.getEnddate());
		Date now = new Date();
		if((vacationDto.getStartdate().before(now) || vacationDto.getStartdate().equals(now)) && vacationDto.getEnddate().after(now)) {
			worker.setStatusvacation(true);
		}
		vacationRepository.save(vacation);
	}

	public List<VacationDto> getWorkerHistory(Company company, Long id) {
		List<Vacation> vacations = vacationRepository.findByCompanyIdAndWorkerId(company.getId(),id);
		List<VacationDto> vacationsDto = new ArrayList<>();
		for(Vacation i : vacations) {
			VacationDto vacationDto = vacationMapper.mapToDto(i);
			vacationsDto.add(vacationDto);
		}
 		return vacationsDto;
	}
	
	  private static int getYearFromDate(Date date) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        return calendar.get(Calendar.YEAR);
	    }

	public List<WorkerDto> getMyWorkerByName(String name, Company company) {
		List<Worker> workers = workerRepository.findByCompanyIdAndNameContaining(name, company.getId());
		if(workers.isEmpty()) {
			throw new RecordNotFoundException(" there is no worker with name : "+name);
		}
		
		List<WorkerDto> workersDto = new ArrayList<>();
		for(Worker i : workers ) {
			WorkerDto workerDto = workerMapper.mapToDto(i);
			workersDto.add(workerDto);
		}
		return workersDto;
	}

	public Long findCompanyIdByUserId(Long userId) {
		logger.warn("begin of get company id bu user id ");
		Long companyId = workerRepository.findCompanyIdByUserId(userId);
		logger.warn("just after of get company id bu user id from repository ");
		return companyId;
	}
	

}
