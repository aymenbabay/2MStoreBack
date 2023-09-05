package com.example.meta.store.Base.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.meta.store.Base.Entity.BaseEntity;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Repository.BaseRepository;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseService<T extends BaseEntity<ID>,ID extends Number> {

	@Autowired
	private BaseRepository<T, ID> baseRepository;
	
	
	public List<T> getAll(){
		List<T> entity = new ArrayList<>();
		entity =  baseRepository.findAll();
			return entity;
	}
	
	public ResponseEntity<T> getById(ID id) {
		Optional<T> entity =  baseRepository.findById(id);
		if(entity.isPresent()) {
			return ResponseEntity.ok(entity.get());
		}else {
			throw new RecordNotFoundException("there is no record with id: "+id);
		}
	}
	
	public ResponseEntity<T> insert(T entity) {
		
		baseRepository.save(entity);
		return	new ResponseEntity<T>(HttpStatus.OK);
	}
	
	public ResponseEntity<T> insertAll(List<T> articles){
		baseRepository.saveAll(articles);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	public void deleteById(ID id) {
		
			baseRepository.deleteById(id);
		
	}

	
}
