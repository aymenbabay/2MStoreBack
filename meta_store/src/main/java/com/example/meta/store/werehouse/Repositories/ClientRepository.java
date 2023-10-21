package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Provider;


public interface ClientRepository extends BaseRepository<Client, Long>{

	
	Optional<Client> findByCodeAndCompanyId(String code, Long companyId);
	
	Optional<Client> getByCompanyIdAndIsVirtualFalse( Long companyId);

	@Query("SELECT c FROM Client c WHERE c.isVisible = 2 AND (c.name LIKE %:search% OR c.code LIKE %:search%)")
	List<Client> findAllByIsVisibleTrueAndNameContainingOrCodeContaining(String search);

	@Query("SELECT c FROM Client c JOIN ClientCompany p WHERE"
			+ " (c.company.id = :companyId OR"
			+ " (c.isVisible = 2 OR (c.isVisible = 1 AND p.company.id = :companyId ))) "
			+ " AND (c.name LIKE %:search% OR c.code LIKE %:search%)")
	List<Client> findAllByIsVisibleAndNameContainingOrCodeContaining(String search, Long companyId);
	
	@Query("SELECT c FROM Client c JOIN ClientCompany p WHERE ((c.company.id = :companyId OR p.company.id =:companyId ) AND (c.name LIKE %:search% OR c.code LIKE %:search%))")
	List<Client> findAllByNameContainingOrCodeContainingAndCompanyId(String search, Long companyId);
	
	///////////////// CE BON /////////////////////////

	@Query("SELECT c FROM Client c WHERE c.company.id IS NOT NULL")
	List<Client> findAllHasCompanyId();






	
	
	

}
