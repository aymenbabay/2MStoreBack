package com.example.meta.store.Base.Entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseDto<ID>  implements Serializable{

    private static final long serialVersionUID = 1234567812L;
    
	private ID id;
	
	private String statusCode;
	
	private boolean isDeleted;

}
