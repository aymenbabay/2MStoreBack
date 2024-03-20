package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Category;

public interface CategoryRepository extends BaseRepository<Category, Long> {

	///////////////////////////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////////////////
	Optional<Category> findByLibelleAndCompanyId(String libelle, Long companyId);
	Optional<Category> findByIdAndCompanyId(Long id , Long companyId);

	
	///////////////////////////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////////////////
	@Query("SELECT a FROM Category a WHERE a.company.id = :companyId")
	List<Category> findAllByCompanyId(@Param("companyId") Long companyId);

	///////////////////////////////////////////////////////////////////////////// ///////////////////////////////////////////////////////////////


}
