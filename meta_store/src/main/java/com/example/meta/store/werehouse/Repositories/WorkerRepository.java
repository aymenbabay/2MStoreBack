package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Worker;

public interface WorkerRepository extends BaseRepository<Worker, Long> {

	
	Optional<Worker> findByNameAndCompanyId(String name, Long id);

	@Query("SELECT a FROM Worker a WHERE a.company.id = :companyId")
	List<Worker> findAllByCompanyId(@Param("companyId") Long companyId);

	Optional<Worker> findByIdAndCompanyId(Long id, Long companyId);

	@Query("SELECT a.company.id FROM Worker a WHERE a.name = :name")
	Long findByName(@Param("name") String name);

	@Query("SELECT w FROM Worker w WHERE w.company.id = :companyId AND w.name LIKE %:name%")
	List<Worker> findByCompanyIdAndNameContaining(String name, Long companyId);

	@Query("SELECT w.company.id FROM Worker w WHERE w.user.id = :userId")
	Long findCompanyIdByUserId(Long userId);

	boolean existsByNameAndCompanyId(String name, Long companyId);


}
