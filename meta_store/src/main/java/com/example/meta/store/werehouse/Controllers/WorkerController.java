package com.example.meta.store.werehouse.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.VacationDto;
import com.example.meta.store.werehouse.Dtos.WorkerDto;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.WorkerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/worker")
@RequiredArgsConstructor
public class WorkerController {

	private final WorkerService workerService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
	
	private final CompanyService companyService;
	
	
	@GetMapping("/getbycompany/{id}")
	public ResponseEntity<List<WorkerDto>> getWorkerByCompany(@PathVariable Long id){
		Company company;
		company = getCompany();
		if(company.getId() != id ) {
			company = companyService.getById(id).getBody();
		}
		return workerService.getWorkerByCompany(company);
	}
	
	@GetMapping("/l/{name}")
	public ResponseEntity<WorkerDto> getWorkerById(@PathVariable String name){
		Company company = getCompany();
		return workerService.getWorkerById(name,company);
		
	}
	
	@GetMapping("/get/{name}")
	public List<WorkerDto> getMyWorkerByName(@PathVariable String name){
		Company company = getCompany();
		return workerService.getMyWorkerByName(name,company);
	}
	
	@PostMapping("/add")
	public ResponseEntity<WorkerDto> insertWorker(@RequestBody @Valid WorkerDto workerDto){
		Company company = getCompany();
		return workerService.insertWorker(workerDto,company);
	}
	
	@PutMapping("/update")
	public ResponseEntity<WorkerDto> upDateWorker(@RequestBody @Valid WorkerDto workerDto){
		Company company = getCompany();
		return workerService.upDateWorker(workerDto,company);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteWorkerById(@PathVariable Long id){
		Company company = getCompany();
			workerService.deleteWorkerById(id,company);
		}
	
	@PostMapping("/addvacation")
	public void addVacation( @RequestBody VacationDto vacationDto) {
		Company company = getCompany();
		workerService.addVacation(vacationDto,company);
		}
	
	@GetMapping("/history/{id}/{companyId}")
	public List<VacationDto> getWorkerHistory(@PathVariable Long id, @PathVariable Long companyId) {
		Company company;
		company = getCompany();
		if(company.getId() != companyId ) { //&& not branshes
			company = companyService.getById(companyId).getBody();
		}
		return workerService.getWorkerHistory(company,id);
	}
	
	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company != null) {
			return company.get();
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}
}
