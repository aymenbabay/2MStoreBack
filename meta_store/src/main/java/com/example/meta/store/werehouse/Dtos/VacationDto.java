package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;
import java.util.Date;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.werehouse.Entities.Worker;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class VacationDto extends BaseDto<Long> implements Serializable {


    private static final long serialVersionUID = 12345678124L;
    
    private long usedday;
	
	private long remainingday;
		
	private int year;
	
	private Date startdate;
	
	private Date enddate;
		
	private Worker worker;
}
