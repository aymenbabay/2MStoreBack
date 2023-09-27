package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import com.example.meta.store.Base.Security.Dto.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto extends BaseDto<Long> implements Serializable {

    private static final long serialVersionUID = 12345678111L;
    
    private String sender;
    
    private String receiver;
    
    private String content;
}
