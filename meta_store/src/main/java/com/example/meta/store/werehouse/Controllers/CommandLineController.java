package com.example.meta.store.werehouse.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Mappers.CommandLineMapper;
import com.example.meta.store.werehouse.Services.CommandLineService;
import com.example.meta.store.werehouse.Services.CompanyService;
import com.example.meta.store.werehouse.Services.ExportInvoicePdf;
import com.example.meta.store.werehouse.Services.InvoiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/werehouse/commandline")
@RequiredArgsConstructor
@Validated
public class CommandLineController {


	private final CommandLineService commandLineService;
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	private final UserService userService;
		
	private final CompanyService companyService;
	
	private final InvoiceService invoiceService;
	
	private final CommandLineMapper commandLineMapper;
	private final Logger logger = LoggerFactory.getLogger(CommandLineController.class);
    private final ObjectMapper objectMapper;
	
	@PostMapping("/{type}/{invoicecode}/{clientid}")
	public ResponseEntity<InputStreamResource> addCommandLine(@RequestBody  List<CommandLineDto> commandLinesDto,
			@PathVariable Long invoicecode, @PathVariable String type, @PathVariable Long clientid) throws JsonProcessingException {
//        String json = objectMapper.writeValueAsString(commandLinesDto);
//		  logger.debug("JSON content: {}", json);
//		System.out.println(invoicecode+" invoice code "+type+" type "+clientid+" clinet id ");
//		
		Long userId = userService.findByUserName(authenticationFilter.userName).getId();
		Company company = companyService.findCompanyIdByUserId(userId);
		return commandLineService.insertLine(commandLinesDto, company,clientid,type);
		
		
	
		
	}
	

	

}
