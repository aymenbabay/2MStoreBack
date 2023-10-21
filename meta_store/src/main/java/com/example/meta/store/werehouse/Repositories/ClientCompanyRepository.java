package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.ClientCompany;

public interface ClientCompanyRepository extends BaseRepository<ClientCompany, Long> {

	void deleteByClientId(Long id);

	void deleteByClientIdAndCompanyId(Long id, Long id2);

	boolean existsByClientIdAndCompanyId(Long clientId, Long companyId);
	
	Optional<ClientCompany> findByClientIdAndCompanyId(Long clientId, Long companyId);

	@Query("SELECT c FROM ClientCompany c WHERE c.company.id = :companyId AND c.isDeleted = false")
	List<ClientCompany> getAllMyClients(Long companyId);

	boolean existsByClientIdAndCompanyIdAndIsDeletedFalse(Long clientId, Long companyId);

	
}
