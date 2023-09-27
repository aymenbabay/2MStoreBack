package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.InvetationClientProvider;

public interface InvetationClientProviderRepository extends BaseRepository<InvetationClientProvider, Long> {

	@Query("SELECT i FROM InvetationClientProvider i WHERE"
			+ " i.company.id = :companyId"
			+ " OR i.client.id = :clientId"
			+ " OR i.provider.id = :providerId")
	List<InvetationClientProvider> findAllByClientIdOrProviderIdOrCompanyId(Long clientId, Long providerId, Long companyId);

}
