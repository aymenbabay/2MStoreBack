package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;
import com.example.meta.store.werehouse.Entities.Provider;

public interface InvetationClientProviderRepository extends BaseRepository<InvetationClientProvider, Long> {

	@Query("SELECT i FROM InvetationClientProvider i WHERE"
			+ " i.company.id = :companyId"
			+ " OR i.client.id = :clientId"
			+ " OR i.provider.id = :providerId"
			+ " OR i.user.id = :userId"
			)
	List<InvetationClientProvider> findAllByClientIdOrProviderIdOrCompanyIdOrUserId(Long clientId, Long providerId, Long companyId, Long userId);

	@Modifying
	@Query("DELETE FROM InvetationClientProvider i WHERE ((i.client = :hisClient OR i.client = :myClient) OR (i.provider = :hisProvider OR i.provider = :myProvider)) AND (i.company = :hisCompany Or i.company = :myCompany)")
	void deleteByClientOrProviderAndCompany(Client hisClient, Client myClient, Provider hisProvider, Provider myProvider, Company hisCompany, Company myCompany);

	void deleteByClientIdAndCompanyId(Long id, Long id2);

	void deleteByProviderIdAndCompanyId(Long id, Long id2);

	@Query("SELECT i FROM InvetationClientProvider i WHERE i.user.id = :id")
	InvetationClientProvider findByWorkerId(Long id);

}


