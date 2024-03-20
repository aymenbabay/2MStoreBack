package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Company;

public interface CompanyRepository extends BaseRepository<Company, Long> {


	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	boolean existsByName(String name);

	boolean existsByUserId(Long id);
	
	Optional<Company> findByUserId(Long userId);
	
	boolean existsByCode(String code);

	boolean existsByMatfisc(String matfisc);
	
	boolean existsByBankaccountnumber(String bankaccountnumber);
	
	@Query("SELECT c FROM Company c WHERE c.name LIKE %:branshe% AND c.id <> :id")
	List<Company> findByNameContaining(String branshe, Long id);
	/////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////
	void deleteByIdAndUserId(Long id, Long userId );



}
