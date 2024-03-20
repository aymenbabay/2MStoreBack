package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.SubCategory;

public interface SubCategoryRepository extends BaseRepository<SubCategory, Long> {
	/////////////////////////////////////////////////////// real work ///////////////////////////////////////////////////
	
	@Query("SELECT a FROM SubCategory a WHERE a.company.id = :companyId")
	List<SubCategory> findAllByCompanyId(@Param("companyId") Long companyId);

	Optional<SubCategory> findByIdAndCompanyId(Long id, Long companyId);

	Optional<SubCategory> findByLibelleAndCompanyId(String libelle, Long companyId);

	List<SubCategory> findAllByCompanyIdAndCategoryId(Long id, Long categoryId);
	/////////////////////////////////////////////////////// future work ///////////////////////////////////////////////////
	/////////////////////////////////////////////////////// not work ///////////////////////////////////////////////////
	Optional<SubCategory> findByCodeAndCompanyId(String code, Long id);
	/////////////////////////////////////////////////////// ///////////////////////////////////////////////////




}
