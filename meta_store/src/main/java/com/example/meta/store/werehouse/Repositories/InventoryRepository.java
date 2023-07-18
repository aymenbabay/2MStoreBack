package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Inventory;

public interface InventoryRepository extends BaseRepository<Inventory, Long>{

	List<Inventory> findByCompanyId(Long companyId);

	Optional<Inventory> findByCompanyIdAndArticleCode(Long companyId, String articleCode);

	void deleteByCompanyIdAndArticleCode(Long companyId, String articleCode);

}
