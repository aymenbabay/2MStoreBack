package com.example.meta.store.Base.Security.Mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.Base.Security.Dto.UserDto;
import com.example.meta.store.Base.Security.Entity.User;
@Mapper
public interface UserMapper {
	
	
//	@Mapping(source = "username", target = "name")
	UserDto mapToDto (User entity);
	
	@InheritInverseConfiguration
	User mapToEntity (UserDto dto);
}
