package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Provider;
import com.example.meta.store.werehouse.Entities.ProviderCompany;

public interface ProviderRepository extends BaseRepository<Provider, Long>{

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	Optional<Provider> findByCode(String code);

	Optional<Provider> findByCodeAndCompanyId(String code, Long companyId);
	
	List<Provider> findAllByCompanyIdAndIsVirtual(Long id, boolean b);
	
	@Query("SELECT p FROM Provider p JOIN ProviderCompany pc WHERE p.company.id = :compnayId OR ( pc.company.id = :companyId AND p.isVirtual = true) ")
	List<Provider> findAllMyVirtualByCompanyId(Long companyId);
	
	Optional<Provider> findByCompanyIdAndIsVirtual(Long id, boolean b);
	
	
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Provider p JOIN ProviderCompany pc WHERE pc.company.id = :companyId AND pc.provider.id = :providerId")
	boolean checkProvider(Long providerId, Long companyId);
	
	/////////////////////////////////////////////////////// future work ///////////////////////////////////////////////////
	Optional<Provider> findByBankaccountnumber(String bankaccountnumber);
	Optional<Provider> findByMatfisc(String matfisc);
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	

	
//	 @Query("SELECT p FROM Provider p WHERE p.isVirtual = false AND NOT EXISTS (SELECT 1 FROM Client c JOIN c.providers cp WHERE cp.id = p.id)")
//	   List<Provider> findAllReal();
	
	
}
