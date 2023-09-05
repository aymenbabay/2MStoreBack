package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientReturnForInvoice implements Serializable {


	private Long id;
	
    private String name; 
    
    private String matfisc;

	private String phone;
	
	private String address;
	
	private String email;
	
    private boolean isVirtual;
}
