package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Provider;


public interface ClientRepository extends BaseRepository<Client, Long>{


	Optional<Client> findByIsVirtualFalseAndCompanyId(Long id);

	Optional<Client> findByCodeAndCompanyId(String code, Long companyId);
	
	List<Client> getAllByCompanyId( Long companyId);


	@Query("SELECT c FROM Client c WHERE c.company.id IS NOT NULL")
	List<Client> findAllHasCompanyId();

	@Query("SELECT c FROM Client c WHERE c.company.id = :id AND c.company.name = :name")
	List<Client> findByNameAndCompanyId(String name, Long id);


	@Query("SELECT p FROM Client c JOIN c.providers p WHERE c.id = :id") // AND c.id <> :p.id
	List<Provider> findAllProvider(Long id);
	
	@Query("SELECT c.id FROM Client c WHERE c.company.id = :id AND c.isVirtual = false")
	Long findidByCompanyId(Long id);
	
	@Query("SELECT c FROM Client c JOIN c.providers p WHERE p.id = :providerId") //c.company.id = :companyId OR
	List<Client> getAllMyClients(Long providerId);

	@Query("SELECT c FROM Client c WHERE c.isVisible = 2 AND (c.name LIKE %:search% OR c.code LIKE %:search%)")
	List<Client> findAllByIsVisibleTrueAndNameContainingOrCodeContaining(String search);


	@Query("SELECT c FROM Client c JOIN c.providers p WHERE"
			+ " (c.company.id = :companyId OR"
			+ " (c.isVisible = 2 OR (c.isVisible = 1 AND p.id = :providerId ))) "
			+ " AND (c.name LIKE %:search% OR c.code LIKE %:search%)")
	List<Client> findAllByIsVisibleAndNameContainingOrCodeContaining(String search, Long companyId, Long providerId);
		
		
	@Query("SELECT p FROM Client c JOIN c.providers p WHERE"
	  		+ " (p.isVisible = 2 OR (p.isVisible = 1"
	  		+ " AND (c.id = :clientId " 
	  		+ " OR EXISTS (SELECT 1 FROM Client cl JOIN cl.providers pr WHERE cl.company.id = p.company.id AND cl.isVirtual = false AND pr.id = :providerId ) )))"
	  		+ " AND (p.name LIKE %:search% OR p.code LIKE %:search%)"
	  		+ " UNION SELECT p FROM Provider p WHERE "
	  		+ " (p.company.id = :companyId)"
	  		+ " AND (p.name LIKE %:search% OR p.code LIKE %:search%)"
	  		)
	List<Provider> findProviderByIsVisibleAndNameAndCodeContaining(String search, Long clientId,Long providerId, Long companyId);
	  

	
	@Query("SELECT c.company.id FROM Client c WHERE c.id = :clientId")
	Long findCompanyIdById(Long clientId);
	
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Client c JOIN c.providers p WHERE c.company.id = :companyId AND p.id = :providerId")
	boolean checkProvider(Long providerId, Long companyId);


	@Query("SELECT c FROM Client c JOIN c.providers p WHERE ((c.company.id = :companyId OR p.id =:providerId ) AND (c.name LIKE %:search% OR c.code LIKE %:search%))")
	List<Client> findAllByNameContainingOrCodeContainingAndCompanyId(String search, Long companyId,Long providerId);

	
	

}
