package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Provider;

import jakarta.validation.Valid;

public interface ClientRepository extends BaseRepository<Client, Long>{

	Optional<Client> findByCode(String code);

	Optional<Client> findByCompanyId(Long id);

	Optional<Client> findByCodeAndCompanyId(String code, Long companyId);
	
	List<Client> getAllByCompanyId( Long companyId);


	@Query("SELECT c FROM Client c WHERE c.company.id IS NOT NULL")
	List<Client> findAllHasCompanyId();

	@Query("SELECT c FROM Client c WHERE c.company.id = :id AND c.company.name = :name")
	List<Client> findByNameAndCompanyId(String name, Long id);


	@Query("SELECT p FROM Client c JOIN c.providers p WHERE c.id = :id") // AND c.id <> p.id
	List<Provider> findAllProvider(Long id);
	
	@Query("SELECT c.id FROM Client c WHERE c.company.id = :id AND c.isVirtual = false")
	Long findidByCompanyId(Long id);
	
	@Query("SELECT c FROM Client c JOIN c.providers p WHERE p.id = :providerId") //c.company.id = :companyId OR
	List<Client> getAllClients(Long providerId);

	@Query("SELECT c FROM Client c WHERE c.isVirtual = false")
	List<Client> findAllClient();

	@Query("SELECT c.company.id FROM Client c WHERE c.id = :clientId")
	Long findCompanyIdById(Long clientId);
	
	

}
