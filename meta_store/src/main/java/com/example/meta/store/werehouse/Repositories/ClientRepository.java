package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.ClientCompany;
import com.example.meta.store.werehouse.Entities.Provider;


public interface ClientRepository extends BaseRepository<Client, Long>{

	
	Optional<Client> findByCodeAndCompanyId(String code, Long companyId);
	
	Optional<Client> getByCompanyIdAndIsVirtualFalse( Long companyId);

	@Query("SELECT c FROM ClientCompany c WHERE c.client.isVisible = 2 AND (c.client.name LIKE %:search% OR c.client.code LIKE %:search%)")
	List<ClientCompany> findAllByIsVisibleTrueAndNameContainingOrCodeContaining(String search);

	@Query("SELECT c FROM ClientCompany c WHERE"
			+ " (c.client.company.id = :companyId OR"
			+ " (c.client.isVisible = 2 OR (c.client.isVisible = 1 AND c.company.id = :companyId ))) "
			+ " AND (c.client.name LIKE %:search% OR c.client.code LIKE %:search%)")
	List<ClientCompany> findAllByIsVisibleAndNameContainingOrCodeContaining(String search, Long companyId);
	
	@Query("SELECT p.client FROM ClientCompany p WHERE (( p.company.id = :companyId ) AND (p.client.name LIKE %:search% OR p.client.code LIKE %:search%))")
	List<Client> findAllByNameContainingOrCodeContainingAndCompanyId(String search, Long companyId);
	
	///////////////// CE BON /////////////////////////

	@Query("SELECT c FROM Client c WHERE c.company.id IS NOT NULL")
	List<Client> findAllHasCompanyId();

	





	
	
	

}
