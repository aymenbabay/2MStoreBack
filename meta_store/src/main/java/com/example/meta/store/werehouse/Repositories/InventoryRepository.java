package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Inventory;

public interface InventoryRepository extends BaseRepository<Inventory, Long>{

	List<Inventory> findByCompanyId(Long companyId);

	@Query("SELECT I FROM Inventory I WHERE I.companyArticle.id = :companyArticle AND I.company.id = :id")
	Optional<Inventory> findByCompanyarticleIdAndCompanyId(Long companyArticle, Long id);

	
	//void deleteByCompanyarticleid(Long companyarticleId);

}
