package com.example.meta.store.werehouse.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Bill;

public interface BillRepository extends BaseRepository<Bill, Long>{

	@Query("SELECT b FROM Bill b WHERE b.invoice.id = :id")
	Optional<Bill> findByInvoiceId(Long id);

}
