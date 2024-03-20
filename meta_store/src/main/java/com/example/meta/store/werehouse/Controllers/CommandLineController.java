package com.example.meta.store.werehouse.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Services.CommandLineService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.WorkerService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/werehouse/commandline/")
@RequiredArgsConstructor
@Validated
public class CommandLineController {


	private final CommandLineService commandLineService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
		
	private final CompanyService companyService;

	private final WorkerService workerService;
	

	private final Logger logger = LoggerFactory.getLogger(CommandLineController.class);

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	@PostMapping("{type}/{invoicecode}/{clientid}/{discount}")
	public ResponseEntity<InputStreamResource> addCommandLine(@RequestBody  List<CommandLineDto> commandLinesDto,
			@PathVariable Long invoicecode, @PathVariable String type, @PathVariable Long clientid, @PathVariable Double discount)
					throws JsonProcessingException {
		Company company = getCompany();
		return commandLineService.insertLine(commandLinesDto, company,clientid,discount,type);
		
	}
	
	@GetMapping("getcommandline/{invoiceId}")
	public List<CommandLineDto> getCommandLines(@PathVariable Long invoiceId){
		return commandLineService.getCommandLines(invoiceId);
	}
	
	/////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////
	

	private Company getCompany() {
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Optional<Company> company = companyService.findCompanyIdByUserId(userId);
		if(company.isPresent()) {
			return company.get();
		}
		Long companyId = workerService.getCompanyIdByUserName(authenticationFilter.userName);
		if(companyId != null) {			
		ResponseEntity<Company> company2 = companyService.getById(companyId);
		return company2.getBody();
		}
			throw new RecordNotFoundException("You Dont Have A Company Please Create One If You Need ");
			
	}

}
