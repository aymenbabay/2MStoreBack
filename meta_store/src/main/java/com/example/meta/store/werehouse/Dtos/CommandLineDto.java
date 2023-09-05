package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CommandLineDto extends BaseDto<Long> implements Serializable{


    private static final long serialVersionUID = 12345678119L;
    
	private Double quantity;

	private Double totTva;

	private Double prixArticleTot;
	
	private Long article;
	
	private String articleLibelle;
	
	private Double articleCost;
	
	private Double articleTva;
	
	private String articleCode;
	
	private String articleUnit;
	
	private Double articleMargin;
}
