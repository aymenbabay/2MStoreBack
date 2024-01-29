package com.example.meta.store.werehouse.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Check;

public interface CheckRepository extends BaseRepository<Check, Long>{

	@Query("SELECT c FROM Check c WHERE c.invoice.id = :id")
	Optional<Check> findByInvoiceId(Long id);

}
