package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.WorkerDto;
import com.example.meta.store.werehouse.Entities.Worker;

@Mapper
public interface WorkerMapper {

	Worker mapToEntity(WorkerDto dto);
	
	WorkerDto mapToDto(Worker entity);
}
