package com.example.meta.store.werehouse.Repositories;

import java.util.Optional;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Company;

public interface CompanyRepository extends BaseRepository<Company, Long> {


	boolean existsByName(String name);
	
	boolean existsByUserId(Long id);
	
	void deleteByIdAndUserId(Long id, Long userId );

	Optional<Company> findByUserId(Long userId);

	boolean existsByCode(String code);

	boolean existsByCodecp(String codecp);

	boolean existsByMatfisc(String matfisc);

	boolean existsByBankaccountnumber(String bankaccountnumber);

	//Company findByClientId(Long clientId);


}
