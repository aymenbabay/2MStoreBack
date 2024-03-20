package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Inventory;

public interface InventoryRepository extends BaseRepository<Inventory, Long>{

	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	List<Inventory> findByCompanyId(Long companyId);
	
	@Query("SELECT i FROM Inventory i WHERE i.article.id = :companyArticle AND i.company.id = :id")
	Optional<Inventory> findByArticleIdAndCompanyId(Long companyArticle, Long id);

	

}
